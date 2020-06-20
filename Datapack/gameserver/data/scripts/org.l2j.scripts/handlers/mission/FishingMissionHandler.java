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
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFishing;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class FishingMissionHandler extends AbstractMissionHandler {
	
	private FishingMissionHandler(MissionDataHolder holder) {
		super(holder);
	}
	
	@Override
	public void init() {
		Listeners.players().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_FISHING, (Consumer<OnPlayerFishing>) this::onPlayerFishing, this));
	}
	
	private void onPlayerFishing(OnPlayerFishing event) {
		final Player player = event.getActiveChar();
		final MissionPlayerData entry = getPlayerEntry(player, true);
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
			return new FishingMissionHandler(data);
		}

		@Override
		public String handlerName() {
			return "fishing";
		}
	}
}
