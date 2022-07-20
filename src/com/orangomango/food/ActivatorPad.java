package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.beans.property.SimpleBooleanProperty;

import com.orangomango.food.ui.GameScreen;

public class ActivatorPad extends GameObject{
	private Image image;
	private Image image2;
	private SimpleBooleanProperty activated = new SimpleBooleanProperty(false);

	public ActivatorPad(GraphicsContext gc, double x, double y, Runnable on, Runnable off){
		super(gc, x, y, 35, 13);
		this.image = new Image(getClass().getClassLoader().getResourceAsStream("activatorpad.png"));
		this.image2 = new Image(getClass().getClassLoader().getResourceAsStream("activatorpad_1.png"));
		this.solid = true;
		makeGravity();
		this.activated.addListener((ob, oldV, newV) -> {
			if (newV){
				this.y += 3;
				this.h -= 3;
				on.run();
			} else {
				this.y -= 3;
				this.h += 3;
				off.run();
			}
		});
	}
	
	@Override
	public void render(){
		gc.drawImage(this.activated.get() ? this.image2 : this.image, this.x, this.y, this.w, this.h);
		boolean ac = false;
		for (GameObject go : GameScreen.getInstance().getSprites()){
			if (go == this) continue;
			if (go.collided(this.x, this.y-4, this.w, 4)){
				ac = true;
				break;
			} else {
				ac = false;
			}
		}
		this.activated.set(ac);
	}
}
