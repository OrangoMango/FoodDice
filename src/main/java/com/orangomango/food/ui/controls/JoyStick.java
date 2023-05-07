package com.orangomango.food.ui.controls;

import javafx.scene.canvas.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;

import static com.orangomango.food.MainApplication.WIDTH;
import static com.orangomango.food.MainApplication.HEIGHT;

public class JoyStick{
	private GraphicsContext gc;
	private Image[] images = new Image[6];
	
	public JoyStick(GraphicsContext gc){
		this.gc = gc;
		this.images[0] = new Image(getClass().getClassLoader().getResourceAsStream("control_left.png"));
		this.images[1] = new Image(getClass().getClassLoader().getResourceAsStream("control_right.png"));
		this.images[2] = new Image(getClass().getClassLoader().getResourceAsStream("control_jump.png"));
		this.images[3] = new Image(getClass().getClassLoader().getResourceAsStream("control_pause.png"));
		this.images[4] = new Image(getClass().getClassLoader().getResourceAsStream("control_minimap.png"));
		this.images[5] = new Image(getClass().getClassLoader().getResourceAsStream("control_kill.png"));
	}
	
	public void render(){
		gc.save();
		gc.setGlobalAlpha(0.7);
		gc.drawImage(this.images[0], 40, HEIGHT-90, 70, 70);
		gc.drawImage(this.images[1], 130, HEIGHT-90, 70, 70);
		
		gc.drawImage(this.images[2], WIDTH-115, HEIGHT-90, 70, 70);
		
		gc.drawImage(this.images[3], WIDTH-270, 25, 35, 35);
		gc.drawImage(this.images[4], WIDTH-225, 25, 35, 35);
		gc.drawImage(this.images[5], WIDTH-180, 25, 35, 35);
		gc.restore();
	}
	
	public KeyCode clicked(double x, double y){
		Rectangle2D moveLeft = new Rectangle2D(40, HEIGHT-90, 70, 70);
		Rectangle2D moveRight = new Rectangle2D(130, HEIGHT-90, 70, 70);
		Rectangle2D jump = new Rectangle2D(WIDTH-115, HEIGHT-90, 70, 70);
		Rectangle2D pause = new Rectangle2D(WIDTH-270, 25, 35, 35);
		Rectangle2D map = new Rectangle2D(WIDTH-225, 25, 35, 35);
		Rectangle2D kill = new Rectangle2D(WIDTH-180, 25, 35, 35);
		if (moveLeft.contains(x, y)) return KeyCode.A;
		if (moveRight.contains(x, y)) return KeyCode.D;
		if (jump.contains(x, y)) return KeyCode.SPACE;
		if (pause.contains(x, y)) return KeyCode.P;
		if (map.contains(x, y)) return KeyCode.M;
		if (kill.contains(x, y)) return KeyCode.K;
		return null;
	}
}
