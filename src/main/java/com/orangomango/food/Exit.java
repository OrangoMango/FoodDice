package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Exit{
	public double x, y;
	public static final double WIDTH = 35;
	public static final double HEIGHT = 40;
	private Image image = MainApplication.loadImage("exit.png");
	
	public Exit(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public void render(GraphicsContext gc){
		gc.drawImage(this.image, this.x, this.y, WIDTH, HEIGHT);
	}
}
