package com.orangomango.food.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;

import com.orangomango.food.MainApplication;

public class WinScreen{
	private Image background = new Image(getClass().getClassLoader().getResourceAsStream("background_home.jpg"));

	public StackPane getLayout(){
		StackPane layout = new StackPane();
		
		Canvas canvas = new Canvas(MainApplication.WIDTH, MainApplication.HEIGHT);
		layout.getChildren().add(canvas);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		MenuButton home = new MenuButton(() -> {
			HomeScreen hs = new HomeScreen();
			MainApplication.stage.getScene().setRoot(hs.getLayout());
		}, 50, 300, 75, 75, new Image(getClass().getClassLoader().getResourceAsStream("button_home.png")));
		canvas.setOnMousePressed(e -> home.click(e.getX(), e.getY()));
		
		gc.drawImage(this.background, 0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		gc.setFont(Font.loadFont(getClass().getClassLoader().getResourceAsStream("font.ttf"), 50));
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText("YOU WIN!", 400, 100);
		gc.setFont(Font.loadFont(getClass().getClassLoader().getResourceAsStream("font.ttf"), 35));
		gc.fillText("Thanks for playing", 400, 200);
		home.render(gc);
		
		return layout;
	}
}
