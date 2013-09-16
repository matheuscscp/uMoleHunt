package org.unbiquitous.examples.umolehunt;

import org.unbiquitous.examples.umolehunt.game.UMoleHunt;
import org.unbiquitous.ubiengine.game.GameStarter;

public final class Main {
  public static void main(String[] args) {
    GameStarter.startGame(UMoleHunt.class.getName());
  }
}
