package com.orangomango.food;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.*;
import javafx.animation.Animation;
import javafx.geometry.Point2D;

import com.orangomango.food.ui.HomeScreen;

/**
 * FoodDice - GMTK game jam 2022
 * Game originally made in 48h without a game engine.
 * This is a post-jam version.
 * https://orangomango.itch.io/food-dice
 * 
 * @author OrangoMango
 */
public class MainApplication extends Application{
	public static final int WIDTH = 800; //1250;
	public static final int HEIGHT = 400; //950;
	public static final double SCALE = 1; //1250.0/800;
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
	
	/**
	 * Clockwise: positive rot
	 * Counterclockwise: negative rot
	 */
	public static Point2D rotatePoint(Point2D point, double rot, double px, double py){
		rot = Math.toRadians(rot);
		double x = point.getX();
		double y = point.getY();
		x -= px;
		y -= py;
		double nx = x*Math.cos(rot)-y*Math.sin(rot);
		double ny = y*Math.cos(rot)+x*Math.sin(rot);
		return new Point2D(nx+px, ny+py);
	}
	
	public static void playSound(Media media, boolean rep){
		MediaPlayer player = new MediaPlayer(media);
		if (rep) player.setCycleCount(Animation.INDEFINITE);
		else player.setOnEndOfMedia(() -> player.dispose());
		player.play();
	}
}
