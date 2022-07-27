package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;

import com.orangomango.food.ui.GameScreen;

public class MovablePlatform extends Platform implements Turnable{
	private double xSpeed, ySpeed, xMax, yMax;
	private boolean forward = true;
	private double startX, startY;
	private int time;
	private volatile boolean on = true;
	
	public MovablePlatform(GraphicsContext gc, double x, double y, Platform.PlatformType type, double xSpeed, double ySpeed, double xMax, double yMax, int time){
		super(gc, x, y, type);
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.xMax = xMax;
		this.yMax = yMax;
		this.startX = this.x;
		this.startY = this.y;
		this.time = time;
		
		startLoop();
	}
	
	@Override
	public void render(){
		gc.setLineWidth(4);
		double startX = 0, startY = 0, endX = 0, endY = 0;
		if (this.xSpeed != 0 && this.ySpeed != 0){
			startX = this.startX;
			startY = this.startY;
			endX = this.startX+this.xMax;
			endY = this.startY+this.yMax;
		} else if (this.xSpeed != 0 && this.ySpeed == 0){
			startX = this.startX;
			startY = this.startY+this.h/2;
			endX = this.startX+this.xMax+this.w;
			endY = this.startY+this.h/2;
		} else if (this.xSpeed == 0 && this.ySpeed != 0){
			startX = this.startX+this.w/2;
			startY = this.startY;
			endX = this.startX+this.w/2;
			endY = this.startY+this.yMax+this.h;
		}
		gc.strokeLine(startX, startY, endX, endY);
		gc.drawImage(this.type, this.x, this.y, this.w, this.h);
	}
	
	private void startLoop(){
		Thread loop = new Thread(() -> {
			while (!this.stopThread){
				if (GameScreen.getInstance().isPaused() || !this.on) continue;
				try {
					double xMove = this.forward ? this.xSpeed : -this.xSpeed;
					double yMove = this.forward ? this.ySpeed : -this.ySpeed;
					Player player = GameScreen.getInstance().getPlayer();
					if (player == null) continue;
					if (player.collided(this.x, this.y-4, this.w, 4)){
						player.setX(player.getX()+xMove);
						player.setY(player.getY()+yMove);
					}
					this.x += xMove;
					this.y += yMove;
					if ((this.x == this.startX+this.xMax && this.xSpeed != 0) || (this.y == this.startY+this.yMax && this.ySpeed != 0)){
						this.forward = false;
					} else if ((this.x == this.startX && this.xSpeed != 0) || (this.y == this.startY && this.ySpeed != 0)){
						this.forward = true;
					}
					Thread.sleep(this.time);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}, "movable-platform");
		loop.setDaemon(true);
		loop.start();
	}
	
	@Override
	public void turnOn(){
		this.on = false;
	}
	
	@Override
	public void turnOff(){
		this.on = true;
	}
}
