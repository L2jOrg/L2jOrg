package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeLock;

/**
 * @author JoeAlisson
 */
public class ExRequestCostumeLock extends ClientPacket {

    private int id;
    private boolean lock;

    @Override
    protected void readImpl() throws Exception {
        id = readInt();
        lock = readBoolean();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        if(player.lockCostume(id, lock)) {
            client.sendPacket(new ExCostumeLock(id, lock, true));
        }
    }
}
