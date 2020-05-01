package org.l2j.gameserver.network.serverpackets.item.upgrade;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExUpgradeSystemResult extends ServerPacket {

    private final int upgradedId;

    public ExUpgradeSystemResult(int upgradedId) {
        this.upgradedId = upgradedId;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_UPGRADE_SYSTEM_RESULT);
        writeShort(true);
        writeInt(upgradedId);
    }
}
