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
	 * 一般情况下，应用程序的View都是在相同的GUI线程中绘制的，该主应用程序线程
	 * 同时处理所有用户交互，且对于View的onDraw()方法，不能够满足其移动到后台线程
	 * ，因为从后台线程修改GUI元素会被显式的禁制。
	 * 
	 * 当需要快速更新View的UI，或者当前渲染代码阻塞GUI线程时间过长时，SurfaceView
	 * 可以很好的解决。SurfaceView封装了一个Surface对象，可以使用后台线程绘制。
	 * 
	 * 创建一个SurfaceView控件，需要创建一个扩展了SurfaceView的类，并实现
	 * SurfaceHolder.Callback。
	 * 
	 * 一个典型的SurfaceView设计模型包括一个由Thread所派生的类，用于接收对当前
	 * SurfaceHolder的引用，并独立更新它。
	 * 
	 * 使用SurfaceView:
	 * (1)继承SurfaceView并实现SurfaceHolder.Callback接口。
	 * (2)重写的方法：
	 * 1）public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){}
     	//在surface的大小发生改变时激发
 		2）public void surfaceCreated(SurfaceHolder holder){}
     	//在创建时激发，一般在这里调用画图的线程。
 		3）public void surfaceDestroyed(SurfaceHolder holder) {}
     	//销毁时激发，一般在这里将画图的线程停止、释放。
     	 * 
     	 * 调用过程：继承SurfaceView并实现SurfaceHolder.Callback接口 
     	 * ----> SurfaceView.getHolder()获得SurfaceHolder对象 
     	 * ---->SurfaceHolder.addCallback(callback)添加回调函数
     	 * ---->SurfaceHolder.lockCanvas()获得Canvas对象并锁定画布
     	 * ----> Canvas绘画
     	 *  ---->SurfaceHolder.unlockCanvasAndPost(Canvas canvas)结束锁定画图，并提交改变，将图形显示。
     	 *  
     	 *  SurfaceHolder：Surface的控制器，用来操控Surface，处理canvas上画的效果、
     	 *  动画、控制表面、大小和像素等。
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
