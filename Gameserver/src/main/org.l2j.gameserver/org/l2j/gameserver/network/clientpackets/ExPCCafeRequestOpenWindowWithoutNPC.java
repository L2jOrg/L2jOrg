package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class ExPCCafeRequestOpenWindowWithoutNPC extends IClientIncomingPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar != null) && Config.PC_CAFE_ENABLED) {
            final NpcHtmlMessage html = new NpcHtmlMessage();
            html.setFile(activeChar, "data/html/pccafe.htm");
            activeChar.sendPacket(html);
        }
    }
}
