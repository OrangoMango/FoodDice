package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;

import com.orangomango.food.ui.GameScreen;

public abstract class GameObject{
	protected double x, y, w, h;
	protected GraphicsContext gc;
	private double gravity = 1.5;
	protected boolean gravityActivated, falling;
	protected boolean movingRight, movingLeft, jumping;
	private static final int X_FRAMES = 4, Y_FRAMES = 6;
	protected int imageIndex;
	private boolean incrementDirection;
	protected boolean solid, movable, died;
	protected Timeline motionLeft, motionRight, motionJump;
	protected double respawnX, respawnY;
	protected double angle;
	protected boolean loadEffect = false;
	protected volatile long lastTimeEffect;
	private boolean soundAllowed = true;
	protected volatile boolean stopThread;
	private Timeline an, gr;
	
	public GameObject(GraphicsContext gc, double x, double y, double w, double h){
		this.gc = gc;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.respawnX = x;
		this.respawnY = y;
		this.lastTimeEffect = System.currentTimeMillis();
	}
	
	public abstract void render();
	
	public boolean isEffectAvailable(){
		return this.loadEffect;
	}
	
	public void destroy(){
		this.stopThread = true;
		if (this.an != null) this.an.stop();
		if (this.gr != null) this.gr.stop();
	}
	
	/**
	 * In order to use this method, be sure to override @link{applyEffects()}
	 */
	protected void startEffectLoop(){
		Thread t = new Thread(() -> {
			try {
				while (!this.stopThread){
					if (System.currentTimeMillis() >= this.lastTimeEffect+15000){
						this.lastTimeEffect = System.currentTimeMillis();
						this.loadEffect = true;
					}
					Thread.sleep(200);
				}
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		}, "effect-loop");
		t.setDaemon(true);
		t.start();
	}
	
	protected boolean isSolid(){
		return this.solid;
	}
	
	public boolean isMovable(){
		return this.movable;
	}
	
	public double getX(){
		return this.x;
	}
	
	public double getY(){
		return this.y;
	}
	
	public double getGravity(){
		return this.gravity;
	}
	
	public void setX(double value){
		this.x = value;
	}
	
	public void setY(double value){
		this.y = value;
	}
	
	public void setRespawnX(double value){
		this.respawnX = value;
	}
	
	public void setRespawnY(double value){
		this.respawnY = value;
	}
	
	public double getWidth(){
		return this.w;
	}
	
	public double getHeight(){
		return this.h;
	}
	
	protected void startImageAnimation(int millis, int max, boolean rev){
		this.an = new Timeline(new KeyFrame(Duration.millis(millis), e -> {
			if (GameScreen.getInstance().isPaused()) return;
			incrementImageIndex(max, rev);
		}));
		an.setCycleCount(Animation.INDEFINITE);
		an.play();
	}
	
	private void incrementImageIndex(int max, boolean reverse){
		if (this.imageIndex == max){
			this.incrementDirection = true;
		} else if (this.imageIndex == 0){
			this.incrementDirection = false;
		}
		this.imageIndex += this.incrementDirection ? -1 : 1;
	}
	
	public void makeGravity(){
		if (this.gravityActivated){
			throw new IllegalArgumentException("Gravity already activated");
		}
		this.gravityActivated = true;
		this.gr = new Timeline(new KeyFrame(Duration.millis(1000.0/60), e -> {
			if (this.jumping || (GameScreen.getInstance().isPaused())) return;
			boolean collided = checkCollision(this.x, this.y+this.h, this.w, this.gravity*1.07);
			if (this.y+this.h+this.gravity*1.07 >= GameScreen.getInstance().getLevelHeight() || collided){
				this.falling = false;
				if (this.gravity > 15 && this instanceof Player){
					((Player)this).die(false);
					this.gravity = 1.5;
					return; // Don't need to fix object's position
				}
				this.gravity = 1.5;
				GameObject foundNearest = getNearestBottomObject(this);
				if (foundNearest != null){
					this.y = foundNearest.getY()-this.h;
				} else {
					//this.y = GameScreen.getInstance().getLevelHeight()-this.h;
				}
			} else {
				if (this instanceof Player && GameScreen.getInstance().getSpecialEffect().slowFall){
					this.gravity = 2;
				} else {
					this.gravity *= 1.07;
				}
				this.y += this.gravity;
				this.falling = true;
			}
		}));
		gr.setCycleCount(Animation.INDEFINITE);
		gr.play();
	}
	
	/*public GameObject getNearestTopObject(){
		GameObject found = null;
		for (GameObject go : GameScreen.getInstance().getSprites()){
			if (go == this) continue;
			Rectangle2D thisColl = new Rectangle2D(this.x-10, this.y-Player.Y_SPEED, this.w+20, this.h+Player.X_SPEED);
			Rectangle2D otherColl = new Rectangle2D(go.getX(), go.getY(), go.getWidth(), go.getHeight());
			if (go.getY()+go.getHeight() < this.y && thisColl.intersects(otherColl)){
				if (found == null || go.getY() > found.getY()){
					found = go;
				}
			}
		}
		return found;
	}*/
	
	public GameObject getNearestBottomObject(GameObject exclude){
		GameObject found = null;
		for (GameObject go : GameScreen.getInstance().getSprites()){
			if (go == exclude || !go.isSolid()) continue;
			try {
				Rectangle2D thisColl = new Rectangle2D(this.x, this.y, this.w, this.h+GameScreen.getInstance().getLevelHeight());
				Rectangle2D otherColl = new Rectangle2D(go.getX(), go.getY(), go.getWidth(), go.getHeight());
				if (go.getY() >= this.y+this.h && thisColl.intersects(otherColl)){
					if (found == null || go.getY() < found.getY()){
						found = go;
					}
				}
			} catch (IllegalArgumentException ex){
			}
		}
		return found;
	}
	
	/*public GameObject getNearestLeftObject(){
		GameObject found = null;
		for (GameObject go : GameScreen.getInstance().getSprites()){
			if (go == this) continue;
			Rectangle2D thisColl = new Rectangle2D(this.x-Player.X_SPEED, this.y, this.w+Player.X_SPEED, this.h);
			Rectangle2D otherColl = new Rectangle2D(go.getX(), go.getY(), go.getWidth(), go.getHeight());
			if (go.getX()+go.getWidth() < this.x+this.w && thisColl.intersects(otherColl)){
				if (found == null || go.getX() > found.getX()){
					found = go;
				}
			}
		}
		return found;
	}
	
	public GameObject getNearestRightObject(){
		GameObject found = null;
		for (GameObject go : GameScreen.getInstance().getSprites()){
			if (go == this) continue;
			Rectangle2D thisColl = new Rectangle2D(this.x, this.y, this.w+Player.X_SPEED, this.h);
			Rectangle2D otherColl = new Rectangle2D(go.getX(), go.getY(), go.getWidth(), go.getHeight());
			if (go.getX() > this.x && thisColl.intersects(otherColl)){
				if (found == null || go.getX() < found.getX()){
					found = go;
				}
			}
		}
		return found;
	}*/
	
	public boolean collided(double x, double y, double w, double h){
		Rectangle2D playerCollision = new Rectangle2D(x, y, w, h);
		Rectangle2D thisCollision = new Rectangle2D(this.x, this.y, this.w, this.h);
		return playerCollision.intersects(thisCollision);
	}
	
	public boolean collided(GameObject go){
		return collided(go.getX(), go.getY(), go.getWidth(), go.getHeight());
	}
	
	/**
	 * This method checks collision between solid objects. It's useful to setup the correct gravity
	 */
	public boolean checkCollision(double px, double py, double pw, double ph){
		for (GameObject ob : GameScreen.getInstance().getSprites()){
			if (ob.isSolid() && ob != this){
				if (ob.collided(px, py, pw, ph)){
					return true;
				}
			}
		}
		return false;
	}
	
	private void startRotation(int n, double angle){
		if (this.angle % 270 != 0) return;
		Timeline rot = new Timeline(new KeyFrame(Duration.millis(100), e -> this.angle += angle));
		rot.setCycleCount(n);
		rot.play();
	}
	
	// This method should be overridden by the player class
	protected void applyEffects(){
		this.lastTimeEffect = System.currentTimeMillis();
	}
	
	// This method should be overridden by the player class
	protected void setRandomDiceFace(){
	}
	
	private void playMoveSound(){
		if (!this.soundAllowed) return;
		this.soundAllowed = false;
		new Thread(() -> {
			try {
				Thread.sleep(500);
				this.soundAllowed = true;
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		}, "move-sound-cooldown").start();
		MainApplication.playSound(MainApplication.MOVE_SOUND, false);
	}
	
	public void moveLeft(double speed){
		if (this.movingLeft || this.movingRight || this.died) return;
		if (this.x-speed < 0) return;
		this.movingLeft = true;
		setRandomDiceFace();
		if (this.loadEffect){
			applyEffects();
			this.loadEffect = false;
		}
		if (this instanceof Player) playMoveSound();
		int n = (int)speed/X_FRAMES;
		if (this instanceof Player && n != 0) startRotation(n, -270/n);
		this.motionLeft = new Timeline(new KeyFrame(Duration.millis(1000.0/60), e -> {
			if (!checkCollision(this.x-X_FRAMES, this.y, this.w+X_FRAMES, this.h)){
				this.x -= X_FRAMES;
			}
			for (GameObject ob : GameScreen.getInstance().getSprites()){
				if (ob.isMovable() && (ob.collided(this.x-X_FRAMES, this.y, this.w+X_FRAMES, this.h) || ob.collided(this.x, this.y-Y_FRAMES, this.w, this.h+Y_FRAMES))){
					ob.moveLeft(3);
				}
			}
		}));
		this.motionLeft.setOnFinished(e -> this.movingLeft = false);
		this.motionLeft.setCycleCount(n);
		this.motionLeft.play();
	}
	
	public void moveRight(double speed){
		if (this.movingRight || this.movingLeft || this.died) return;
		if (this.x+this.w+speed > GameScreen.getInstance().getLevelWidth()) return;
		this.movingRight = true;
		setRandomDiceFace();
		if (this.loadEffect){
			applyEffects();
			this.loadEffect = false;
		}
		if (this instanceof Player) playMoveSound();
		int n = (int)speed/X_FRAMES;
		if (this instanceof Player && n != 0) startRotation(n, 270/n);
		this.motionRight = new Timeline(new KeyFrame(Duration.millis(1000.0/60), e -> {
			if (!checkCollision(this.x, this.y, this.w+X_FRAMES, this.h)){
				this.x += X_FRAMES;
			}
			for (GameObject ob : GameScreen.getInstance().getSprites()){
				if (ob.isMovable() && (ob.collided(this.x, this.y, this.w+X_FRAMES, this.h) || ob.collided(this.x, this.y-Y_FRAMES, this.w, this.h+Y_FRAMES))){
					ob.moveRight(3);
				}
			}
		}));
		this.motionRight.setOnFinished(e -> this.movingRight = false);
		this.motionRight.setCycleCount(n);
		this.motionRight.play();
	}
	
	public void moveUp(double speed){
		if (this.falling || this.jumping || this.died) return;
		this.jumping = true;
		if (this instanceof Player) MainApplication.playSound(MainApplication.JUMP_SOUND, false);
		GameScreen.getInstance().getEffects().add(new Particle(GameScreen.getInstance().getGC(), this.x, this.y+this.h, "tail", 20, false));
		this.motionJump = new Timeline(new KeyFrame(Duration.millis(1000.0/60), e -> {
			if (!checkCollision(this.x, this.y-Y_FRAMES, this.w, this.h+Y_FRAMES)){
				this.y -= Y_FRAMES;
				this.falling = true;
			}
		}));
		this.motionJump.setOnFinished(e -> this.jumping = false);
		this.motionJump.setCycleCount((int)speed/Y_FRAMES);
		this.motionJump.play();
	}
}
