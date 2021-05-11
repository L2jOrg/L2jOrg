package org.l2j.gameserver.network.serverpackets.magiclamp;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.MagicLampData;
import org.l2j.gameserver.enums.LampMode;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.GreaterMagicLampHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author L2CCCP
 * Reworked Vicochips
 */
public class ExMagicLampGameInfoUI extends ServerPacket {

    private final Player _player;
    private final byte _mode;
    private final int _count;

    public ExMagicLampGameInfoUI(Player player, byte mode, int count)
    {
        _player = player;
        _mode = mode;
        _count = count;
    }

   @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_MAGICLAMP_GAME_INFO, buffer );
        buffer.writeInt(Config.MAGIC_LAMP_MAX_GAME_COUNT); // nMagicLampGameMaxCCount
        buffer.writeInt(_count); // cMagicLampGameCCount
       switch (LampMode.getByMode(_mode)) {
           case NORMAL -> {
               buffer.writeInt(Config.MAGIC_LAMP_REWARD_COUNT);// cMagicLampCountPerGame
           }
           case GREATER -> {
               buffer.writeInt(Config.MAGIC_LAMP_GREATER_REWARD_COUNT); // cMagicLampCountPerGame
           }
       }
        buffer.writeInt(_player.getLampCount()); // cMagicLampCount
        buffer.writeByte(_mode); // cGameMode
        final List<GreaterMagicLampHolder> greater = MagicLampData.getInstance().getGreaterLamps();
        buffer.writeInt(greater.size()); // costItemList
        greater.forEach(lamp ->
        {
            buffer.writeInt(lamp.getItemId()); // nItemClassID
            buffer.writeLong(lamp.getCount()); // nItemAmountPerGame
            buffer.writeLong(_player.getInventory().getInventoryItemCount(lamp.getItemId(), -1)); // nItemAmount
        });
    }
}
