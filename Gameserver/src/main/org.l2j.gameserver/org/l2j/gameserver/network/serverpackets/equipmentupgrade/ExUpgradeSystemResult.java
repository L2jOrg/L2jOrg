package org.l2j.gameserver.network.serverpackets.equipmentupgrade;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExUpgradeSystemResult extends AbstractItemPacket
{
    private final int _objectId;
    private final int _success;

    public ExUpgradeSystemResult(int objectId, int success)
    {
        _objectId = objectId;
        _success = success;
    }

    @Override
    public void writeImpl(GameClient client)
    {
        writeId(ServerPacketId.EX_UPGRADE_SYSTEM_RESULT);
        writeShort(_success);
        writeInt(_objectId);
    }
}
