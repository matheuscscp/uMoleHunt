package org.unbiquitous.examples.umolehunt.object;

import java.awt.Color;
import java.awt.Font;

import org.unbiquitous.ubiengine.engine.GameObject;
import org.unbiquitous.ubiengine.engine.Screen;
import org.unbiquitous.ubiengine.engine.asset.video.texture.Text;
import org.unbiquitous.ubiengine.engine.time.DeltaTime;
import org.unbiquitous.ubiengine.util.ComponentContainer;
import org.unbiquitous.ubiengine.util.mathematics.linearalgebra.Vector3;

public class Team extends GameObject {
  
  private int points = 0;
  private String word = "";
  private Text text_points;
  private boolean[] available_chars = new boolean[26];
  private boolean won = false;
  private FollowerObject word_object, alphabet_object;
  
  public Team(ComponentContainer components) {
    super(components.get(DeltaTime.class));
    text_points = new Text(
      components.get(Screen.class),
      String.format("%d", points),
      new Font(Font.SANS_SERIF, Font.BOLD, 45),
      Color.BLACK
    );
    
    word_object = new FollowerObject(deltatime);
    word_object.setText(new Text(
      components.get(Screen.class),
      "",
      new Font(Font.MONOSPACED, Font.BOLD, 30),
      Color.BLACK
    ));
    word_object.setCenter(true);
    word_object.setPos(new Vector3(640, 900, 0));
    word_object.setTargetPos(new Vector3(640, 900, 0));
    
    alphabet_object = new FollowerObject(deltatime);
    alphabet_object.setText(new Text(
      components.get(Screen.class),
      "",
      new Font(Font.SANS_SERIF, Font.BOLD, 22),
      Color.BLACK
    ));
    alphabet_object.setCenter(true);
    alphabet_object.setPos(new Vector3(640, 900, 0));
    alphabet_object.setTargetPos(new Vector3(640, 900, 0));
  }
  
  public void input() {
    
  }
  
  public void update() {
    word_object.update();
    alphabet_object.update();
  }

  public void render() {
    text_points.render((int) pos.x(), (int) pos.y(), true);
    word_object.render();
    alphabet_object.render();
  }

  public boolean hasWon() {
    return won;
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
    for (int i = 0; i < 26; ++i)
      available_chars[i] = true;
    won = false;

    String word_object_text = "_";
    for (int i = 1; i < word.length(); ++i)
      word_object_text += " _";
    
    String alphabet_object_text = "A";
    for (char c = 'B'; c <= 'Z'; c++)
      alphabet_object_text += " " + c;

    word_object.getText().setText(word_object_text);
    alphabet_object.getText().setText(alphabet_object_text);
  }
  
  public boolean setChar(int uchar) {
    if (!available_chars[uchar - 65])
      return false;
    
    available_chars[uchar - 65] = false;
    
    String word_object_text;
    boolean underline = false;
    if (available_chars[word.codePointAt(0) - 65])
      word_object_text = "_";
    else
      word_object_text = "" + (char) word.codePointAt(0);
    for (int i = 1; i < word.length(); ++i) {
      if (available_chars[word.codePointAt(i) - 65]) {
        word_object_text += " _";
        underline = true;
      }
      else
        word_object_text += " " + (char) word.codePointAt(i);
    }
    won = !underline;

    String alphabet_object_text = "";
    boolean has_char = false;
    for (char c = 'A'; c <= 'Z'; c++) {
      if (available_chars[c - 65]) {
        if (has_char)
          alphabet_object_text += " ";
        alphabet_object_text += c;
        has_char = true;
      }
    }

    word_object.getText().setText(word_object_text);
    alphabet_object.getText().setText(alphabet_object_text);
    
    return true;
  }
  
  public void show() {
    word_object.setTargetPos(new Vector3(640, 430, 0));
    alphabet_object.setTargetPos(new Vector3(640, 580, 0));
  }
  
  public void hide() {
    word_object.setTargetPos(new Vector3(640, 900, 0));
    alphabet_object.setTargetPos(new Vector3(640, 900, 0));
  }
  
  public boolean isSpeedNull() {
    return (
      word_object.getSpeed().length() < 0.5f &&
      alphabet_object.getSpeed().length() < 0.5f
    );
  }
}
