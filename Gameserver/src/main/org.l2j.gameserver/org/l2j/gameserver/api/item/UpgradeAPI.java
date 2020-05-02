package org.l2j.gameserver.api.item;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.item.upgrade.ExShowUpgradeSystem;
import org.l2j.gameserver.network.serverpackets.item.upgrade.ExShowUpgradeSystemNormal;

/**
 * @author JoeAlisson
 */
public class UpgradeAPI {

    public static boolean showUpgradeUI(Player player, UpgradeType type) {
        if(type == UpgradeType.RARE) {
            player.sendPacket(new ExShowUpgradeSystem());
        } else {
            player.sendPacket(new ExShowUpgradeSystemNormal(type));
        }
        return true;
    }
}
