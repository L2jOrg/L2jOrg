package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.serverpackets.ExShowFortressSiegeInfo;

/**
 * @author KenM
 */
public class RequestFortressSiegeInfo extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        for (Fort fort : FortDataManager.getInstance().getForts()) {
            if ((fort != null) && fort.getSiege().isInProgress()) {
                client.sendPacket(new ExShowFortressSiegeInfo(fort));
            }
        }
    }
}
