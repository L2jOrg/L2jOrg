package org.l2j.gameserver.model.instances;

import java.util.HashMap;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.commons.geometry.Circle;
import org.l2j.commons.geometry.Shape;
import org.l2j.gameserver.geodata.GeoControl;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.templates.npc.NpcTemplate;

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