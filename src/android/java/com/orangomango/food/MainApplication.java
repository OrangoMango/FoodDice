package com.orangomango.food;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.animation.Animation;
import javafx.geometry.Point2D;

import java.lang.reflect.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;

import javafxports.android.FXActivity;
import android.media.MediaPlayer;
import android.os.Build;
import android.media.AudioManager;
import android.os.Vibrator;
import android.content.Context;
import android.view.View;

import com.orangomango.food.ui.HomeScreen;

public class MainApplication extends Application{
	public static final int WIDTH = 800;
	public static final int HEIGHT = 400;
	public static final double SCALE = 1;
	public static final int FPS = 40;
	public static Stage stage;
	
	public static String BACKGROUND_MUSIC = "background.mp3";
	public static String DIE_SOUND = "die.wav";
	public static String JUMP_SOUND = "jump.wav";
	public static String NOTIFICATION_SOUND = "notification.wav";
	public static String LEVEL_COMPLETE_SOUND = "level_complete.wav";
	public static String CLICK_SOUND = "click.wav";
	public static String CHECKPOINT_SOUND = "checkpoint.wav";
	public static String MOVE_SOUND = "move.wav";
	public static String COIN_SOUND = "coin.wav";
	public static String PORTAL_SOUND = "portal.wav";

	private static Map<String, MediaPlayer> players = new HashMap<>();
	public static Vibrator vibrator = (Vibrator)FXActivity.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
	
	public static void main(String[] args){
		launch(args);
	}
	
	public void start(Stage stage) throws Exception{
		if (Build.VERSION.SDK_INT >= 29){
			Method forName = Class.class.getDeclaredMethod("forName", String.class);
			Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
			Class vmRuntimeClass = (Class) forName.invoke(null, "dalvik.system.VMRuntime");
			Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
			Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[] { String[].class} );
			Object vmRuntime = getRuntime.invoke(null);
			setHiddenApiExemptions.invoke(vmRuntime, (Object[])new String[][]{new String[]{"L"}});
		}

		loadSounds();

		FXActivity.getInstance().runOnUiThread(() -> {
			FXActivity.getInstance().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

			// Clear useless temp files in cache of previous sessions
			for (File f : FXActivity.getInstance().getCacheDir().listFiles()){
				f.delete();
			}
		});
		
		playSound(BACKGROUND_MUSIC, true);
		MainApplication.stage = stage;
		HomeScreen gs = new HomeScreen();
		Scene mainScene = new Scene(gs.getLayout(), WIDTH, HEIGHT);
		mainScene.setFill(Color.BLACK);
		mainScene.setOnKeyPressed(e -> {
			AudioManager manager = (AudioManager)FXActivity.getInstance().getSystemService(Context.AUDIO_SERVICE);
			switch (e.getCode()){
				case VOLUME_UP:
					manager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
					break;
				case VOLUME_DOWN:
					manager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
					break;
			}
		});
		stage.setScene(mainScene);
		stage.setResizable(false);
		stage.getIcons().add(loadImage("icon.png"));
		stage.setTitle("Food Dice");
		stage.show();
	}

	private static void copyFile(String name){
		File file = new File(FXActivity.getInstance().getFilesDir().getAbsolutePath(), name.split("/")[2]);
		if (!file.exists()){
			try {
				Files.copy(MainApplication.class.getResourceAsStream(name), file.toPath());
			} catch (IOException ioe){
				ioe.printStackTrace();
			}
		}
	}

	private static void loadSounds(){
		copyFile("/audio/background.mp3");
		copyFile("/audio/die.wav");
		copyFile("/audio/jump.wav");
		copyFile("/audio/notification.wav");
		copyFile("/audio/level_complete.wav");
		copyFile("/audio/click.wav");
		copyFile("/audio/checkpoint.wav");
		copyFile("/audio/move.wav");
		copyFile("/audio/coin.wav");
		copyFile("/audio/portal.wav");
	}
	
	public static void playSound(String media, boolean rep){
		MediaPlayer mp = players.getOrDefault(media, new MediaPlayer());
		boolean first = false;
		if (!players.containsKey(media)){
			players.put(media, mp);
			first = true;
		}
		try {
			if (first){
				mp.setDataSource(FXActivity.getInstance().getFilesDir().getAbsolutePath()+"/"+media);
				mp.prepare();
				mp.setLooping(rep);
				mp.setOnCompletionListener(player -> {
					player.release();
					players.remove(media);
				});
			}
			mp.start();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
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

	public static Image loadImage(String name){
		return new Image(MainApplication.class.getResourceAsStream("/images/"+name));
	}
}
