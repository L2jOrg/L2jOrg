package org.l2j.commons.geometry;

public interface Shape
{
	public boolean isInside(int x, int y);

	public boolean isInside(int x, int y, int z);

	public boolean isOnPerimeter(int x, int y);

	public boolean isOnPerimeter(int x, int y, int z);

	public int getXmax();

	public int getXmin();

	public int getYmax();

	public int getYmin();

	public int getZmax();

	public int getZmin();

	public Point2D getCenter();

	public Point2D getNearestPoint(int x, int y);

	public int getRadius();
}
