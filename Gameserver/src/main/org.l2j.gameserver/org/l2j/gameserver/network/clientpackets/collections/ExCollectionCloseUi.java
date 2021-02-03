package org.l2j.gameserver.network.clientpackets.collections;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.collections.CollectionCloseUi;


public class ExCollectionCloseUi extends ClientPacket {
    @Override
    protected void readImpl() throws Exception {

    }

    @Override
    protected void runImpl() {
        final Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }
        player.sendPacket(new CollectionCloseUi(player));
    }
}