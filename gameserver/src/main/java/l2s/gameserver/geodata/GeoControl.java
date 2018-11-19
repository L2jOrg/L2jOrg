package l2s.gameserver.geodata;

import java.util.HashMap;

import l2s.commons.geometry.Shape;
import l2s.gameserver.model.entity.Reflection;

public interface GeoControl
{
	public abstract Shape getGeoShape();

	public abstract HashMap<Long, Byte> getGeoAround();

	public abstract void setGeoAround(HashMap<Long, Byte> value);

	public abstract int getGeoIndex();
}