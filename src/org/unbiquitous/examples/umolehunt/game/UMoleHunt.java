package org.unbiquitous.examples.umolehunt.game;

import java.util.HashMap;

import org.unbiquitous.examples.umolehunt.game.state.StateGame;
import org.unbiquitous.ubiengine.game.UosGame;

public final class UMoleHunt extends UosGame {
  public HashMap<String, Object> getSettings() {
    HashMap<String, Object> prop = new HashMap<String, Object>();
    
    prop.put("window_title", "uMoleHunt");
    prop.put("window_width", 1280);
    prop.put("window_height", 720);
    
    prop.put("first_state", StateGame.class.getName());
    
    return prop;
  }
}
