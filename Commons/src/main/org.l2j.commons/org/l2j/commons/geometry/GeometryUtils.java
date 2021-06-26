package org.l2j.commons.geometry;

public class GeometryUtils
{
	private GeometryUtils()
	{
		
	}
	
	public static boolean checkIfLinesIntersects(Point2D a, Point2D b, Point2D c, Point2D d)
	{
		return checkIfLinesIntersects(a, b, c, d, null);
	}

	/**
	 * public domain function by Darel Rex Finley, 2006<br>
	 * <br>
	 * Determines the intersection point of the line defined by points A and B with the<br>
	 * line defined by points C and D.
	 * 
	 * @return true if the intersection point was found<br>
	 *         false if there is no determinable intersection point<br>
	 *         r intersection point
	 */
	public static boolean checkIfLinesIntersects(Point2D a, Point2D b, Point2D c, Point2D d, Point2D r)
	{
		double distAB, theCos, theSin, newX, ABpos;

		//  Fail if either line is undefined.
		if(a.x == b.x && a.y == b.y || c.x == d.x && c.y == d.y)
			return false;

		//  (1) Translate the system so that point A is on the origin.
		double Bx = b.x - a.x;
		double By = b.y - a.y;
		double Cx = c.x - a.x;
		double Cy = c.y - a.y;
		double Dx = d.x - a.x;
		double Dy = d.y - a.y;

		//  Discover the length of segment A-B.
		distAB = Math.sqrt(Bx * Bx + By * By);

		//  (2) Rotate the system so that point B is on the positive X axis.
		theCos = Bx / distAB;
		theSin = By / distAB;
		newX = Cx * theCos + Cy * theSin;
		Cy = (int) (Cy * theCos - Cx * theSin);
		Cx = newX;
		newX = Dx * theCos + Dy * theSin;
		Dy = (int) (Dy * theCos - Dx * theSin);
		Dx = newX;

		//  Fail if the lines are parallel.
		if(Cy == Dy)
			return false;

		//  (3) Discover the position of the intersection point along line A-B.
		ABpos = Dx + (Cx - Dx) * Dy / (Dy - Cy);

		//  (4) Apply the discovered position to line A-B in the original coordinate system.
		if(r != null)
		{
			r.x = (int) (a.x + ABpos * theCos);
			r.y = (int) (a.y + ABpos * theSin);
		}

		//  Success.
		return true;
	}

	public static boolean checkIfLineSegementsIntersects(Point2D a, Point2D b, Point2D c, Point2D d)
	{
		return checkIfLineSegementsIntersects(a, b, c, d, null);
	}

	/**
	 * public domain function by Darel Rex Finley, 2006<br>
	 * <br>
	 * Determines the intersection point of the line segment defined by points A and B<br>
	 * with the line segment defined by points C and D.
	 * 
	 * @return true if the intersection point was found<br>
	 *         false if there is no determinable intersection point<br>
	 *         r intersection point
	 */
	public static boolean checkIfLineSegementsIntersects(Point2D a, Point2D b, Point2D c, Point2D d, Point2D r)
	{
		double distAB, theCos, theSin, newX, ABpos;

		//  Fail if either line is undefined.
		if(a.x == b.x && a.y == b.y || c.x == d.x && c.y == d.y)
			return false;

		//  Fail if the segments share an end-point.
		if(a.x == c.x && a.y == c.y || b.x == c.x && b.y == c.y || a.x == d.x && a.y == d.y || b.x == d.x && b.y == d.y)
			return false;

		//  (1) Translate the system so that point A is on the origin.
		double Bx = b.x - a.x;
		double By = b.y - a.y;
		double Cx = c.x - a.x;
		double Cy = c.y - a.y;
		double Dx = d.x - a.x;
		double Dy = d.y - a.y;

		//  Discover the length of segment A-B.
		distAB = Math.sqrt(Bx * Bx + By * By);

		//  (2) Rotate the system so that point B is on the positive X axis.
		theCos = Bx / distAB;
		theSin = By / distAB;
		newX = Cx * theCos + Cy * theSin;
		Cy = (int) (Cy * theCos - Cx * theSin);
		Cx = newX;
		newX = Dx * theCos + Dy * theSin;
		Dy = (int) (Dy * theCos - Dx * theSin);
		Dx = newX;

		//  Fail if segment C-D doesn't cross line A-B.
		if(Cy < 0. && Dy < 0. || Cy >= 0. && Dy >= 0.)
			return false;

		//  (3) Discover the position of the intersection point along line A-B.
		ABpos = Dx + (Cx - Dx) * Dy / (Dy - Cy);

		//  Fail if segment C-D crosses line A-B outside of segment A-B.
		if(ABpos < 0. || ABpos > distAB)
			return false;

		//  (4) Apply the discovered position to line A-B in the original coordinate system.
		if(r != null)
		{
			r.x = (int) (a.x + ABpos * theCos);
			r.y = (int) (a.y + ABpos * theSin);
		}

		//  Success.
		return true;
	}

	public static int calculateDistance(Point2D a, Point2D b)
	{
		return calculateDistance(a.x, a.y, b.x, b.y);
	}

	public static int calculateDistance(Point3D a, Point3D b, boolean includeZAxis)
	{
		return calculateDistance(a.x, a.y, a.z, b.x, b.y, b.z, includeZAxis);
	}

	public static int calculateDistance(int x1, int y1, int x2, int y2)
	{
		return calculateDistance(x1, y1, 0, x2, y2, 0, false);
	}

	public static int calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2, boolean includeZAxis)
	{
		long dx = x1 - x2;
		long dy = y1 - y2;

		if(includeZAxis)
		{
			long dz = z1 - z2;
			return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
		}
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	public static double calculateAngleFrom(Point2D a, Point2D b)
	{
		return calculateAngleFrom(a.x, a.y, b.x, b.y);
	}

	public static double calculateAngleFrom(int x1, int y1, int x2, int y2)
	{
		double angleTarget = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
		if(angleTarget < 0)
			angleTarget = 360 + angleTarget;
		return angleTarget;
	}

	// !add: Обрезать от прямой AB кусок, получаем точку обреза
	// (A)--<OFFSET>--(RESULT)------(B)
	// add: Добавить прямой AB отрезок длиной 'offset' после точки A
	public static Point2D applyOffset(Point2D a, Point2D b, int offset, boolean add)
	{
		Point2D result = new Point2D();
		if(offset <= 0)
		{
			result.x = a.x;
			result.y = a.y;
			return result;
		}

		long dx = a.x - b.x;
		long dy = a.y - b.y;

		double distance = Math.sqrt(dx * dx + dy * dy);

		if(!add)
		{
			if(distance <= offset)
			{
				result.x = b.x;
				result.y = b.y;
				return result;
			}
		}
		else
			offset += distance;

		if(distance >= 1)
		{
			double cut = offset / distance;
			result.x = a.x - (int) (dx * cut + 0.5);
			result.y = a.y - (int) (dy * cut + 0.5);
		}
		return result;
	}

	public static Point2D applyOffset(int x1, int y1, int x2, int y2, int offset, boolean add)
	{
		return applyOffset(new Point2D(x1, y1), new Point2D(x2, y2), offset, add);
	}

	// Проверяем находится ли точка на прямой.
	public static boolean isOnLine(Point2D a, Point2D b, int x, int y)
	{
		return (x - a.x) * (b.y - a.y) - (b.x - a.x) * (y - a.y) == 0;
	}

	// Определяем координаты центра прямой.
	public static Point2D getLineCenter(Point2D a, Point2D b)
	{
		return getLineCenter(a.x, a.y, b.x, b.y);
	}

	// Определяем координаты центра прямой.
	public static Point2D getLineCenter(int x1, int y1, int x2, int y2)
	{
		return new Point2D((x1 + x2) / 2, (y1 + y2) / 2);
	}

	// Определяем ближайшую точку на окружности круга к указанной точке.
	// Формула самоизобретение, если фигня, переписать на правильную.
	public static Point2D getNearestPointOnCircle(Point2D center, int r, int x, int y)
	{
		return applyOffset(center, new Point2D(x, y), r, false);
	}

	// Определяем ближайшую точку на прямой к указанной точке.
	// Формула самоизобретение, если фигня, переписать на правильную.
	public static Point2D getNearestPointOnLine(Point2D p1, Point2D p2, int x, int y)
	{
		Point2D nearestPoint = new Point2D();
		int r1 = calculateDistance(p1.x, p1.y, x, y);
		Point2D np1 = getNearestPointOnCircle(p1, r1, p2.x, p2.y);
		int r2 = calculateDistance(p2.x, p2.y, x, y);
		Point2D np2 = getNearestPointOnCircle(p2, r2, p1.x, p1.y);
		return getLineCenter(np1, np2);
	}

	// Определяем ближайшую точку на периметре полигона к указанной точке.
	public static Point2D getNearestPointOnPolygon(Point2D[] points, int x, int y)
	{
		Point2D nearestPoint = new Point2D();
		if(points.length == 0)
		{
			nearestPoint = null;
		}
		else if(points.length == 1)
		{
			nearestPoint.x = points[0].x;
			nearestPoint.y = points[0].y;
		}
		else
		{
			for(int i = 1; i <= points.length; i++)
			{
				Point2D p1 = points[i - 1];
				Point2D p2 = i == points.length ? points[0] : points[i];
				Point2D n = getNearestPointOnLine(p1, p2, x, y);
				if(calculateDistance(n.x, n.y, x, y) < calculateDistance(nearestPoint.x, nearestPoint.y, x, y))
					nearestPoint = n;
			}
		}
		return nearestPoint;
	}

	// Проверяем находится ли точка на периметре полигона.
	public static boolean isOnPolygonPerimeter(Point2D[] points, int x, int y)
	{
		if(points.length == 0)
			return false;

		if(points.length == 1)
			return points[0].x == x && points[1].y == y;

		for(int i = 1; i <= points.length; i++)
		{
			Point2D p1 = points[i - 1];
			Point2D p2 = i == points.length ? points[0] : points[i];
			if(isOnLine(p1, p2, x, y))
				return true;
		}
		return false;
	}
}