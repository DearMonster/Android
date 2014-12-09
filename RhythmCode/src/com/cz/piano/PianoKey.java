package com.cz.piano;

import com.cz.rhythmcode.PianoActivity;
import com.cz.util.GameConfig;

import android.graphics.Canvas;
public class PianoKey {
	private PianoActivity activity;
	public float x;
	public float y;
	public int width;
	public int height;
	public boolean inWindow = false;
	private int soundId;
	private boolean pushed = false;
	private int id = -1;
	private int pointerIndex;
	private boolean isInKeyArea;

	
	
	

	private int keyType = 0;

	public static final int WHITE_KEY = 0;

	public static final int BLACK_KEY = 1;

	/**
	 * 
	 * @param activity
	 * @param x：键的横坐标
	 * @param y：键的纵坐标
	 * @param width：键宽度
	 * @param height：键高度
	 * @param keyType：键类型，0为白键，1为黑键
	 * @param soundId：音调文件Id
	 * @param lable：标记
	 */
	public PianoKey(PianoActivity activity, float x, float y, int width,
			int height, int keyType, int soundId, String lable) {
		this.activity = activity;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.keyType = keyType;
		this.soundId = soundId;
	}

	/**
	 * 
	 */
	public void playKeySFX() {
		activity.playSFX(soundId);
	}

	/**
	 * 
	 * @param pointX
	 * @param pointY
	 */
	public void keyDownAction(float pointX, float pointY) {
		if (pointerIndex == 0) {
			pointerIndex = 1;
			pushed = true;
			playKeySFX();
		}
	}

	public void keyUpAction(float pointX, float pointY) {
		if (pointerIndex == 1) {
			pointerIndex = 0;
			pushed = false;
		}
	}

	public void keyMoveAction(float pointX, float pointY, int id) {
		if (isInRect(pointX, pointY)) {
			this.id = id;
			if (!isInKeyArea) {
				isInKeyArea = true;
				if (pointerIndex == 0) {
					pointerIndex = 1;
					pushed = true;
					playKeySFX();
				}
			}
		}
		if (!isInRect(pointX, pointY)) {
			if (isInKeyArea && (this.id == id)) {
				isInKeyArea = false;
				if (pointerIndex == 1) {
					pointerIndex = 0;
					pushed = false;
				}
			}
		}
	}

	/**
	 * 
	 * @param canvas
	 */
	public void onDraw(Canvas canvas) {

		switch (keyType) {
		case WHITE_KEY:
			if (pushed) {
				canvas.drawBitmap(GameConfig.whiteKey[1], x, y, null);
			} else {
				canvas.drawBitmap(GameConfig.whiteKey[0], x, y, null);
			}
			break;

		case BLACK_KEY:
			if (pushed) {
				canvas.drawBitmap(GameConfig.blackKey[1], x, y, null);
			} else {
				canvas.drawBitmap(GameConfig.blackKey[0], x, y, null);
			}
			break;
		}
	}

	/**
	 * 
	 * @param pointX
	 * @param pointY
	 * @return
	 */
	public boolean isInRect(float pointX, float pointY) {
		//白建的keyType为0
		if(keyType == 0)
		{
			//if语句表示只有按白建露出部分才有效
		    if (pointX > x && pointX < x + width && pointY > y+GameConfig.blackKeyHeight
				    && pointY < y + height) {
			    return true;
		    }
	    }
		else
		{
			//黑键情况
			 if (pointX > x && pointX < x + width && pointY > y
					    && pointY < y + height) {
				 return true;
			 }
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isInWindow() {
		if (x >= 0 && x <= GameConfig.SCREEN_WIDTH && y >= 0
				&& y <= GameConfig.SCREEN_HEIGHT) {
			return true;
		} else {
			return false;
		}

	}

}
