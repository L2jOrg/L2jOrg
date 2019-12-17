package org.l2j.gameserver.network.clientpackets.autoplay;

import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;

import java.util.stream.IntStream;

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
        var player = client.getPlayer();
        if(room == -1) {
            for (int i = 0; i < Shortcut.MAX_SLOTS_PER_PAGE; i++) {
                var shortcut = player.getShortCut(i, Shortcut.AUTO_SUPPLY_PAGE);
                if(nonNull(shortcut) && handleAutoSupply(player, shortcut)) {
                    client.sendPacket(new ExActivateAutoShortcut(shortcut.getId(), activate));
                }
            }
        } else {
            var shortcut = player.getShortCut(room);

            if (nonNull(shortcut)) {
                var slot = room % 12;
                var page = room / 12;

                if (page == Shortcut.AUTO_PLAY_PAGE && slot == Shortcut.AUTO_POTION_SLOT) { // auto potion
                    if (!handleAutoPotion(player, shortcut)) {
                        return;
                    }
                } else if(page == Shortcut.AUTO_SUPPLY_PAGE) {
                    if(!handleAutoSupply(player, shortcut)) {
                        return;
                    }
                }
                // TODO auto skill
                client.sendPacket(new ExActivateAutoShortcut(room, activate));
            } else {
                client.sendPacket(new ExActivateAutoShortcut(room, false));
            }
        }

    }

    private boolean handleAutoSupply(Player player, Shortcut shortcut) {
        var item = player.getInventory().getItemByObjectId(shortcut.getId());
        if(isNull(item) || !item.isAutoSupply()) {
            player.deleteShortCut(shortcut.getSlot(), shortcut.getPage());
            client.sendPacket(new ExActivateAutoShortcut(shortcut.getId(), false));
            return false;
        }
        if(activate) {
            AutoPlayEngine.getInstance().startAutoSupply(player, shortcut.getClientId());
        } else {
            AutoPlayEngine.getInstance().stopAutoSupply(player, shortcut.getClientId());
        }
        return true;
    }

    private boolean handleAutoPotion(Player player, Shortcut shortcut) {
        var item = player.getInventory().getItemByObjectId(shortcut.getId());
        if (isNull(item) || !item.isAutoPotion()) {
            player.deleteShortCut(shortcut.getSlot(), shortcut.getPage());
            client.sendPacket(new ExActivateAutoShortcut(room, false));
            return false;
        }
        if(activate) {
            AutoPlayEngine.getInstance().startAutoPotion(player);
        } else {
            AutoPlayEngine.getInstance().stopAutoPotion(player);
        }
        return true;
    }
}