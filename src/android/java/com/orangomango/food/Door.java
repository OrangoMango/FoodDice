package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Door extends GameObject implements Turnable{
	private boolean opened;
	private static Image[] IMAGES = new Image[8];
	private boolean animating;
	private volatile boolean stopCurrentAnimation;

	static {
		for (int i = 0; i < 8; i++){
			IMAGES[i] = MainApplication.loadImage("door_"+i+".png");
		}
	}

	public Door(double x, double y){
		super(x, y, IMAGES[0].getWidth(), IMAGES[0].getHeight());
	}
	
	@Override
	public void render(GraphicsContext gc){
		this.solid = !this.opened;
		gc.drawImage(IMAGES[this.imageIndex], this.x, this.y, this.w, this.h);
	}
	
	@Override
	public void turnOn(){
		if (this.animating) this.stopCurrentAnimation = true;
		this.animating = true;
		new Thread(() -> {
			for (int i = 0; i < 8; i++){
				this.imageIndex = i;
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
				if (this.stopCurrentAnimation){
					this.stopCurrentAnimation = false;
					break;
				}
			}
			this.opened = true;
			this.animating = false;
		}, "door-opening").start();
	}
	
	@Override
	public void turnOff(){
		if (this.animating) this.stopCurrentAnimation = true;
		this.animating = true;
		new Thread(() -> {
			for (int i = 7; i >= 0; i--){
				this.imageIndex = i;
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
				if (this.stopCurrentAnimation){
					this.stopCurrentAnimation = false;
					break;
				}
			}
			this.opened = false;
			this.animating = false;
		}, "door-closing").start();
	}
}
