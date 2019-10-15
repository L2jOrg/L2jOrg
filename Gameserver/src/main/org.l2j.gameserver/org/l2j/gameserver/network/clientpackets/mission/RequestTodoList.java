package org.l2j.gameserver.network.clientpackets.mission;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.mission.ExOneDayReceiveRewardList;

/**
 * @author UnAfraid
 */
public class RequestTodoList extends ClientPacket {
    private int _tab;
    @SuppressWarnings("unused")
    private boolean _showAllLevels;

    @Override
    public void readImpl() {
        _tab = readByte(); // Daily Reward = 9, Event = 1, Instance Zone = 2
        _showAllLevels = readByte() == 1; // Disabled = 0, Enabled = 1
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
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
