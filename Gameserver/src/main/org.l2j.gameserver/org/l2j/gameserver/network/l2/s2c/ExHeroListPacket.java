package org.l2j.gameserver.network.l2.s2c;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.templates.StatsSet;

/**
 * Format: (ch) d [SdSdSdd]
 * d: size
 * [
 * S: hero name
 * d: hero class ID
 * S: hero clan name
 * d: hero clan crest id
 * S: hero ally name
 * d: hero Ally id
 * d: count
 * ]
 */
public class ExHeroListPacket extends L2GameServerPacket
{
	private Collection<StatsSet> _heroList;

	public ExHeroListPacket()
	{
		_heroList = Hero.getInstance().getHeroes().values();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_heroList.size());
		for(StatsSet hero : _heroList)
		{
			writeS(hero.getString(Hero.CHAR_NAME));
			writeD(hero.getInteger(Hero.CLASS_ID));
			writeS(hero.getString(Hero.CLAN_NAME, StringUtils.EMPTY));
			writeD(hero.getInteger(Hero.CLAN_CREST, 0));
			writeS(hero.getString(Hero.ALLY_NAME, StringUtils.EMPTY));
			writeD(hero.getInteger(Hero.ALLY_CREST, 0));
			writeD(hero.getInteger(Hero.COUNT));
			writeD(0);
		}
	}
}