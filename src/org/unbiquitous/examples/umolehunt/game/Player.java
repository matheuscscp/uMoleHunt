package org.unbiquitous.examples.umolehunt.game;

import java.awt.Color;
import java.awt.Font;

import org.unbiquitous.ubiengine.resources.input.keyboard.KeyboardDevice;
import org.unbiquitous.ubiengine.resources.time.DeltaTime;
import org.unbiquitous.ubiengine.resources.video.Screen;
import org.unbiquitous.ubiengine.resources.video.texture.Sprite;
import org.unbiquitous.ubiengine.resources.video.texture.Text;
import org.unbiquitous.ubiengine.util.ComponentContainer;

public class Player extends FollowerObject {
  private KeyboardDevice keyboard_device;
  private String nick;
  
  public Player(ComponentContainer components, KeyboardDevice kdev, String nick_) {
    super(components.get(DeltaTime.class));
    keyboard_device = kdev;
    nick = nick_;
    sprite = new Sprite(components.get(Screen.class), new Text(
      components.get(Screen.class),
      nick,
      new Font(Font.SANS_SERIF,Font.BOLD, 25),
      Color.BLACK
    ));
  }

  public void update() {
    super.update();
  }

  public String getNick() {
    return nick;
  }
}
