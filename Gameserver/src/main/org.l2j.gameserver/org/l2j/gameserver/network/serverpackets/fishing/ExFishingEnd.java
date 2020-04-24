package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author -Wooden-
 */
public class ExFishingEnd extends ServerPacket {
    private final Player _player;
    private final FishingEndReason _reason;

    public ExFishingEnd(Player player, FishingEndReason reason) {
        _player = player;
        _reason = reason;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_FISHING_END);
        writeInt(_player.getObjectId());
        writeByte((byte) _reason.getReason());
    }


    public enum FishingEndReason {
        LOSE(0),
        WIN(1),
        STOP(2);

        private final int _reason;

        FishingEndReason(int reason) {
            _reason = reason;
        }

        public int getReason() {
            return _reason;
        }
    }

    public enum FishingEndType {
        PLAYER_STOP,
        PLAYER_CANCEL,
        ERROR;
    }
}
