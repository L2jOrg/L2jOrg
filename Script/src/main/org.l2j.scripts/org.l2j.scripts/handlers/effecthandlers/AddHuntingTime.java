/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.scripts.handlers.effecthandlers;


import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.network.serverpackets.sessionzones.TimedHuntingZoneList;

/**
 * @author Mobius
 */
public class AddHuntingTime extends AbstractEffect
{
	private final int _zoneId;
	private final long _time;
	
	private AddHuntingTime(StatsSet params)
	{
		_zoneId = params.getInt("zoneId", 0);
		_time = params.getLong("time", 3600000);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		final Player player = effected.getActingPlayer();
		if (player == null)
		{
			return;
		}
		
		final long currentTime = System.currentTimeMillis();
		long endTime = player.getHuntingZoneResetTime(_zoneId);
		if ((endTime > currentTime) && (((endTime - currentTime) + _time) >= Config.TIME_LIMITED_MAX_ADDED_TIME))
		{
			player.getInventory().addItem("AddHuntingTime effect refund", item.getId(), 1, player, false);
			player.sendMessage("You cannot exceed the time zone limit.");
			return;
		}
		
		if (player.isInTimedHuntingZone(_zoneId))
		{
			endTime = _time + player.getTimedHuntingZoneRemainingTime();
			player.setHuntingZoneResetTime(_zoneId, currentTime + endTime);
			player.startTimedHuntingZone(_zoneId, endTime);
		}
		else
		{
			if ((endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY) < currentTime)
			{
				endTime = currentTime + Config.TIME_LIMITED_ZONE_INITIAL_TIME;
			}
			else if (endTime < currentTime)
			{
				endTime = currentTime;
			}
			player.setHuntingZoneResetTime(_zoneId,  endTime + _time);
		}
		
		player.sendPacket(new TimedHuntingZoneList(player));
	}

	public static final class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new AddHuntingTime(data);
		}

		@Override
		public String effectName() {
			return "AddHuntingTime";
		}
	}
}
