package org.l2j.gameserver.mobius.gameserver.network.clientpackets.dailymission;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.dailymission.ExOneDayReceiveRewardList;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestTodoList extends IClientIncomingPacket {
    private int _tab;
    @SuppressWarnings("unused")
    private boolean _showAllLevels;

    @Override
    public void readImpl(ByteBuffer packet) {
        _tab = packet.get(); // Daily Reward = 9, Event = 1, Instance Zone = 2
        _showAllLevels = packet.get() == 1; // Disabled = 0, Enabled = 1
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        switch (_tab) {
            // case 1:
            // {
            // player.sendPacket(new ExTodoListInzone());
            // break;
            // }
            // case 2:
            // {
            // player.sendPacket(new ExTodoListInzone());
            // break;
            // }
            case 9: // Daily Rewards
            {
                // Initial EW request should be false
                player.sendPacket(new ExOneDayReceiveRewardList(player, true));
                break;
            }
        }
    }
}
