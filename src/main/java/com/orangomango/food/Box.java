package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;

public class Box extends GameObject{
	private Image image;

	public Box(GraphicsContext gc, double x, double y){
		super(gc, x, y, 25, 25);
		this.image = MainApplication.loadImage("box.png");
		this.movable = true;
		this.solid = true;
		makeGravity();
	}
	
	@Override
	public void render(){
		gc.drawImage(this.image, this.x, this.y, this.w, this.h);
	}
}
