package org.unbiquitous.examples.umolehunt.stateWaitingDevices;

import java.awt.Color;
import java.awt.Font;

import org.unbiquitous.ubiengine.resources.input.keyboard.KeyboardDevice;
import org.unbiquitous.ubiengine.resources.input.keyboard.KeyboardDevice.KeyEvent;
import org.unbiquitous.ubiengine.resources.time.DeltaTime;
import org.unbiquitous.ubiengine.resources.video.Screen;
import org.unbiquitous.ubiengine.util.ComponentContainer;
import org.unbiquitous.ubiengine.util.observer.Event;

public class Player {
  private Screen screen;
  private KeyboardDevice keyboard_device;
  private boolean listening;
  private boolean ready;
  private String nick;
  private DeltaTime deltatime;
  
  public Player(ComponentContainer components, KeyboardDevice kdev) {
    screen = components.get(Screen.class);
    keyboard_device = kdev;
    listening = false;
    ready = false;
    nick = "<nickname>";
    deltatime = components.get(DeltaTime.class);
  }

  public void input() {
    
  }

  public void update() {
    if (keyboard_device.isPlugged()) {
      if (!listening) {
        listening = true;
        try {
          keyboard_device.connect(KeyboardDevice.KEYDOWN,  this, Player.class.getDeclaredMethod("handleKeyDown", Event.class));
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        }
      }
    }
    else {
      if (listening) {
        ready = false;
        listening = false;
        keyboard_device.disconnect(this);
      }
    }
  }

  public void render(int x, int y) {
    Color color;
    
    if (ready) {
      color = new Color(0.0f, 0.5f, 0.0f);
      screen.renderText(nick, new Font(Font.MONOSPACED, Font.BOLD, 25), color, x, y, false);
      return;
    }
    
    if (nick.indexOf('>') != -1 || nick.indexOf('<') != -1 || nick.length() < 6)
      color = Color.RED;
    else
      color = Color.BLUE;
    screen.renderText(nick + ((deltatime.getBegin()/1000)%2 != 0 ? " " : "|"), new Font(Font.MONOSPACED, Font.BOLD, 25), color, x, y, false);
  }

  public KeyboardDevice getKeyboardDevice() {
    return keyboard_device;
  }
  
  public String getNick() {
    return nick;
  }
  
  public boolean isReady() {
    return ready;
  }
  
  public void disconnect() {
    keyboard_device.disconnect(this);
  }
  
  protected void handleKeyDown(Event event) {
    int key = ((KeyEvent) event).getUnicodeChar();
    
    // if ready and (enter or backspace), not ready
    if (ready) {
      if (key == 10)
        ready = false;
      else if (key == 0) {
        ready = false;
        nick = nick.substring(0, nick.length() - 1);
      }
      return;
    }
    
    // if not ready and enter, ready if nick.length() >= 6 and not equals "<nickname>"
    if (key == 10) {
      if (nick.length() >= 6 && nick.indexOf('>') == -1 && nick.indexOf('<') == -1)
        ready = true;
      return;
    }
    
    // if backspace, drop char
    if (key == 0) {
      if (nick.indexOf('>') != -1 || nick.indexOf('<') != -1)
        nick = "";
      else if (nick.length() > 0)
        nick = nick.substring(0, nick.length() - 1);
      return;
    }
    
    // adjusting char
    if (key >= 97 && key <= 122)
      key -= 32;
    
    // append if valid char
    if ((key >= 65 && key <= 90) || (key >= 48 && key <= 57) || key == 95) {
      if (nick.indexOf('>') != -1 || nick.indexOf('<') != -1)
        nick = "";
      nick += (char) key;
      
      // new length == 10, ready
      if (nick.length() == 10 && nick.indexOf('>') == -1 && nick.indexOf('<') == -1)
        ready = true;
    }
  }
}
