package com.cz.util;

import java.util.List;

import com.cz.piano.PianoKey;

import android.graphics.Bitmap;

public class GameConfig {

	// 钢琴相关
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	public static int WHITE_KEY_COUNT = 7;
	public static int BLACK_KEY_COUNT = 5;
	public static int whiteKeyWidth;
	public static int whiteKeyHeight;
	public static int blackKeyWidth;
	public static int blackKeyHeight;
	public static List<PianoKey> keys;
	public static int countPerScreen = 7;// 屏幕显示7个按键
	public static Object synchObject = new Object();
	public static Bitmap blackKey[] = new Bitmap[2]; 
	public static Bitmap whiteKey[] = new Bitmap[2];
	
	// 音乐记录相关
	public static String[] trainMusics = new String[3];// 保存训练阶段音乐序列
	public static String[] rhythms = new String[3];
	public static int trainIndex = 0;// 记录训练次数
	public static int trainInputIndex = 0;// 记录训练阶段用户按键次数
	public static int testInputIndex = 0;// 记录检测阶段按键次数
	public static long lastTime = 0;// 上次时间
	public static String[] finalSequence = new String[100];// 最终的按键序列
	public static String finalMusicRythem = "";// 最终的旋律
	public static long[][] trainTimeIntervals = new long[3][100];// 记录训练阶段的时间间隔。
	public static String testInputMusic = new String();// 检测阶段输入的音乐序列
	public static long[] testTimeInterval = new long[100];// 检测阶段的时间间隔	
	public static int musicLength = 0;// 密码长度。
	
	public static boolean passedTest = false;// 是否通过检测。
	public static int tryTimes = 0;// 尝试输入密码的次数
	public static int maxTryTimes = 3;
}
