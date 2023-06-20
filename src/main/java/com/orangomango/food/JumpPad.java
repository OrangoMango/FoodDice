package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;

import com.orangomango.food.ui.GameScreen;

public class JumpPad extends GameObject{
	private static Image IMAGE = MainApplication.loadImage("jumppad.png");
	
	public JumpPad(GraphicsContext gc, double x, double y){
		super(gc, x, y, IMAGE.getWidth(), IMAGE.getHeight());
		this.solid = true;
		this.movable = true;
		makeGravity();
	}
	
	@Override
	public void render(){
		if (GameScreen.getInstance().getPlayer().collided(this.x, this.y-3, this.w, 3)){
			GameScreen.getInstance().getPlayer().moveUp(150);
			gc.drawImage(IMAGE, this.x, this.y+4, this.w, this.h-4);
		} else {
			gc.drawImage(IMAGE, this.x, this.y, this.w, this.h);
		}
	}
}
