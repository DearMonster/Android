package com.cz.piano;

import com.cz.util.GameConfig;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PianoSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {
	/**
	 * һ������£�Ӧ�ó����View��������ͬ��GUI�߳��л��Ƶģ�����Ӧ�ó����߳�
	 * ͬʱ���������û��������Ҷ���View��onDraw()���������ܹ��������ƶ�����̨�߳�
	 * ����Ϊ�Ӻ�̨�߳��޸�GUIԪ�ػᱻ��ʽ�Ľ��ơ�
	 * 
	 * ����Ҫ���ٸ���View��UI�����ߵ�ǰ��Ⱦ��������GUI�߳�ʱ�����ʱ��SurfaceView
	 * ���ԺܺõĽ����SurfaceView��װ��һ��Surface���󣬿���ʹ�ú�̨�̻߳��ơ�
	 * 
	 * ����һ��SurfaceView�ؼ�����Ҫ����һ����չ��SurfaceView���࣬��ʵ��
	 * SurfaceHolder.Callback��
	 * 
	 * һ�����͵�SurfaceView���ģ�Ͱ���һ����Thread���������࣬���ڽ��նԵ�ǰ
	 * SurfaceHolder�����ã���������������
	 * 
	 * ʹ��SurfaceView:
	 * (1)�̳�SurfaceView��ʵ��SurfaceHolder.Callback�ӿڡ�
	 * (2)��д�ķ�����
	 * 1��public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){}
     	//��surface�Ĵ�С�����ı�ʱ����
 		2��public void surfaceCreated(SurfaceHolder holder){}
     	//�ڴ���ʱ������һ����������û�ͼ���̡߳�
 		3��public void surfaceDestroyed(SurfaceHolder holder) {}
     	//����ʱ������һ�������ｫ��ͼ���߳�ֹͣ���ͷš�
     	 * 
     	 * ���ù��̣��̳�SurfaceView��ʵ��SurfaceHolder.Callback�ӿ� 
     	 * ----> SurfaceView.getHolder()���SurfaceHolder���� 
     	 * ---->SurfaceHolder.addCallback(callback)��ӻص�����
     	 * ---->SurfaceHolder.lockCanvas()���Canvas������������
     	 * ----> Canvas�滭
     	 *  ---->SurfaceHolder.unlockCanvasAndPost(Canvas canvas)����������ͼ�����ύ�ı䣬��ͼ����ʾ��
     	 *  
     	 *  SurfaceHolder��Surface�Ŀ������������ٿ�Surface������canvas�ϻ���Ч����
     	 *  ���������Ʊ��桢��С�����صȡ�
	 */
	private SurfaceHolder holder;
	private Thread mainThread;

	public PianoSurfaceView(Context context, int maxSpriteCount) {
		super(context);
		holder = getHolder();
		holder.addCallback(this);
		this.requestFocus();
	}

	public PianoSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder = getHolder();
		holder.addCallback(this);
		this.requestFocus();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mainThread = new Thread(this);
		mainThread.setDaemon(true);
		mainThread.start();
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		synchronized (GameConfig.synchObject) {
			for (PianoKey temp : GameConfig.keys) {
				temp.onDraw(canvas);
			}
		}

	}

	public void doDraw() {
		Canvas canvas = holder.lockCanvas();
		onDraw(canvas);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.holder = null;
	}

	@Override
	public void run() {
		try {
			while (true) {
				doDraw();
			}
		} catch (Exception e) {
			e.printStackTrace();
			new Thread(this).start();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		int index = event.getActionIndex();
		float x = event.getX(index);
		float y = event.getY(index);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			for (int i = 0; i < GameConfig.keys.size(); i++) {
				PianoKey key = GameConfig.keys.get(GameConfig.keys.size() - i
						- 1);
				if (key.isInRect(x, y)) {
					key.keyDownAction(x, y);
					break;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < event.getPointerCount(); i++) {
				float tempX = event.getX(i);
				float tempY = event.getY(i);
				int id = event.getPointerId(i);
				for (PianoKey temp : GameConfig.keys) {
					temp.keyMoveAction(tempX, tempY, id);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			for (PianoKey temp : GameConfig.keys) {
				temp.keyUpAction(x, y);
			}
			break;
		}
		return true;
	}
}
