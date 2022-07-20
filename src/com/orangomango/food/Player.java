package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Random;

import com.orangomango.food.ui.GameScreen;

public class Player extends GameObject{
	public static final double Y_SPEED = 75;
	public static final double X_SPEED = 10;
	public static final int SIZE = 16;
	private Image[] images = new Image[6];
	private Image diedImage = new Image(getClass().getClassLoader().getResourceAsStream("player_died.png"));
	private double onDieX, onDieY;
	
	public Player(GraphicsContext gc, double x, double y, double w, double h){
		super(gc, x, y, w, h);
		this.onDieX = this.respawnX;
		this.onDieY = this.respawnY;
		makeGravity();
		startEffectLoop();
		this.solid = true;
		for (int i = 1; i <= 6; i++){
			this.images[i-1] = new Image(getClass().getClassLoader().getResourceAsStream("player_"+i+".png"));
		}
	}
	
	@Override
	public void render(){
		//gc.setFill(Color.RED);
		//gc.fillRect(this.x, this.y, this.w, this.h);
		gc.save();
		gc.translate(this.x+this.w/2, this.y+this.h/2);
		gc.rotate(this.angle);
		gc.drawImage(this.died ? this.diedImage : this.images[this.imageIndex], -this.w/2, -this.h/2, this.w, this.h);
		gc.restore();
		//gc.setFill(javafx.scene.paint.Color.BLACK);
		//gc.fillText(""+this.falling, this.x, this.y-5);
	}
	
	@Override
	protected void setRandomDiceFace(){
		Random random = new Random();
		this.imageIndex = random.nextInt(6);
	}
	
	@Override
	protected void applyEffects(){
		super.applyEffects();
		GameScreen.getInstance().getSpecialEffect().makeEffect();
		GameScreen.getInstance().getNotification().setText(GameScreen.getInstance().getSpecialEffect().toString(), 2500);
	}
	
	public void setRespawnPoint(double x, double y){
		this.onDieX = x;
		this.onDieY = y;
	}
	
	public void die(){
		GameScreen.getInstance().shakeCamera();
		if (this.died || GameScreen.getInstance().getSpecialEffect().invulnerability) return;
		this.died = true;
		MainApplication.playSound(MainApplication.DIE_SOUND, false);
		if (this.motionLeft != null && this.movingLeft){
			this.motionLeft.stop();
			this.movingLeft = false;
		}
		if (this.motionRight != null && this.movingRight){
			this.motionRight.stop();
			this.movingRight = false;
		}
		if (this.motionJump != null && this.jumping){
			this.motionJump.stop();
			this.jumping = false;
		}
		GameScreen.getInstance().getEffects().add(new Particle(this.gc, this.x, this.y, "circle", 30, false));
		new Thread(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
			this.x = this.onDieX;
			this.y = this.onDieY;
			this.died = false;
		}, "after-die").start();
	}
}
