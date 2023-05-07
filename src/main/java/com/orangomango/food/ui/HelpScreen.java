package com.orangomango.food.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

import com.orangomango.food.MainApplication;

public class HelpScreen{
	private Timeline loop;
	private MenuButton home;
	private Image[] images = new Image[13];
	private boolean forward = true;
	private double extraY = 1;
	private Image background = MainApplication.loadImage("background_home.jpg");
	
	public HelpScreen(){
		this.images[0] = MainApplication.loadImage("key_a.png");
		this.images[1] = MainApplication.loadImage("key_d.png");
		this.images[2] = MainApplication.loadImage("key_left.png");
		this.images[3] = MainApplication.loadImage("key_right.png");
		this.images[4] = MainApplication.loadImage("key_esc.png");
		this.images[5] = MainApplication.loadImage("key_p.png");
		this.images[6] = MainApplication.loadImage("key_k.png");
		this.images[7] = MainApplication.loadImage("key_l.png");
		this.images[8] = MainApplication.loadImage("key_f1.png");
		this.images[9] = MainApplication.loadImage("key_f2.png");
		this.images[10] = MainApplication.loadImage("key_f3.png");
		this.images[11] = MainApplication.loadImage("key_f4.png");
		this.images[12] = MainApplication.loadImage("key_m.png");
	}

	public StackPane getLayout(){
		StackPane layout = new StackPane();
		
		Canvas canvas = new Canvas(MainApplication.WIDTH, MainApplication.HEIGHT);
		layout.getChildren().add(canvas);
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		this.home = new MenuButton(() -> {
			HomeScreen hs = new HomeScreen();
			MainApplication.stage.getScene().setRoot(hs.getLayout());
		}, 50, 300, 75, 75, MainApplication.loadImage("button_home.png"));
		canvas.setOnMousePressed(e -> home.click(e.getX()/MainApplication.SCALE, e.getY()/MainApplication.SCALE));
		
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
		gc.save();
		gc.scale(MainApplication.SCALE, MainApplication.SCALE);
		this.home.render(gc);
		
		gc.drawImage(this.images[0], 75, 55+this.extraY);
		gc.drawImage(this.images[1], 120, 55+this.extraY);
		gc.drawImage(this.images[2], 75, 110+this.extraY);
		gc.drawImage(this.images[3], 120, 110+this.extraY);
		gc.drawImage(this.images[4], 75, 200+this.extraY);
		gc.drawImage(this.images[5], 130, 200+this.extraY);
		gc.drawImage(this.images[6], 500, 55+this.extraY);
		gc.drawImage(this.images[7], 500, 110+this.extraY);
		gc.drawImage(this.images[8], 490, 215+this.extraY);
		gc.drawImage(this.images[9], 490, 260+this.extraY);
		gc.drawImage(this.images[10], 490, 305+this.extraY);
		gc.drawImage(this.images[11], 490, 350+this.extraY);
		gc.drawImage(this.images[12], 170, 310+this.extraY);
		
		this.extraY += this.forward ? 0.1 : -0.1;
		if (this.extraY >= 2.5){
			this.forward = false;
		} else if (this.extraY <= 0){
			this.forward = true;
		}
		
		gc.setFill(Color.BLACK);
		gc.setFont(Font.loadFont(getClass().getClassLoader().getResourceAsStream("font.ttf"), 20));
		gc.fillText("Use A/D or arrow keys to move", 187, 100);
		gc.fillText("Use ESC or P to pause/resume", 187, 230);
		gc.fillText("Use K to kill yourself", 550, 75);
		gc.fillText("Use L to reload the level", 550, 130);
		gc.fillText("Use M to toggle game info", 227, 330);
		gc.fillText("Load the test level", 550, 240);
		gc.fillText("View nearest bottom object", 550, 290);
		gc.fillText("View player hitboxes", 550, 335);
		gc.fillText("Turn on/off camera", 550, 380);
		gc.setFill(Color.RED);
		gc.fillText("Debug keys", 460, 190);
		gc.restore();
	}
}
