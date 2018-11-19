package l2s.gameserver.templates.npc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bonux
 **/
public class WalkerRoute
{
	private final int _id;
	private final WalkerRouteType _type;
	private final List<WalkerRoutePoint> _points = new ArrayList<WalkerRoutePoint>();

	public WalkerRoute(int id, WalkerRouteType type)
	{
		_id = id;
		_type = type;
	}

	public int getId()
	{
		return _id;
	}

	public WalkerRouteType getType()
	{
		return _type;
	}

	public void addPoint(WalkerRoutePoint route)
	{
		_points.add(route);
	}

	public WalkerRoutePoint getPoint(int id)
	{
		return _points.get(id);
	}

	public int size()
	{
		return _points.size();
	}

	public boolean isValid()
	{
		if(_type == WalkerRouteType.DELETE || _type == WalkerRouteType.FINISH)
			return size() > 0;

		return size() > 1;
	}
}