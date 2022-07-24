package com.orangomango.food;

import java.util.Random;

public class SpecialEffect{
	public boolean specialJump;
	public boolean slowFall;
	public boolean invulnerability;
	public boolean noCheckpoints;
	public boolean screenRotated;
	
	public void makeEffect(){
		Random random = new Random();
		specialJump = false;
		slowFall = false;
		invulnerability = false;
		noCheckpoints = false;
		screenRotated = false;
		switch (random.nextInt(100)/20){
			case 0:
				specialJump = true;
				break;
			case 1:
				slowFall = true;
				break;
			case 2:
				invulnerability = true;
				break;
			case 3:
				noCheckpoints = true;
				break;
			case 4:
				screenRotated = true;
				break;
		}
	}
	
	public boolean areAllFalse(){
		return !specialJump && !slowFall && !invulnerability && !noCheckpoints && !screenRotated;
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
		} else if (screenRotated){
			output += " 5:\nScreen rotated";
		}
		return output;
	}
}
