package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeV3Info;

public class ExRequestPledgeV3Info extends ClientPacket {
    @Override
    protected void readImpl() throws Exception
    {
    }

    @Override
    protected void runImpl()
    {
        final Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }
        client.sendPacket(new ExPledgeV3Info(player));
    }
}
