package com.cz.util;

public class Point {
	private long x;
	private long y;
	private boolean isClassed;
	public boolean isClassed() 
	{
		return isClassed;
	}
	public void setClassed(boolean isClassed) 
	{
		this.isClassed = isClassed;
	}
	public long getX() 
	{
		return x;
	}
	public void setX(long x) 
	{
		this.x = x;
	}
	public long getY() 
	{
		return y;
	}
	public void setY(long y) 
	{
		this.y = y;
	}
	public Point()
	{
		x=0;
		y=0;
	}
	public Point(long x,long y)
	{
		this.x=x;
		this.y=y;
		this.isClassed = false;
	}
	public Point(String str)
	{
		String[] p=str.split(",");
		this.x=Integer.parseInt(p[0]);
		this.y=Integer.parseInt(p[1]);
	}
	public String print()
	{
		return "<"+this.x+","+this.y+">";
	}
}
