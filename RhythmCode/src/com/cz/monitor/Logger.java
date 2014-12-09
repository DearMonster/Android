package com.cz.monitor;

import android.content.Context;
import android.content.SharedPreferences;

public class Logger {
	/**
	 * Logger����¼�û����������
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
	// ��¼ʱ����
	public void recordTimeInterval(int index, long[] timeInterval, String stage)
	{
		// ����:(1)������������ţ�(2)ʱ�����У�(3)����׶�
		int length = timeInterval.length;
		String str = "";
		for(int i = 0; i < length; i ++)
		{
			str += (timeInterval[i] + "&");
		}
		editor.putString(stage+index, str);
		editor.commit();
	}
	
	// ��¼��������
	public void recordRhythm(int index,String rhythm, String stage)
	{
		editor.putString(stage+index, rhythm);
		editor.commit();
	}
	
	// ��ȡָ����ŵļ�¼
	public String readTimeInterval(int index,String stage)
	{
		String result = preferences.getString(stage+index, null);
		return result;
	}
	
	// ��ȡ���е�ʱ���¼
	public String[] readAllInterval(int total, String stage)
	{
		String[] all = new String[total];
		for(int i = 0; i < total; i ++)
		{
			all[i] = readTimeInterval(i,stage);
		}
		
		return all;
	}
	// ��ȡ���е�ʱ���¼
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
