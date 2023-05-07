package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class Platform extends GameObject{
	public static enum PlatformType{
		SMALL(68, 20, "platform_small.png"),
		MEDIUM(100, 20, "platform_medium.png");
		
		private double width;
		private double height;
		private Image image;
		
		private PlatformType(double w, double h, String imageName){
			this.width = w;
			this.height = h;
			this.image = MainApplication.loadImage(imageName);
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
	
	protected Image type;
	private boolean repeat;
	
	public Platform(GraphicsContext gc, double x, double y, double w, double h, Image image){
		super(gc, x, y, w, h);
		this.type = image;
		this.repeat = true;
		this.solid = true;
	}
	
	public Platform(GraphicsContext gc, double x, double y, PlatformType type){
		this(gc, x, y, type.getWidth(), type.getHeight(), type.getImage());
		this.repeat = false;
	}
	
	@Override
	public void render(){
		//gc.setFill(Color.BLUE);
		//gc.fillRect(this.x, this.y, this.w, this.h);
		
		if (this.repeat){
			for (int i = 0; i < this.h/32; i++){
				for (int j = 0; j < this.w/32; j++){
					double tempW = j*32+32 < this.w ? 32 : this.w-j*32;
					double tempH = i*32+32 < this.h ? 32 : this.h-i*32;
					gc.drawImage(this.type, 0, 0, tempW, tempH, this.x+j*32, this.y+i*32, tempW, tempH);
				}
			}
		} else {
			gc.drawImage(this.type, this.x, this.y, this.w, this.h);
		}
		
		//gc.setStroke(Color.BLACK);
		//gc.strokeText(String.format("%s %s", this.x, this.y), this.x, this.y+this.h+15);
	}
}
