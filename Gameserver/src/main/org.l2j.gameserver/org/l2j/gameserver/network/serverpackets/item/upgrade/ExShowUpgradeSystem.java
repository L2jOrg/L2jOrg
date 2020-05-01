package org.l2j.gameserver.network.serverpackets.item.upgrade;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collections;

/**
 * @author JoeAlisson
 */
public class ExShowUpgradeSystem  extends AbstractUpgradeSystem {

    /**
     * FE CD 01 :  01 00       64 00 00 00 00 00 00 00 00 00 - Rare
     */
    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeId(ServerExPacketId.EX_SHOW_UPGRADE_SYSTEM);
        writeShort(0x01); // flag
        writeShort(0x64); // commission ratio

        writeMaterial(Collections.emptyList());

    }
}
