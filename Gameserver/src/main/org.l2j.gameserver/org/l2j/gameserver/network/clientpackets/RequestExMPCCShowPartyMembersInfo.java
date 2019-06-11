package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExMPCCShowPartyMemberInfo;

import java.nio.ByteBuffer;

/**
 * Format:(ch) d
 *
 * @author chris_00
 */
public final class RequestExMPCCShowPartyMembersInfo extends IClientIncomingPacket {
    private int _partyLeaderId;

    @Override
    public void readImpl() {
        _partyLeaderId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2PcInstance player = L2World.getInstance().getPlayer(_partyLeaderId);
        if ((player != null) && (player.getParty() != null)) {
            client.sendPacket(new ExMPCCShowPartyMemberInfo(player.getParty()));
        }
    }
}
