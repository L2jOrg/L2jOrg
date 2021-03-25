package org.l2j.gameserver.network.clientpackets.magiclamp;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.magiclamp.ExMagicLampGameResult;


public class ExMagicLampGameStart extends ClientPacket {
    private int _count;
    private byte _mode;

    @Override
    protected void readImpl() throws Exception {
        _count = readInt(); // nMagicLampGameCCount
        _mode = readByte(); // cGameMode
    }

    @Override
    protected void runImpl() {
        final Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }
        client.sendPacket(new ExMagicLampGameResult(player, _count, _mode));
    }
}
