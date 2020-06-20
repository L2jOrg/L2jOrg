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
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.Calendar;
import java.util.function.Consumer;

import static org.l2j.commons.util.Util.isInteger;

/**
 * @author JoeAlisson
 */
public class LoginMissionHandler extends AbstractMissionHandler {

    private byte days = 0;

    private LoginMissionHandler(MissionDataHolder holder) {
        super(holder);
        var days = holder.getParams().getString("days", "");
        for (String day : days.split(" ")) {
            if(isInteger(day)) {
                this.days |= 1 << Integer.parseInt(day);
            }
        }
    }

    @Override
    public boolean isAvailable(Player player) {
        final MissionPlayerData entry = getPlayerEntry(player, false);
        return (entry != null) && (MissionStatus.AVAILABLE == entry.getStatus());
    }

    @Override
    public void init() {
        Listeners.Global().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) this::onPlayerLogin, this));
    }

    private void onPlayerLogin(OnPlayerLogin event) {
        final MissionPlayerData entry = getPlayerEntry(event.getPlayer(), true);
        if(MissionStatus.COMPLETED.equals(entry.getStatus())) {
            return;
        }

        final int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(days != 0 && (days & 1 << currentDay) == 0) {
            entry.setProgress(0);
            entry.setStatus(MissionStatus.NOT_AVAILABLE);
        } else {
            entry.setProgress(1);
            entry.setStatus(MissionStatus.AVAILABLE);
            notifyAvailablesReward(event.getPlayer());
        }
    }

    public static class Factory implements MissionHandlerFactory {

        @Override
        public AbstractMissionHandler create(MissionDataHolder data) {
            return new LoginMissionHandler(data);
        }

        @Override
        public String handlerName() {
            return "login";
        }
    }
}
