package com.orangomango.food.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;
import java.io.*;
import shadow.org.json.JSONObject;

import com.orangomango.food.MainApplication;

public class LevelsScreen{
	private static LevelManager levelManager;
	public static final int FINAL_LEVEL = 7;
	private static final Map<Integer, Integer> LEVELCOINS = new HashMap<>();
	
	public static class LevelManager{
		private JSONObject json;
		private File file;
		
		public LevelManager(){
			this.json = new JSONObject();
			this.file = new File(Editor.saveDirectory, "data.json");
			if (this.file.exists()){
				load();
			} else {
				for (int i = 0; i < LevelsScreen.FINAL_LEVEL; i++){
					JSONObject level = new JSONObject();
					level.put("coins", 0);
					level.put("deaths", 0);
					level.put("bestTime", 0);
					this.json.put("level"+(i+1), level);
				}
				save();
			}
		}
		
		public JSONObject getLevelData(int level){
			return this.json.optJSONObject("level"+level);
		}
		
		public void put(int level, String key, int value){
			getLevelData(level).put(key, value);
		}
		
		public void save(){
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(this.file));
				for (String line : this.json.toString(4).split("\n")){
					writer.write(line);
					writer.write("\n");
				}
				writer.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		
		public void load(){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(this.file));
				StringBuilder builder = new StringBuilder();
				reader.lines().forEach(builder::append);
				reader.close();
				this.json = new JSONObject(builder.toString());
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	static {
		levelManager = new LevelManager();
		for (int i = 1; i <= FINAL_LEVEL; i++){
			int n = 0;
			if (i == 1){ // TODO convert the level to a file
				n = 3;
			} else if (i == 4){
				n = 4;
			} else {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(LevelsScreen.class.getResourceAsStream("/levels/level"+i+".lvl")));
					n = (int)reader.lines().filter(line -> line.split(";")[0].equals("12")).count();
					reader.close();
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}
			LEVELCOINS.put(i, n);
		}
	}
	
	private Timeline loop;
	private List<MenuButton> buttons = new ArrayList<>();
	private MenuButton selectButton, quitButton;
	private Image background = MainApplication.loadImage("background_home.jpg");
	private boolean forward = true;
	private double extraY = 1;
	private String selectedText = "No level selected";
	private int selectedLevel;
	private double scrollY = 0;
	private static double MAX_SCROLL;

	public StackPane getLayout(){
		StackPane layout = new StackPane();
		
		Canvas canvas = new Canvas(MainApplication.WIDTH, MainApplication.HEIGHT);
		canvas.setOnMousePressed(e -> {
			for (MenuButton mb : this.buttons){
				mb.click(e.getX()/MainApplication.SCALE, (e.getY()-this.scrollY)/MainApplication.SCALE);
			}
			this.selectButton.click(e.getX()/MainApplication.SCALE, e.getY()/MainApplication.SCALE);
			this.quitButton.click(e.getX()/MainApplication.SCALE, e.getY()/MainApplication.SCALE);
		});
		canvas.setOnScroll(e -> {
			this.scrollY += e.getDeltaY();
			if (this.scrollY > 0){
				this.scrollY = 0;
			} else if (this.scrollY < -MAX_SCROLL){
				this.scrollY = -MAX_SCROLL;
			}
		});
		layout.getChildren().add(canvas);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFont(Font.loadFont(getClass().getResourceAsStream("/font.ttf"), 25));
		
		final int GAP = 150;
		final int ROW = 5;
		
		for (int i = 0; i < FINAL_LEVEL; i++){
			final int levelNumber = i+1;
			buttons.add(new MenuButton("Level "+levelNumber, () -> loadLevel(levelNumber), 260+100*(i%ROW), 40+(i/ROW)*GAP, 75, 75, MainApplication.loadImage("button_play.png")));
		}
		
		List<String> userLevels = Editor.getUserLevels();
		for (int i = 0; i < userLevels.size(); i++){
			final String lvl = userLevels.get(i);
			MenuButton mb = new MenuButton(lvl.split("\\.")[0], () -> {
				try {
					this.loop.stop();
					StringBuilder builder = new StringBuilder();
					BufferedReader reader = new BufferedReader(new FileReader(new File(Editor.saveDirectory, lvl)));
					reader.lines().forEach(line -> builder.append(line).append("\n"));
					reader.close();
					GameScreen gs = new GameScreen(-1, builder.toString());
					MainApplication.stage.getScene().setRoot(gs.getLayout());
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}, 260+100*(i%ROW), (40+(FINAL_LEVEL-1)/ROW*GAP)+190+(i/ROW)*GAP, 75, 75, MainApplication.loadImage("button_play.png"));
			buttons.add(mb);
			MAX_SCROLL = mb.getY()-305; // Default Y (reset) is 305
		}
		
		this.selectButton = new MenuButton("", () -> playLevel(), 30, 200, 200, 60, MainApplication.loadImage("button_select.png"));
		this.quitButton = new MenuButton("", () -> {
			this.loop.stop();
			HomeScreen hs = new HomeScreen();
			MainApplication.stage.getScene().setRoot(hs.getLayout());
		}, 50, 300, 75, 75, MainApplication.loadImage("button_home.png"));
		
		update(gc);
		
		this.loop = new Timeline(new KeyFrame(Duration.millis(1000.0/MainApplication.FPS), e -> update(gc)));
		this.loop.setCycleCount(Animation.INDEFINITE);
		this.loop.play();
		
		return layout;
	}
	
	public static LevelManager getLevelManager(){
		return levelManager;
	}
	
	private void loadLevel(int l){
		this.selectedLevel = l;
		StringBuilder builder = new StringBuilder();
		builder.append("Level: "+l);
		if (levelManager.getLevelData(l).getInt("bestTime") == 0){
			builder.append("\n\nComplete the level\nto see your stats");
		} else {
			builder.append("\nCoins: "+levelManager.getLevelData(l).getInt("coins")+"/"+LEVELCOINS.get(l));
			builder.append("\nDeaths: "+levelManager.getLevelData(l).getInt("deaths"));
			builder.append("\nBest time: "+String.format("%d:%02d", levelManager.getLevelData(l).getInt("bestTime")/60000, levelManager.getLevelData(l).getInt("bestTime")/1000%60));
		}
		this.selectedText = builder.toString();
	}
	
	private void playLevel(){
		if (this.selectedLevel == 0) return;
		this.loop.stop();
		GameScreen gs = new GameScreen(this.selectedLevel);
		MainApplication.stage.getScene().setRoot(gs.getLayout());
	}
	
	private void update(GraphicsContext gc){
		gc.drawImage(this.background, 0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		gc.save();
		gc.scale(MainApplication.SCALE, MainApplication.SCALE);
		gc.translate(0, this.extraY+this.scrollY);
		for (MenuButton mb : this.buttons){
			mb.render(gc);
		}
		gc.translate(0, -this.scrollY);
		this.quitButton.render(gc);
		this.selectButton.render(gc);
		
		gc.save();
		gc.setFill(Color.BLACK);
		gc.setGlobalAlpha(0.8);
		gc.fillRect(30, 30, 200, 150);
		gc.restore();
		
		gc.setFill(Color.WHITE);
		gc.fillText(this.selectedText, 35, 55);
		
		if (MAX_SCROLL != 0){
			gc.fillRect(770, 0, 30, 400);
			gc.setFill(Color.BLACK);
			gc.fillRect(770, -this.scrollY, 30, 400-MAX_SCROLL);
		}
		
		this.extraY += this.forward ? 0.1 : -0.1;
		if (this.extraY >= 3){
			this.forward = false;
		} else if (this.extraY <= 0){
			this.forward = true;
		}
		
		gc.restore();
	}
}
