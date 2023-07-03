package com.orangomango.food;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.*;

public class Particle{
	private static class ParticlePiece{
		private double x, init;
		private double angle;
		private double startX, startY;
		private boolean finished;
		private int size;
		private Color color;
		
		public ParticlePiece(double a, double x, double y, double startX, int size, Color col){
			this.angle = a;
			this.startX = x;
			this.startY = y;
			this.x = startX;
			this.init = startX;
			this.size = size;
			this.color = col;
		}
		
		public boolean isFinished(){
			return this.finished;
		}
		
		public void render(GraphicsContext gc){
			if (this.x < 0){
				this.x += 1;
				return;
			}
			gc.save();
			gc.translate(this.startX, this.startY);
			gc.rotate(this.angle);
			gc.setFill(this.color.deriveColor(1, 1, 1, 0.6));
			gc.fillRect(this.x, 0, 7, 7);
			gc.restore();
			this.x += 1;
			if (this.x == this.size){
				this.finished = true;
				this.x = this.init;
			}
		}
	}
	
	private double x, y;
	private List<ParticlePiece> pieces = new ArrayList<>();
	private boolean repeat, finished;
	
	public Particle(double x, double y, String type, int size, boolean repeat){
		this.x = x;
		this.y = y;
		this.repeat = repeat;
		populateParticles(type, size);
	}
	
	private void populateParticles(String type, int size){
		switch (type){
			case "circle":
				for (int a = 0; a < 3; a++){
					for (double i = 0; i < 360; i += 360.0/10){
						pieces.add(new ParticlePiece(i+a*15, this.x, this.y, -a*5, size, Color.RED));
					}
				}
				break;
			case "tail":
				for (int a = 0; a < 6; a++){
					for (int i = 0; i < 4; i++){
						Random random = new Random();
						pieces.add(new ParticlePiece(90+(random.nextInt(60)-30), this.x, this.y, -a*5, size, Color.WHITE));
					}
				}
				break;
		}
	}
	
	public boolean isFinished(){
		return this.finished;
	}
	
	public void render(GraphicsContext gc){
		for (int i = 0; i < this.pieces.size(); i++){
			ParticlePiece p = this.pieces.get(i);
			p.render(gc);
			if (p.isFinished() && !this.repeat){
				this.pieces.remove(p);
				this.finished = true;
				i--;
			}
		}
	}
}
