package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Random;

import com.orangomango.food.ui.GameScreen;

public class Player extends GameObject{
	public static final double Y_SPEED = 95; // Jump height
	public static final double X_SPEED = 10;
	private static Image[] IMAGES = new Image[6];
	private static Image DIED_IMAGE = MainApplication.loadImage("player_died.png");
	private double onDieX, onDieY;
	private volatile boolean blinking, blink;
	
	static {
		for (int i = 1; i <= 6; i++){
			IMAGES[i-1] = MainApplication.loadImage("player_"+i+".png");
		}
	}
	
	public Player(double x, double y){
		super(x, y, IMAGES[0].getWidth(), IMAGES[0].getHeight());
		this.onDieX = this.respawnX;
		this.onDieY = this.respawnY;
		makeGravity();
		startEffectLoop();
		this.solid = true;
	}
	
	@Override
	public void render(GraphicsContext gc){
		gc.save();
		gc.translate(this.x+this.w/2, this.y+this.h/2);
		gc.rotate(this.angle);
		gc.drawImage(this.died ? DIED_IMAGE : IMAGES[this.imageIndex], -this.w/2, -this.h/2, this.w, this.h);
		if (System.currentTimeMillis() >= this.lastTimeEffect+12000 && GameScreen.getInstance().getSpecialEffect().available()){
			if (!this.blinking){
				this.blinking = true;
				new Thread(() -> {
					try {
						for (int i = 0; i < (15000-12000)/250; i++){
							this.blink = i % 2 == 0;
							Thread.sleep(250);
						}
						this.blinking = false;
					} catch (InterruptedException ex){
						ex.printStackTrace();
					}
				}).start();
			}
			if (this.blink){
				gc.setGlobalAlpha(0.6);
				gc.setFill(Color.BLUE);
				gc.fillRect(-this.w/2, -this.h/2, this.w, this.h);
			}
		}
		gc.restore();
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
	
	public void die(boolean force){
		GameScreen.getInstance().shakeCamera();
		if ((this.died || GameScreen.getInstance().getSpecialEffect().invulnerability) && !force) return;
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
		GameScreen.getInstance().deaths++;
		GameScreen.getInstance().getEffects().add(new Particle(this.x, this.y, "circle", 30, false));
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
