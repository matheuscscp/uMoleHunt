package org.unbiquitous.examples.umolehunt.game.state;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.unbiquitous.examples.umolehunt.game.BigMessage;
import org.unbiquitous.examples.umolehunt.game.FollowerObject;
import org.unbiquitous.examples.umolehunt.game.Player;
import org.unbiquitous.examples.umolehunt.game.PlayerSync;
import org.unbiquitous.examples.umolehunt.game.Team;
import org.unbiquitous.ubiengine.game.state.ChangeState;
import org.unbiquitous.ubiengine.game.state.CommonChange;
import org.unbiquitous.ubiengine.game.state.GameState;
import org.unbiquitous.ubiengine.game.state.GameStateArgs;
import org.unbiquitous.ubiengine.resources.input.keyboard.KeyboardDevice;
import org.unbiquitous.ubiengine.resources.input.keyboard.KeyboardManager;
import org.unbiquitous.ubiengine.resources.time.DeltaTime;
import org.unbiquitous.ubiengine.resources.video.Screen;
import org.unbiquitous.ubiengine.resources.video.texture.Animation;
import org.unbiquitous.ubiengine.resources.video.texture.Sprite;
import org.unbiquitous.ubiengine.util.ComponentContainer;
import org.unbiquitous.ubiengine.util.mathematics.linearalgebra.Vector3;

public class StateGame extends GameState {

  public static final class Args extends GameStateArgs {
    private List<PlayerSync> players;
    
    public Args(List<PlayerSync> plrs) {
      players = plrs;
    }
    
    public List<PlayerSync> getPlayers() {
      return players;
    }
  }

  private static final int START_ROUND = 0;
  private static final int SYNC        = 1;
  private static final int SHOW_PLAYER = 2;
  private static final int WAIT_MOVE   = 3;
  private static final int SHOW_MOVE   = 4;
  private static final int ROUND_OVER  = 5;
  
  private int state;
  
  private String[] all_words;
  private List<String> available_words;

  private List<PlayerSync> wait_list;
  private List<Player> sync_list;
  private List<Player> players;
  
  private Sprite bg;
  
  private Queue<KeyboardDevice> down_devices;

  private Team[] team;
  
  private int round = 0;
  
  private BigMessage big_message;
  
  private boolean current_team; // false: team1, true: team2
  
  private FollowerObject marker;
  
  public StateGame(ComponentContainer components, GameStateArgs args) {
    super(components, args);
  }

  public void init(GameStateArgs args) {
    loadWords();
    available_words = new LinkedList<String>();
    
    //wait_list = ((Args) args).getPlayers(); FIXME
    components.get(Screen.class).showFPS(true);
    if (args != null) wait_list = ((Args) args).getPlayers(); else
    wait_list = new LinkedList<PlayerSync>();
    sync_list = new LinkedList<Player>();
    players = new LinkedList<Player>();
    
    bg = new Sprite(components.get(Screen.class), "img/stateGame/bg.png");
    
    down_devices = new LinkedList<KeyboardDevice>();
    
    loadTeams();
    
    current_team = false;
    
    loadMarker();
    
    initSTART_ROUND();
  }
  
  private void loadWords() {
    // loading words
    try {
      List<String> tmplist = Files.readAllLines(Paths.get("words.txt"), StandardCharsets.UTF_8);
      
      // counting not empty strings
      int total = 0;
      Iterator<String> it = tmplist.iterator();
      while (it.hasNext()) {
        String tmpstr = it.next();
        if (tmpstr.length() > 0)
          ++total;
      }
      
      // abort execution if no words found
      if (total == 0)
        throw new Error("Nenhuma palavra encontrada no arquivo de palavras!");
      
      all_words = new String[total];
      
      // getting strings into the array
      int i = 0;
      it = tmplist.iterator();
      while (it.hasNext()) {
        String tmpstr = it.next();
        if (tmpstr.length() > 0)
          all_words[i++] = tmpstr.replaceAll("[^A-Z]", "");
      }
    }
    catch (IOException e) {
      throw new Error("Arquivo de palavras \"words.txt\" não encontrado!");
    }
  }
  
  public void loadTeams() {
    team = new Team[2];
    team[0] = new Team(components);
    team[0].setPos(new Vector3(700, 80, 0));
    team[1] = new Team(components);
    team[1].setPos(new Vector3(1044, 80, 0));
  }
  
  public void loadMarker() {
    marker = new FollowerObject(components.get(DeltaTime.class));
    marker.setPos(new Vector3(616, 74, 0));
    marker.setTargetPos(new Vector3(616, 74, 0));
    marker.setCenter(true);
    marker.setSprite(new Animation(
      components.get(Screen.class),
      "img/stateGame/marker.png",
      components.get(DeltaTime.class),
      12, 1, 15, 12
    ));
  }
  
  public void close() {
    
  }

  public void input() {
    
  }

  public void update() throws ChangeState {
    // update wait list
    for (PlayerSync p : wait_list)
      p.update();

    // update players
    for (Player p : players)
      p.update();
    
    marker.update();
    
    switch (state) {
      case START_ROUND: updateSTART_ROUND(); break;
      case SYNC:        updateSYNC();        break;
      case SHOW_PLAYER: updateSHOW_PLAYER(); break;
      case WAIT_MOVE:   updateWAIT_MOVE();   break;
      case SHOW_MOVE:   updateSHOW_MOVE();   break;
      case ROUND_OVER:  updateROUND_OVER();  break;
      
      default:
        throw new Error("Invalid state " + state + " in StateGame update");
    }
  }

  private void updateSTART_ROUND() throws ChangeState {
    if (big_message != null) {
      big_message.update();
      if (big_message.getScale() == 0.0f)
        big_message = null;
    }
    else
      initSYNC();
  }

  private void updateSYNC() {
    if (sync_list.size() == 0) {
      initSHOW_PLAYER();
      return;
    }
    
    int total = 0;
    for (Player p : sync_list) {
      p.update();
      if (p.getSpeed().length() < 0.5f)
        total++;
    }
    if (total == sync_list.size())
      initSHOW_PLAYER();
  }

  private void updateSHOW_PLAYER() {
    if (big_message != null) {
      big_message.update();
      if (big_message.getScale() == 0.0f)
        big_message = null;
    }
    else
      initWAIT_MOVE();
  }

  private void updateWAIT_MOVE() {
    
  }

  private void updateSHOW_MOVE() {
    
  }

  private void updateROUND_OVER() {
    
  }
  
  public void render() {
    bg.render();
    
    // render wait list
    int i = 0;
    for (PlayerSync p : wait_list)
      p.render(18, (i++)*25 + 170, 20);

    // render players
    for (Player p : players)
      p.render();
    
    marker.render();

    team[0].render();
    team[1].render();
    
    switch (state) {
      case START_ROUND: renderSTART_ROUND(); break;
      case SYNC:        renderSYNC();       break;
      case SHOW_PLAYER: renderSHOW_PLAYER(); break;
      case WAIT_MOVE:   renderWAIT_MOVE();   break;
      case SHOW_MOVE:   renderSHOW_MOVE();   break;
      case ROUND_OVER:  renderROUND_OVER();  break;
      
      default:
        throw new Error("Invalid state " + state + " in StateGame render");
    }
  }

  private void renderSTART_ROUND() {
    if (big_message != null)
      big_message.render();
  }

  private void renderSYNC() {
    for (Player p : sync_list)
      p.render();
  }

  private void renderSHOW_PLAYER() {
    if (big_message != null)
      big_message.render();
  }

  private void renderWAIT_MOVE() {
    
  }

  private void renderSHOW_MOVE() {
    
  }

  private void renderROUND_OVER() {
    
  }

  protected void handleNewKeyboardDevice(KeyboardDevice keyboard_device) {
    wait_list.add(new PlayerSync(components, keyboard_device));
    components.get(KeyboardManager.class).sendRequest(keyboard_device);
  }

  protected void handleKeyboardDeviceDown(KeyboardDevice keyboard_device) {
    for (Iterator<PlayerSync> it = wait_list.iterator(); it.hasNext();) {
      if (it.next().getKeyboardDevice() == keyboard_device) {
        it.remove();
        return;
      }
    }
    
    down_devices.add(keyboard_device);//FIXME
  }
  
  private void initSTART_ROUND() {
    if (available_words.size() == 0) {
      for (String word : all_words)
        available_words.add(word);
    }
    
    team[0].setWord(available_words.remove(new Random().nextInt(available_words.size())));
    
    if (available_words.size() == 0) {
      for (String word : all_words) {
        if (!team[0].getWord().equals(word))
          available_words.add(word);
      }
    }
    
    team[1].setWord(available_words.remove(new Random().nextInt(available_words.size())));
    
    round++;
    big_message = new BigMessage(components.get(DeltaTime.class), components.get(Screen.class), String.format("Round %d", round));
    
    state = START_ROUND;
  }
  
  private void initSYNC() throws CommonChange {
    int i = 0, id = 0;
    for (Iterator<PlayerSync> it = wait_list.iterator(); it.hasNext();) {
      PlayerSync p = it.next();
      if (p.isReady()) {
        it.remove();
        p.disconnect();
        Player player = new Player(
          components,
          p.getKeyboardDevice(),
          p.getNick()
        );
        player.setPos(new Vector3(18.0f, i*25.0f + 170.0f, 0.0f));
        player.setTargetPos(new Vector3(1075.0f, (players.size() + id)*30.0f + 240.0f, 0.0f));
        sync_list.add(player);
        id++;
      }
      i++;
    }
    
    // checking minimum amount of players
    if (sync_list.size() + players.size() < 2)//FIXME
      throw new CommonChange(null, StateWaitingDevices.class);
    
    state = SYNC;
  }
  
  private void initSHOW_PLAYER() {
    while (!sync_list.isEmpty())
      players.add(sync_list.remove(0));
    
    big_message = new BigMessage(components.get(DeltaTime.class), components.get(Screen.class), players.get(0).getNick() + "'s turn");
    
    state = SHOW_PLAYER;
  }
  
  private void initWAIT_MOVE() {
    
  }
  
  private void changeCurrentTeam() {
    current_team = !current_team;
    
    if (!current_team)
      marker.setTargetPos(new Vector3(616, 74, 0));
    else
      marker.setTargetPos(new Vector3(960, 74, 0));
  }
}
