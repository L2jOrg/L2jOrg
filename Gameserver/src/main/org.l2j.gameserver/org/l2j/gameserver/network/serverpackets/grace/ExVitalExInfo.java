package org.l2j.gameserver.network.serverpackets.grace;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;


public class ExVitalExInfo extends ServerPacket {
    private final Player _player;
    private final int _sayhaGraceBonus;
    private final int _sayhaGraceItemsRemaining;
    private final int _points;


    public ExVitalExInfo(Player player)
    {
        _player = player;
        _points = player.getSayhaGracePoints();
        _sayhaGraceBonus = (int) (player.getStats().getSayhaGraceExpBonus() * 100);
        _sayhaGraceItemsRemaining = 0; //Config.SAYHA_GRACE_MAX_ITEMS_ALLOWED - player.getSayhaGraceItemsUsed();

    }
    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_VITAL_EX_INFO, buffer );
        buffer.writeInt((int) (_points)); // Sayha's Grace Bonus
        buffer.writeInt((int) (_sayhaGraceBonus)); //  bonus in %
        buffer.writeShort(0x00); // Limited sayha bonus
        buffer.writeShort(_sayhaGraceItemsRemaining); // Sayha's Grace items remaining
        buffer.writeShort(Config.SAYHA_GRACE_MAX_ITEMS_ALLOWED); // Item to used

    }

}
