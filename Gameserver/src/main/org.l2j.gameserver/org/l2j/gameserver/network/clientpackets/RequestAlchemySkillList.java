package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExAlchemySkillList;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestAlchemySkillList extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || (activeChar.getRace() != Race.ERTHEIA)) {
            return;
        }
        client.sendPacket(new ExAlchemySkillList(activeChar));
    }
}
