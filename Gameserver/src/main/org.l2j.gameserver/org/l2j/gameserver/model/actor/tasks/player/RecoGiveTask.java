package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;

/**
 * Task dedicated to increase player's recommendation bonus.
 *
 * @author UnAfraid
 */
public class RecoGiveTask implements Runnable {
    private final Player _player;

    public RecoGiveTask(Player player) {
        _player = player;
    }

    @Override
    public void run() {
        if (_player != null) {
            // 10 recommendations to give out after 2 hours of being logged in
            // 1 more recommendation to give out every hour after that.
            int recoToGive = 1;
            if (!_player.isRecoTwoHoursGiven()) {
                recoToGive = 10;
                _player.setRecoTwoHoursGiven(true);
            }

            _player.setRecomLeft(_player.getRecomLeft() + recoToGive);

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_OBTAINED_S1_RECOMMENDATION_S);
            sm.addInt(recoToGive);
            _player.sendPacket(sm);
            _player.sendPacket(new UserInfo(_player));
        }
    }
}
