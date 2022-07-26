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
		private static Image image = new Image(Shooter.class.getClassLoader().getResourceAsStream("shooter_bullet.png"));
		
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
	
	private static final double SIZE = 20;
	private Image[] images = new Image[2];
	private boolean left;
	private List<Bullet> bullets = new ArrayList<>();
	
	public Shooter(GraphicsContext gc, double x, double y, boolean left){
		super(gc, x, y, SIZE, SIZE);
		this.images[0] = new Image(getClass().getClassLoader().getResourceAsStream("shooter.png"));
		this.images[1] = new Image(getClass().getClassLoader().getResourceAsStream("shooter_1.png"));
		this.solid = true;
		this.left = left;
		Thread anim = new Thread(() -> {
			while (!this.stopThread){
				if (GameScreen.getInstance().isPaused()) continue;
				try {
					this.imageIndex = 0;
					Thread.sleep(1300);
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
	
	@Override
	public void render(){
		if (this.left){
			gc.drawImage(this.images[this.imageIndex], this.x, this.y, SIZE, SIZE);
		} else {
			gc.drawImage(this.images[this.imageIndex], this.x+SIZE, this.y, -SIZE, SIZE);
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
