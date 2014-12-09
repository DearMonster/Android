package com.cz.rhythmcode;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import com.cz.monitor.Logger;
import com.cz.monitor.MyAdapter;

public class LogActivity extends Activity {

	private Logger log;
    private ListView listView;
    private Button button;
    private HorizontalScrollView layout;
    private TextView rhythm1;
    private TextView rhythm2;
    private TextView rhythm3;
    private ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
    public static int itemNum = 9;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		rhythm1 = (TextView)findViewById(R.id.thythm1);
		rhythm2 = (TextView)findViewById(R.id.thythm2);
		rhythm3 = (TextView)findViewById(R.id.thythm3);
        listView = (ListView) findViewById(R.id.listview);
        button = (Button) findViewById(R.id.button);
        layout = (HorizontalScrollView) findViewById(R.id.layout);
        ArrayList<String> list = new ArrayList<String>();
        list.add("");
        list.add("间隔1");
        list.add("间隔2");
        list.add("间隔3");
        list.add("间隔4");
        list.add("间隔5");
        list.add("间隔6");
        list.add("间隔7");
        list.add("间隔8");
        list.add("间隔9");
        lists.add(list);
		
		log = new Logger(this);
		// 填充数据：
        String[]datas = log.readAllInterval(3, Logger.TRAIN);
        int num = datas.length;
        for(int i = 0; i < num; i ++)
        {
        	// 解析出上一次按键序列。
			String temp  = datas[i].substring(0,datas[i].length() - 1);
			String[] str =temp.split("&");
			ArrayList<String> data = new ArrayList<String>();
			data.add("" + (i+1));
			for(int j = 0; j < itemNum; j ++)
			{
				data.add(str[j]);
			}
			lists.add(data);
        }
        
        MyAdapter adapter = new MyAdapter(LogActivity.this, lists);
        listView.setAdapter(adapter);
        layout.setVisibility(View.VISIBLE);
        
        String[] rhythms = log.readAllRhythm(3, Logger.RHYTHM);
        rhythm1.setText(rhythms[0]);
        rhythm2.setText(rhythms[1]);
        rhythm3.setText(rhythms[2]);
        
        button.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(LogActivity.this,PianoActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				startActivity(intent);
			}
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}

}
