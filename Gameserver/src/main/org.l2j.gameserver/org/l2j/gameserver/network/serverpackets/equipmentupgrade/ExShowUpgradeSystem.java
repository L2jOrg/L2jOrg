package org.l2j.gameserver.network.serverpackets.equipmentupgrade;


import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExShowUpgradeSystem extends AbstractItemPacket
{

    @Override
    public void writeImpl(GameClient client)
    {
        writeId(ServerPacketId.EX_SHOW_UPGRADE_SYSTEM);
        writeShort(0x01);
    }
}
