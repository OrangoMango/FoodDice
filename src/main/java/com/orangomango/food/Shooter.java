package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.*;
import com.orangomango.food.ui.GameScreen;

public class Shooter extends GameObject{
	private static class Bullet{
		private double x, y;
		private boolean left;
		private static final double speed = 1.5;
		public static final double SIZE = 12;
		private static Image image = MainApplication.loadImage("shooter_bullet.png");
		
		public Bullet(double x, double y, boolean l){
			this.x = x;
			this.y = y;
			this.left = l;
		}
		
		public void render(GraphicsContext gc){
			gc.drawImage(image, this.x, this.y, SIZE, SIZE);
			this.x += this.left ? -speed : speed;
		}
		
		public double getX(){
			return this.x;
		}
		
		public double getY(){
			return this.y;
		}
	}
	
	private static Image[] IMAGES = new Image[]{MainApplication.loadImage("shooter.png"), MainApplication.loadImage("shooter_1.png")};
	private boolean left;
	private List<Bullet> bullets = new ArrayList<>();
	private int timeOff = 1300;
	
	public Shooter(GraphicsContext gc, double x, double y, boolean left){
		super(gc, x, y, IMAGES[0].getWidth(), IMAGES[0].getHeight());
		this.solid = true;
		this.left = left;
		Thread anim = new Thread(() -> {
			while (!this.stopThread){
				if (GameScreen.getInstance().isPaused()) continue;
				try {
					this.imageIndex = 0;
					Thread.sleep(this.timeOff);
					this.imageIndex = 1;
					this.bullets.add(new Bullet(this.left ? this.x : this.x+this.w, this.y, this.left));
					Thread.sleep(200);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}, "shooter");
		anim.setDaemon(true);
		anim.start();
	}
	
	public void setTimeOff(int time){
		this.timeOff = time;
	}
	
	@Override
	public void render(){
		if (this.left){
			gc.drawImage(IMAGES[this.imageIndex], this.x, this.y, this.w, this.h);
		} else {
			gc.drawImage(IMAGES[this.imageIndex], this.x+this.w, this.y, -this.w, this.h);
		}
		for (int i = 0; i < this.bullets.size(); i++){
			Bullet b = this.bullets.get(i);
			b.render(gc);
			boolean col = false;
			if (b.getX() >= GameScreen.getInstance().getLevelWidth() || b.getX()+Bullet.SIZE <= 0) col = true;
			for (GameObject go : GameScreen.getInstance().getSprites()){
				if (go.collided(b.getX(), b.getY(), Bullet.SIZE, Bullet.SIZE) && go != this && go.isSolid()){
					if (go instanceof Player) GameScreen.getInstance().getPlayer().die(false);
					col = true;
					break;
				}
			}
			if (col){
				this.bullets.remove(b);
				i--;
			}
		}
	}
}
