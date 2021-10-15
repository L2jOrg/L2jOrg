package org.l2j.gameserver.network.serverpackets.pledge;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeCoinInfo extends ServerPacket
{
    private final int _count;

    public ExPledgeCoinInfo(Player player)
    {
        _count = player.getHonorCoins();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer)
    {
        writeId(ServerExPacketId.EX_PLEDGE_COIN_INFO, buffer);
        buffer.writeInt(_count);
    }
}