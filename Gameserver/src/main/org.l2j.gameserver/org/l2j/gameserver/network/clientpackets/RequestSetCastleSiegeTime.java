package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SiegeInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

/**
 * @author UnAfraid
 */
public class RequestSetCastleSiegeTime extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestSetCastleSiegeTime.class);
    private int _castleId;
    private long _time;

    private static boolean isSiegeTimeValid(long siegeDate, long choosenDate) {
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(siegeDate);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);

        final Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(choosenDate);

        for (int hour : Config.SIEGE_HOUR_LIST) {
            cal1.set(Calendar.HOUR_OF_DAY, hour);
            if (isEqual(cal1, cal2, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEqual(Calendar cal1, Calendar cal2, int... fields) {
        for (int field : fields) {
            if (cal1.get(field) != cal2.get(field)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void readImpl() {
        _castleId = readInt();
        _time = readInt();
        _time *= 1000;
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        final Castle castle = CastleManager.getInstance().getCastleById(_castleId);
        if ((activeChar == null) || (castle == null)) {
            LOGGER.warn(getClass().getSimpleName() + ": activeChar: " + activeChar + " castle: " + castle + " castleId: " + _castleId);
            return;
        }
        if ((castle.getOwnerId() > 0) && (castle.getOwnerId() != activeChar.getClanId())) {
            LOGGER.warn(getClass().getSimpleName() + ": activeChar: " + activeChar + " castle: " + castle + " castleId: " + _castleId + " is trying to change siege date of not his own castle!");
            return;
        } else if (!activeChar.isClanLeader()) {
            LOGGER.warn(getClass().getSimpleName() + ": activeChar: " + activeChar + " castle: " + castle + " castleId: " + _castleId + " is trying to change siege date but is not clan leader!");
            return;
        } else if (!castle.getIsTimeRegistrationOver()) {
            if (isSiegeTimeValid(castle.getSiegeDate().getTimeInMillis(), _time)) {
                castle.getSiegeDate().setTimeInMillis(_time);
                castle.setIsTimeRegistrationOver(true);
                castle.getSiege().saveSiegeDate();
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME);
                msg.addCastleId(_castleId);
                Broadcast.toAllOnlinePlayers(msg);
                activeChar.sendPacket(new SiegeInfo(castle, activeChar));
            } else {
                LOGGER.warn(getClass().getSimpleName() + ": activeChar: " + activeChar + " castle: " + castle + " castleId: " + _castleId + " is trying to an invalid time (" + new Date(_time) + " !");
            }
        } else {
            LOGGER.warn(getClass().getSimpleName() + ": activeChar: " + activeChar + " castle: " + castle + " castleId: " + _castleId + " is trying to change siege date but currently not possible!");
        }
    }
}
