package org.unbiquitous.examples.umolehunt;

import org.unbiquitous.examples.umolehunt.state.StateWaitingDevices;
import org.unbiquitous.ubiengine.engine.UosGame;

public final class UMoleHunt extends UosGame {
  @SuppressWarnings("serial")
  protected Settings getSettings() {
    return new Settings() {{
      put("root_path", ".");
      put("window_title", "uMoleHunt");
      put("window_width", 1280);
      put("window_height", 720);
      put("first_state", StateWaitingDevices.class.getName());
    }};
  }
  
  public static void main(String[] args) {
    UosGame.run(UMoleHunt.class);
  }
}
