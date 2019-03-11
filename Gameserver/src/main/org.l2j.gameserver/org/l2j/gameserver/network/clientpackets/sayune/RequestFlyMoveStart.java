package org.l2j.gameserver.network.clientpackets.sayune;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.data.xml.impl.SayuneData;
import org.l2j.gameserver.model.SayuneEntry;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.SayuneRequest;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.model.zone.type.L2SayuneZone;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestFlyMoveStart extends IClientIncomingPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestFlyMoveStart.class);

    @Override
    public void readImpl(ByteBuffer packet) {
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || !activeChar.isInsideZone(ZoneId.SAYUNE) || activeChar.hasRequest(SayuneRequest.class) || (!activeChar.isInCategory(CategoryType.SIXTH_CLASS_GROUP) && !Config.FREE_JUMPS_FOR_ALL)) {
            return;
        }

        if (activeChar.hasSummon()) {
            activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_USE_SAYUNE_WHILE_A_SERVITOR_IS_AROUND);
            return;
        }

        if (activeChar.getReputation() < 0) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_SAYUNE_WHILE_IN_A_CHAOTIC_STATE);
            return;
        }

        if (activeChar.hasRequests()) {
            activeChar.sendPacket(SystemMessageId.SAYUNE_CANNOT_BE_USED_WHILE_TAKING_OTHER_ACTIONS);
            return;
        }

        final L2SayuneZone zone = ZoneManager.getInstance().getZone(activeChar, L2SayuneZone.class);
        if (zone.getMapId() == -1) {
            activeChar.sendMessage("That zone is not supported yet!");
            LOGGER.warn(getClass().getSimpleName() + ": " + activeChar + " Requested sayune on zone with no map id set");
            return;
        }

        final SayuneEntry map = SayuneData.getInstance().getMap(zone.getMapId());
        if (map == null) {
            activeChar.sendMessage("This zone is not handled yet!!");
            LOGGER.warn(getClass().getSimpleName() + ": " + activeChar + " Requested sayune on unhandled map zone " + zone.getName());
            return;
        }

        final SayuneRequest request = new SayuneRequest(activeChar, map.getId());
        if (activeChar.addRequest(request)) {
            request.move(activeChar, 0);
        }
    }
}
