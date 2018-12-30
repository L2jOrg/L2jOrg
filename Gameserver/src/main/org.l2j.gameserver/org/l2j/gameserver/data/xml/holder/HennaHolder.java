package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.HennaTemplate;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date  9:03/06.12.2010
 */
public final class HennaHolder extends AbstractHolder
{
	private static final HennaHolder _instance = new HennaHolder();

	private HashIntObjectMap<HennaTemplate> _hennas = new HashIntObjectMap<HennaTemplate>();

	public static HennaHolder getInstance()
	{
		return _instance;
	}

	public void addHenna(HennaTemplate h)
	{
		_hennas.put(h.getSymbolId(), h);
	}

	public HennaTemplate getHenna(int symbolId)
	{
		return _hennas.get(symbolId);
	}

	public List<HennaTemplate> generateList(Player player)
	{
		return new ArrayList<>(_hennas.values());
	}

	@Override
	public int size()
	{
		return _hennas.size();
	}

	@Override
	public void clear()
	{
		_hennas.clear();
	}
}