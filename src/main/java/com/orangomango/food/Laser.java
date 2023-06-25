package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import com.orangomango.food.ui.GameScreen;

public class Laser extends GameObject implements Turnable{
	private volatile boolean shooting = false;
	private static Image IMAGE = MainApplication.loadImage("laser.png");
	private volatile double drawAmount;
	private volatile boolean on = true;
	private int timeOff = 1400;
	
	public Laser(GraphicsContext gc, double x, double y){
		super(gc, x, y, IMAGE.getWidth(), IMAGE.getHeight());
		this.solid = true;
		runThread(() -> {
			if (!this.on) return;
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
		});
	}
	
	public void setTimeOff(int time){
		this.timeOff = time;
	}
	
	@Override
	public void turnOn(){
		this.on = true;
	}
	
	@Override
	public void turnOff(){
		this.on = false;
		if (this.shooting) this.shooting = false;
	}
	
	@Override
	public void render(){
		gc.drawImage(IMAGE, this.x, this.y, this.w, this.h);
		if (this.shooting){
			GameObject found = getNearestBottomObject(this);
			gc.setFill(Color.RED);
			double height = ((found == null ? GameScreen.getInstance().getLevelHeight() : found.getY())-(this.y+this.h))*this.drawAmount;
			gc.fillRect(this.x+this.w/2-1.5, this.y+this.h, 3, height);
			if (GameScreen.getInstance().getPlayer().collided(this.x+this.w/2-1.5, this.y+this.h, 3, height)){
				GameScreen.getInstance().getPlayer().die(false);
			}
		}
	}
}
