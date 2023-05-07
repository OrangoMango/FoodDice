package com.orangomango.food.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.animation.*;
import javafx.util.Duration;
import java.util.*;
import javafx.scene.image.*;

import com.orangomango.food.MainApplication;

public class LevelsScreen{
	private Timeline loop;
	private List<MenuButton> buttons = new ArrayList<>();
	private Image background = MainApplication.loadImage("background_home.jpg");
	private boolean forward = true;
	private double extraY = 1;

	public StackPane getLayout(){
		StackPane layout = new StackPane();
		
		Canvas canvas = new Canvas(MainApplication.WIDTH, MainApplication.HEIGHT);
		canvas.setOnMousePressed(e -> {
			for (MenuButton mb : this.buttons){
				mb.click(e.getX()/MainApplication.SCALE, e.getY()/MainApplication.SCALE);
			}
		});
		layout.getChildren().add(canvas);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		buttons.add(new MenuButton(() -> loadLevel(1), 100, 120, 75, 75, MainApplication.loadImage("button_level_1.png")));
		buttons.add(new MenuButton(() -> loadLevel(2), 200, 120, 75, 75, MainApplication.loadImage("button_level_2.png")));
		buttons.add(new MenuButton(() -> loadLevel(3), 300, 120, 75, 75, MainApplication.loadImage("button_level_3.png")));
		buttons.add(new MenuButton(() -> loadLevel(4), 400, 120, 75, 75, MainApplication.loadImage("button_level_4.png")));
		
		buttons.add(new MenuButton(() -> {
			this.loop.stop();
			HomeScreen hs = new HomeScreen();
			MainApplication.stage.getScene().setRoot(hs.getLayout());
		}, 50, 300, 75, 75, MainApplication.loadImage("button_home.png")));
		
		update(gc);
		
		this.loop = new Timeline(new KeyFrame(Duration.millis(1000.0/MainApplication.FPS), e -> update(gc)));
		this.loop.setCycleCount(Animation.INDEFINITE);
		this.loop.play();
		
		return layout;
	}
	
	private void loadLevel(int l){
		this.loop.stop();
		GameScreen gs = new GameScreen(l);
		MainApplication.stage.getScene().setRoot(gs.getLayout());
	}
	
	private void update(GraphicsContext gc){
		//gc.clearRect(0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		//gc.setFill(Color.web("#409B85"));
		gc.drawImage(this.background, 0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		gc.save();
		gc.scale(MainApplication.SCALE, MainApplication.SCALE);
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
