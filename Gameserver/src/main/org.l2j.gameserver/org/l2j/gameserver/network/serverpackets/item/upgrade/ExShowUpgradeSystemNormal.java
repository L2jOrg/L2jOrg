package org.l2j.gameserver.network.serverpackets.item.upgrade;

import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collections;

/**
 * @author JoeAlisson
 */
public class ExShowUpgradeSystemNormal extends AbstractUpgradeSystem {

    private final UpgradeType type;

    public ExShowUpgradeSystemNormal(UpgradeType type) {
        this.type = type;
    }

    /**
     *   FE 03 02 :  01 00 02 00 64 00 00 00 00 00 00 00 00 00 - Normal
     */
    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_UPGRADE_SYSTEM_NORMAL);
        writeShort(0x01); // flag
        writeShort(type.ordinal());
        writeShort(0x64); // commission ratio
        writeMaterial(Collections.emptyList());
    }
}
