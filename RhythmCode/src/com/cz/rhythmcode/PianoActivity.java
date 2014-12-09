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
	// 播放声音
	private SoundPool sfxPool;
	private AudioManager audioManager;
	private HashMap<Integer, Integer> sfxList;
	// 进度条，加载声音资源
	private ProgressDialog loadingBar;

	private Handler mHandler;
	Button leftButton;// 训练
	Button rightButton; // 测试
	Button logButton;// 显示记录
	boolean trainStage; // 训练阶段
	boolean testStage; // 测试阶段
	
	String[] datas; // 保存按键序列
	Logger log;// 记录

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

		// 根据屏幕调整应用程序长宽
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		GameConfig.SCREEN_WIDTH = windowManager.getDefaultDisplay().getWidth();
		GameConfig.SCREEN_HEIGHT = windowManager.getDefaultDisplay().getHeight();
		
		audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

		// 进度条
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
		// 资源加载线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				initSfx();// 初始化键盘音乐
				initBitmap(); // 初始化按键图片
				synchronized (GameConfig.synchObject) {
					initPianoKeys();
				}
				mHandler.sendEmptyMessage(1);
			}
		}).start();
	}
	private void initBitmap() {
		// 初始化钢琴键的状态位图
		Resources res = getResources();
		GameConfig.blackKey[0] = BitmapFactory.decodeResource(res,R.drawable.black_up);
		GameConfig.blackKey[1] = BitmapFactory.decodeResource(res,R.drawable.black_down);
		GameConfig.whiteKey[0] = BitmapFactory.decodeResource(res,R.drawable.white_up);
		GameConfig.whiteKey[1] = BitmapFactory.decodeResource(res,R.drawable.white_down);
	}
	private void initPianoKeys() {

		// 初始化钢琴键位
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

		// 添加白键
		for (int i = 0; i < GameConfig.WHITE_KEY_COUNT; i++)
		{
			GameConfig.keys.add(new PianoKey(this,i * GameConfig.whiteKeyWidth, 0, GameConfig.whiteKeyWidth,
					GameConfig.whiteKeyHeight, 0, i+37, ""));
		}

		// 添加黑键
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
		// 初始化声音资源
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
		// 训练阶段		
		if(trainStage && GameConfig.trainIndex<=3)
		{
			// 记录按键
			GameConfig.trainMusics[GameConfig.trainIndex-1] += (sound+"&");	
			long currentTime = System.currentTimeMillis();
			// 记录两次按键的时间间隔
			if(GameConfig.trainInputIndex > 0)
			{
				GameConfig.trainTimeIntervals[GameConfig.trainIndex-1][GameConfig.trainInputIndex - 1] =currentTime - GameConfig.lastTime;
			}
			
			// 如果当前按键和上一次输入的对应按键不同，则直接报错。
			if (GameConfig.trainIndex>1 && GameConfig.trainInputIndex < GameConfig.musicLength) 
			{
				if(datas[GameConfig.trainInputIndex].equals(String.valueOf(sound)) == false)
				{
					Toast.makeText(this, "您的输入和上次不同，请重新输入。", Toast.LENGTH_SHORT).show();
					GameConfig.trainInputIndex = 0;
				    -- GameConfig.trainIndex;
					return; 
				}
				// 与前一次的rhythm比较
				if (GameConfig.trainInputIndex+1 == GameConfig.musicLength)
				{
					GameConfig.rhythms[GameConfig.trainIndex-1] = getRhythm(GameConfig.trainTimeIntervals[GameConfig.trainIndex-1],GameConfig.musicLength-1);
					if(!GameConfig.rhythms[GameConfig.trainIndex-1].equals(GameConfig.rhythms[GameConfig.trainIndex-2]))
					{
						// 当输入的序列与上一次不同时，重新输入
						Toast.makeText(this, "您的输入和上次不同，请重新输入。", Toast.LENGTH_SHORT).show();
						GameConfig.trainInputIndex = 0;
					    -- GameConfig.trainIndex;
					}
					else
					{
						log.recordRhythm(GameConfig.trainIndex-1, GameConfig.rhythms[GameConfig.trainIndex-1], Logger.RHYTHM);
					}
				}
				
				// 如果输入的第三次成功，则记录该密码序列。
				if (GameConfig.trainIndex == 3 && GameConfig.trainInputIndex+1 == datas.length)
				{
					String temp  = GameConfig.trainMusics[GameConfig.trainIndex-1].substring(0,GameConfig.trainMusics[GameConfig.trainIndex-1].length() - 1);
					GameConfig.finalSequence =temp.split("&");
					Toast.makeText(this, "恭喜您，密码设置成功，可以开始测试。", Toast.LENGTH_SHORT).show();
				}
			}
			GameConfig.lastTime = currentTime;
			GameConfig.trainInputIndex ++;
		}
		
		// 检测阶段
		if(testStage && GameConfig.testInputIndex<GameConfig.trainInputIndex)
		{
			GameConfig.testInputMusic += (sound+"&");
			if(Integer.parseInt(GameConfig.finalSequence[GameConfig.testInputIndex]) != sound)
			{
				//Toast.makeText(this, "您的输入错误，请重新输入。", Toast.LENGTH_SHORT).show();
				final String[] items = {"您的输入错误，请重新开始测试"};   
				new AlertDialog.Builder(PianoActivity.this).setTitle("提示").setIcon(R.drawable.header)  
				.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {   
				 public void onClick(DialogInterface dialog, int item) {
				        dialog.cancel();   
				  }   
				}).show();//显示对话框   
				
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
					Toast.makeText(this, "您的输入错误，请重新输入;或者重新训练。", Toast.LENGTH_SHORT).show();
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
			// 进入训练阶段
			trainStage = true;
			testStage = false;

			if (GameConfig.trainIndex<=2)
			{
				// 共训练3次
				if(GameConfig.musicLength == 0 && GameConfig.trainInputIndex!= 0)
				{
					GameConfig.musicLength = GameConfig.trainInputIndex;
				}
				GameConfig.trainInputIndex = 0;
				GameConfig.trainMusics[GameConfig.trainIndex] = "";
				Toast.makeText(this, "开始训练第"+(GameConfig.trainIndex + 1)+"次", Toast.LENGTH_SHORT).show();
				GameConfig.trainIndex ++;
				
				// 从第二次训练开始：预检测每次输入的按键是否正确。如错误则直接提示输入错误，不用进行后续的时间间隔检查.
				if(GameConfig.trainIndex > 1)
				{
					// 写入第1、2次的训练数据
					log.recordTimeInterval(GameConfig.trainIndex-2, GameConfig.trainTimeIntervals[GameConfig.trainIndex-2], Logger.TRAIN);
					
					// 判断待检测序列是否为空：为了程序健壮性。
					if(GameConfig.trainMusics[GameConfig.trainIndex-2].length() == 0)
					{
						Toast.makeText(this, "您上一次输入的序列为空，请重新输入。", Toast.LENGTH_SHORT).show();
						GameConfig.trainIndex --;
						GameConfig.trainInputIndex = 0;
						return;
					}
					// 解析出上一次按键序列。
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
				Toast.makeText(this, "重新开始3次训练。请单击<训练>按钮开始训练。", Toast.LENGTH_SHORT).show();
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
			
			// 开始训练阶段
			// 首先写入训练阶段最后一次数据
			trainStage=false;
			testStage=true;
			if (GameConfig.trainIndex == 3 && datas != null && GameConfig.trainInputIndex == datas.length)
			{
				log.recordTimeInterval(GameConfig.trainIndex-1, GameConfig.trainTimeIntervals[GameConfig.trainIndex-1], Logger.TRAIN);
				clearData();
				
				
				
					GameConfig.testInputIndex=0;// 检测阶段输入序列计数
					GameConfig.testInputMusic = ""; // 输入的按键序列
					GameConfig.musicLength = 0;
					GameConfig.trainIndex = 0;
					Toast.makeText(this, "进入检测模式", Toast.LENGTH_SHORT).show();
					GameConfig.tryTimes = 0;
				
			}
			else{
				
			if(GameConfig.passedTest)
			{
				final String[] items = {"测试密码", "重新设置密码","取消"};   
				new AlertDialog.Builder(PianoActivity.this).setTitle("选择操作").setIcon(R.drawable.header)  
				.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {   
				 public void onClick(DialogInterface dialog, int item) {   
				       if(item == 0){
				    	   trainStage=false;
						   testStage=true;
				    	   
				    	   GameConfig.testInputIndex=0;// 检测阶段输入序列计数
				    	   GameConfig.testInputMusic = ""; // 输入的按键序列
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
				}).show();//显示对话框   
			}
			else
			{
				GameConfig.trainIndex=0;
				GameConfig.trainInputIndex=0;
				trainStage = false;
				testStage = false;
				Toast.makeText(this, "请在设置密码后检查", Toast.LENGTH_SHORT).show();
			}
			}
			break;
			
		case R.id.record:
			// 跳转到记录页面
					
			
			trainStage = false;
			testStage = false;
			Intent intent = new Intent(PianoActivity.this,LogActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(intent);
			break;
		}
	}
}