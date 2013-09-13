package org.unbiquitous.examples.umolehunt.stateGame;

import org.unbiquitous.ubiengine.game.state.GameState;
import org.unbiquitous.ubiengine.game.state.GameStateArgs;
import org.unbiquitous.ubiengine.resources.video.Screen;
import org.unbiquitous.ubiengine.resources.video.texture.Sprite;
import org.unbiquitous.ubiengine.util.ComponentContainer;

public class StateGame extends GameState {

  public static final class Args extends GameStateArgs {
    
  }
  
  private Sprite bg;
  
  public StateGame(ComponentContainer components, GameStateArgs args) {
    super(components, args);
  }

  public void init(GameStateArgs args) {
    bg = new Sprite(components.get(Screen.class), "img/stateGame/bg.png");
  }

  public void close() {
    
  }

  public void input() {
    
  }

  public void update() {
    
  }

  public void render() {
    bg.render();
  }
  
}
