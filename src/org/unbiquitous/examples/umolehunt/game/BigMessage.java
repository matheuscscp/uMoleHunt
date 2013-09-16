package org.unbiquitous.examples.umolehunt.game;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import org.unbiquitous.ubiengine.game.GameObject;
import org.unbiquitous.ubiengine.resources.time.DeltaTime;
import org.unbiquitous.ubiengine.resources.time.Stopwatch;
import org.unbiquitous.ubiengine.resources.video.Screen;
import org.unbiquitous.ubiengine.resources.video.texture.Sprite;
import org.unbiquitous.ubiengine.resources.video.texture.Text;
import org.unbiquitous.ubiengine.util.mathematics.linearalgebra.Vector3;

public class BigMessage extends GameObject {
  
  private Sprite message;
  private float scale;
  private float scale_speed, scale_accel;
  private boolean waiting;
  private float speed, accel;
  private Stopwatch stopwatch;
  private float sign;
  
  public BigMessage(DeltaTime deltatime, Screen screen, String message) {
    super(deltatime);
    int fsize = 10;
    Text tmptext = new Text(
      screen, message, new Font(Font.SANS_SERIF, Font.BOLD, fsize), Color.BLACK
    );
    while (tmptext.getWidth() < 640) {
      fsize++;
      tmptext.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fsize));
    }
    this.message = new Sprite(screen, tmptext);
    pos = new Vector3(640, 200, 0);
    scale = 0.01f;
    scale_speed = 4.0f;
    scale_accel = 1.0f;
    waiting = false;
    speed = -500.0f;
    accel = 1500.0f;
    stopwatch = new Stopwatch();
    sign = (float) (new Random().nextInt(3) - 1);
  }
  
  public void input() {
    
  }
  
  public void update() {
    if (!waiting)
      scale += scale_speed*deltatime.getRealDT();
    
    if (scale > 1.0f) {
      scale = 1.0f;
      waiting = true;
      stopwatch.start();
    }
    else if (scale < 0.0f)
      scale = 0.0f;
    
    if (stopwatch.time() > 1000) {
      if (waiting)
        waiting = false;
      else {
        speed += accel*deltatime.getRealDT();
        scale_speed -= scale_accel*deltatime.getRealDT();
        pos.add(new Vector3(sign*500.0f, speed, 0.0f).mult(deltatime.getRealDT()));
      }
    }
    
    message.setAlpha(scale);
    message.rotozoom(0, scale, scale, false);
    
    if (scale == 1.0f && scale_speed > 0.0f)
      scale_speed = -0.5f;
  }
  
  public void render() {
    message.render((int) pos.x(), (int) pos.y(), true);
  }
  
  public float getScale() {
    return scale;
  }
}
