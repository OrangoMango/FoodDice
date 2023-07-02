package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Box extends GameObject{
	private static Image IMAGE = MainApplication.loadImage("box.png");

	public Box(GraphicsContext gc, double x, double y){
		super(gc, x, y, IMAGE.getWidth(), IMAGE.getHeight());
		this.movable = true;
		this.solid = true;
		makeGravity();
	}
	
	@Override
	public void render(){
		gc.drawImage(IMAGE, this.x, this.y, this.w, this.h);
	}
}
