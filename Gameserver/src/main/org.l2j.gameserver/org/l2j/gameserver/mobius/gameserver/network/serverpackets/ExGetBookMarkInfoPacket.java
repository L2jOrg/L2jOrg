package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.TeleportBookmark;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author ShanSoft
 */
public class ExGetBookMarkInfoPacket implements IClientOutgoingPacket
{
    private final L2PcInstance player;

    public ExGetBookMarkInfoPacket(L2PcInstance cha)
    {
        player = cha;
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.EX_GET_BOOK_MARK_INFO.writeId(packet);

        packet.writeD(0x00); // Dummy
        packet.writeD(player.getBookmarkslot());
        packet.writeD(player.getTeleportBookmarks().size());

        for (TeleportBookmark tpbm : player.getTeleportBookmarks())
        {
            packet.writeD(tpbm.getId());
            packet.writeD(tpbm.getX());
            packet.writeD(tpbm.getY());
            packet.writeD(tpbm.getZ());
            packet.writeS(tpbm.getName());
            packet.writeD(tpbm.getIcon());
            packet.writeS(tpbm.getTag());
        }
        return true;
    }
}
