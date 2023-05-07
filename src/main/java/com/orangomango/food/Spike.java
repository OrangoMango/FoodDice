package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import java.util.Random;

public class Spike extends GameObject{
	private static final double SIZE = 25;
	private Image[] images = new Image[4];
	
	public Spike(GraphicsContext gc, double x, double y, String type){
		super(gc, x, y, SIZE, SIZE);
		switch (type){
			case "fire":
				this.images[0] = MainApplication.loadImage("spike_1.png");
				this.images[1] = MainApplication.loadImage("spike.png");
				this.images[2] = MainApplication.loadImage("spike_2.png");
				startImageAnimation(200, 2, true);
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
	}
	
	@Override
	public void render(){
		gc.drawImage(this.images[this.imageIndex], this.x, this.y, this.w, this.h);
	}
}
