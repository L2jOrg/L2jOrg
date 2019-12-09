package org.l2j.gameserver.network.clientpackets.autoplay;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ExRequestActivateAutoShortcut extends ClientPacket {

    private boolean activate;
    private int room;

    @Override
    protected void readImpl()  {
        room = readShort();
        activate = readByteAsBoolean();
    }

    @Override
    protected void runImpl() {
        if(!activate) {
            client.sendPacket(new ExActivateAutoShortcut(room, activate));
            return;
        }

        if(room == -1) {
            // TODO auto supply
            client.sendPacket(new ExActivateAutoShortcut(room, activate));
        } else {

            var slot = room % 12;
            var page = room / 12;

            var player = client.getPlayer();
            var shortcut = player.getShortCut(slot, page);
            if (nonNull(shortcut)) {

                if (page == 23 && slot == 1) { // auto potion
                    var item = player.getInventory().getItemByObjectId(shortcut.getId());
                    if (isNull(item) || !item.isPotion()) {
                        return;
                    }
                }

                // TODO auto skill
                client.sendPacket(new ExActivateAutoShortcut(room, activate));
            }
        }

    }
}
