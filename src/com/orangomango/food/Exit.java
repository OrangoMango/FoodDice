package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;

public class Exit{
	private GraphicsContext gc;
	public double x, y;
	public static final double WIDTH = 35;
	public static final double HEIGHT = 40;
	private Image image = new Image(getClass().getClassLoader().getResourceAsStream("exit.png"));
	
	public Exit(GraphicsContext gc, double x, double y){
		this.gc = gc;
		this.x = x;
		this.y = y;
	}
	
	public void render(){
		//gc.setFill(Color.BLUE);
		//gc.fillRect(this.x, this.y, WIDTH, HEIGHT);
		gc.drawImage(this.image, this.x, this.y, WIDTH, HEIGHT);
	}
}
