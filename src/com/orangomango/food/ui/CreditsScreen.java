package com.orangomango.food.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;

import com.orangomango.food.MainApplication;

public class CreditsScreen{
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
		
		//gc.setFill(Color.web("#409B85"));
		//gc.fillRect(0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		gc.drawImage(this.background, 0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		home.render(gc);
		gc.setFill(Color.BLACK);
		gc.setFont(Font.loadFont(getClass().getClassLoader().getResourceAsStream("font.ttf"), 30));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText("Game written in Java\nFramework used: JavaFX\nCode and images made by OrangoMango\nSounds from freesound.org\nGMTK Game Jam 2022\n...\n\nhttps://github.com/OrangoMango", 400, 50);
		
		return layout;
	}
}
