package com.orangomango.food.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.control.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.image.*;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.input.MouseButton;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;

import java.util.*;
import java.io.*;
import com.orangomango.food.MainApplication;
import com.orangomango.food.Platform;

public class Editor{
	private static final String saveDirectory = System.getProperty("user.dir");
	public static String lastFile;
	
	private static class SelectedImage{
		public double x, y;
		private double w, h;
		private Image image;
		
		public void setImage(Image img){
			this.image = img;
			if (img != null){
				this.w = img.getWidth();
				this.h = img.getHeight();
			}
		}
		
		public Image getImage(){
			return this.image;
		}
		
		public void render(GraphicsContext gc, int sb){
			if (sb == 0 || sb == 16){
				this.w = Platform.PlatformType.SMALL.getWidth();
				this.h = Platform.PlatformType.SMALL.getHeight();
			} else if (sb == 1 || sb == 17){
				this.w = Platform.PlatformType.MEDIUM.getWidth();
				this.h = Platform.PlatformType.MEDIUM.getHeight();
			}
			if (this.image != null){
				gc.drawImage(this.image, this.x, this.y, this.w, this.h);
			}
		}
	}
	
	private static class LevelItem{
		public double x, y, w, h;
		public String extra;
		private Image image;
		private String id;
		
		public LevelItem(double x, double y, double w, double h, Image image, String id){
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.image = image;
			this.id = id;
			if (Integer.parseInt(this.id.split(";")[0]) == 7){
				this.extra = ",true";
			}
		}
		
		public String getID(){
			return this.id;
		}
		
		public void render(GraphicsContext gc){
			if (Integer.parseInt(this.id.split(";")[0]) == 7){
				if (this.extra.equals(",true")){
					gc.drawImage(this.image, this.x, this.y, this.w, this.h);
				} else {
					gc.drawImage(this.image, this.x+this.w, this.y, -this.w, this.h);
				}
			} else {
				if (Integer.parseInt(this.id.split(";")[0]) != 2 && Integer.parseInt(this.id.split(";")[0]) != 3){
					gc.drawImage(this.image, this.x, this.y, this.w, this.h);
				} else {
					for (int i = 0; i < this.h/32; i++){
						for (int j = 0; j < this.w/32; j++){
							double tempW = j*32+32 < this.w ? 32 : this.w-j*32;
							double tempH = i*32+32 < this.h ? 32 : this.h-i*32;
							gc.drawImage(this.image, 0, 0, tempW, tempH, this.x+j*32, this.y+i*32, tempW, tempH);
						}
					}
				}
			}
		}
		
		public boolean clicked(double x, double y){
			Rectangle2D rect = new Rectangle2D(this.x, this.y, this.w, this.h);
			if (rect.contains(x, y)){
				//System.out.println(this.id);
				return true;
			}
			return false;
		}
		
		@Override
		public String toString(){
			String out = String.format(Locale.US, "%s,%.2f,%.2f,%.2f,%.2f", this.id, this.x, this.y, this.w, this.h);
			if (this.extra != null){
				out += this.extra;
			}
			return out;
		}
	}

	private int selectedBlock = -1;
	private int levelWidth = 800; //500;
	private int levelHeight = 800; //350;
	private Timeline loop;
	private Image background = loadImage("background.png");
	private int[][] angles;
	private SelectedImage selectedImage;
	private List<LevelItem> items = new ArrayList<>();
	private int blockID;
	private String clickSelected;
	private GridPane props;
	private String saveFileName = "NotSaved.lvl";
	private Canvas canvas;
	private boolean showCamera = true;
	
	public Editor(){
		this.selectedImage = new SelectedImage();
	}
	
	public Editor(String name){
		this();
		loadFile(name);
	}
	
	private boolean containsItem(int type){
		for (LevelItem li : this.items){
			if (Integer.parseInt(li.getID().split(";")[0]) == type){
				return true;
			}
		}
		return false;
	}
	
	private LevelItem getByID(int id){
		for (LevelItem li : this.items){
			if (Integer.parseInt(li.getID().split(";")[1]) == id){
				return li;
			}
		}
		return null;
	}
	
	private void loadAngles(){
		this.angles = new int[this.levelWidth/25][this.levelHeight/25];
		Random random = new Random();
		for (int i = 0; i < this.levelWidth/25; i++){
			for (int j = 0; j < this.levelHeight/25; j++){
				this.angles[i][j] = random.nextInt(4);
			}
		}
	}
		
	public GridPane getLayout(){
		GridPane layout = new GridPane();
		SplitPane pane = new SplitPane();
		
		loadAngles();
		
		this.canvas = new Canvas(levelWidth, levelHeight);
		canvas.setOnMouseMoved(e -> {
			this.selectedImage.x = e.getX();
			this.selectedImage.y = e.getY();
		});
		canvas.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY){
				if (this.selectedImage.getImage() == null){
					for (LevelItem li : this.items){
						boolean cl = li.clicked(e.getX(), e.getY());
						if (cl){
							if (this.clickSelected != null && this.clickSelected.equals(li.getID())){
								this.clickSelected = null;
							} else {
								this.clickSelected = li.getID();
							}
						}
					}
				} else if (!(this.selectedBlock == 14 && this.containsItem(14)) && !(this.selectedBlock == 15 && this.containsItem(15))){
					double w = this.selectedImage.getImage().getWidth();
					double h = this.selectedImage.getImage().getHeight();
					if (this.selectedBlock == 0 || this.selectedBlock == 16){
						w = Platform.PlatformType.SMALL.getWidth();
						h = Platform.PlatformType.SMALL.getHeight();
					} else if (this.selectedBlock == 1 || this.selectedBlock == 17){
						w = Platform.PlatformType.MEDIUM.getWidth();
						h = Platform.PlatformType.MEDIUM.getHeight();
					}
					this.items.add(new LevelItem(e.getX(), e.getY(), w, h, this.selectedImage.getImage(), this.selectedBlock+";"+(this.blockID++)));
				}
				updatePropsLayout();
			} else if (e.getButton() == MouseButton.SECONDARY){
				this.selectedImage.setImage(null);
			}
		});
		GraphicsContext gc = canvas.getGraphicsContext2D();
		Accordion accordion = new Accordion();
		accordion.setMaxWidth(250);
		accordion.setMinWidth(250);
		
		// Set layouts for buttons
		ToggleGroup tg = new ToggleGroup();
		
		// Blocks
		TilePane blocksPane = new TilePane();
		blocksPane.setHgap(10);
		blocksPane.setVgap(10);
		ToggleButton b1 = new ToggleButton();
		b1.setGraphic(new ImageView(loadImage("platform_small.png")));
		b1.setOnAction(e -> {
			selectedBlock = 0;
			this.selectedImage.setImage(loadImage("platform_small.png"));
		});
		ToggleButton b2 = new ToggleButton();
		b2.setGraphic(new ImageView(loadImage("platform_medium.png")));
		b2.setOnAction(e -> {
			selectedBlock = 1;
			this.selectedImage.setImage(loadImage("platform_medium.png"));
		});
		ToggleButton b3 = new ToggleButton();
		b3.setGraphic(new ImageView(loadImage("ground.png")));
		b3.setOnAction(e -> {
			selectedBlock = 2;
			this.selectedImage.setImage(loadImage("ground.png"));
		});
		ToggleButton b4 = new ToggleButton();
		b4.setGraphic(new ImageView(loadImage("wood.png")));
		b4.setOnAction(e -> {
			selectedBlock = 3;
			this.selectedImage.setImage(loadImage("wood.png"));
		});
		ToggleButton b17 = new ToggleButton();
		b17.setGraphic(new ImageView(loadImage("platform_small_editor.png")));
		b17.setOnAction(e -> {
			selectedBlock = 16;
			this.selectedImage.setImage(loadImage("platform_small_editor.png"));
		});
		ToggleButton b18 = new ToggleButton();
		b18.setGraphic(new ImageView(loadImage("platform_medium_editor.png")));
		b18.setOnAction(e -> {
			selectedBlock = 17;
			this.selectedImage.setImage(loadImage("platform_medium_editor.png"));
		});
		b1.setToggleGroup(tg);
		b2.setToggleGroup(tg);
		b3.setToggleGroup(tg);
		b4.setToggleGroup(tg);
		b17.setToggleGroup(tg);
		b18.setToggleGroup(tg);
		blocksPane.getChildren().addAll(b1, b2, b3, b4, b17, b18);
		TitledPane blocks = new TitledPane("Blocks", blocksPane);
		accordion.getPanes().add(blocks);
		
		// Damage blocks
		TilePane damagePane = new TilePane();
		damagePane.setHgap(10);
		damagePane.setVgap(10);
		ToggleButton b5 = new ToggleButton();
		b5.setGraphic(new ImageView(loadImage("spike.png")));
		b5.setOnAction(e -> {
			selectedBlock = 4;
			this.selectedImage.setImage(loadImage("spike.png"));
		});
		ToggleButton b6 = new ToggleButton();
		b6.setGraphic(new ImageView(loadImage("cactus_0.png")));
		b6.setOnAction(e -> {
			selectedBlock = 5;
			this.selectedImage.setImage(loadImage("cactus_0.png"));
		});
		ToggleButton b7 = new ToggleButton();
		b7.setGraphic(new ImageView(loadImage("laser.png")));
		b7.setOnAction(e -> {
			selectedBlock = 6;
			this.selectedImage.setImage(loadImage("laser.png"));
		});
		ToggleButton b8 = new ToggleButton();
		b8.setGraphic(new ImageView(loadImage("shooter.png")));
		b8.setOnAction(e -> {
			selectedBlock = 7;
			this.selectedImage.setImage(loadImage("shooter.png"));
		});
		b5.setToggleGroup(tg);
		b6.setToggleGroup(tg);
		b7.setToggleGroup(tg);
		b8.setToggleGroup(tg);
		damagePane.getChildren().addAll(b5, b6, b7, b8);
		TitledPane damage = new TitledPane("Damage Blocks", damagePane);
		accordion.getPanes().add(damage);
		
		// Pushable blocks
		TilePane pushablePane = new TilePane();
		pushablePane.setHgap(10);
		pushablePane.setVgap(10);
		ToggleButton b9 = new ToggleButton();
		b9.setGraphic(new ImageView(loadImage("box.png")));
		b9.setOnAction(e -> {
			selectedBlock = 8;
			this.selectedImage.setImage(loadImage("box.png"));
		});
		ToggleButton b10 = new ToggleButton();
		b10.setGraphic(new ImageView(loadImage("jumppad.png")));
		b10.setOnAction(e -> {
			selectedBlock = 9;
			this.selectedImage.setImage(loadImage("jumppad.png"));
		});
		b9.setToggleGroup(tg);
		b10.setToggleGroup(tg);
		pushablePane.getChildren().addAll(b9, b10);
		TitledPane pushable = new TitledPane("Pushable Blocks", pushablePane);
		accordion.getPanes().add(pushable);
		
		// Activable blocks
		TilePane activablePane = new TilePane();
		activablePane.setHgap(10);
		activablePane.setVgap(10);
		ToggleButton b11 = new ToggleButton();
		b11.setGraphic(new ImageView(loadImage("activatorpad.png")));
		b11.setOnAction(e -> {
			selectedBlock = 10;
			this.selectedImage.setImage(loadImage("activatorpad.png"));
		});
		ToggleButton b12 = new ToggleButton();
		b12.setGraphic(new ImageView(loadImage("door_0.png")));
		b12.setOnAction(e -> {
			selectedBlock = 11;
			this.selectedImage.setImage(loadImage("door_0.png"));
		});
		b11.setToggleGroup(tg);
		b12.setToggleGroup(tg);
		activablePane.getChildren().addAll(b11, b12);
		TitledPane activable = new TitledPane("Activable Blocks", activablePane);
		accordion.getPanes().add(activable);
		
		// Collectable objects
		TilePane collectablePane = new TilePane();
		collectablePane.setHgap(10);
		collectablePane.setVgap(10);
		ToggleButton b13 = new ToggleButton();
		b13.setGraphic(new ImageView(loadImage("coin.png")));
		b13.setOnAction(e -> {
			selectedBlock = 12;
			this.selectedImage.setImage(loadImage("coin.png"));
		});
		b13.setToggleGroup(tg);
		collectablePane.getChildren().addAll(b13);
		TitledPane collectable = new TitledPane("Collectable Objects", collectablePane);
		accordion.getPanes().add(collectable);
		
		// Special objects
		TilePane specialPane = new TilePane();
		specialPane.setHgap(10);
		specialPane.setVgap(10);
		ToggleButton b14 = new ToggleButton();
		b14.setGraphic(new ImageView(loadImage("checkpoint_off.png")));
		b14.setOnAction(e -> {
			selectedBlock = 13;
			this.selectedImage.setImage(loadImage("checkpoint_off.png"));
		});
		b14.setToggleGroup(tg);
		ToggleButton b15 = new ToggleButton();
		b15.setGraphic(new ImageView(loadImage("player_3.png")));
		b15.setOnAction(e -> {
			selectedBlock = 14;
			this.selectedImage.setImage(loadImage("player_3.png"));
		});
		b15.setToggleGroup(tg);
		ToggleButton b16 = new ToggleButton();
		b16.setGraphic(new ImageView(loadImage("exit.png")));
		b16.setOnAction(e -> {
			selectedBlock = 15;
			this.selectedImage.setImage(loadImage("exit.png"));
		});
		b16.setToggleGroup(tg);
		specialPane.getChildren().addAll(b14, b15, b16);
		TitledPane special = new TitledPane("Special Objects", specialPane);
		accordion.getPanes().add(special);
		
		ScrollPane canvasPane = new ScrollPane(canvas);
		canvasPane.setMaxWidth(500);
		canvasPane.setMaxHeight(350);
		canvasPane.setMinWidth(500);
		canvasPane.setMinHeight(350);
		
		TabPane tabs = new TabPane();
		Tab blk = new Tab("Blocks");
		blk.setClosable(false);
		blk.setContent(accordion);
		Tab prop = new Tab("Properties");
		prop.setClosable(false);
		
		this.props = new GridPane();
		this.props.setPadding(new Insets(5, 5, 5, 5));
		this.props.setHgap(5);
		this.props.setVgap(5);
		updatePropsLayout();
		prop.setContent(this.props);
		
		tabs.getTabs().addAll(blk, prop);
		
		pane.getItems().addAll(canvasPane, tabs);
		
		ToolBar tools = new ToolBar();
		Button newB = new Button("New");
		newB.setOnAction(e -> {
			this.items.clear();
			lastFile = null;
		});
		Button save = new Button("Save");
		save.setOnAction(e -> {
			this.saveFile();
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText("File saved");
			alert.setContentText("File saved successfully");
			alert.showAndWait();
		});
		Button open = new Button("Open");
		open.setOnAction(e -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText("Load level");
			ListView<String> list = new ListView<>();
			list.setPrefHeight(150);
			for (File f : new File(saveDirectory).listFiles()){
				String name = f.getName();
				if (name.endsWith(".lvl")){
					list.getItems().add(name);
				}
			}
			alert.getDialogPane().setContent(list);
			alert.showAndWait();
			String name = list.getSelectionModel().getSelectedItem();
			if (name != null){
				this.clickSelected = null;
				this.loadFile(name);
				updatePropsLayout();
			}
		});
		Button exit = new Button("Exit");
		exit.setOnAction(e -> {
			lastFile = null;
			this.loop.stop();
			HomeScreen hs = new HomeScreen();
			MainApplication.stage.getScene().setRoot(hs.getLayout());
		});
		Button run = new Button("Run");
		run.setOnAction(e -> {
			if (containsItem(14) && containsItem(15)){
				this.loop.stop();
				this.saveFile();
				GameScreen gs = new GameScreen(-1, this.save());
				MainApplication.stage.getScene().setRoot(gs.getLayout());
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText("Error");
				alert.setContentText("The level must have a player and an exit");
				alert.showAndWait();
			}
		});
		Button delete = new Button("Delete");
		delete.setOnAction(e -> {
			if (this.clickSelected != null){
				this.loop.stop();
				this.items.remove(getByID(Integer.parseInt(this.clickSelected.split(";")[1])));
				this.loop.play();
				this.clickSelected = null;
				updatePropsLayout();
			}
		});
		tools.getItems().addAll(newB, save, open, exit, run, delete);
		
		layout.add(tools, 0, 0);
		layout.add(pane, 0, 1, 2, 1);
		
		loop = new Timeline(new KeyFrame(Duration.millis(50), e -> update(gc)));
		loop.setCycleCount(Animation.INDEFINITE);
		loop.play();
		
		return layout;
	}
	
	private void updatePropsLayout(){
		this.props.getChildren().clear();
		if (this.clickSelected == null){
			// Level settings
			Label wid = new Label("Width:");
			Label hei = new Label("Height:");
			Label nameL = new Label("Name: ");
			TextField w = new TextField(Integer.toString(this.levelWidth));
			TextField h = new TextField(Integer.toString(this.levelHeight));
			TextField name = new TextField(lastFile == null ? this.saveFileName : lastFile);
			Button apply = new Button("Apply");
			apply.setOnAction(e -> {
				Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
				confirm.setHeaderText("Are you sure?");
				confirm.setContentText("By continuing you are going to clear everything");
				confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
					this.loop.pause();
					this.items.clear();
					this.levelWidth = Integer.parseInt(w.getText());
					this.levelHeight = Integer.parseInt(h.getText());
					loadAngles();
					this.saveFileName = name.getText();
					this.canvas.setWidth(this.levelWidth);
					this.canvas.setHeight(this.levelHeight);
					this.loop.play();
				});
			});
			CheckBox cameraAllowed = new CheckBox("Game-Camera");
			cameraAllowed.setSelected(this.showCamera);
			cameraAllowed.selectedProperty().addListener((ob, oldV, newV) -> this.showCamera = newV);
			this.props.add(wid, 0, 0);
			this.props.add(hei, 0, 1);
			this.props.add(nameL, 0, 2);
			this.props.add(w, 1, 0);
			this.props.add(h, 1, 1);
			this.props.add(name, 1, 2);
			this.props.add(apply, 1, 3);
			this.props.add(cameraAllowed, 0, 4, 2, 1);
		} else {
			// Block settings
			LevelItem item = getByID(Integer.parseInt(this.clickSelected.split(";")[1]));
			int type = Integer.parseInt(this.clickSelected.split(";")[0]);
			Label xp = new Label("X:");
			Label yp = new Label("Y:");
			Label wp = new Label("Width:");
			Label hp = new Label("Height:");
			Label idL = new Label("Block id: "+Integer.parseInt(this.clickSelected.split(";")[1]));
			Spinner<Double> xpos = new Spinner<>(0, this.levelWidth, item.x);
			xpos.setEditable(true);
			Spinner<Double> ypos = new Spinner<>(0, this.levelHeight, item.y);
			ypos.setEditable(true);
			Spinner<Double> width = new Spinner<>(0, this.levelWidth, item.w);
			width.setEditable(true);
			Spinner<Double> height = new Spinner<>(0, this.levelHeight, item.h);
			height.setEditable(true);
			xpos.valueProperty().addListener((ob, oldV, newV) -> item.x = newV);
			ypos.valueProperty().addListener((ob, oldV, newV) -> item.y = newV);
			width.valueProperty().addListener((ob, oldV, newV) -> item.w = newV);
			height.valueProperty().addListener((ob, oldV, newV) -> item.h = newV);
			if (type != 2 && type != 3){
				wp.setDisable(true);
				hp.setDisable(true);
				width.setDisable(true);
				height.setDisable(true);
			}
			this.props.add(xp, 0, 0);
			this.props.add(yp, 0, 1);
			this.props.add(wp, 0, 2);
			this.props.add(hp, 0, 3);
			this.props.add(xpos, 1, 0);
			this.props.add(ypos, 1, 1);
			this.props.add(width, 1, 2);
			this.props.add(height, 1, 3);
			this.props.add(idL, 0, 4, 2, 1);
			
			switch (type){
				// Activator pad
				case 10:
					Label lockID = new Label("Lock id (separate with -): ");
					TextField lid = new TextField(item.extra != null ? item.extra.substring(1, item.extra.length()) : "");
					Button savePr = new Button("Save");
					savePr.setOnAction(e -> {
						boolean ok = false;
						for (String txt : lid.getText().split("-")){ 
							int id = Integer.parseInt(txt);
							LevelItem lock = getByID(id);
							if (lock != null){
								if (Integer.parseInt(lock.getID().split(";")[0]) == 0 || Integer.parseInt(lock.getID().split(";")[0]) == 1 || Integer.parseInt(lock.getID().split(";")[0]) == 6 || Integer.parseInt(lock.getID().split(";")[0]) == 11 || Integer.parseInt(lock.getID().split(";")[0]) == 16 || Integer.parseInt(lock.getID().split(";")[0]) == 17){
									ok = true;
								} else {
									ok = false;
									break;
								}
							}
						}
						if (ok){
							item.extra = ","+lid.getText();
							Alert info = new Alert(Alert.AlertType.INFORMATION);
							info.setHeaderText("Information");
							info.setContentText("Block attached successfully");
							info.showAndWait();
						} else {
							Alert error = new Alert(Alert.AlertType.ERROR);
							error.setHeaderText("Error");
							error.setContentText("Could not turn on/off this block");
							error.showAndWait();
						}
					});
					this.props.add(new Separator(), 0, 5, 2, 1);
					this.props.add(lockID, 0, 6, 2, 1);
					this.props.add(lid, 0, 7, 2, 1);
					this.props.add(savePr, 1, 8);
					break;
				// Shooter
				case 7:
					CheckBox toRight = new CheckBox("To right");
					toRight.setSelected(item.extra.equals(",false"));
					Button savePr2 = new Button("Save");
					savePr2.setOnAction(e -> item.extra = ","+(!toRight.isSelected()));
					this.props.add(new Separator(), 0, 5, 2, 1);
					this.props.add(toRight, 0, 6, 2, 1);
					this.props.add(savePr2, 1, 7);
					break;
				// Movable platform
				case 16:
				case 17:
					Label moveXL = new Label("Move X: ");
					Label moveYL = new Label("Move Y: ");
					Label maxXL = new Label("Max X: ");
					Label maxYL = new Label("Max Y: ");
					Label moveTimeL = new Label("Millis: ");
					TextField moveX = new TextField(item.extra != null ? item.extra.substring(1, item.extra.length()).split("-")[0] : "");
					TextField moveY = new TextField(item.extra != null ? item.extra.substring(1, item.extra.length()).split("-")[1] : "");
					TextField maxX = new TextField(item.extra != null ? item.extra.substring(1, item.extra.length()).split("-")[2] : "");
					TextField maxY = new TextField(item.extra != null ? item.extra.substring(1, item.extra.length()).split("-")[3] : "");
					TextField moveTime = new TextField(item.extra != null ? item.extra.substring(1, item.extra.length()).split("-")[4] : "");
					Button savePr3 = new Button("Save");
					savePr3.setOnAction(e -> item.extra = ","+moveX.getText()+"-"+moveY.getText()+"-"+maxX.getText()+"-"+maxY.getText()+"-"+moveTime.getText());
					this.props.add(new Separator(), 0, 5, 2, 1);
					this.props.add(moveXL, 0, 6);
					this.props.add(moveYL, 0, 7);
					this.props.add(maxXL, 0, 8);
					this.props.add(maxYL, 0, 9);
					this.props.add(moveTimeL, 0, 10);
					this.props.add(moveX, 1, 6);
					this.props.add(moveY, 1, 7);
					this.props.add(maxX, 1, 8);
					this.props.add(maxY, 1, 9);
					this.props.add(moveTime, 1, 10);
					this.props.add(savePr3, 1, 11);
					break;
			}
		}
	}
	
	private String save(){
		StringBuilder builder = new StringBuilder();
		builder.append(this.levelWidth+"x"+this.levelHeight+"x"+this.showCamera).append("\n");
		int c = 0;
		for (LevelItem li : this.items){
			builder.append(li.toString());
			if (c < this.items.size()-1){
				builder.append("\n");
			}
			c++;
		}
		return builder.toString();
	}
	
	private void saveFile(){
		lastFile = this.saveFileName;
		File file = new File(saveDirectory, lastFile);
		try {
			if (!file.exists()){
				file.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (String line : this.save().split("\n")){
				writer.write(line+"\n");
			}
			writer.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}	
	}
	
	private void loadFile(String name){
		lastFile = name;
		File file = new File(saveDirectory, name);
		if (this.loop != null) this.loop.pause();
		this.items.clear();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			int c = 0;
			do {
				line = reader.readLine();
				if (line == null) continue;
				if (c == 0){
					this.levelWidth = Integer.parseInt(line.split("x")[0]);
					this.levelHeight = Integer.parseInt(line.split("x")[1]);
					this.showCamera = Boolean.parseBoolean(line.split("x")[2]);
				} else {
					int type = Integer.parseInt(line.split(",")[0].split(";")[0]);
					double px = Double.parseDouble(line.split(",")[1]);
					double py = Double.parseDouble(line.split(",")[2]);
					double pw = Double.parseDouble(line.split(",")[3]);
					double ph = Double.parseDouble(line.split(",")[4]);
					Image image = null;
					switch (type){
						case 0:
							image = loadImage("platform_small.png");
							break;
						case 1:
							image = loadImage("platform_medium.png");
							break;
						case 2:
							image = loadImage("ground.png");
							break;
						case 3:
							image = loadImage("wood.png");
							break;
						case 4:
							image = loadImage("spike.png");
							break;
						case 5:
							image = loadImage("cactus_0.png");
							break;
						case 6:
							image = loadImage("laser.png");
							break;
						case 7:
							image = loadImage("shooter.png");
							break;
						case 8:
							image = loadImage("box.png");
							break;
						case 9:
							image = loadImage("jumppad.png");
							break;
						case 10:
							image = loadImage("activatorpad.png");
							break;
						case 11:
							image = loadImage("door_0.png");
							break;
						case 12:
							image = loadImage("coin.png");
							break;
						case 13:
							image = loadImage("checkpoint_off.png");
							break;
						case 14:
							image = loadImage("player_3.png");
							break;
						case 15:
							image = loadImage("exit.png");
							break;
						case 16:
							image = loadImage("platform_small_editor.png");
							break;
						case 17:
							image = loadImage("platform_medium_editor.png");
							break;
					}
					LevelItem levelitem = new LevelItem(px, py, pw, ph, image, type+";"+(c-1));
					if (line.split(",").length == 6){
						levelitem.extra = ","+line.split(",")[5];
					}
					this.items.add(levelitem);
					this.blockID = c;
				}
				c++;
			} while (line != null);
			loadAngles();
			if (this.canvas != null){
				this.canvas.setWidth(this.levelWidth);
				this.canvas.setHeight(this.levelHeight);
			}
			if (this.loop != null) this.loop.play();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, levelWidth, levelHeight);
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
				gc.drawImage(this.background, 0, 0, 25, 25);
				gc.restore();
			}
		}
		for (LevelItem li : this.items){
			li.render(gc);
			if (li.getID().equals(this.clickSelected)){
				gc.setStroke(Color.RED);
				gc.setLineWidth(3);
				gc.strokeRect(li.x, li.y, li.w, li.h);
			}
		}
		gc.setGlobalAlpha(0.7);
		this.selectedImage.render(gc, this.selectedBlock);
		gc.setGlobalAlpha(1);
	}
	
	private Image loadImage(String name){
		return new Image(getClass().getClassLoader().getResourceAsStream(name));
	}
}
