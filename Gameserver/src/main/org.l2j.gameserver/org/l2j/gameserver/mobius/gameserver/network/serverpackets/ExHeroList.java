/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.entity.Hero;
import org.l2j.gameserver.mobius.gameserver.model.olympiad.Olympiad;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Map;

/**
 * @author -Wooden-, KenM, godson
 */
public class ExHeroList implements IClientOutgoingPacket
{
	private final Map<Integer, StatsSet> _heroList;
	
	public ExHeroList()
	{
		_heroList = Hero.getInstance().getHeroes();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_HERO_LIST.writeId(packet);
		
		packet.writeD(_heroList.size());
		for (Integer heroId : _heroList.keySet())
		{
			final StatsSet hero = _heroList.get(heroId);
			packet.writeS(hero.getString(Olympiad.CHAR_NAME));
			packet.writeD(hero.getInt(Olympiad.CLASS_ID));
			packet.writeS(hero.getString(Hero.CLAN_NAME, ""));
			packet.writeD(hero.getInt(Hero.CLAN_CREST, 0));
			packet.writeS(hero.getString(Hero.ALLY_NAME, ""));
			packet.writeD(hero.getInt(Hero.ALLY_CREST, 0));
			packet.writeD(hero.getInt(Hero.COUNT));
			packet.writeD(0x00);
		}
		return true;
	}
}
