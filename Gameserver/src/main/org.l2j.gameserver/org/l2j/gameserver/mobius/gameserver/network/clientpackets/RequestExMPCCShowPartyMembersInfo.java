package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExMPCCShowPartyMemberInfo;

import java.nio.ByteBuffer;

/**
 * Format:(ch) d
 * @author chris_00
 */
public final class RequestExMPCCShowPartyMembersInfo extends IClientIncomingPacket
{
    private int _partyLeaderId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _partyLeaderId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        final L2PcInstance player = L2World.getInstance().getPlayer(_partyLeaderId);
        if ((player != null) && (player.getParty() != null))
        {
            client.sendPacket(new ExMPCCShowPartyMemberInfo(player.getParty()));
        }
    }
}
