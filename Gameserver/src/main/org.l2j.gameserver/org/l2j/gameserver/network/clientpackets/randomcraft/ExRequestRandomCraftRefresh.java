package org.l2j.gameserver.network.clientpackets.randomcraft;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.PlayerRandomCraft;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestRandomCraftRefresh extends ClientPacket {
    @Override
    protected void readImpl() throws Exception {

    }

    @Override
    protected void runImpl() {
        if (!Config.ENABLE_RANDOM_CRAFT) {
            return;
        }

        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final PlayerRandomCraft rc = player.getRandomCraft();
        rc.refresh();
    }
}
