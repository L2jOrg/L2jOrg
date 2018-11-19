package l2s.gameserver.data.xml.holder;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.HennaTemplate;

/**
 * @author VISTALL
 * @date  9:03/06.12.2010
 */
public final class HennaHolder extends AbstractHolder
{
	private static final HennaHolder _instance = new HennaHolder();

	private TIntObjectHashMap<HennaTemplate> _hennas = new TIntObjectHashMap<HennaTemplate>();

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
		List<HennaTemplate> list = new ArrayList<HennaTemplate>();
		for(TIntObjectIterator<HennaTemplate> iterator = _hennas.iterator(); iterator.hasNext();)
		{
			iterator.advance();
			HennaTemplate h = iterator.value();
			//if(h.isForThisClass(player))
				list.add(h);
		}

		return list;
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