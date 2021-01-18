package org.l2j.gameserver.network.clientpackets.randomcraft;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftRandomInfo;

public class ExRequestRandomCraftInfo extends ClientPacket {
    @Override
    protected void readImpl() throws Exception {

    }

    @Override
    protected void runImpl() {
        if (!Config.ENABLE_RANDOM_CRAFT)
        {
            return;
        }

        final Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }

        player.sendPacket(new ExCraftRandomInfo(player));
    }
}

