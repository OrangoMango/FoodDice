package com.orangomango.food.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.animation.*;
import javafx.util.Duration;
import java.util.*;
import javafx.scene.image.*;

import com.orangomango.food.MainApplication;

public class HomeScreen{
	private Timeline loop;
	private List<MenuButton> buttons = new ArrayList<>();
	private boolean forward = true;
	private double extraY = 1;
	private Image background = new Image(getClass().getClassLoader().getResourceAsStream("background_home.jpg"));
	private Image logo = new Image(getClass().getClassLoader().getResourceAsStream("logo.png"));
	
	public StackPane getLayout(){		
		StackPane layout = new StackPane();
		
		Canvas canvas = new Canvas(MainApplication.WIDTH, MainApplication.HEIGHT);
		canvas.setOnMousePressed(e -> {
			for (MenuButton mb : this.buttons){
				mb.click(e.getX(), e.getY());
			}
		});
		layout.getChildren().add(canvas);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		this.buttons.add(new MenuButton(() -> {
			this.loop.stop();
			LevelsScreen ls = new LevelsScreen();
			MainApplication.stage.getScene().setRoot(ls.getLayout());
		}, 200, 230, 75, 75, new Image(getClass().getClassLoader().getResourceAsStream("button_play.png"))));
		this.buttons.add(new MenuButton(() -> {
			this.loop.stop();
			HelpScreen hs = new HelpScreen();
			MainApplication.stage.getScene().setRoot(hs.getLayout());
		}, 350, 230, 75, 75, new Image(getClass().getClassLoader().getResourceAsStream("button_help.png"))));
		this.buttons.add(new MenuButton(() -> {
			this.loop.stop();
			CreditsScreen cs = new CreditsScreen();
			MainApplication.stage.getScene().setRoot(cs.getLayout());
		}, 500, 230, 75, 75, new Image(getClass().getClassLoader().getResourceAsStream("button_credits.png"))));
		
		update(gc);
		
		this.loop = new Timeline(new KeyFrame(Duration.millis(1000.0/MainApplication.FPS), e -> update(gc)));
		this.loop.setCycleCount(Animation.INDEFINITE);
		this.loop.play();
		
		return layout;
	}
	
	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		//gc.setFill(Color.web("#409B85"));
		//gc.fillRect(0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		gc.drawImage(this.background, 0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		gc.drawImage(this.logo, 165, 50);
		
		gc.save();
		gc.translate(0, this.extraY);
		for (MenuButton mb : this.buttons){
			mb.render(gc);
		}
		gc.restore();
		
		this.extraY += this.forward ? 0.1 : -0.1;
		if (this.extraY >= 3){
			this.forward = false;
		} else if (this.extraY <= 0){
			this.forward = true;
		}
	}
}
