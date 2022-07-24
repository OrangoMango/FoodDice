package com.orangomango.food;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.*;
import javafx.scene.canvas.Canvas;
import javafx.animation.Animation;

import com.orangomango.food.ui.*;

public class MainApplication extends Application{
	public static final int WIDTH = 800;
	public static final int HEIGHT = 400;
	public static final int FPS = 40;
	public static Stage stage;
	
	public static Media BACKGROUND_MUSIC;
	public static Media DIE_SOUND;
	public static Media JUMP_SOUND;
	public static Media NOTIFICATION_SOUND;
	public static Media LEVEL_COMPLETE_SOUND;
	public static Media CLICK_SOUND;
	public static Media CHECKPOINT_SOUND;
	public static Media MOVE_SOUND;
	public static Media COIN_SOUND;
	
	public static void main(String[] args){
		launch(args);
	}
	
	public void start(Stage stage){
		loadSounds();
		
		playSound(BACKGROUND_MUSIC, true);
		MainApplication.stage = stage;
		HomeScreen gs = new HomeScreen();
		stage.setScene(new Scene(gs.getLayout(), WIDTH, HEIGHT));
		stage.setResizable(false);
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.ico")));
		stage.setTitle("Food Dice");
		stage.show();
	}
	
	public static void sizeOnResize(Canvas canvas){
		stage.widthProperty().addListener((o, ol, ne) ->  {
			canvas.setWidth((double)ne);
			//canvas.setScaleX((double)ne/WIDTH);
		});
		stage.heightProperty().addListener((o, ol, ne) -> {
			canvas.setHeight((double)ne);
			//canvas.setScaleY((double)ne/HEIGHT);
		});
	}
	
	private static void loadSounds(){
		BACKGROUND_MUSIC = new Media(MainApplication.class.getClassLoader().getResource("background.mp3").toExternalForm());
		DIE_SOUND = new Media(MainApplication.class.getClassLoader().getResource("die.wav").toExternalForm());
		JUMP_SOUND = new Media(MainApplication.class.getClassLoader().getResource("jump.wav").toExternalForm());
		NOTIFICATION_SOUND = new Media(MainApplication.class.getClassLoader().getResource("notification.wav").toExternalForm());
		LEVEL_COMPLETE_SOUND = new Media(MainApplication.class.getClassLoader().getResource("level_complete.wav").toExternalForm());
		CLICK_SOUND = new Media(MainApplication.class.getClassLoader().getResource("click.wav").toExternalForm());
		CHECKPOINT_SOUND = new Media(MainApplication.class.getClassLoader().getResource("checkpoint.wav").toExternalForm());
		MOVE_SOUND = new Media(MainApplication.class.getClassLoader().getResource("move.wav").toExternalForm());
		COIN_SOUND = new Media(MainApplication.class.getClassLoader().getResource("coin.wav").toExternalForm());
	}
	
	public static void playSound(Media media, boolean rep){
//		new Thread(() -> {
			MediaPlayer player = new MediaPlayer(media);
			if (rep) player.setCycleCount(Animation.INDEFINITE);
			else player.setOnEndOfMedia(() -> player.dispose());
			player.play();
//		}, "sound-thread").start();
	}
}
