package org.l2j.gameserver.network.clientpackets.upgrade;

import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.engine.upgrade.UpgradeItemEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author JoeAlisson
 */
public class ExUpgradeSystemRequest extends ClientPacket {

    private int objectId;
    private int upgradeId;

    @Override
    protected void readImpl() throws Exception {
        objectId = readInt();
        upgradeId = readInt();
    }

    @Override
    protected void runImpl() {
        UpgradeItemEngine.getInstance().upgradeItem(client.getPlayer(), objectId, UpgradeType.RARE, upgradeId);
    }
}
