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
