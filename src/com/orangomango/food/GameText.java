package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameText extends GameObject{
	private String text;
	private Font font;
	
	public GameText(GraphicsContext gc, double x, double y, double w, double h, String text){
		super(gc, x, y, w, h);
		this.text = text;
		this.font = Font.loadFont(getClass().getClassLoader().getResourceAsStream("font.ttf"), this.h);
	}
	
	@Override
	public void render(){
		gc.setFont(this.font);
		gc.setFill(Color.BLACK);
		gc.fillText(this.text, this.x, this.y);
	}
}
