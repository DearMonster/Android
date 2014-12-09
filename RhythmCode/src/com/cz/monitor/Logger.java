package com.cz.monitor;

import android.content.Context;
import android.content.SharedPreferences;

public class Logger {
	/**
	 * Logger：记录用户输入的数据
	 */
	private Context activity;
	public static SharedPreferences preferences;
	public static SharedPreferences.Editor editor;
	public static String TRAIN = "TrainningStage";
	public static String TEST = "TestStage";
	public static String RHYTHM = "Rhythm";
	
	public Logger(Context activity)
	{
		this.activity = activity;
		preferences = this.activity.getSharedPreferences("RhythmCode", Context.MODE_PRIVATE);
		editor = preferences.edit();
	}
	// 记录时间间隔
	public void recordTimeInterval(int index, long[] timeInterval, String stage)
	{
		// 参数:(1)待保存数据序号；(2)时间序列；(3)程序阶段
		int length = timeInterval.length;
		String str = "";
		for(int i = 0; i < length; i ++)
		{
			str += (timeInterval[i] + "&");
		}
		editor.putString(stage+index, str);
		editor.commit();
	}
	
	// 记录节奏特征
	public void recordRhythm(int index,String rhythm, String stage)
	{
		editor.putString(stage+index, rhythm);
		editor.commit();
	}
	
	// 读取指定序号的记录
	public String readTimeInterval(int index,String stage)
	{
		String result = preferences.getString(stage+index, null);
		return result;
	}
	
	// 读取所有的时间记录
	public String[] readAllInterval(int total, String stage)
	{
		String[] all = new String[total];
		for(int i = 0; i < total; i ++)
		{
			all[i] = readTimeInterval(i,stage);
		}
		
		return all;
	}
	// 读取所有的时间记录
		public String[] readAllRhythm(int total, String stage)
		{
			String[] all = new String[total];
			for(int i = 0; i < total; i ++)
			{
				all[i] = preferences.getString(stage+i, null);
			}
			
			return all;
		}
}
