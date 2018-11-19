package l2s.gameserver.model.instances;

import java.util.HashMap;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.geometry.Circle;
import l2s.commons.geometry.Shape;
import l2s.gameserver.geodata.GeoControl;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.templates.npc.NpcTemplate;

public class SymbolWithGeodataInstance extends SymbolInstance implements GeoControl
{
	private final int _geoRadius;
	private final int _geoHeight;
	private HashMap<Long, Byte> _geoAround;
	private Shape _shape;

	public SymbolWithGeodataInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		_geoRadius = getParameter("geodata_radius", (int) getCollisionRadius());
		_geoHeight = getParameter("geodata_height", (int) getCollisionHeight());
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		Circle circle = new Circle(getLoc().x, getLoc().y, _geoRadius);
		circle.setZmin(getLoc().z - 50);
		circle.setZmax(getLoc().z + _geoHeight);

		_shape = circle;

		GeoEngine.applyGeoControl(this);
	}

	@Override
	protected void onDespawn()
	{
		super.onDespawn();
		GeoEngine.returnGeoControl(this);
	}

	@Override
	public Shape getGeoShape()
	{
		return _shape;
	}

	@Override
	public HashMap<Long, Byte> getGeoAround()
	{
		return _geoAround;
	}

	@Override
	public void setGeoAround(HashMap<Long, Byte> value)
	{
		_geoAround = value;
	}
}