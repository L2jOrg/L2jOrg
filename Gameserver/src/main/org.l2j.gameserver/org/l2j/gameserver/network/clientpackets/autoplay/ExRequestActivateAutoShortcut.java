package org.l2j.gameserver.network.clientpackets.autoplay;

import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;

/**
 * @author JoeAlisson
 */
public class ExRequestActivateAutoShortcut extends ClientPacket {

    private boolean activate;
    private int room;

    @Override
    protected void readImpl()  {
        room = readShort();
        activate = readBoolean();
    }

    @Override
    protected void runImpl() {
        if(AutoPlayEngine.getInstance().setActiveAutoShortcut(client.getPlayer(), room, activate)) {
            client.sendPacket(new ExActivateAutoShortcut(room, activate));
        } else {
            client.sendPacket(new ExActivateAutoShortcut(room, false));
        }
    }
}