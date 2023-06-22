package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.beans.property.SimpleBooleanProperty;

import com.orangomango.food.ui.GameScreen;

public class ActivatorPad extends GameObject{
	private static Image IMAGE = MainApplication.loadImage("activatorpad.png");
	private static Image IMAGE2 = MainApplication.loadImage("activatorpad_1.png");
	private SimpleBooleanProperty activated = new SimpleBooleanProperty(false);

	public ActivatorPad(GraphicsContext gc, double x, double y, Runnable on, Runnable off){
		super(gc, x, y, IMAGE.getWidth(), IMAGE.getHeight());
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
		gc.drawImage(this.activated.get() ? IMAGE2 : IMAGE, this.x, this.y, this.w, this.h);
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
