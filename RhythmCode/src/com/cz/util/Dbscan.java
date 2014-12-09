package com.cz.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Dbscan {
	private List<Point> pointsList=new ArrayList<Point>();//存储所有点的集合
	private List<List<Point>> resultList=new ArrayList<List<Point>>();//存储DBSCAN算法返回的结果集
	private int e=80;//e半径
	private int minp=2;//密度阈值
	/**
	　　* 提取文本中的的所有点并存储在pointsList中
	　　* @throws IOException
	　　*/
	public String display(long[] data,int len)
	{
		// 迭代结束，则将输入的times时间间隔转换为聚簇标号的序列。
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
	//找出所有可以直达的聚类
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
	//对所有可以直达的聚类进行合并，即找出间接可达的点并进行合并
	public List<List<Point>> getResult(long[] data,int length){
		applyDbscan(data,length);//找到所有直达的聚类
		
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
//	　* 程序主函数
//	　* @param args
//	　*/
//	public static void main(String[] args) {
//		long[] testData0 = {374,415,1510,800,432,346};//,890,689,308,821,1146,518,1542,337,1318,385,321,324};
//		getResult(testData0);
//		String temp = display(testData0);
//		System.out.println(temp);
//	}
}