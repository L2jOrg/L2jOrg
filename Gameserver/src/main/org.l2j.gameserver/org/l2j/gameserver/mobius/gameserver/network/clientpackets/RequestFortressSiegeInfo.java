package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.FortManager;
import org.l2j.gameserver.mobius.gameserver.model.entity.Fort;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExShowFortressSiegeInfo;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class RequestFortressSiegeInfo extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {
    }

    @Override
    public void runImpl()
    {
        for (Fort fort : FortManager.getInstance().getForts())
        {
            if ((fort != null) && fort.getSiege().isInProgress())
            {
                client.sendPacket(new ExShowFortressSiegeInfo(fort));
            }
        }
    }
}
