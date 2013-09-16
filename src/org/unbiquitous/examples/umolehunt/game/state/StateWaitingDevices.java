package org.unbiquitous.examples.umolehunt.game.state;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.unbiquitous.examples.umolehunt.game.PlayerSync;
import org.unbiquitous.ubiengine.game.state.CommonChange;
import org.unbiquitous.ubiengine.game.state.GameState;
import org.unbiquitous.ubiengine.game.state.GameStateArgs;
import org.unbiquitous.ubiengine.resources.input.keyboard.KeyboardDevice;
import org.unbiquitous.ubiengine.resources.input.keyboard.KeyboardManager;
import org.unbiquitous.ubiengine.resources.input.mouse.MouseManager;
import org.unbiquitous.ubiengine.resources.time.DeltaTime;
import org.unbiquitous.ubiengine.resources.video.Screen;
import org.unbiquitous.ubiengine.resources.video.gui.Button;
import org.unbiquitous.ubiengine.resources.video.texture.Animation;
import org.unbiquitous.ubiengine.resources.video.texture.Sprite;
import org.unbiquitous.ubiengine.util.ComponentContainer;
import org.unbiquitous.ubiengine.util.observer.Event;

public class StateWaitingDevices extends GameState {

  private Sprite bg;
  private Animation waiting;
  private Button start;
  private List<PlayerSync> players = new LinkedList<PlayerSync>();
  
  public StateWaitingDevices(ComponentContainer components, GameStateArgs args) {
    super(components, args);
  }

  public void init(GameStateArgs args) {
    components.get(Screen.class).showFPS(true);
    
    bg = new Sprite(components.get(Screen.class), "img/stateWaitingDevices/bg.png");
    
    waiting = new Animation(
      components.get(Screen.class),
      "img/stateWaitingDevices/waiting.png",
      components.get(DeltaTime.class),
      4, 1, 2, 4
    );
    
    start = new Button(
      components.get(KeyboardManager.class).getMainKeyboard(),
      components.get(MouseManager.class).getMainMouse(),
      new Sprite(components.get(Screen.class), "img/stateWaitingDevices/start.png")
    );
    start.enable(false);
    start.renderByCenter(true);
    start.setPos(640, 250);
    try {
      start.connect(Button.CLICKED, this, StateWaitingDevices.class.getDeclaredMethod("handleStart", Event.class));
    } catch (NoSuchMethodException e) {
    } catch (SecurityException e) {
    }
  }

  public void close() {
    
  }

  public void input() {
    
  }

  public void update() {
    waiting.update();
    start.update();
    
    int total = 0;
    for (Iterator<PlayerSync> it = players.iterator(); it.hasNext();) {
      PlayerSync p = it.next();
      p.update();
      if (p.isReady())
        total++;
    }
    if (!start.isEnabled() && total >= 2)
      start.enable(true);
    else if (start.isEnabled() && total < 2)
      start.enable(false);
  }

  public void render() {
    bg.render();
    
    if (!start.isEnabled())
      waiting.render(640, 250, true);
    else
      start.render();

    int i = 0;
    for (Iterator<PlayerSync> it = players.iterator(); it.hasNext(); i += 2) {
      int y = i*22 + 320;
      if (y >= components.get(Screen.class).getSize().getHeight())
        break;
      
      it.next().render(410, y, 25);
      if (it.hasNext())
        it.next().render(720, y, 25);
    }
  }

  protected void handleNewKeyboardDevice(KeyboardDevice keyboard_device) {
    players.add(new PlayerSync(components, keyboard_device));
    components.get(KeyboardManager.class).sendRequest(keyboard_device);
  }

  protected void handleKeyboardDeviceDown(KeyboardDevice keyboard_device) {
    for (Iterator<PlayerSync> it = players.iterator(); it.hasNext();) {
      if (it.next().getKeyboardDevice() == keyboard_device)
        it.remove();
    }
  }
  
  protected void handleStart(Event event) throws CommonChange {
    throw new CommonChange(new StateGame.Args(players), StateGame.class);
  }
}
