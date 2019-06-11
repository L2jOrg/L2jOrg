package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * D0 0F 00 5A 00 77 00 65 00 72 00 67 00 00 00
 *
 * @author -Wooden-
 */
public final class RequestExOustFromMPCC extends IClientIncomingPacket {
    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        final L2PcInstance target = L2World.getInstance().getPlayer(_name);
        final L2PcInstance activeChar = client.getActiveChar();

        if ((target != null) && target.isInParty() && activeChar.isInParty() && activeChar.getParty().isInCommandChannel() && target.getParty().isInCommandChannel() && activeChar.getParty().getCommandChannel().getLeader().equals(activeChar) && activeChar.getParty().getCommandChannel().equals(target.getParty().getCommandChannel())) {
            if (activeChar.equals(target)) {
                return;
            }

            target.getParty().getCommandChannel().removeParty(target.getParty());

            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL);
            target.getParty().broadcastPacket(sm);

            // check if CC has not been canceled
            if (activeChar.getParty().isInCommandChannel()) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL);
                sm.addString(target.getParty().getLeader().getName());
                activeChar.getParty().getCommandChannel().broadcastPacket(sm);
            }
        } else {
            activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
        }
    }
}
