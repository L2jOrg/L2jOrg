package org.l2j.gameserver.engine.geoscripts;

import gnu.trove.map.TIntObjectMap;
import org.l2j.commons.geometry.Shape;
import org.l2j.gameserver.engine.geoscripts.GeoEngine.CeilGeoControlType;

import org.napile.primitive.pair.ByteObjectPair;

public interface GeoControl
{
	Shape getGeoShape();

	TIntObjectMap<ByteObjectPair<CeilGeoControlType>> getGeoAround();

	void setGeoAround(TIntObjectMap<ByteObjectPair<CeilGeoControlType>> value);

	int getGeoControlIndex();

	boolean isHollowGeo();
}