package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Mobius
 */
public class ExPCCafeRequestOpenWindowWithoutNPC extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if ((activeChar != null) && Config.PC_CAFE_ENABLED) {
            final NpcHtmlMessage html = new NpcHtmlMessage();
            html.setFile(activeChar, "data/html/pccafe.htm");
            activeChar.sendPacket(html);
        }
    }
}
