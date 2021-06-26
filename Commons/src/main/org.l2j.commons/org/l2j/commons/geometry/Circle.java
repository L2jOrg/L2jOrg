package org.l2j.commons.geometry;

public class Circle extends AbstractShape
{
	protected final Point2D c;
	protected final int r;

	public Circle(Point2D center, int radius)
	{
		c = center;
		r = radius;
		min.x = (c.x - r);
		max.x = (c.x + r);
		min.y = (c.y - r);
		max.y = (c.y + r);
	}

	public Circle(int x, int y, int radius)
	{
		this(new Point2D(x, y), radius);
	}

	@Override
	public Circle setZmax(int z)
	{
		max.z = z;
		return this;
	}

	@Override
	public Circle setZmin(int z)
	{
		min.z = z;
		return this;
	}

	@Override
	public boolean isInside(int x, int y)
	{
		return (int) Math.pow(x - c.x, 2) + (int) Math.pow(y - c.y, 2) <= (int) Math.pow(r, 2);
	}

	@Override
	public boolean isOnPerimeter(int x, int y)
	{
		return (int) Math.pow(x - c.x, 2) + (int) Math.pow(y - c.y, 2) == (int) Math.pow(r, 2);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(c).append("{ radius: ").append(r).append("}");
		sb.append("]");
		return sb.toString();
	}

	@Override
	public Point2D getCenter()
	{
		return c;
	}

	@Override
	public Point2D getNearestPoint(int x, int y)
	{
		return GeometryUtils.getNearestPointOnCircle(c, r, x, y);
	}

	@Override
	public int getRadius()
	{
		return r;
	}
}
