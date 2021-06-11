package org.l2j.gameserver.network.serverpackets.pledge;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPledgeV3Info extends ServerPacket {
    private final Player _player;

    public ExPledgeV3Info(Player player)
    {
        _player = player;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer)
    {
        writeId(ServerExPacketId.EX_PLEDGE_V3_INFO, buffer);
        final Clan clan = _player.getClan();
        if (clan == null)
        {
            buffer.writeInt(0x00); // Total XP
            buffer.writeInt(0x00); // Clan rank
            buffer.writeString(""); // Clan notice
            buffer.writeShort(0x00); // Unk
            buffer.writeByte(0x00); // Unk
        }
        else
        {
            buffer.writeInt((int) clan.getExp()); // Total XP
            buffer.writeInt(0x00); // Clan rank
            buffer.writeString(clan.getNotice()); // Clan notice
            buffer.writeShort(0x0A); // Unk
            buffer.writeByte(0x01); // Unk
        }
    }
}
