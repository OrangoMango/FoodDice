package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.*;
import com.orangomango.food.ui.GameScreen;

public class Shooter extends GameObject implements Turnable{
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
	
	private boolean left;
	private List<Bullet> bullets = new ArrayList<>();
	private int timeOff = 1300;
	private static Image[][] IMAGES = new Image[][]{new Image[]{MainApplication.loadImage("shooter_0.png"), MainApplication.loadImage("shooter_1.png")},
													new Image[]{MainApplication.loadImage("shooter2_0.png"), MainApplication.loadImage("shooter2_1.png")}};
	private Image[] images;
	private volatile boolean on = true;
	
	public Shooter(double x, double y, boolean left){
		super(x, y, IMAGES[0][0].getWidth(), IMAGES[0][0].getHeight());
		this.solid = true;
		this.images = IMAGES[0];
		this.left = left;
		runThread(() -> {
			try {
				this.imageIndex = 0;
				if (this.on){
					Thread.sleep(this.timeOff);
					this.imageIndex = 1;
					this.bullets.add(new Bullet(this.left ? this.x : this.x+this.w, this.y, this.left));
				}
				Thread.sleep(200);
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		});
	}
	
	public void setTimeOff(int time){
		this.timeOff = time;
	}
	
	public void changeImages(int index){
		this.images = IMAGES[index];
	}
	
	@Override
	public void turnOn(){
		this.on = true;
	}
	
	@Override
	public void turnOff(){
		this.on = false;
	}
	
	@Override
	public void render(GraphicsContext gc){
		if (this.left){
			gc.drawImage(this.images[this.imageIndex], this.x, this.y, this.w, this.h);
		} else {
			gc.drawImage(this.images[this.imageIndex], this.x+this.w, this.y, -this.w, this.h);
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
