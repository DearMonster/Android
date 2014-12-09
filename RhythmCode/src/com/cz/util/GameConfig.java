package com.cz.util;

import java.util.List;

import com.cz.piano.PianoKey;

import android.graphics.Bitmap;

public class GameConfig {

	// �������
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	public static int WHITE_KEY_COUNT = 7;
	public static int BLACK_KEY_COUNT = 5;
	public static int whiteKeyWidth;
	public static int whiteKeyHeight;
	public static int blackKeyWidth;
	public static int blackKeyHeight;
	public static List<PianoKey> keys;
	public static int countPerScreen = 7;// ��Ļ��ʾ7������
	public static Object synchObject = new Object();
	public static Bitmap blackKey[] = new Bitmap[2]; 
	public static Bitmap whiteKey[] = new Bitmap[2];
	
	// ���ּ�¼���
	public static String[] trainMusics = new String[3];// ����ѵ���׶���������
	public static String[] rhythms = new String[3];
	public static int trainIndex = 0;// ��¼ѵ������
	public static int trainInputIndex = 0;// ��¼ѵ���׶��û���������
	public static int testInputIndex = 0;// ��¼���׶ΰ�������
	public static long lastTime = 0;// �ϴ�ʱ��
	public static String[] finalSequence = new String[100];// ���յİ�������
	public static String finalMusicRythem = "";// ���յ�����
	public static long[][] trainTimeIntervals = new long[3][100];// ��¼ѵ���׶ε�ʱ������
	public static String testInputMusic = new String();// ���׶��������������
	public static long[] testTimeInterval = new long[100];// ���׶ε�ʱ����	
	public static int musicLength = 0;// ���볤�ȡ�
	
	public static boolean passedTest = false;// �Ƿ�ͨ����⡣
	public static int tryTimes = 0;// ������������Ĵ���
	public static int maxTryTimes = 3;
}
