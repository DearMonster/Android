package com.cz.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Dbscan {
	private List<Point> pointsList=new ArrayList<Point>();//�洢���е�ļ���
	private List<List<Point>> resultList=new ArrayList<List<Point>>();//�洢DBSCAN�㷨���صĽ����
	private int e=80;//e�뾶
	private int minp=2;//�ܶ���ֵ
	/**
	����* ��ȡ�ı��еĵ����е㲢�洢��pointsList��
	����* @throws IOException
	����*/
	public String display(long[] data,int len)
	{
		// �����������������timesʱ����ת��Ϊ�۴ر�ŵ����С�
		String musicSequence = "";
		int length = len;
		for(int i = 0; i < length; i++)
		{
			for(int j = 0; j < resultList.size(); j ++)
			{
				List<Point> temp = resultList.get(j);
				for(Iterator<Point> it = temp.iterator();it.hasNext();)
				{
					Point p = it.next();
					if(p.getY() == data[i])
					{
						musicSequence += String.valueOf(j);
						break;
					}
				}	
			}
		}
		return musicSequence;
	}
	//�ҳ����п���ֱ��ľ���
	public void applyDbscan(long[] data,int length)
	{
		
		for(int i = 0; i < length; i ++)
		{
			pointsList.add(new Point(i,data[i]));
		}
		for(Iterator<Point> it = pointsList.iterator();it.hasNext();)
		{
			Point p = it.next();
			if(!p.isClassed())
			{
				List<Point> tmpList = null;
				if((tmpList = Util.isKeyPoint(pointsList, p, e, minp)) != null)
				{
					Util.setListClassed(tmpList);
					resultList.add(tmpList);
				}
			}
		}
	}
	//�����п���ֱ��ľ�����кϲ������ҳ���ӿɴ�ĵ㲢���кϲ�
	public List<List<Point>> getResult(long[] data,int length){
		applyDbscan(data,length);//�ҵ�����ֱ��ľ���
		
		for(int i=0;i<resultList.size();++i)
		{
			for(int j=i+1;j<resultList.size();++j)
			{
				if(Util.mergeList(resultList.get(i), resultList.get(j)))
				{
					List<Point> temp = resultList.get(j);
					resultList.remove(temp);
				}
			}
		}
		
		for(Iterator<Point> it = pointsList.iterator();it.hasNext();)
		{
			Point p = it.next();
			if(!p.isClassed())
			{
				List<Point> temp = new ArrayList<Point>();
				temp.add(p);
				resultList.add(temp);
			}
		}
		return resultList;
	}
//	/**
//	��* ����������
//	��* @param args
//	��*/
//	public static void main(String[] args) {
//		long[] testData0 = {374,415,1510,800,432,346};//,890,689,308,821,1146,518,1542,337,1318,385,321,324};
//		getResult(testData0);
//		String temp = display(testData0);
//		System.out.println(temp);
//	}
}