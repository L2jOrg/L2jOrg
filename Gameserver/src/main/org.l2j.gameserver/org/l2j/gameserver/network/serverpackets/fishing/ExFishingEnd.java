package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public class ExFishingEnd extends IClientOutgoingPacket {
    private final L2PcInstance _player;
    private final FishingEndReason _reason;

    public ExFishingEnd(L2PcInstance player, FishingEndReason reason) {
        _player = player;
        _reason = reason;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_FISHING_END.writeId(packet);
        packet.putInt(_player.getObjectId());
        packet.put((byte) _reason.getReason());
    }

    @Override
    protected int size(L2GameClient client) {
        return 10;
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
