package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;

import com.orangomango.food.ui.GameScreen;

public class CheckPoint extends GameObject{
	private boolean activated;
	private Image onImage, offImage;
	
	public CheckPoint(GraphicsContext gc, double x, double y){
		super(gc, x, y, 25, 50);
		this.onImage = new Image(getClass().getClassLoader().getResourceAsStream("checkpoint_on.png"));
		this.offImage = new Image(getClass().getClassLoader().getResourceAsStream("checkpoint_off.png"));
	}
	
	@Override
	public void render(){
		gc.setGlobalAlpha(0.7);
		gc.drawImage(this.activated ? this.onImage : this.offImage, this.x, this.y, this.w, this.h);
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
