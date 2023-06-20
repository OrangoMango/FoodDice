package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;

import com.orangomango.food.ui.GameScreen;

public class CheckPoint extends GameObject{
	private boolean activated;
	private static Image ON_IMAGE = MainApplication.loadImage("checkpoint_on.png");
	private static Image OFF_IMAGE = MainApplication.loadImage("checkpoint_off.png");
	
	public CheckPoint(GraphicsContext gc, double x, double y){
		super(gc, x, y, ON_IMAGE.getWidth(), ON_IMAGE.getHeight());
	}
	
	@Override
	public void render(){
		gc.setGlobalAlpha(0.7);
		gc.drawImage(this.activated ? ON_IMAGE : OFF_IMAGE, this.x, this.y, this.w, this.h);
		gc.setGlobalAlpha(1);
		if (!this.activated){
			this.activated = collided(GameScreen.getInstance().getPlayer()) && !GameScreen.getInstance().getSpecialEffect().noCheckpoints;
			if (this.activated){
				GameScreen.getInstance().getPlayer().setRespawnPoint(this.x, this.y);
				MainApplication.playSound(MainApplication.CHECKPOINT_SOUND, false);
			}
		}
	}
}
