package org.unbiquitous.examples.umolehunt.game.object;

import java.awt.Color;
import java.awt.Font;

import org.unbiquitous.ubiengine.game.GameObject;
import org.unbiquitous.ubiengine.resources.time.DeltaTime;
import org.unbiquitous.ubiengine.resources.time.Stopwatch;
import org.unbiquitous.ubiengine.resources.video.Screen;
import org.unbiquitous.ubiengine.resources.video.texture.Text;
import org.unbiquitous.ubiengine.util.ComponentContainer;
import org.unbiquitous.ubiengine.util.mathematics.linearalgebra.Vector3;

public class TimeRemaining extends GameObject {

  private Text text;
  private boolean timeup;
  private float alpha, alpha_speed;
  private Stopwatch stopwatch;
  private int time_remaining;
  private static final int TIME_REMAINING = 30;
  
  public TimeRemaining(ComponentContainer components) {
    super(components.get(DeltaTime.class));
    text = new Text(
      components.get(Screen.class),
      String.format("Time remaining: %d s", TIME_REMAINING),
      new Font(Font.SANS_SERIF, Font.BOLD, 35),
      Color.BLACK
    );
    timeup = false;
    alpha = 0.0f;
    text.setAlpha(alpha);
    alpha_speed = 2.0f;
    stopwatch = new Stopwatch();
    time_remaining = TIME_REMAINING;
    pos = new Vector3(640, 200, 0);
  }

  public void input() {
    
  }

  public void update() {
    alpha += alpha_speed*deltatime.getRealDT();
    if (alpha > 1.0f) {
      alpha = 1.0f;
      alpha_speed = 0.0f;
      stopwatch.start();
    }
    else if (alpha < 0.0f) {
      alpha = 0.0f;
      timeup = true;
    }
    text.setAlpha(alpha);
    
    if (alpha == 1.0f && stopwatch.time() > (TIME_REMAINING + 1)*1000)
      alpha_speed = -2.0f;
    
    if (alpha_speed == 0.0f) {
      time_remaining = TIME_REMAINING - (int) (stopwatch.time()/1000);
      if (time_remaining < 0)
        time_remaining = 0;
      text.setText(String.format("Time remaining: %d s", time_remaining));
    }
  }

  public void render() {
    text.render((int) pos.x(), (int) pos.y(), true);
  }
  
  public void start() {
    timeup = false;
    alpha = 0.0f;
    alpha_speed = 2.0f;
    time_remaining = TIME_REMAINING;
    text.setText(String.format("Time remaining: %d s", TIME_REMAINING));
  }
  
  public void stop() {
    alpha_speed = -2.0f;
  }
  
  public boolean isTimeUp() {
    return timeup;
  }
}
