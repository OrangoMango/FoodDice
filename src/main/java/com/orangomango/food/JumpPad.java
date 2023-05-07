package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;

import com.orangomango.food.ui.GameScreen;

public class JumpPad extends GameObject{
	private static final double SIZE = 18;
	private Image image = MainApplication.loadImage("jumppad.png");
	
	public JumpPad(GraphicsContext gc, double x, double y){
		super(gc, x, y, SIZE, SIZE);
		this.solid = true;
		this.movable = true;
		makeGravity();
	}
	
	@Override
	public void render(){
		if (GameScreen.getInstance().getPlayer().collided(this.x, this.y-3, this.w, 3)){
			GameScreen.getInstance().getPlayer().moveUp(150);
			gc.drawImage(this.image, this.x, this.y+4, SIZE, SIZE-4);
		} else {
			gc.drawImage(this.image, this.x, this.y, SIZE, SIZE);
		}
	}
}
