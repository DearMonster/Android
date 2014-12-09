package com.cz.monitor;

import java.util.ArrayList;

import com.cz.rhythmcode.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<ArrayList<String>> lists;
	
	public MyAdapter(Context context, ArrayList<ArrayList<String>> lists) {
		super();
		this.lists = lists;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int index, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ArrayList<String> list = lists.get(index);
		if(view == null){
			view = inflater.inflate(R.layout.list_item, null);
		}
		view.setBackgroundColor(Color.WHITE);
		TextView textView1 = (TextView) view.findViewById(R.id.text1);
		TextView textView2 = (TextView) view.findViewById(R.id.text2);
		TextView textView3 = (TextView) view.findViewById(R.id.text3);
		TextView textView4 = (TextView) view.findViewById(R.id.text4);
		TextView textView5 = (TextView) view.findViewById(R.id.text5);
		TextView textView6 = (TextView) view.findViewById(R.id.text6);
		TextView textView7 = (TextView) view.findViewById(R.id.text7);
		TextView textView8 = (TextView) view.findViewById(R.id.text8);
		TextView textView9 = (TextView) view.findViewById(R.id.text9);
		TextView textView10 = (TextView) view.findViewById(R.id.text10);
		
		textView1.setTextColor(Color.BLACK);
		textView2.setTextColor(Color.BLACK);
		textView3.setTextColor(Color.BLACK);
		textView4.setTextColor(Color.BLACK);
		textView5.setTextColor(Color.BLACK);
		textView6.setTextColor(Color.BLACK);
		textView7.setTextColor(Color.BLACK);
		textView8.setTextColor(Color.BLACK);
		textView9.setTextColor(Color.BLACK);
		textView10.setTextColor(Color.BLACK);
		
		textView1.setText(list.get(0));
		textView2.setText(list.get(1));
		textView3.setText(list.get(2));
		textView4.setText(list.get(3));
		textView5.setText(list.get(4));
		textView6.setText(list.get(5));
		textView7.setText(list.get(6));
		textView8.setText(list.get(7));
		textView9.setText(list.get(8));
		textView10.setText(list.get(9));
		
		if(index == 0){
			view.setBackgroundResource(R.color.head_bg);
			textView1.setTextColor(Color.WHITE);
			textView2.setTextColor(Color.WHITE);
			textView3.setTextColor(Color.WHITE);
			textView4.setTextColor(Color.WHITE);
			textView5.setTextColor(Color.WHITE);
			textView6.setTextColor(Color.WHITE);
			textView7.setTextColor(Color.WHITE);
			textView8.setTextColor(Color.WHITE);
			textView9.setTextColor(Color.WHITE);
			textView10.setTextColor(Color.WHITE);
		}else{
			if(index%2 != 0){
				view.setBackgroundColor(Color.argb(250 ,  255 ,  255 ,  255 )); 
			}else{
				view.setBackgroundColor(Color.argb(250 ,  224 ,  243 ,  250 ));    
			}
		}
		
		return view;
	}

}
