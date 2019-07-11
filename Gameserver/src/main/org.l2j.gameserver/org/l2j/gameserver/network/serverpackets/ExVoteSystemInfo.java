package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * ExVoteSystemInfo packet implementation.
 *
 * @author Gnacik
 */
public class ExVoteSystemInfo extends ServerPacket {
    private final int _recomLeft;
    private final int _recomHave;
    private final int _bonusTime;
    private final int _bonusVal;
    private final int _bonusType;

    public ExVoteSystemInfo(Player player) {
        _recomLeft = player.getRecomLeft();
        _recomHave = player.getRecomHave();
        _bonusTime = 0;
        _bonusVal = 0;
        _bonusType = 0;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_VOTE_SYSTEM_INFO);

        writeInt(_recomLeft);
        writeInt(_recomHave);
        writeInt(_bonusTime);
        writeInt(_bonusVal);
        writeInt(_bonusType);
    }

}
