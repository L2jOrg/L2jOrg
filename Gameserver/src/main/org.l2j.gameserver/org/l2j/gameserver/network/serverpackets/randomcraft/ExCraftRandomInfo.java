package org.l2j.gameserver.network.serverpackets.randomcraft;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.RandomCraftRewardItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

public class ExCraftRandomInfo extends ServerPacket {
    private final Player _player;

    public ExCraftRandomInfo(Player player)
    {
        _player = player;
    }


    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_CRAFT_RANDOM_INFO, buffer );
        final List<RandomCraftRewardItemHolder> rewards = _player.getRandomCraft().getRewards();
        int size = 5;
        buffer.writeInt(size); // size
        for (int i = 0; i < rewards.size(); i++)
        {
            final RandomCraftRewardItemHolder holder = rewards.get(i);
            if ((holder != null) && (holder.getItemId() != 0))
            {
                buffer.writeByte(holder.isLocked() ? 0x01 : 0x00); // Locked
                buffer.writeInt(holder.getLockLeft()); // Rolls it will stay locked
                buffer.writeInt(holder.getItemId()); // Item id
                buffer.writeLong(holder.getItemCount()); // Item count
            }
            else
            {
                buffer.writeByte(0x00);
                buffer.writeInt(0x00);
                buffer.writeInt(0x00);
                buffer.writeLong(0x00);
            }
            size--;
        }

        // Write missing
        for (int i = size; i > 0; i--)
        {
            buffer.writeByte(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeLong(0x00);
        }
    }
}
