package org.l2j.gameserver.network.clientpackets.pledgebonus;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.pledgebonus.ExPledgeBonusList;

/**
 * @author UnAfraid
 */
public class RequestPledgeBonusRewardList extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if ((player == null) || (player.getClan() == null)) {
            return;
        }

        player.sendPacket(new ExPledgeBonusList());
    }
}
