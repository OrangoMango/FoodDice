package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Box extends GameObject{
	private static Image IMAGE = MainApplication.loadImage("box.png");

	public Box(double x, double y){
		super(x, y, IMAGE.getWidth(), IMAGE.getHeight());
		this.movable = true;
		this.solid = true;
		makeGravity();
	}
	
	@Override
	public void render(GraphicsContext gc){
		gc.drawImage(IMAGE, this.x, this.y, this.w, this.h);
	}
}
