package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;

public class Door extends GameObject implements Turnable{
	private boolean opened;
	private Image[] images = new Image[8];
	private boolean animating;
	private volatile boolean stopCurrentAnimation;

	public Door(GraphicsContext gc, double x, double y){
		super(gc, x, y, 20, 50);
		for (int i = 0; i < 8; i++){
			this.images[i] = MainApplication.loadImage("door_"+i+".png");
		}
	}
	
	@Override
	public void render(){
		this.solid = !this.opened;
		gc.drawImage(this.images[this.imageIndex], this.x, this.y, this.w, this.h);
	}
	
	@Override
	public void turnOn(){
		this.open();
	}
	
	@Override
	public void turnOff(){
		this.close();
	}
	
	public void open(){
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
					return;
				}
			}
			this.opened = true;
			this.animating = false;
		}, "door-opening").start();
	}
	
	public void close(){
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
					return;
				}
			}
			this.opened = false;
			this.animating = false;
		}, "door-closing").start();
	}
}
