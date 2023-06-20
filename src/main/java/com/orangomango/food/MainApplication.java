package com.orangomango.food;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.*;
import javafx.animation.Animation;

import com.orangomango.food.ui.*;

public class MainApplication extends Application{
	public static final int WIDTH = 800;
	public static final int HEIGHT = 400;
	public static final double SCALE = 1;
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
	public static Media PORTAL_SOUND;
	
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
		stage.getIcons().add(loadImage("icon.png"));
		stage.setTitle("Food Dice");
		stage.show();
	}
	
	private static void loadSounds(){
		BACKGROUND_MUSIC = new Media(MainApplication.class.getResource("/audio/background.mp3").toExternalForm());
		DIE_SOUND = new Media(MainApplication.class.getResource("/audio/die.wav").toExternalForm());
		JUMP_SOUND = new Media(MainApplication.class.getResource("/audio/jump.wav").toExternalForm());
		NOTIFICATION_SOUND = new Media(MainApplication.class.getResource("/audio/notification.wav").toExternalForm());
		LEVEL_COMPLETE_SOUND = new Media(MainApplication.class.getResource("/audio/level_complete.wav").toExternalForm());
		CLICK_SOUND = new Media(MainApplication.class.getResource("/audio/click.wav").toExternalForm());
		CHECKPOINT_SOUND = new Media(MainApplication.class.getResource("/audio/checkpoint.wav").toExternalForm());
		MOVE_SOUND = new Media(MainApplication.class.getResource("/audio/move.wav").toExternalForm());
		COIN_SOUND = new Media(MainApplication.class.getResource("/audio/coin.wav").toExternalForm());
		PORTAL_SOUND = new Media(MainApplication.class.getResource("/audio/portal.wav").toExternalForm());
	}
	
	public static Image loadImage(String name){
		return new Image(MainApplication.class.getResourceAsStream("/images/"+name));
	}
	
	public static void playSound(Media media, boolean rep){
		MediaPlayer player = new MediaPlayer(media);
		if (rep) player.setCycleCount(Animation.INDEFINITE);
		else player.setOnEndOfMedia(() -> player.dispose());
		player.play();
	}
}
