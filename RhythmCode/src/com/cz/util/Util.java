package com.cz.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Util {
	

	public static double getDistance(Point p,Point q)
	{
		double dy=Math.abs(p.getY()-q.getY());
		return dy;
	}
	/**
	　　* 检查给定点是不是核心点
	　　* @param lst 存放点的链表
	　　* @param p 待测试的点
	　　* @param e e半径
	　　* @param minp 密度阈值
	　　* @return 暂时存放访问过的点
	　　*/
	public static List<Point> isKeyPoint(List<Point> lst,Point p,int e,int minp)
	{
		int count=0;
		List<Point> tmpLst=new ArrayList<Point>();
		for(Iterator<Point> it=lst.iterator();it.hasNext();)
		{
			Point q=it.next();
			if(getDistance(p,q)<=e)
			{
				++count;
				if(!tmpLst.contains(q))
				{
					tmpLst.add(q);
				}
			}
		}
		if(count>=minp)
		{
			return tmpLst;
		}
		return null;
	}
	public static void setListClassed(List<Point> lst)
	{
		for(Iterator<Point> it = lst.iterator(); it.hasNext();)
		{
			Point p = it.next();
			if(!p.isClassed())
			{
				p.setClassed(true);
			}
		}
	}
	/**
	* @param a
	* @param b
	* @return a
	*/
	public static boolean mergeList(List<Point> a,List<Point> b)
	{
		boolean merge=false;
		for(int index=0;index<b.size();++index)
		{
			if(a.contains(b.get(index)))
			{
				merge=true;
				break;
			}
		}
		if(merge)
		{
			for(int index=0;index<b.size();++index)
			{
				if(!a.contains(b.get(index)))
				{
					a.add(b.get(index));
				}
			}
		}
		return merge;
	}
}