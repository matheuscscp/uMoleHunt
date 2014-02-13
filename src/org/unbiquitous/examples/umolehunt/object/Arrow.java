package org.unbiquitous.examples.umolehunt.object;

import org.unbiquitous.ubiengine.engine.Screen;
import org.unbiquitous.ubiengine.engine.asset.video.texture.Sprite;
import org.unbiquitous.ubiengine.engine.time.DeltaTime;
import org.unbiquitous.ubiengine.util.ComponentContainer;
import org.unbiquitous.ubiengine.util.mathematics.linearalgebra.Vector3;

public class Arrow extends FollowerObject {

  public Arrow(ComponentContainer components) {
    super(components.get(DeltaTime.class));
    sprite = new Sprite(components.get(Screen.class), "img/stateGame/arrow.png");
    pos = new Vector3(-200, -200, 0);
    target_pos = new Vector3(-200, -200, 0);
    center = true;
  }
  
  public void update() {
    super.update();
    pos.add(new Vector3((float) (1.0f*Math.cos(deltatime.getBegin()/100)), 0.0f, 0.0f));
  }
  
  public void setTargetPos(Player player) {
    target_pos = player.getPos().sum(new Vector3(
      -sprite.getWidth()/2,
      player.getSprite().getHeight()/2,
      0
    ));
  }
  
  public void setTargetPosByTargetPos(Player player) {
    target_pos = player.getTargetPos().sum(new Vector3(
      -sprite.getWidth()/2,
      player.getSprite().getHeight()/2,
      0
    ));
  }
}
