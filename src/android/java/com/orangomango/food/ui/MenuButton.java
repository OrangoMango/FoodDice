package com.orangomango.food.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import com.orangomango.food.MainApplication;

public class MenuButton{
	private double x, y, w, h;
	private Runnable onClick;
	private Image image;
	private String label;
	
	public MenuButton(String text, Runnable o, double x, double y, double w, double h, Image image){
		this.label = text;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.image = image;
		this.onClick = o;
	}
	
	public double getX(){
		return this.x+this.w/2;
	}
	
	public double getY(){
		return this.y+this.h;
	}
	
	public void render(GraphicsContext gc){
		gc.save();
		gc.drawImage(this.image, this.x, this.y, this.w, this.h);		
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText(this.label, getX(), getY()+30);
		gc.restore();
	}
	
	public void click(double x, double y){
		Rectangle2D thisRect = new Rectangle2D(this.x, this.y, this.w, this.h);
		if (thisRect.contains(x, y)){
			MainApplication.playSound(MainApplication.CLICK_SOUND, false);
			this.onClick.run();
		}
	}
}
