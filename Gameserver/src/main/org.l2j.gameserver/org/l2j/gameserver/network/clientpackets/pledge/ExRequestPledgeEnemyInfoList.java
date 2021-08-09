package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeEnemyInfoList;

public class ExRequestPledgeEnemyInfoList extends ClientPacket {

    @Override
    protected void readImpl() throws Exception {
    }

    @Override
    protected void runImpl() {
        Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }
        client.sendPacket(new ExPledgeEnemyInfoList(player));
    }
}
