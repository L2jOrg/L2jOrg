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

import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OlympiadMissionHandler extends AbstractMissionHandler {
	private final boolean winnerOnly;

	private OlympiadMissionHandler(MissionDataHolder holder) {
		super(holder);
		winnerOnly = holder.getParams().getBoolean("win", false);
	}
	
	@Override
	public void init() {
		Listeners.Global().addListener(new ConsumerEventListener(this, EventType.ON_OLYMPIAD_MATCH_RESULT, (Consumer<OnOlympiadMatchResult>) this::onOlympiadMatchResult, this));
	}
	
	private void onOlympiadMatchResult(OnOlympiadMatchResult event) {
		if (nonNull(event.getWinner())) {
			final MissionPlayerData winnerEntry = getPlayerEntry(event.getWinner().getPlayer(), true);
			increaseProgress(winnerEntry, event.getWinner().getPlayer());
		}
		
		if (nonNull(event.getLoser()) && !winnerOnly) {
			final MissionPlayerData loseEntry = getPlayerEntry(event.getLoser().getPlayer(), true);
			increaseProgress(loseEntry, event.getLoser().getPlayer());
		}
	}

	private void increaseProgress(MissionPlayerData entry, Player player) {
		if (entry.getStatus() == MissionStatus.NOT_AVAILABLE) {
			if (entry.increaseProgress() >= getRequiredCompletion()) {
				entry.setStatus(MissionStatus.AVAILABLE);
				notifyAvailablesReward(player);
			}
			storePlayerEntry(entry);
		}
	}

	public static class Factory implements MissionHandlerFactory {

		@Override
		public AbstractMissionHandler create(MissionDataHolder data) {
			return new OlympiadMissionHandler(data);
		}

		@Override
		public String handlerName() {
			return "olympiad";
		}
	}
}
