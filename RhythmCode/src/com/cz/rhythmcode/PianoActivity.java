package com.cz.rhythmcode;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cz.monitor.Logger;
import com.cz.piano.PianoKey;
import com.cz.util.Dbscan;
import com.cz.util.GameConfig;

public class PianoActivity extends Activity implements OnClickListener {
	// ��������
	private SoundPool sfxPool;
	private AudioManager audioManager;
	private HashMap<Integer, Integer> sfxList;
	// ������������������Դ
	private ProgressDialog loadingBar;

	private Handler mHandler;
	Button leftButton;// ѵ��
	Button rightButton; // ����
	Button logButton;// ��ʾ��¼
	boolean trainStage; // ѵ���׶�
	boolean testStage; // ���Խ׶�
	
	String[] datas; // ���水������
	Logger log;// ��¼

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_piano);

		leftButton = (Button) findViewById(R.id.left);
		rightButton = (Button) findViewById(R.id.right);
		logButton = (Button) findViewById(R.id.record);
		
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		logButton.setOnClickListener(this);
		
		trainStage = false;
		testStage=false;
		
		GameConfig.keys = new ArrayList<PianoKey>();
		log = new Logger(this);
		GameConfig.musicLength = 0;
		GameConfig.trainInputIndex = 0;
		GameConfig.trainIndex = 0;

		// ������Ļ����Ӧ�ó��򳤿�
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		GameConfig.SCREEN_WIDTH = windowManager.getDefaultDisplay().getWidth();
		GameConfig.SCREEN_HEIGHT = windowManager.getDefaultDisplay().getHeight();
		
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

		// ������
		loadingBar = new ProgressDialog(this);
		loadingBar.setMessage(getString(R.string.loading));
		loadingBar.setCancelable(false);
		loadingBar.setCanceledOnTouchOutside(false);
		loadingBar.show();

		mHandler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				loadingBar.cancel();
			};
		};
		// ��Դ�����߳�
		new Thread(new Runnable() {
			@Override
			public void run() {
				initSfx();// ��ʼ����������
				initBitmap(); // ��ʼ������ͼƬ
				synchronized (GameConfig.synchObject) {
					initPianoKeys();
				}
				mHandler.sendEmptyMessage(1);
			}
		}).start();
	}
	private void initBitmap() {
		// ��ʼ�����ټ���״̬λͼ
		Resources res = getResources();
		GameConfig.blackKey[0] = BitmapFactory.decodeResource(res,R.drawable.black_up);
		GameConfig.blackKey[1] = BitmapFactory.decodeResource(res,R.drawable.black_down);
		GameConfig.whiteKey[0] = BitmapFactory.decodeResource(res,R.drawable.white_up);
		GameConfig.whiteKey[1] = BitmapFactory.decodeResource(res,R.drawable.white_down);
	}
	private void initPianoKeys() {

		// ��ʼ�����ټ�λ
		GameConfig.whiteKeyWidth = GameConfig.SCREEN_WIDTH / GameConfig.countPerScreen;
		GameConfig.whiteKeyHeight = (GameConfig.SCREEN_HEIGHT / 5) << 2;

		GameConfig.blackKeyHeight = (GameConfig.whiteKeyHeight / 3) << 1;
		GameConfig.blackKeyWidth = (GameConfig.whiteKeyWidth / 9) * 7;

		GameConfig.blackKey[0] = Bitmap.createScaledBitmap(GameConfig.blackKey[0], GameConfig.blackKeyWidth,
				GameConfig.blackKeyHeight, false);
		GameConfig.blackKey[1] = Bitmap.createScaledBitmap(GameConfig.blackKey[1], GameConfig.blackKeyWidth,
				GameConfig.blackKeyHeight, false);

		GameConfig.whiteKey[0] = Bitmap.createScaledBitmap(GameConfig.whiteKey[0], GameConfig.whiteKeyWidth,
				GameConfig.whiteKeyHeight, false);
		GameConfig.whiteKey[1] = Bitmap.createScaledBitmap(GameConfig.whiteKey[1], GameConfig.whiteKeyWidth,
				GameConfig.whiteKeyHeight, false);

		// ��Ӱ׼�
		for (int i = 0; i < GameConfig.WHITE_KEY_COUNT; i++)
		{
			GameConfig.keys.add(new PianoKey(this,i * GameConfig.whiteKeyWidth, 0, GameConfig.whiteKeyWidth,
					GameConfig.whiteKeyHeight, 0, i+37, ""));
		}

		// ��Ӻڼ�
		for (int k = 0; k < GameConfig.WHITE_KEY_COUNT; k++) 
		{
			if (k % 7 == 0 || k % 7 == 1) 
			{
				GameConfig.keys.add(new PianoKey(this, (k + 1)* GameConfig.whiteKeyWidth- (GameConfig.blackKeyWidth >> 1), 0,
						GameConfig.blackKeyWidth,GameConfig.blackKeyHeight, 1, 78+k, null));
			}
			else if (k % 7 == 3 || k % 7 == 4 || k % 7 == 5)
			{
				GameConfig.keys.add(new PianoKey(this, (k + 1)* GameConfig.whiteKeyWidth- (GameConfig.blackKeyWidth >> 1), 0,
						GameConfig.blackKeyWidth,GameConfig.blackKeyHeight, 1, 78+k-2, null));
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initSfx() {
		// ��ʼ��������Դ
		sfxPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		sfxList = new HashMap<Integer, Integer>();
		sfxList.put(37, sfxPool.load(this, R.raw.c6, 1));
		sfxList.put(38, sfxPool.load(this, R.raw.d6, 1));
		sfxList.put(39, sfxPool.load(this, R.raw.e6, 1));
		sfxList.put(40, sfxPool.load(this, R.raw.f6, 1));
		sfxList.put(41, sfxPool.load(this, R.raw.g6, 1));
		sfxList.put(42, sfxPool.load(this, R.raw.a6, 1));
		sfxList.put(43, sfxPool.load(this, R.raw.b6, 1));

		sfxList.put(78, sfxPool.load(this, R.raw.c6m, 1));
		sfxList.put(79, sfxPool.load(this, R.raw.d6m, 1));
		sfxList.put(80, sfxPool.load(this, R.raw.f6m, 1));
		sfxList.put(81, sfxPool.load(this, R.raw.g6m, 1));
		sfxList.put(82, sfxPool.load(this, R.raw.a6m, 1));
		
		sfxList.put(110, sfxPool.load(this, R.raw.alert, 1));

	}

	public void playSFX(int sound) {
		// ѵ���׶�		
		if(trainStage && GameConfig.trainIndex<=3)
		{
			// ��¼����
			GameConfig.trainMusics[GameConfig.trainIndex-1] += (sound+"&");	
			long currentTime = System.currentTimeMillis();
			// ��¼���ΰ�����ʱ����
			if(GameConfig.trainInputIndex > 0)
			{
				GameConfig.trainTimeIntervals[GameConfig.trainIndex-1][GameConfig.trainInputIndex - 1] =currentTime - GameConfig.lastTime;
			}
			
			// �����ǰ��������һ������Ķ�Ӧ������ͬ����ֱ�ӱ���
			if (GameConfig.trainIndex>1 && GameConfig.trainInputIndex < GameConfig.musicLength) 
			{
				if(datas[GameConfig.trainInputIndex].equals(String.valueOf(sound)) == false)
				{
					Toast.makeText(this, "����������ϴβ�ͬ�����������롣", Toast.LENGTH_SHORT).show();
					GameConfig.trainInputIndex = 0;
				    -- GameConfig.trainIndex;
					return; 
				}
				// ��ǰһ�ε�rhythm�Ƚ�
				if (GameConfig.trainInputIndex+1 == GameConfig.musicLength)
				{
					GameConfig.rhythms[GameConfig.trainIndex-1] = getRhythm(GameConfig.trainTimeIntervals[GameConfig.trainIndex-1],GameConfig.musicLength-1);
					if(!GameConfig.rhythms[GameConfig.trainIndex-1].equals(GameConfig.rhythms[GameConfig.trainIndex-2]))
					{
						// ���������������һ�β�ͬʱ����������
						Toast.makeText(this, "����������ϴβ�ͬ�����������롣", Toast.LENGTH_SHORT).show();
						GameConfig.trainInputIndex = 0;
					    -- GameConfig.trainIndex;
					}
					else
					{
						log.recordRhythm(GameConfig.trainIndex-1, GameConfig.rhythms[GameConfig.trainIndex-1], Logger.RHYTHM);
					}
				}
				
				// �������ĵ����γɹ������¼���������С�
				if (GameConfig.trainIndex == 3 && GameConfig.trainInputIndex+1 == datas.length)
				{
					String temp  = GameConfig.trainMusics[GameConfig.trainIndex-1].substring(0,GameConfig.trainMusics[GameConfig.trainIndex-1].length() - 1);
					GameConfig.finalSequence =temp.split("&");
					Toast.makeText(this, "��ϲ�����������óɹ������Կ�ʼ���ԡ�", Toast.LENGTH_SHORT).show();
				}
			}
			GameConfig.lastTime = currentTime;
			GameConfig.trainInputIndex ++;
		}
		
		// ���׶�
		if(testStage && GameConfig.testInputIndex<GameConfig.trainInputIndex)
		{
			GameConfig.testInputMusic += (sound+"&");
			if(Integer.parseInt(GameConfig.finalSequence[GameConfig.testInputIndex]) != sound)
			{
				//Toast.makeText(this, "��������������������롣", Toast.LENGTH_SHORT).show();
				final String[] items = {"����������������¿�ʼ����"};   
				new AlertDialog.Builder(PianoActivity.this).setTitle("��ʾ").setIcon(R.drawable.header)  
				.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {   
				 public void onClick(DialogInterface dialog, int item) {
				        dialog.cancel();   
				  }   
				}).show();//��ʾ�Ի���   
				
				if(GameConfig.tryTimes ++ >= GameConfig.maxTryTimes){
					GameConfig.tryTimes = 0;
					try {
						float streamVolumeCurrent = audioManager
								.getStreamVolume(AudioManager.STREAM_MUSIC);
						float streamVolumeMax = audioManager
								.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
						float volume = streamVolumeCurrent / streamVolumeMax;
						sfxPool.play(sfxList.get(110), volume, volume, 1, 0, 1f);
					} catch (Exception e) {
					}
				}
				GameConfig.testInputIndex = 0;
				GameConfig.testInputMusic = "";
				return;
			}
			long currentTime = System.currentTimeMillis();
			if(GameConfig.testInputIndex > 0)
			{
				GameConfig.testTimeInterval[GameConfig.testInputIndex - 1] =currentTime - GameConfig.lastTime;
			}
			
			if(GameConfig.testInputIndex+1 == GameConfig.finalSequence.length)
			{
				String testStr = getRhythm(GameConfig.testTimeInterval,GameConfig.testInputIndex);
				if(!testStr.equals(GameConfig.finalMusicRythem))
				{
					Toast.makeText(this, "���������������������;��������ѵ����", Toast.LENGTH_SHORT).show();
					GameConfig.testInputIndex = 0;
					GameConfig.testInputMusic = "";
				}
				else
				{
					Toast.makeText(this, "Pass!", Toast.LENGTH_SHORT).show();
					GameConfig.passedTest = true;
					GameConfig.tryTimes = 0;
				}
			}
			GameConfig.lastTime = currentTime;
			GameConfig.testInputIndex ++;	
		}		
		try {
			float streamVolumeCurrent = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			float streamVolumeMax = audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float volume = streamVolumeCurrent / streamVolumeMax;
			sfxPool.play(sfxList.get(sound), volume, volume, 1, 0, 1f);
		} catch (Exception e) {
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.exit(0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private String getRhythm(long[] data,int length)
	{
		String musicSequence = "";
		Dbscan d = new Dbscan();
		d.getResult(data,length);
		musicSequence = d.display(data,length);
		return musicSequence;
	}
	
	private void clearData()
	{
		for(int i = 0; i < 3; i ++)
		{
			for(int j = 0; j < 100; j ++)
			{
				GameConfig.trainTimeIntervals[i][j] = 0;
				GameConfig.rhythms[i] = "";
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			// ����ѵ���׶�
			trainStage = true;
			testStage = false;

			if (GameConfig.trainIndex<=2)
			{
				// ��ѵ��3��
				if(GameConfig.musicLength == 0 && GameConfig.trainInputIndex!= 0)
				{
					GameConfig.musicLength = GameConfig.trainInputIndex;
				}
				GameConfig.trainInputIndex = 0;
				GameConfig.trainMusics[GameConfig.trainIndex] = "";
				Toast.makeText(this, "��ʼѵ����"+(GameConfig.trainIndex + 1)+"��", Toast.LENGTH_SHORT).show();
				GameConfig.trainIndex ++;
				
				// �ӵڶ���ѵ����ʼ��Ԥ���ÿ������İ����Ƿ���ȷ���������ֱ����ʾ������󣬲��ý��к�����ʱ�������.
				if(GameConfig.trainIndex > 1)
				{
					// д���1��2�ε�ѵ������
					log.recordTimeInterval(GameConfig.trainIndex-2, GameConfig.trainTimeIntervals[GameConfig.trainIndex-2], Logger.TRAIN);
					
					// �жϴ���������Ƿ�Ϊ�գ�Ϊ�˳���׳�ԡ�
					if(GameConfig.trainMusics[GameConfig.trainIndex-2].length() == 0)
					{
						Toast.makeText(this, "����һ�����������Ϊ�գ����������롣", Toast.LENGTH_SHORT).show();
						GameConfig.trainIndex --;
						GameConfig.trainInputIndex = 0;
						return;
					}
					// ��������һ�ΰ������С�
					String temp  = GameConfig.trainMusics[GameConfig.trainIndex-2].substring(0,GameConfig.trainMusics[GameConfig.trainIndex-2].length() - 1);
					datas =temp.split("&");
					if(GameConfig.trainIndex-2 == 0)
					{
						GameConfig.rhythms[0] = getRhythm(GameConfig.trainTimeIntervals[GameConfig.trainIndex-2],GameConfig.musicLength-1);
						log.recordRhythm(0, GameConfig.rhythms[0], Logger.RHYTHM);
						GameConfig.finalMusicRythem = GameConfig.rhythms[0];
					}
				}
			}
			else 
			{
				Toast.makeText(this, "���¿�ʼ3��ѵ�����뵥��<ѵ��>��ť��ʼѵ����", Toast.LENGTH_SHORT).show();
				GameConfig.trainIndex=0;
				for(int i = 0; i < GameConfig.trainMusics.length; i ++)
				{
					GameConfig.trainMusics[i] = "";
				}
				if(datas != null)
				{
					for(int i = 0; i < datas.length; i ++)
					{
						datas[i]="";
					}
				}
				trainStage = false;
			}	
			break;

		case R.id.right:
			
			// ��ʼѵ���׶�
			// ����д��ѵ���׶����һ������
			trainStage=false;
			testStage=true;
			if (GameConfig.trainIndex == 3 && datas != null && GameConfig.trainInputIndex == datas.length)
			{
				log.recordTimeInterval(GameConfig.trainIndex-1, GameConfig.trainTimeIntervals[GameConfig.trainIndex-1], Logger.TRAIN);
				clearData();
				
				
				
					GameConfig.testInputIndex=0;// ���׶��������м���
					GameConfig.testInputMusic = ""; // ����İ�������
					GameConfig.musicLength = 0;
					GameConfig.trainIndex = 0;
					Toast.makeText(this, "������ģʽ", Toast.LENGTH_SHORT).show();
					GameConfig.tryTimes = 0;
				
			}
			else{
				
			if(GameConfig.passedTest)
			{
				final String[] items = {"��������", "������������","ȡ��"};   
				new AlertDialog.Builder(PianoActivity.this).setTitle("ѡ�����").setIcon(R.drawable.header)  
				.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {   
				 public void onClick(DialogInterface dialog, int item) {   
				       if(item == 0){
				    	   trainStage=false;
						   testStage=true;
				    	   
				    	   GameConfig.testInputIndex=0;// ���׶��������м���
				    	   GameConfig.testInputMusic = ""; // ����İ�������
				    	   GameConfig.musicLength = 0;
				    	   GameConfig.trainIndex = 0;
				    	   GameConfig.passedTest = false;
				       }
				       else if(item == 1){
				    	   GameConfig.trainIndex=0;
							GameConfig.trainInputIndex=0;
							trainStage = false;
							testStage = false;
							GameConfig.passedTest = false;
							GameConfig.tryTimes = 0;
				       }
				       else{
				    	   trainStage = false;
							testStage = false; 
				       }
				        dialog.cancel();   
				  }   
				}).show();//��ʾ�Ի���   
			}
			else
			{
				GameConfig.trainIndex=0;
				GameConfig.trainInputIndex=0;
				trainStage = false;
				testStage = false;
				Toast.makeText(this, "���������������", Toast.LENGTH_SHORT).show();
			}
			}
			break;
			
		case R.id.record:
			// ��ת����¼ҳ��
					
			
			trainStage = false;
			testStage = false;
			Intent intent = new Intent(PianoActivity.this,LogActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(intent);
			break;
		}
	}
}