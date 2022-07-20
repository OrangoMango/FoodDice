package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;

public class Liquid extends GameObject{
	public Liquid(GraphicsContext gc, double x, double y, double w, double h){
		super(gc, x, y, w, h);
	}
	
	@Override
	public void render(){
		gc.setFill(Color.ORANGE);
		gc.fillRect(this.x, this.y, this.w, this.h);
	}
}
