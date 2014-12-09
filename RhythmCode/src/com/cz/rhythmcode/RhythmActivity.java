package com.cz.rhythmcode;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;

public class RhythmActivity extends Activity {
	
	/**
	 * RhythmActivity:
	 * ∏÷«Ÿ√‹¬Î÷˜ΩÁ√Ê
	 */
	@SuppressWarnings("deprecation")
	private GestureDetector gusterDecter = new GestureDetector(new MyGusterDetecterListener(this));
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rhythm);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rhythm, menu);
		return true;
	}

	private class MyGusterDetecterListener extends SimpleOnGestureListener
	{

		private Context context;
		public MyGusterDetecterListener(Context context)
		{
			this.context = context;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			Intent intent = new Intent(RhythmActivity.this,PianoActivity.class);
			context.startActivity(intent);
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gusterDecter.onTouchEvent(event);
	}
	
}
