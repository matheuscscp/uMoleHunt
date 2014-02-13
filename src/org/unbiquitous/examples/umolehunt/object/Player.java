package org.unbiquitous.examples.umolehunt.object;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Method;

import org.unbiquitous.ubiengine.engine.Screen;
import org.unbiquitous.ubiengine.engine.asset.video.texture.Sprite;
import org.unbiquitous.ubiengine.engine.asset.video.texture.Text;
import org.unbiquitous.ubiengine.engine.input.keyboard.KeyboardDevice;
import org.unbiquitous.ubiengine.engine.input.keyboard.KeyboardDevice.KeyDownEvent;
import org.unbiquitous.ubiengine.engine.time.DeltaTime;
import org.unbiquitous.ubiengine.util.ComponentContainer;
import org.unbiquitous.ubiengine.util.observer.Event;
import org.unbiquitous.ubiengine.util.observer.Subject;
import org.unbiquitous.ubiengine.util.observer.SubjectDevice;

public class Player extends FollowerObject implements Subject {
  
  public static final class PlayerTypedEvent extends Event {
    private int typed_char;
    
    public PlayerTypedEvent(int typed_c) {
      typed_char = typed_c;
    }
    
    public int getTypedChar() {
      return typed_char;
    }
  }
  
  private KeyboardDevice keyboard_device;
  private String nick;
  private boolean typed, broadcasted;
  private int typed_char;
  
  public Player(ComponentContainer components, KeyboardDevice kdev, String nick_) {
    super(components.get(DeltaTime.class));
    
    subject = new SubjectDevice(TYPED);
    
    keyboard_device = kdev;
    nick = nick_;
    sprite = new Sprite(components.get(Screen.class), new Text(
      components.get(Screen.class),
      nick,
      new Font(Font.SANS_SERIF,Font.BOLD, 25),
      Color.BLACK
    ));
    
    try {
      keyboard_device.connect(
        KeyboardDevice.KEYDOWN,
        this,
        Player.class.getDeclaredMethod("handleKeyDown", Event.class)
      );
    } catch (NoSuchMethodException e) {
    } catch (SecurityException e) {
    }
    
    broadcasted = true;
  }

  public String getNick() {
    return nick;
  }
  
  public void resetChar() {
    typed = false;
    broadcasted = false;
  }
  
  protected void handleKeyDown(Event event) throws Exception {
    if (broadcasted)
      return;
    
    int uchar = ((KeyDownEvent) event).getUnicodeChar();
    
    if (uchar == 10 && typed) {
      subject.broadcast(TYPED, new PlayerTypedEvent(typed_char));
      broadcasted = true;
      return;
    }
    
    // adjusting char
    if (uchar >= 97 && uchar <= 122)
      uchar -= 32;
    
    // invalid char
    if (uchar < 65 || uchar > 90)
      return;
    
    typed = true;
    typed_char = uchar;
  }

  public static final String TYPED = "TYPED";
  
  protected SubjectDevice subject;
  
  public void connect(String event_type, Method handler) {
    subject.connect(event_type, handler);
  }

  public void connect(String event_type, Object observer, Method handler) {
    subject.connect(event_type, observer, handler);
  }

  public void disconnect(Method handler) {
    subject.disconnect(handler);
  }

  public void disconnect(String event_type, Method handler) {
    subject.disconnect(event_type, handler);
  }

  public void disconnect(Object observer) {
    subject.disconnect(observer);
  }

  public void disconnect(String event_type, Object observer) {
    subject.disconnect(event_type, observer);
  }
}
