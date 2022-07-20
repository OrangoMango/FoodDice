package com.orangomango.food;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Notification{
	private String text = "";
	private boolean mustShow;
	private static Font font = Font.loadFont(Notification.class.getClassLoader().getResourceAsStream("font.ttf"), 30);
	
	public void setText(String t, int millis){
		this.text = t;
		this.mustShow = true;
		MainApplication.playSound(MainApplication.NOTIFICATION_SOUND, false);
		new Thread(() -> {
			try {
				Thread.sleep(millis);
				this.mustShow = false;
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		}, "notification").start();
	}
	
	public void render(GraphicsContext gc){
		if (!this.mustShow) return;
		gc.save();
		gc.setGlobalAlpha(0.6);
		gc.setFill(Color.BLACK);
		gc.fillRect(175, 50, 375, 35+(this.text.split("\n").length-1)*35);
		gc.setFill(Color.WHITE);
		gc.setGlobalAlpha(1);
		gc.setFont(font);
		gc.fillText(this.text, 185, 77);
		gc.restore();
	}
}
