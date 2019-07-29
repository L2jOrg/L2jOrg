package org.l2j.gameserver.network.clientpackets.attributechange;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.attributechange.ExChangeAttributeFail;

/**
 * @author Mobius
 */
public class RequestChangeAttributeCancel extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }
        activeChar.sendPacket(ExChangeAttributeFail.STATIC);
    }
}