package org.l2j.gameserver.network.serverpackets.magiclamp;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExMagicLampExpInfoUI extends ServerPacket {
    private final Player _player;

    public ExMagicLampExpInfoUI(Player player)
    {
        _player = player;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_MAGICLAMP_EXP_INFO, buffer );
        buffer.writeInt(Config.ENABLE_MAGIC_LAMP ? 0x01 : 0x00); // IsOpen
        buffer.writeInt(Config.MAGIC_LAMP_MAX_LEVEL_EXP); // MaxMagicLampExp
        buffer.writeInt(_player.getLampExp()); // MagicLampExp
        buffer.writeInt(_player.getLampCount()); // MagicLampCount

    }
}
