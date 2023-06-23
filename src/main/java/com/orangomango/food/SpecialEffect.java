package com.orangomango.food;

import java.util.Random;

public class SpecialEffect{
	public boolean specialJump;
	public boolean slowFall;
	public boolean invulnerability;
	public boolean noCheckpoints;
	public boolean speedBoost;
	public boolean fog;
	
	public void makeEffect(){
		Random random = new Random();
		specialJump = false;
		slowFall = false;
		invulnerability = false;
		noCheckpoints = false;
		speedBoost = false;
		fog = false;
		switch (random.nextInt(6)){
			case 0 -> specialJump = true;
			case 1 -> slowFall = true;
			case 2 -> invulnerability = true;
			case 3 -> noCheckpoints = true;
			case 4 -> speedBoost = true;
			case 5 -> fog = true;
		}
	}
	
	public boolean available(){
		return specialJump || slowFall || invulnerability || noCheckpoints || speedBoost || fog;
	}
	
	@Override
	public String toString(){
		String output = "The dice landed on ";
		if (specialJump){
			output += " 1:\nYou have Super jump";
		} else if (slowFall){
			output += " 2:\nYou can glide";
		} else if (invulnerability){
			output += " 3:\nYou are invulnerable";
		} else if (noCheckpoints){
			output += " 4:\nCheckpoints disabled";
		} else if (speedBoost){
			output += " 5:\nSpeed boost activated";
		} else if (fog){
			output += " 6:\nFog enabled";
		}
		return output;
	}
}
