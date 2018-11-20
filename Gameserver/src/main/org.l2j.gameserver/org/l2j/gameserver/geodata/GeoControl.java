package org.l2j.gameserver.geodata;

import java.util.HashMap;

import org.l2j.commons.geometry.Shape;
import org.l2j.gameserver.model.entity.Reflection;

public interface GeoControl
{
	public abstract Shape getGeoShape();

	public abstract HashMap<Long, Byte> getGeoAround();

	public abstract void setGeoAround(HashMap<Long, Byte> value);

	public abstract int getGeoIndex();
}