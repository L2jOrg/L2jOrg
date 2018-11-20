package org.l2j.commons.geometry;

public abstract class AbstractShape implements Shape
{
	protected final Point3D max = new Point3D();
	protected final Point3D min = new Point3D();

	@Override
	public boolean isInside(int x, int y, int z)
	{	
		return (min.z <= z) && (max.z >= z) && (isInside(x, y));
	}

	@Override
	public boolean isOnPerimeter(int x, int y, int z)
	{
		return (min.z <= z) && (max.z >= z) && (isOnPerimeter(x, y));
	}

	@Override
	public int getXmax()
	{
		return max.x;
	}

	@Override
	public int getXmin()
	{
		return min.x;
	}

	@Override
	public int getYmax()
	{
		return max.y;
	}

	@Override
	public int getYmin()
	{
		return min.y;
	}

	public AbstractShape setZmax(int z)
	{
		max.z = z;
		return this;
	}

	public AbstractShape setZmin(int z)
	{
		min.z = z;
		return this;
	}

	@Override
	public int getZmax()
	{
		return max.z;
	}

	@Override
	public int getZmin()
	{
		return min.z;
	}
}
