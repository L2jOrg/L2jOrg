package org.l2j.gameserver.network.clientpackets.teleport;

import org.l2j.gameserver.data.xml.impl.TeleportEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author JoeAlisson
 */
public class ExRequestTeleportFavoritesAddDel extends ClientPacket {

    private boolean activate;
    private int teleportId;

    @Override
    protected void readImpl()  {
        activate = readBoolean();
        teleportId = readInt();
    }

    @Override
    protected void runImpl()  {
        TeleportEngine.getInstance().getInfo(teleportId).ifPresent(teleport -> {
            var player = client.getPlayer();
            if(activate) {
                player.addTeleportFavorite(teleportId);
            } else {
                player.removeTeleportFavorite(teleportId);
            }
        });
    }
}
