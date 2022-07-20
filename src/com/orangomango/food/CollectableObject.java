package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.animation.*;
import javafx.util.Duration;

import com.orangomango.food.ui.GameScreen;

public class CollectableObject{
	public static enum CollectableType{
		COIN(15, 15, "coin.png");
		
		private double width;
		private double height;
		private Image image;
		
		private CollectableType(double w, double h, String imageName){
			this.width = w;
			this.height = h;
			this.image = new Image(getClass().getClassLoader().getResourceAsStream(imageName));
		}
		
		public double getHeight(){
			return this.height;
		}
		
		public double getWidth(){
			return this.width;
		}
		
		public Image getImage(){
			return this.image;
		}
	}
	
	private CollectableType type;
	private GraphicsContext gc;
	private double x, y;
	private double startY;
	private double moveAmount = 1;
	
	public CollectableObject(CollectableType type, GraphicsContext gc, double x, double y){
		this.type = type;
		this.gc = gc;
		this.x = x;
		this.y = y;
		this.startY = y;
		Timeline loop = new Timeline(new KeyFrame(Duration.millis(50), e -> {
			if (GameScreen.getInstance().isPaused()) return;
			if (this.y == this.startY){
				this.moveAmount = 1;
			} else if (this.y == this.startY+15){
				this.moveAmount = -1;
			}			
			this.y += this.moveAmount;
		}));
		loop.setCycleCount(Animation.INDEFINITE);
		loop.play();
	}
	
	public CollectableType getType(){
		return this.type;
	}
	
	public void render(){
		gc.drawImage(this.type.getImage(), this.x, this.y, this.type.getWidth(), this.type.getHeight());
	}
	
	public boolean collided(GameObject go){
		Rectangle2D thisRect = new Rectangle2D(this.x, this.y, this.type.getWidth(), this.type.getHeight());
		Rectangle2D otherRect = new Rectangle2D(go.getX(), go.getY(), go.getWidth(), go.getHeight());
		return thisRect.intersects(otherRect);
	}
}
