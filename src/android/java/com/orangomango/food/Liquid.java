package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Liquid extends GameObject{
	private static final Image IMAGE = MainApplication.loadImage("lava.png");
	
	public Liquid(double x, double y, double w, double h){
		super(x, y, w, h);
	}
	
	@Override
	public void render(GraphicsContext gc){
		for (int i = 0; i < this.h/32; i++){
			for (int j = 0; j < this.w/32; j++){
				double tempW = j*32+32 < this.w ? 32 : this.w-j*32;
				double tempH = i*32+32 < this.h ? 32 : this.h-i*32;
				gc.drawImage(IMAGE, 0, 0, tempW, tempH, this.x+j*32, this.y+i*32, tempW, tempH);
			}
		}
	}
}
