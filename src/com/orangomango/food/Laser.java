package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import com.orangomango.food.ui.GameScreen;

public class Laser extends GameObject{
	private boolean shooting = false;
	private Image image = new Image(getClass().getClassLoader().getResourceAsStream("laser.png"));
	private double drawAmount;
	private volatile boolean on = true;
	private volatile int timeOff = 1400;
	
	public Laser(GraphicsContext gc, double x, double y, double w, double h){
		super(gc, x, y, w, h);
		if (w != h) throw new IllegalArgumentException("W and H must be equal");
		this.solid = true;
		Thread shoot = new Thread(() -> {
			while (!this.stopThread){
				if (!this.on || GameScreen.getInstance().isPaused()) continue;
				try {
					this.shooting = true;
					for (int i = 0; i < 20; i++){
						this.drawAmount += 0.05;
						while (GameScreen.getInstance().isPaused()){
							Thread.sleep(50);
						}
						Thread.sleep(40);
					}
					Thread.sleep(1600);
					this.shooting = false;
					Thread.sleep(this.timeOff); // Time off
					this.drawAmount = 0;
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}, "laser");
		shoot.setDaemon(true);
		shoot.start();
	}
	
	public void setTimeOff(int time){
		this.timeOff = time;
	}
	
	public void turnOn(){
		this.on = true;
	}
	
	public void turnOff(){
		this.on = false;
		if (this.shooting) this.shooting = false;
	}
	
	@Override
	public void render(){
		gc.drawImage(this.image, this.x, this.y, this.w, this.h);
		if (this.shooting){
			GameObject found = getNearestBottomObject(GameScreen.getInstance().getPlayer());
			gc.setFill(Color.RED);
			double height = ((found == null ? GameScreen.getInstance().getLevelHeight() : found.getY())-(this.y+this.h))*this.drawAmount;
			gc.fillRect(this.x+this.w/2-1.5, this.y+this.h, 3, height);
			if (GameScreen.getInstance().getPlayer().collided(this.x+this.w/2-1.5, this.y+this.h, 3, height)){
				GameScreen.getInstance().getPlayer().die();
			}
		}
	}
}
