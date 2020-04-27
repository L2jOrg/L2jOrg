package org.l2j.gameserver.network.clientpackets.teleport;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.teleport.ExTeleportFavoritesList;

/**
 * @author JoeAlisson
 */
public class ExRequestTeleportFavoritesUIToggle extends ClientPacket {

    private boolean on;

    @Override
    protected void readImpl()  {
        on = readBoolean();
    }

    @Override
    protected void runImpl()  {
        client.sendPacket(new ExTeleportFavoritesList(on));
    }
}
