package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;

import com.orangomango.food.ui.GameScreen;

public class Portal extends GameObject{
	private static Image IMAGE = MainApplication.loadImage("portal.png");
	private double tpX, tpY;

	public Portal(GraphicsContext gc, double x, double y){
		super(gc, x, y, IMAGE.getWidth(), IMAGE.getHeight());
		this.solid = true;
	}
	
	public void setTeleport(double x, double y){
		this.tpX = x;
		this.tpY = y;
	}
	
	@Override
	public void render(){
		gc.drawImage(IMAGE, this.x, this.y, this.w, this.h);
		if (GameScreen.getInstance().getPlayer().collided(this.x, this.y-3, this.w, 3)){
			GameScreen.getInstance().getPlayer().setX(this.tpX);
			GameScreen.getInstance().getPlayer().setY(this.tpY);
			MainApplication.playSound(MainApplication.PORTAL_SOUND, false);
		}
	}
}
