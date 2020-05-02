package org.l2j.gameserver.network.clientpackets.upgrade;

import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.engine.upgrade.UpgradeItemEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class ExUpgradeSystemNormalRequest extends ClientPacket {

    private int objectId;
    private int type;
    private int upgradeId;

    @Override
    protected void readImpl() throws Exception {
        objectId = readInt();
        type = readInt();
        upgradeId = readInt();
    }

    @Override
    protected void runImpl()  {
        doIfNonNull(UpgradeType.ofId(type),
            upgradeType -> UpgradeItemEngine.getInstance().upgradeItem(client.getPlayer(), objectId, upgradeType, upgradeId));
    }
}
