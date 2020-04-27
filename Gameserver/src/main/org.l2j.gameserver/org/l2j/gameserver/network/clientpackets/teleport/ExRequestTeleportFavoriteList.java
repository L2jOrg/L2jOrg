package org.l2j.gameserver.network.clientpackets.teleport;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.teleport.ExTeleportFavoritesList;

/**
 * @author JoeAlisson
 */
public class ExRequestTeleportFavoriteList extends ClientPacket {

    @Override
    protected void readImpl()  {
        // trigger packet
    }

    @Override
    protected void runImpl()  {
        client.sendPacket(new ExTeleportFavoritesList(true));
    }
}
