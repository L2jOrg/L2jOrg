package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Sdw
 */
public class ExUserInfoCubic implements IClientOutgoingPacket
{
    private final L2PcInstance _activeChar;

    public ExUserInfoCubic(L2PcInstance cha)
    {
        _activeChar = cha;
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.EX_USER_INFO_CUBIC.writeId(packet);

        packet.writeD(_activeChar.getObjectId());
        packet.writeH(_activeChar.getCubics().size());

        _activeChar.getCubics().keySet().forEach(packet::writeH);

        packet.writeD(_activeChar.getAgathionId());
        return true;
    }
}
