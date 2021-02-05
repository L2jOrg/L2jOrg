package org.l2j.gameserver.network.serverpackets.grace;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;


public class ExVitalExInfo extends ServerPacket {
    private final Player _player;

    public ExVitalExInfo(Player player)
    {
        _player = player;
    }
    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_VITAL_EX_INFO, buffer );
        buffer.writeInt((int) (50)); // currentmilis / 1000, when limited sayha ends
        buffer.writeInt((int) (50)); // currentmilis / 1000, when sayha grace suport ends
        buffer.writeInt((int) (50)); // Limited sayha bonus
        buffer.writeInt(0x82); // Limited sayha bonus adena (shown as 130%, actually 30%)
    }

}
