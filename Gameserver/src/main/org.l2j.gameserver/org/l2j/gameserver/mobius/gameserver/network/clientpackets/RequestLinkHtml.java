package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2j.gameserver.mobius.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Lets drink to code!
 *
 * @author zabbix, HorridoJoho
 */
public final class RequestLinkHtml extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLinkHtml.class);
    private String _link;

    @Override
    public void readImpl(ByteBuffer packet) {
        _link = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance actor = client.getActiveChar();
        if (actor == null) {
            return;
        }

        if (_link.isEmpty()) {
            LOGGER.warn("Player " + actor.getName() + " sent empty html link!");
            return;
        }

        if (_link.contains("..")) {
            LOGGER.warn("Player " + actor.getName() + " sent invalid html link: link " + _link);
            return;
        }

        final int htmlObjectId = actor.validateHtmlAction("link " + _link);
        if (htmlObjectId == -1) {
            LOGGER.warn("Player " + actor.getName() + " sent non cached  html link: link " + _link);
            return;
        }

        if ((htmlObjectId > 0) && !Util.isInsideRangeOfObjectId(actor, htmlObjectId, L2Npc.INTERACTION_DISTANCE)) {
            // No logging here, this could be a common case
            return;
        }

        final String filename = "data/html/" + _link;
        final NpcHtmlMessage msg = new NpcHtmlMessage(htmlObjectId);
        msg.setFile(actor, filename);
        actor.sendPacket(msg);
    }
}
