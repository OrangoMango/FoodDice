package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.Random;

public class Spike extends GameObject{
	private Image[] images = new Image[4];
	
	public Spike(GraphicsContext gc, double x, double y, String type){
		super(gc, x, y, 0, 0);
		switch (type){
			case "fire":
				this.images[0] = MainApplication.loadImage("fire_1.png");
				this.images[1] = MainApplication.loadImage("fire.png");
				this.images[2] = MainApplication.loadImage("fire_2.png");
				startImageAnimation(200, 2, true);
				break;
			case "spike":
				this.images[0] = MainApplication.loadImage("spike.png");
				break;
			case "cactus":
				for (int i = 0; i < 4; i++){
					this.images[i] = MainApplication.loadImage("cactus_"+i+".png");
				}
				Random random = new Random();
				this.imageIndex = random.nextInt(4);
				break;
			default:
				throw new IllegalArgumentException("Spike type not available");
		}
		this.w = this.images[0].getWidth();
		this.h = this.images[0].getHeight();
	}
	
	@Override
	public void render(){
		gc.drawImage(this.images[this.imageIndex], this.x, this.y, this.w, this.h);
	}
}
