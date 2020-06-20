/*
 * Copyright © 2019-2020 L2JOrg
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
package handlers.mission;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.util.MathUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class BossMissionHandler extends AbstractMissionHandler
{
	private final int _amount;
	
	private BossMissionHandler(MissionDataHolder holder)
	{
		super(holder);
		_amount = holder.getRequiredCompletions();
	}
	
	@Override
	public void init()
	{
		Listeners.Monsters().addListener(new ConsumerEventListener(this, EventType.ON_ATTACKABLE_KILL, (Consumer<OnAttackableKill>) this::onAttackableKill, this));
	}

	
	private void onAttackableKill(OnAttackableKill event)
	{
		final Attackable monster = event.getTarget();
		final Player player = event.getAttacker();
		if (monster.isRaid() && (monster.getInstanceId() > 0) && (player != null))
		{
			final Party party = player.getParty();
			if (party != null)
			{
				final CommandChannel channel = party.getCommandChannel();
				final List<Player> members = channel != null ? channel.getMembers() : party.getMembers();
				members.stream().filter(member -> MathUtil.isInsideRadius3D(member, monster, Config.ALT_PARTY_RANGE)).forEach(this::processPlayerProgress);
			}
			else
			{
				processPlayerProgress(player);
			}
		}
	}
	
	private void processPlayerProgress(Player player)
	{
		final MissionPlayerData entry = getPlayerEntry(player, true);
		if (entry.getStatus() == MissionStatus.NOT_AVAILABLE)
		{
			if (entry.increaseProgress() >= _amount)
			{
				entry.setStatus(MissionStatus.AVAILABLE);
			}
			storePlayerEntry(entry);
		}
	}

	public static class Factory implements MissionHandlerFactory {

		@Override
		public AbstractMissionHandler create(MissionDataHolder data) {
			return new BossMissionHandler(data);
		}

		@Override
		public String handlerName() {
			return "boss";
		}
	}
}
