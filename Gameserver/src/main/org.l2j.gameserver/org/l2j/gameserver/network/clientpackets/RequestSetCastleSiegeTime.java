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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SiegeInfo;
import org.l2j.gameserver.settings.FeatureSettings;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class RequestSetCastleSiegeTime extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestSetCastleSiegeTime.class);
    private int castleId;
    private long time;

    @Override
    public void readImpl() {
        castleId = readInt();
        time = readInt();
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();
        var castle = CastleManager.getInstance().getCastleById(castleId);

        if (isNull(castle)) {
            LOGGER.warn("player {} requested Siege time to invalid castle {}", player, castleId);
            return;
        }

        if(castle.getOwnerId() == 0 || castle.getOwnerId() != player.getClanId()) {
            LOGGER.warn("player {} is trying to change siege date of not his own castle {}!", player, castle);
        } else if(!player.isClanLeader()) {
            LOGGER.warn("player {} is trying to change siege date of castle {} but is not clan leader!", player, castle);
        } else if(castle.isSiegeTimeRegistrationSeason()) {

            var requestedTime = Instant.ofEpochSecond(time).atZone(ZoneId.systemDefault());
            var requestedHour = requestedTime.getHour();

            if(Util.contains(getSettings(FeatureSettings.class).siegeHours(), requestedHour)) {
                CastleManager.getInstance().registerSiegeDate(castle, castle.getSiegeDate().withHour(requestedHour));
                castle.setSiegeTimeRegistrationEnd(LocalDateTime.now());
                Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME).addCastleId(castleId));
                player.sendPacket(new SiegeInfo(castle, player));
            } else {
                LOGGER.warn("player {} is trying to an invalid time {}  !", player, requestedTime);
            }
        } else {
            LOGGER.warn("player {}  is trying to change siege date of castle {} but currently not possible!", player, castle);
        }
    }
}
