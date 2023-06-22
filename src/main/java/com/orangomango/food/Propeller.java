package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Point2D;

import com.orangomango.food.ui.GameScreen;

public class Propeller extends GameObject{
	private static Image IMAGE = MainApplication.loadImage("propeller.png");
	private volatile double angle = 0;
	private int direction = -1;
	private int time = 50;
	
	public Propeller(GraphicsContext gc, double x, double y){
		super(gc, x, y, IMAGE.getWidth(), IMAGE.getHeight());
		runThread(() -> {
			try {
				this.angle += 10*direction;
				Thread.sleep(this.time);
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		});
	}
	
	public void setData(int direction, int time){
		this.direction = direction;
		this.time = time;
	}
	
	@Override
	public void render(){
		gc.save();
		gc.translate(this.x+this.w/2, this.y+this.h/2);
		gc.rotate(this.angle);
		gc.drawImage(IMAGE, -this.w/2, -this.h/2, this.w, this.h);
		gc.restore();
		Point2D[] thisVertices = new Point2D[4];
		thisVertices[0] = MainApplication.rotatePoint(new Point2D(this.x, this.y), this.angle, this.x+this.w/2, this.y+this.h/2);
		thisVertices[1] = MainApplication.rotatePoint(new Point2D(this.x+this.w, this.y), this.angle, this.x+this.w/2, this.y+this.h/2);
		thisVertices[2] = MainApplication.rotatePoint(new Point2D(this.x+this.w, this.y+this.h), this.angle, this.x+this.w/2, this.y+this.h/2);
		thisVertices[3] = MainApplication.rotatePoint(new Point2D(this.x, this.y+this.h), this.angle, this.x+this.w/2, this.y+this.h/2);
		Player player = GameScreen.getInstance().getPlayer();
		Point2D[] playerVertices = new Point2D[]{new Point2D(player.getX(), player.getY()),
							new Point2D(player.getX()+player.getWidth(), player.getY()),
							new Point2D(player.getX()+player.getWidth(), player.getY()+player.getHeight()),
							new Point2D(player.getX(), player.getY()+player.getHeight())};
		if (collidedConvex(thisVertices, playerVertices)){
			player.die(false);
		}
	}
}
