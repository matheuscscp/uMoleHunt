package org.unbiquitous.examples.umolehunt.game;

import java.awt.Color;
import java.awt.Font;

import org.unbiquitous.ubiengine.game.GameObject;
import org.unbiquitous.ubiengine.resources.time.DeltaTime;
import org.unbiquitous.ubiengine.resources.video.Screen;
import org.unbiquitous.ubiengine.resources.video.texture.Text;
import org.unbiquitous.ubiengine.util.ComponentContainer;

public class Team extends GameObject {
  
  private int points = 0;
  private String word = "";
  private Text text_points;
  
  public Team(ComponentContainer components) {
    super(components.get(DeltaTime.class));
    text_points = new Text(
      components.get(Screen.class),
      String.format("%d", points),
      new Font(Font.SANS_SERIF, Font.BOLD, 45),
      Color.BLACK
    );
  }
  
  public void input() {
    
  }
  
  public void update() {
    
  }

  public void render() {
    text_points.render((int) pos.x(), (int) pos.y(), true);
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
    text_points.setText(String.format("%d", points));
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }
}
