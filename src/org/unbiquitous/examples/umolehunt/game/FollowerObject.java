package org.unbiquitous.examples.umolehunt.game;

import org.unbiquitous.ubiengine.game.GameObject;
import org.unbiquitous.ubiengine.resources.time.DeltaTime;
import org.unbiquitous.ubiengine.resources.video.texture.Sprite;
import org.unbiquitous.ubiengine.util.mathematics.linearalgebra.Vector3;

public class FollowerObject extends GameObject {

  protected Sprite sprite;
  protected Vector3 target_pos;
  protected boolean center;
  
  public FollowerObject(DeltaTime deltatime) {
    super(deltatime);
    sprite = null;
    target_pos = new Vector3();
    center = false;
  }
  
  public void input() {
    
  }
  
  public void update() {
    if (sprite != null)
      sprite.update();
    pos.add(target_pos.subtract(pos).mult(10.0f*deltatime.getRealDT()));
  }
  
  public void render() {
    if (sprite != null)
      sprite.render((int) pos.x(), (int) pos.y(), center);
  }

  public Sprite getSprite() {
    return sprite;
  }

  public void setSprite(Sprite sprite) {
    this.sprite = sprite;
  }

  public Vector3 getTargetPos() {
    return target_pos;
  }

  public void setTargetPos(Vector3 target_pos) {
    this.target_pos = target_pos;
  }
  
  public boolean getCenter() {
    return center;
  }
  
  public void setCenter(boolean center) {
    this.center = center;
  }
  
  public Vector3 getSpeed() {
    return target_pos.subtract(pos).mult(10.0f);
  }
}
