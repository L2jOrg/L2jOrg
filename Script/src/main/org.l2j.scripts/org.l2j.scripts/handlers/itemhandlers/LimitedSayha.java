package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.scripts.handlers.targethandlers.Item;

/**
 * @author Mode
 */
/*public class LimitedSayha implements IItemHandler {
    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse) {
        if (!playable.isPlayer()) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }

        final Player player = playable.getActingPlayer();
        long time = 0;
        switch (item.getId()) {
            case 71899: {
                time = 86400000L * 30;
                break;
            }
            case 71900: {
                time = 86400000L * 1;
                break;
            }
            case 71901: {
                time = 86400000L * 7;
                break;
            }
            default: {
                time = 0;
                break;
            }
        }
        if ((time > 0) && player.setLimitedSayhaGraceEndTime(System.currentTimeMillis() + time)) {
            player.destroyItem("LimitedSayha potion", item, 1, player, true);
        } else {
            player.sendMessage("Your Limited Sayha's Grace remaining time is greater than item's.");
            return false;
        }
        return true;
    }
}*/