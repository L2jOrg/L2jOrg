package org.l2j.gameserver.network.clientpackets.magiclamp;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.magiclamp.ExMagicLampGameInfoUI;

public class ExMagicLampGameInfo  extends ClientPacket {
    private byte _mode;
    @Override
    protected void readImpl() throws Exception {
        _mode = readByte(); // cGameMode
    }
    @Override
    protected void runImpl() {
        final Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }

        player.sendPacket(new ExMagicLampGameInfoUI(player, _mode, 1));
    }
}
