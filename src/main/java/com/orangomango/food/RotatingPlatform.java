package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

import java.util.*;

import com.orangomango.food.ui.GameScreen;

public class RotatingPlatform extends GameObject implements Turnable{
	private int n;
	private List<Point2D> rotatingPoints = new ArrayList<>();
	private List<Platform> platforms = new ArrayList<>();
	private double angle;
	private int direction = 1;
	private int time = 100;
	private boolean on = true;
	
	private static final double LENGTH = 75;
	
	public RotatingPlatform(GraphicsContext gc, int n, double x, double y){
		super(gc, x, y, 30, 30);
		this.solid = true;
		this.n = n;
		for (int i = 0; i < n; i++){
			Point2D point = new Point2D(this.x, this.y-LENGTH);
			point = MainApplication.rotatePoint(point, 360/n*i, this.x+this.w/2, this.y+this.h/2);
			this.rotatingPoints.add(point);
			this.platforms.add(new Platform(this.gc, point.getX()-Platform.PlatformType.SMALL.getWidth()/2, point.getY()-Platform.PlatformType.SMALL.getHeight()/2, Platform.PlatformType.SMALL));
		}
		GameScreen.getInstance().getSprites().addAll(this.platforms);
		runThread(() -> {
			try {
				this.angle += 10*direction;
				Thread.sleep(this.time);
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		});
	}
	
	public void setData(int time, int direction){
		this.direction = direction;
		this.time = time;
	}
	
	@Override
	public void render(){
		gc.setLineWidth(4);
		for (int i = 0; i < this.n; i++){
			Point2D rotated = MainApplication.rotatePoint(this.rotatingPoints.get(i), this.angle, this.x+this.w/2, this.y+this.w/2);
			gc.strokeLine(this.x+this.w/2, this.y+this.h/2, rotated.getX(), rotated.getY());
			Platform pf = this.platforms.get(i);
			pf.setX(rotated.getX()-pf.getWidth()/2);
			pf.setY(rotated.getY()-pf.getHeight()/2);
			pf.render();
			Player player = GameScreen.getInstance().getPlayer();
			if (player.collided(pf.getX(), pf.getY()-4, pf.getWidth(), 4)){
				player.setY(pf.getY()-player.getHeight());
			}
		}
		gc.setFill(Color.RED);
		gc.fillRoundRect(this.x, this.y, this.w, this.h, 5, 5);
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
