package com.zoho.files.rainbow;

public enum RainbowColors {
	VIOLET(1),
	INDIGO(2),
	BLUE(3),
	GREEN(4),
	YELLOW(5),
	ORANGE(6),
	RED(7);
	
	private int code;
	RainbowColors(int code){
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}

}
