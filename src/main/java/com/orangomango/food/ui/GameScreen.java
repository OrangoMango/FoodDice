package com.orangomango.food.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.scene.image.*;
import javafx.scene.text.Font;

import java.util.*;
import java.io.*;
import org.json.JSONObject;

import com.orangomango.food.*;
import com.orangomango.food.ui.controls.JoyStick;

public class GameScreen{
	private volatile List<GameObject> sprites = new ArrayList<>();
	private List<CollectableObject> collectables = new ArrayList<>();
	private List<Particle> effects = new ArrayList<>();
	private Map<KeyCode, Boolean> keys = new HashMap<>();
	private Player player;
	private static GameScreen instance;
	private GraphicsContext gc;
	public Timeline loop;
	private Exit exit;
	private int currentLevel;
	private Image backgroundTile = MainApplication.loadImage("background.png");
	private double levelWidth, levelHeight;
	private volatile boolean paused;
	private Image pausedImage;
	private boolean showCamera = true;
	private int[][] angles;
	private int currentFPS = 0;
	private volatile int framesDone = 0;
	private List<MenuButton> buttons = new ArrayList<>();
	private long levelStart, pausedStart, pausedTime;
	private int coinsCollected = 0;
	private SpecialEffect specialEffect;
	private Notification notification = new Notification();
	private double cameraShakeX, cameraShakeY;
	private boolean shaking;
	public int deaths;
	private String loadString;
	private Map<Integer, Integer> spritesID = new HashMap<>();
	private Image fogImage = MainApplication.loadImage("fog.png");
	
	private JoyStick joystick;
	
	private static Font FONT_20 = Font.loadFont(GameScreen.class.getResourceAsStream("/font.ttf"), 20);
	private static Font FONT_55 = Font.loadFont(GameScreen.class.getResourceAsStream("/font.ttf"), 55);
	
	public GameScreen(int l){
		this(l, null);
	}
	
	public GameScreen(int l, String ll){
		this.currentLevel = l;
		this.loadString = ll;
		GameScreen.instance = this;
		
		Thread frameCounter = new Thread(() -> {
			while (GameScreen.instance != null){
				try {
					Thread.sleep(1000);
					this.currentFPS = this.framesDone;
					this.framesDone = 0;
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}, "frame-counter");
		frameCounter.setDaemon(true);
		frameCounter.start();
	}
	
	public boolean isPaused(){
		return this.paused;
	}
	
	public static GameScreen getInstance(){
		return GameScreen.instance;
	}
	
	public List<GameObject> getSprites(){
		return this.sprites;
	}
	
	public List<Particle> getEffects(){
		return this.effects;
	}
	
	public List<CollectableObject> getCollectables(){
		return this.collectables;
	}
	
	public Map<KeyCode, Boolean> getKeys(){
		return this.keys;
	}
	
	public GraphicsContext getGC(){
		return this.gc;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public SpecialEffect getSpecialEffect(){
		return this.specialEffect;
	}
	
	public Notification getNotification(){
		return this.notification;
	}
	
	public double getLevelWidth(){
		return this.levelWidth;
	}
	
	public double getLevelHeight(){
		return this.levelHeight;
	}
	
	private void loadAngles(int w, int h){
		this.angles = new int[w][h];
		Random random = new Random();
		for (int i = 0; i < w; i++){
			for (int j = 0; j < h; j++){
				this.angles[i][j] = random.nextInt(4);
			}
		}
	}
	
	private void loadLevel(GraphicsContext gc, int level){
		loadLevel(gc, level, null);
	}

	private void loadLevel(GraphicsContext gc, int levelN, String[] level){
		for (GameObject go : sprites){
			go.destroy();
		}
		sprites.clear();
		collectables.clear();
		effects.clear();
		spritesID.clear();
		this.player = null;
		this.exit = null;
		this.levelWidth = 0;
		this.levelHeight = 0;
		this.levelStart = System.currentTimeMillis();
		this.pausedTime = 0;
		this.coinsCollected = 0;
		this.deaths = 0;
		this.specialEffect = new SpecialEffect();
		switch (levelN){
			case -1:
				this.levelWidth = Double.parseDouble(level[0].split("x")[0]);
				this.levelHeight = Double.parseDouble(level[0].split("x")[1]);
				this.showCamera = Boolean.parseBoolean(level[0].split("x")[2]);
				
				for (int i = 1; i < level.length; i++){
					String line = level[i];
					int type = Integer.parseInt(line.split(",")[0].split(";")[0]);
					double px = Double.parseDouble(line.split(",")[1]);
					double py = Double.parseDouble(line.split(",")[2]);
					double pw = Double.parseDouble(line.split(",")[3]);
					double ph = Double.parseDouble(line.split(",")[4]);
					switch (type){
						case 0:
							sprites.add(new Platform(gc, px, py, Platform.PlatformType.SMALL));
							break;
						case 1:
							sprites.add(new Platform(gc, px, py, Platform.PlatformType.MEDIUM));
							break;
						case 2:
							sprites.add(new Platform(gc, px, py, pw, ph, MainApplication.loadImage("ground.png")));
							break;
						case 3:
							sprites.add(new Platform(gc, px, py, pw, ph, MainApplication.loadImage("wood.png")));
							break;
						case 4:
							sprites.add(new Spike(gc, px, py, "fire"));
							break;
						case 5:
							sprites.add(new Spike(gc, px, py, "cactus"));
							break;
						case 6:
							Laser laser = new Laser(gc, px, py);
							if (line.split(",").length == 6){
								laser.setTimeOff(Integer.parseInt(line.split(",")[5]));
							}
							sprites.add(laser);
							break;
						case 7:
							Shooter shooter = new Shooter(gc, px, py, Boolean.parseBoolean(line.split(",")[5].split("-")[0]));
							shooter.setTimeOff(Integer.parseInt(line.split(",")[5].split("-")[1]));
							shooter.changeImages(0);
							sprites.add(shooter);
							break;
						case 8:
							sprites.add(new Box(gc, px, py));
							break;
						case 9:
							sprites.add(new JumpPad(gc, px, py));
							break;
						case 10:
							if (line.split(",").length == 6){
								String txt = line.split(",")[5];
								sprites.add(new ActivatorPad(gc, px, py, () -> {
									for (String part : txt.split("-")){
										int n = Integer.parseInt(part);
										((Turnable)sprites.get(spritesID.get(n))).turnOn();
									}
								}, () -> {
									for (String part : txt.split("-")){
										int n = Integer.parseInt(part);
										((Turnable)sprites.get(spritesID.get(n))).turnOff();
									}
								}));
							} else {
								sprites.add(new ActivatorPad(gc, px, py, () -> System.out.println("On"), () -> System.out.println("Off")));
							}
							break;
						case 11:
							sprites.add(new Door(gc, px, py));
							break;
						case 12:
							collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, px, py));
							break;
						case 13:
							sprites.add(new CheckPoint(gc, px, py));
							break;
						case 14:
							this.player = new Player(gc, px, py);
							sprites.add(player);
							break;
						case 15:
							this.exit = new Exit(gc, px, py);
							break;
						case 16:
						case 17:
							double moveX = 0;
							double moveY = 0;
							double maxX = 0;
							double maxY = 0;
							int moveTime = 0;
							if (line.split(",").length == 6){
								String[] data = line.split(",")[5].split("-");
								moveX = Double.parseDouble(data[0]);
								moveY = Double.parseDouble(data[1]);
								maxX = Double.parseDouble(data[2]);
								maxY = Double.parseDouble(data[3]);
								moveTime = Integer.parseInt(data[4]);
							}
							sprites.add(new MovablePlatform(gc, px, py, type == 16 ? Platform.PlatformType.SMALL : Platform.PlatformType.MEDIUM, moveX, moveY, maxX, maxY, moveTime));
							break;
						case 18:
							Portal portal = new Portal(gc, px, py);
							if (line.split(",").length == 6){
								String txt = line.split(",")[5];
								portal.setTeleport(Double.parseDouble(txt.split("-")[0]), Double.parseDouble(txt.split("-")[1]));
							}
							sprites.add(portal);
							break;
						case 19:
							Propeller propeller = new Propeller(gc, px, py);
							if (line.split(",").length == 6){
								String txt = line.split(",")[5];
								propeller.setData(Integer.parseInt(txt.split("-")[0]), Boolean.parseBoolean(txt.split("-")[1]) ? 1 : -1);
							}
							sprites.add(propeller);
							break;
						case 20:
							sprites.add(new Spike(gc, px, py, "spike"));
							break;
						case 21:
							int n = Integer.parseInt(line.split(",")[5].split("-")[0]);
							int l = Integer.parseInt(line.split(",")[5].split("-")[1]);
							RotatingPlatform rotatingPlatform = new RotatingPlatform(gc, n, l, px, py);
							if (line.split(",")[5].split("-").length > 2){
								rotatingPlatform.setData(Integer.parseInt(line.split(",")[5].split("-")[2]), Boolean.parseBoolean(line.split(",")[5].split("-")[3]) ? 1 : -1);
							}
							sprites.add(rotatingPlatform);
							break;
						case 22:
							sprites.add(new Liquid(gc, px, py, pw, ph));
							break;
						case 23:
							FallingBlock fallingBlock = new FallingBlock(gc, px, py);
							if (line.split(",").length == 6){
								fallingBlock.setFallingTime(Integer.parseInt(line.split(",")[5]));
							}
							sprites.add(fallingBlock);
							break;
						case 24:
							Shooter vshooter = new Shooter(gc, px, py, Boolean.parseBoolean(line.split(",")[5].split("-")[0]));
							vshooter.setTimeOff(Integer.parseInt(line.split(",")[5].split("-")[1]));
							vshooter.changeImages(1);
							sprites.add(vshooter);
							break;
					}
					spritesID.put(Integer.parseInt(line.split(",")[0].split(";")[1]), sprites.size()-1);
				}
				
				break;
			case 0:
				this.levelWidth = 1300;
				this.levelHeight = 800;
				this.showCamera = true;
			
				this.player = new Player(gc, 40, 240);
				sprites.add(player);
				
				sprites.add(new Platform(gc, 0, 256, 96, this.levelHeight-256-150, MainApplication.loadImage("ground.png")));
				sprites.add(new Box(gc, 65, 30));
				sprites.add(new Box(gc, 65, 0));
				Door door = new Door(gc, 385, 206);
				Laser laser = new Laser(gc, 450, 20);
				Shooter shooter = new Shooter(gc, 460, 236, false);
				MovablePlatform mob = new MovablePlatform(gc, 130, 270, Platform.PlatformType.SMALL, 2, 0, 50, 0, 100);
				sprites.add(door);
				sprites.add(new ActivatorPad(gc, 0, -30, () -> {
					door.turnOn();
					mob.turnOff();
					shooter.turnOff();
				}, () -> {
					door.turnOff();
					mob.turnOn();
					shooter.turnOn();
				}));
				sprites.add(new ActivatorPad(gc, 420, 180, () -> laser.turnOff(), () -> laser.turnOn()));
				sprites.add(new Platform(gc, 385, 256, 224, this.levelHeight-256-150, MainApplication.loadImage("wood.png")));
				sprites.add(mob);
				sprites.add(new Platform(gc, 260, 270, Platform.PlatformType.MEDIUM));
				sprites.add(new Platform(gc, 150, 150, Platform.PlatformType.MEDIUM));
				sprites.add(new Platform(gc, 300, 100, Platform.PlatformType.MEDIUM));
				sprites.add(new JumpPad(gc, 350, 50));
				sprites.add(new MovablePlatform(gc, 625, 250, Platform.PlatformType.SMALL, 0, 5, 0, 500, 50));
				sprites.add(new JumpPad(gc, 270, 252));
				sprites.add(new CheckPoint(gc, 150, 750));
				
				for (int i = 0; i < 9; i++){
					if (i % 3 == 0 || i > 6) sprites.add(new Spike(gc, 120+i*25, 375, "cactus"));
				}

				//shooter.changeImages(1);
				sprites.add(shooter);
				
				sprites.add(laser);
				sprites.add(new Laser(gc, 500, 20));
				Portal portal = new Portal(gc, 200, 770);
				portal.setTeleport(700, 750);
				sprites.add(portal);
				
				sprites.add(new Propeller(gc, 400, 750));
				sprites.add(new RotatingPlatform(gc, 5, 75, 1000, 650));
				
				collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, 180, 235));
				collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, 320, 235));
				
				//effects.add(new Particle(gc, 100, 100, "tail", 40, true));
				
				this.exit = new Exit(gc, 175, 110);
				break;
			case 1:
				this.levelWidth = 800;
				this.levelHeight = 400;
				this.showCamera = false;
				
				this.player = new Player(gc, 20, 240);
				sprites.add(new GameText(gc, 35, 190, 300, 25, "You get special effect every 15s based on your dice face"));
				sprites.add(this.player);
				sprites.add(new Platform(gc, 0, 256, 192, 400-256, MainApplication.loadImage("ground.png")));
				sprites.add(new Platform(gc, 348, 256, 192, 400-256, MainApplication.loadImage("ground.png")));
				sprites.add(new Liquid(gc, 348+192, 350, 800-348-192, 50));
				sprites.add(new Platform(gc, 0, 0, 200, 125, MainApplication.loadImage("ground.png")));
				sprites.add(new Platform(gc, 220, 256, Platform.PlatformType.MEDIUM));
				sprites.add(new Platform(gc, 700, 175, Platform.PlatformType.SMALL));
				sprites.add(new Platform(gc, 600, 115, Platform.PlatformType.SMALL));
				sprites.add(new MovablePlatform(gc, 390, 85, Platform.PlatformType.SMALL, 2, 0, 100, 0, 50));
				sprites.add(new Platform(gc, 250, 80, Platform.PlatformType.MEDIUM));
				for (int i = 0; i < 6; i++){
					sprites.add(new Spike(gc, 192+i*25, 375, "fire"));
				}
				sprites.add(new Platform(gc, 570, 220, Platform.PlatformType.MEDIUM));
				
				collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, 255, 215));
				collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, 480, 215));
				collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, 490, 40));
				
				this.exit = new Exit(gc, 270, 40);
				break;
			case 2:
				loadLevel(gc, -1, getLevelData(2));
				sprites.add(new GameText(gc, 460, 435, 300, 20, "JumpPads can be pushed"));
				return;
			case 3:
				loadLevel(gc, -1, getLevelData(3));
				sprites.add(new GameText(gc, 400, 390, 300, 25, "Boxes can be pushed"));
				return;
			case 4:
				this.levelWidth = 800;
				this.levelHeight = 800;
				this.showCamera = true;
				
				this.player = new Player(gc, 5, 700);
				this.player.setRespawnX(40);
				this.player.setRespawnY(240);
				sprites.add(this.player);
				
				// Grounds
				sprites.add(new Platform(gc, 0, 0, 800, 72, MainApplication.loadImage("ground.png")));
				sprites.add(new Platform(gc, 0, 728, 800, 72, MainApplication.loadImage("ground.png")));
				sprites.add(new Platform(gc, 0, 512, 290, 72, MainApplication.loadImage("ground.png")));
				
				sprites.add(new Laser(gc, 20, 584));
				sprites.add(new Laser(gc, 120, 584));
				sprites.add(new Laser(gc, 240, 584));
				sprites.add(new Platform(gc, 300, 700, Platform.PlatformType.MEDIUM));
				sprites.add(new Platform(gc, 415, 650, Platform.PlatformType.MEDIUM));
				sprites.add(new Spike(gc, 407, 703, "cactus"));
				sprites.add(new Spike(gc, 437, 703, "cactus"));
				sprites.add(new Spike(gc, 467, 703, "cactus"));
				Door door_l4 = new Door(gc, 547, 170);
				sprites.add(new Spike(gc, 530, 703, "fire"));
				sprites.add(new ActivatorPad(gc, 450, 630, () -> door_l4.turnOn(), () -> door_l4.turnOff()));
				sprites.add(new Spike(gc, 575, 703, "fire"));
				sprites.add(new Spike(gc, 615, 703, "cactus"));
				sprites.add(new Spike(gc, 655, 703, "cactus"));
				sprites.add(new Spike(gc, 695, 703, "cactus"));
				sprites.add(new Spike(gc, 735, 703, "cactus"));
				sprites.add(new Spike(gc, 775, 703, "cactus"));
				sprites.add(new MovablePlatform(gc, 530, 650, Platform.PlatformType.SMALL, 2, 0, 100, 0, 50));
				sprites.add(new Platform(gc, 700, 615, Platform.PlatformType.MEDIUM));
				sprites.add(new CheckPoint(gc, 730, 565));
				sprites.add(new Platform(gc, 570, 585, Platform.PlatformType.MEDIUM));
				sprites.add(new Shooter(gc, 570, 565, false));
				sprites.add(new Platform(gc, 700, 537, Platform.PlatformType.MEDIUM));
				sprites.add(new MovablePlatform(gc, 470, 510, Platform.PlatformType.SMALL, 2, 0, 144, 0, 50));
				sprites.add(new Platform(gc, 300, 490, 143, 40, MainApplication.loadImage("wood.png")));
				for (int i = 0; i < 11; i++){
					sprites.add(new Spike(gc, i*25, 487, "fire"));
				}
				sprites.add(new CheckPoint(gc, 340, 440));
				sprites.add(new Platform(gc, 190, 450, Platform.PlatformType.MEDIUM));
				sprites.add(new MovablePlatform(gc, 110, 315, Platform.PlatformType.SMALL, 0, 2, 0, 120, 50));
				sprites.add(new Platform(gc, 5, 290, Platform.PlatformType.MEDIUM));
				sprites.add(new Platform(gc, 180, 290, Platform.PlatformType.MEDIUM));
				sprites.add(new Platform(gc, 300, 290, 140, 40, MainApplication.loadImage("wood.png")));
				sprites.add(new Laser(gc, 300, 330));
				sprites.add(new Laser(gc, 390, 330));
				sprites.add(new Laser(gc, 300, 72));
				sprites.add(new Laser(gc, 390, 72));
				sprites.add(new Laser(gc, 60, 72));
				sprites.add(new Laser(gc, 235, 72));
				sprites.add(new Platform(gc, 475, 270, Platform.PlatformType.SMALL));
				sprites.add(new Box(gc, 480, 235));
				sprites.add(new Platform(gc, 547, 220, 33, 150, MainApplication.loadImage("wood.png")));
				sprites.add(door_l4);
				sprites.add(new Platform(gc, 580, 328, Platform.PlatformType.MEDIUM));
				sprites.add(new Platform(gc, 733, 328, Platform.PlatformType.SMALL));
				sprites.add(new Shooter(gc, 753, 310, true));
				sprites.add(new Platform(gc, 580, 268, Platform.PlatformType.SMALL));
				sprites.add(new Laser(gc, 590, 72));
				
				collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, 13, 255));
				collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, 193, 255));
				collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, 760, 690));
				collectables.add(new CollectableObject(CollectableObject.CollectableType.COIN, gc, 550, 435));
				
				this.exit = new Exit(gc, 585, 288);
				break;
			default:
				loadLevel(gc, -1, getLevelData(levelN));
				return;
		}
		loadAngles((int)this.levelWidth/25, (int)this.levelHeight/25);
	}
	
	private String[] getLevelData(int n){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/levels/level"+n+".lvl")));
			List<String> lines = new ArrayList<>();
			reader.lines().forEach(lines::add);
			reader.close();
			return lines.toArray(new String[lines.size()]);
		} catch (IOException ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	public StackPane getLayout(){
		StackPane layout = new StackPane();
		Canvas canvas = new Canvas(MainApplication.WIDTH, MainApplication.HEIGHT);
		this.gc = canvas.getGraphicsContext2D();
		
		if (this.loadString != null){
			loadLevel(gc, -1, this.loadString.split("\n"));
		} else {
			loadLevel(gc, this.currentLevel);
		}
		this.joystick = new JoyStick(gc);
		
		canvas.setFocusTraversable(true);
		canvas.setOnMousePressed(e -> {
			for (int i = 0; i < this.buttons.size(); i++){
				MenuButton mb = this.buttons.get(i);
				mb.click(e.getX()/MainApplication.SCALE, e.getY()/MainApplication.SCALE);
			}
			KeyCode k = this.joystick.clicked(e.getX()/MainApplication.SCALE, e.getY()/MainApplication.SCALE);
			if (k != null){
				handlePress(k, canvas);
			}
		});
		
		this.loop = new Timeline(new KeyFrame(Duration.millis(1000.0/MainApplication.FPS), e -> update(gc)));
		loop.setCycleCount(Animation.INDEFINITE);
		loop.play();
		
		canvas.setOnKeyPressed(e -> handlePress(e.getCode(), canvas));
		canvas.setOnKeyReleased(e -> keys.put(e.getCode(), false));
		canvas.setOnMouseReleased(e -> {
			KeyCode k = this.joystick.clicked(e.getX(), e.getY());
			if (k != null){
				keys.put(k, false);
			}
		});
		
		AnimationTimer fTimer = new AnimationTimer(){
			@Override
			public void handle(long time){
				GameScreen.this.framesDone++;
			}
		};
		fTimer.start();
		
		layout.getChildren().add(canvas);
		return layout;
	}
	
	private void handlePress(KeyCode key, Canvas canvas){
		if (key == KeyCode.P || key == KeyCode.ESCAPE){
			this.paused = !this.paused;
			if (this.paused){
				this.pausedStart = System.currentTimeMillis();
				this.pausedImage = canvas.snapshot(null, new WritableImage(MainApplication.WIDTH, MainApplication.HEIGHT));
				this.buttons.add(new MenuButton("", () -> {
					clearEverything();
					if (this.currentLevel < 0 && Editor.lastFile != null){
						Editor ed = new Editor(Editor.lastFile);
						MainApplication.stage.getScene().setRoot(ed.getLayout());
					} else {
						HomeScreen hs = new HomeScreen();
						MainApplication.stage.getScene().setRoot(hs.getLayout());
					}
				}, 250, 200, 75, 75, MainApplication.loadImage("button_home.png")));
				this.buttons.add(new MenuButton("", () -> {
					if (this.currentLevel < 0){
						loadLevel(gc, -1, this.loadString.split("\n"));
					} else {
						loadLevel(gc, this.currentLevel);
					}
					this.paused = false;
					this.pausedImage = null;
					this.buttons.clear();
				}, 350, 200, 75, 75, MainApplication.loadImage("button_restart.png")));
				this.buttons.add(new MenuButton("", () -> handlePress(KeyCode.P, canvas), 450, 200, 75, 75, MainApplication.loadImage("button_continue.png")));
			} else {
				this.pausedImage = null;
				this.pausedTime += System.currentTimeMillis()-this.pausedStart;
				this.buttons.clear();
			}
		} else {
			keys.put(key, true);
		}
	}
	
	private void clearEverything(){
		this.loop.stop();
		for (GameObject go : sprites){
			go.destroy();
		}
		sprites.clear();
		spritesID.clear();
		collectables.clear();
		effects.clear();
		this.player = null;
		this.exit = null;
	}
	
	public void shakeCamera(){
		if (this.shaking) return;
		this.shaking = true;
		new Thread(() -> {
			try {
				this.cameraShakeX = -7;
				Thread.sleep(100);
				this.cameraShakeX = 0;
				Thread.sleep(50);
				this.cameraShakeY = -7;
				Thread.sleep(100);
				this.cameraShakeY = 0;
				this.shaking = false;
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		}, "camera-shake").start();
	}
	
	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		gc.setFill(Color.web("#00694F"));
		gc.fillRect(0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		long difference = System.currentTimeMillis()-this.levelStart-this.pausedTime;
		
		if (this.paused){
			gc.drawImage(this.pausedImage, 0, 0);
			gc.save();
			gc.setGlobalAlpha(0.6);
			gc.setFill(Color.BLACK);
			gc.fillRect(0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
			gc.setGlobalAlpha(1);
			gc.setFill(Color.WHITE);
			gc.scale(MainApplication.SCALE, MainApplication.SCALE);
			gc.setFont(FONT_55);
			gc.fillText("PAUSED", 310, 150);
			for (MenuButton mb : this.buttons){
				mb.render(gc);
			}
			gc.restore();
			return;
		}
		
		gc.save();
		if (this.showCamera){
			gc.scale(MainApplication.SCALE, MainApplication.SCALE);
			gc.translate(-this.player.getX()+200-this.cameraShakeX, -this.player.getY()+175-this.cameraShakeY);
		} else {
			gc.scale(MainApplication.WIDTH/this.levelWidth, MainApplication.HEIGHT/this.levelHeight);
		}
		
		// Make background
		for (int x = 0; x < this.levelWidth; x += 25){
			for (int y = 0; y < this.levelHeight; y += 25){
				gc.save();
				switch (this.angles[x/25][y/25]){
					case 0:
						gc.translate(x+25, y);
						gc.rotate(90);
						break;
					case 1:
						gc.translate(x+25, y+25);
						gc.rotate(180);
						break;
					case 2:
						gc.translate(x, y+25);
						gc.rotate(270);
						break;
					case 3:
						gc.translate(x, y);
						gc.rotate(360);
						break;
				}
				gc.drawImage(this.backgroundTile, 0, 0, 25, 25);
				gc.restore();
			}
		}
		
		for (GameObject go : sprites){
			if (go.isRenderingEnabled()) go.render();
			if (go instanceof Spike || go instanceof Liquid){
				if (go.collided(this.player)){
					this.player.die(false);
				}
			}
		}
		for (int i = 0; i < collectables.size(); i++){
			CollectableObject co = collectables.get(i);
			co.render();
			if (co.collided(this.player)){
				if (co.getType() == CollectableObject.CollectableType.COIN) this.coinsCollected++;
				MainApplication.playSound(MainApplication.COIN_SOUND, false);
				collectables.remove(co);
				i--;
			}
		}
		this.exit.render();
		for (int i = 0; i < effects.size(); i++){
			Particle ef = effects.get(i);
			ef.render();
			if (ef.isFinished()){
				effects.remove(ef);
				i--;
			}
		}
		if (this.player.collided(this.exit.x, this.exit.y, Exit.WIDTH, Exit.HEIGHT)){
			MainApplication.playSound(MainApplication.LEVEL_COMPLETE_SOUND, false);
			LevelsScreen.LevelManager levelManager = LevelsScreen.getLevelManager();
			JSONObject level = levelManager.getLevelData(this.currentLevel);
			if (level != null){
				if (difference < level.getInt("bestTime") || level.getInt("bestTime") == 0 || this.coinsCollected > level.getInt("coins")){
					levelManager.put(this.currentLevel, "bestTime", (int)(difference));
					if (this.deaths < level.getInt("deaths") || level.getInt("deaths") == 0){
						levelManager.put(this.currentLevel, "deaths", this.deaths);
					}
					if (this.coinsCollected > level.getInt("coins")){
						levelManager.put(this.currentLevel, "coins", this.coinsCollected);				
					}
				}
				levelManager.save();
			}
			if (this.currentLevel == LevelsScreen.FINAL_LEVEL){ // Final level
				clearEverything();
				WinScreen ws = new WinScreen();
				MainApplication.stage.getScene().setRoot(ws.getLayout());
				return;
			} else if (this.currentLevel < 0){
				clearEverything();
				if (Editor.lastFile != null){
					Editor ed = new Editor(Editor.lastFile);
					MainApplication.stage.getScene().setRoot(ed.getLayout());
				} else {
					HomeScreen hs = new HomeScreen();
					MainApplication.stage.getScene().setRoot(hs.getLayout());
				}
				return;
			} else {
				loadLevel(gc, ++this.currentLevel);
			}
		}
		if (keys.getOrDefault(KeyCode.A, false) || keys.getOrDefault(KeyCode.LEFT, false)){
			this.player.moveLeft(Player.X_SPEED*(this.specialEffect.speedBoost ? 2 : 1));
		}
		if (keys.getOrDefault(KeyCode.D, false) || keys.getOrDefault(KeyCode.RIGHT, false)){
			this.player.moveRight(Player.X_SPEED*(this.specialEffect.speedBoost ? 2 : 1));
		}
		if (keys.getOrDefault(KeyCode.SPACE, false) || keys.getOrDefault(KeyCode.UP, false)){
			this.player.moveUp(this.specialEffect.specialJump ? Player.Y_SPEED+50 : Player.Y_SPEED);
		}
		if (keys.getOrDefault(KeyCode.K, false)){
			this.player.die(true);
			keys.put(KeyCode.K, false);
		}
		if (keys.getOrDefault(KeyCode.L, false)){
			if (this.currentLevel < 0){
				loadLevel(gc, -1, this.loadString.split("\n"));
			} else {
				loadLevel(gc, this.currentLevel);
			}
			keys.put(KeyCode.L, false);
		}
		if (keys.getOrDefault(KeyCode.F1, false)){
			loadLevel(gc, 0);
			this.currentLevel = 0;
			keys.put(KeyCode.F1, false);
		}
		if (keys.getOrDefault(KeyCode.F3, false)){
			gc.setStroke(Color.GREEN);
			gc.setLineWidth(1);
			gc.strokeRect(this.player.getX(), this.player.getY(), this.player.getWidth(), this.player.getHeight());
			gc.strokeRect(this.player.getX()-Player.X_SPEED, this.player.getY(), this.player.getWidth()+Player.X_SPEED, this.player.getHeight());
			gc.strokeRect(this.player.getX(), this.player.getY(), this.player.getWidth()+Player.X_SPEED, this.player.getHeight());
			gc.strokeRect(this.player.getX(), this.player.getY()-Player.Y_SPEED, this.player.getWidth(), this.player.getHeight()+Player.Y_SPEED);
		}
		if (keys.getOrDefault(KeyCode.F4, false)){
			this.showCamera = !this.showCamera;
			keys.put(KeyCode.F4, false);
		}

		if (keys.getOrDefault(KeyCode.F2, false)){
			// Display the nearest objects to the player
			gc.save();
			gc.setStroke(Color.RED);
			GameObject red = this.player.getNearestBottomObject(this.player);
			gc.setLineWidth(4);
			if (red != null) gc.strokeRect(red.getX(), red.getY(), red.getWidth(), red.getHeight());
			gc.restore();
		}
		gc.restore();
		
		gc.save();
		gc.scale(MainApplication.SCALE, MainApplication.SCALE);

		if (this.specialEffect.fog && this.currentLevel != 1){
			gc.drawImage(this.fogImage, 0, 0, MainApplication.WIDTH, MainApplication.HEIGHT);
		}

		if (keys.getOrDefault(KeyCode.I, false)){
			gc.setFill(Color.WHITE);
			gc.fillText(String.format("Player at X:%.2f Y:%.2f", this.player.getX(), this.player.getY())+"\nCamera available: "+this.showCamera+String.format("\nFPS: %s\nGravity: %.2f\nLevel: %s\nRunning threads: %s", this.currentFPS, this.player.getGravity(), this.currentLevel, Thread.getAllStackTraces().keySet().size()), 50, 30);
		}

		gc.save();
		gc.setGlobalAlpha(0.6);
		gc.setFill(Color.BLACK);
		gc.fillRect(690, 10, 90, 75);
		gc.setGlobalAlpha(1);
		gc.setFill(Color.WHITE);
		gc.setFont(FONT_20);
		gc.fillText(String.format("%d:%02d\nCoins: %s\nDeaths: %s", difference/60000, difference/1000%60, this.coinsCollected, this.deaths), 695, 30);
		gc.restore();
		
		this.notification.render(gc);
		// For mobile
		//if (this.showMinimap) this.joystick.render();
		gc.restore();
	}
}
