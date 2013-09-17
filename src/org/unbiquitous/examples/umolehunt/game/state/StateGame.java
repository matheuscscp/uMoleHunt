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

import org.unbiquitous.examples.umolehunt.game.PlayerSync;
import org.unbiquitous.examples.umolehunt.game.object.Arrow;
import org.unbiquitous.examples.umolehunt.game.object.BigMessage;
import org.unbiquitous.examples.umolehunt.game.object.FollowerObject;
import org.unbiquitous.examples.umolehunt.game.object.Player;
import org.unbiquitous.examples.umolehunt.game.object.Player.PlayerTypedEvent;
import org.unbiquitous.examples.umolehunt.game.object.Team;
import org.unbiquitous.examples.umolehunt.game.object.TimeRemaining;
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
import org.unbiquitous.ubiengine.util.observer.Event;

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
  private static final int CHANGE_TEAM = 5;
  private static final int ROUND_OVER  = 6;
  
  private int state;
  
  private String[] all_words;
  private List<String> available_words;

  private List<PlayerSync> wait_list;
  private List<Player> sync_list;
  private List<Player> players;
  
  private Sprite bg;
  
  private Queue<KeyboardDevice> down_devices;

  private Team[] team;
  
  private int round;
  
  private BigMessage big_message;
  
  private int current_team; // 0: team1, 1: team2
  
  private FollowerObject marker;
  
  private TimeRemaining time_remaining;
  
  private boolean player_typed;
  private int player_typed_char;
  
  private Arrow arrow;
  
  public StateGame(ComponentContainer components, GameStateArgs args) {
    super(components, args);
  }

  public void init(GameStateArgs args) {
    loadWords();
    available_words = new LinkedList<String>();
    
    wait_list = ((Args) args).getPlayers();
    sync_list = new LinkedList<Player>();
    players = new LinkedList<Player>();
    
    bg = new Sprite(components.get(Screen.class), "img/stateGame/bg.png");
    
    down_devices = new LinkedList<KeyboardDevice>();
    
    loadTeams();
    
    round = 0;
    
    current_team = 0;
    
    loadMarker();
    
    time_remaining = new TimeRemaining(components);
    
    arrow = new Arrow(components);
    
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
    team[0].show();
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
    checkMinimumAmount();
    
    // update wait list
    for (PlayerSync p : wait_list)
      p.update();

    // update players
    for (Player p : players)
      p.update();
    
    marker.update();

    team[0].update();
    team[1].update();
    
    arrow.update();
    
    switch (state) {
      case START_ROUND: updateSTART_ROUND(); break;
      case SYNC:        updateSYNC();        break;
      case SHOW_PLAYER: updateSHOW_PLAYER(); break;
      case WAIT_MOVE:   updateWAIT_MOVE();   break;
      case SHOW_MOVE:   updateSHOW_MOVE();   break;
      case CHANGE_TEAM: updateCHANGE_TEAM(); break;
      case ROUND_OVER:  updateROUND_OVER();  break;
      
      default:
        throw new Error("Invalid state " + state + " in StateGame update");
    }
  }

  private void checkMinimumAmount() throws CommonChange {
    // FIXME
    //if (sync_list.size() + players.size() < 2)
      //throw new CommonChange(new StateWaitingDevices.Args(wait_list), StateWaitingDevices.class);
  }
  
  private void updateSTART_ROUND() {
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
    if (player_typed)
      time_remaining.stop();
    
    time_remaining.update();
    
    if (time_remaining.isTimeUp())
      initSHOW_MOVE();
  }

  private void updateSHOW_MOVE() {
    if (big_message != null) {
      big_message.update();
      if (big_message.getScale() == 0.0f)
        big_message = null;
    }
    else if (!team[current_team].hasWon())
      initCHANGE_TEAM();
    else
      initROUND_OVER();
  }

  private void updateCHANGE_TEAM() {
    if (marker.getSpeed().length() >= 0.5f)
      return;
    
    for (Player p : players) {
      if (p.getSpeed().length() >= 0.5f)
        return;
    }
    
    if (!team[0].isSpeedNull() || !team[1].isSpeedNull())
      return;
    
    initSYNC();
  }
  
  private void updateROUND_OVER() {
    if (big_message != null) {
      big_message.update();
      if (big_message.getScale() == 0.0f)
        big_message = null;
    }
    else
      initSTART_ROUND();
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
    
    arrow.render();
    
    switch (state) {
      case START_ROUND: renderSTART_ROUND(); break;
      case SYNC:        renderSYNC();        break;
      case SHOW_PLAYER: renderSHOW_PLAYER(); break;
      case WAIT_MOVE:   renderWAIT_MOVE();   break;
      case SHOW_MOVE:   renderSHOW_MOVE();   break;
      case CHANGE_TEAM: renderCHANGE_TEAM(); break;
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
    time_remaining.render();
  }

  private void renderSHOW_MOVE() {
    if (big_message != null)
      big_message.render();
  }
  
  private void renderCHANGE_TEAM() {
    // nothing to be done. just wait objects reach theirs targets
  }

  private void renderROUND_OVER() {
    if (big_message != null)
      big_message.render();
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
    
    down_devices.add(keyboard_device);//FIXME handle down devices rightly
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
  
  private void initSYNC() {
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
        
        try {
          player.connect(
            Player.TYPED,
            this,
            StateGame.class.getDeclaredMethod("handlePlayerTyped", Event.class)
          );
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        }
        
        sync_list.add(player);
        id++;
      }
      i++;
    }
    
    state = SYNC;
  }
  
  private void initSHOW_PLAYER() {
    while (!sync_list.isEmpty())
      players.add(sync_list.remove(0));

    players.get(0).resetChar();
    player_typed = false;
    
    arrow.setTargetPos(players.get(0));
    
    big_message = new BigMessage(components.get(DeltaTime.class), components.get(Screen.class), players.get(0).getNick() + "'s turn");
    
    state = SHOW_PLAYER;
  }

  private void initWAIT_MOVE() {
    time_remaining.start();
    
    state = WAIT_MOVE;
  }

  private void initSHOW_MOVE() {
    if (player_typed)
      big_message = new BigMessage(components.get(DeltaTime.class), components.get(Screen.class), players.get(0).getNick() + " typed '" + (char) player_typed_char + "'");
    else
      big_message = new BigMessage(components.get(DeltaTime.class), components.get(Screen.class), players.get(0).getNick() + " lost his turn");
    
    state = SHOW_MOVE;
  }

  private void initCHANGE_TEAM() {
    if (current_team == 0)
      current_team = 1;
    else
      current_team = 0;
    
    if (current_team == 0) {
      marker.setTargetPos(new Vector3(616, 74, 0));
      team[0].show();
      team[1].hide();
    }
    else {
      marker.setTargetPos(new Vector3(960, 74, 0));
      team[0].hide();
      team[1].show();
    }
    
    players.add(players.remove(0));
    int i = 0;
    for (Iterator<Player> it = players.iterator(); it.hasNext(); ++i)
      it.next().setTargetPos(new Vector3(1075.0f, i*30.0f + 240.0f, 0.0f));
    arrow.setTargetPosByTargetPos(players.get(players.size() - 1));
    
    state = CHANGE_TEAM;
  }

  private void initROUND_OVER() {
    team[current_team].setPoints(team[current_team].getPoints() + 1);
    
    big_message = new BigMessage(components.get(DeltaTime.class), components.get(Screen.class), String.format("Round over. Team %d wins!", current_team + 1));
    
    state = ROUND_OVER;
  }
  
  protected void handlePlayerTyped(Event event) {
    int uchar = ((PlayerTypedEvent) event).getTypedChar();
    
    if (!team[current_team].setChar(uchar)) {
      players.get(0).resetChar();
      return;
    }
    
    player_typed = true;
    player_typed_char = uchar;
  }
}
