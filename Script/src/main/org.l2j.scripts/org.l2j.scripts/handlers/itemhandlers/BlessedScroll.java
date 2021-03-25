package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.BlessItemRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.bless.ExOpenBlessOptionScroll;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class BlessedScroll implements IItemHandler {
    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse) {

        if (!isPlayer(playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }

        final Player player = playable.getActingPlayer();
        if (player.isCastingNow()) {
            return false;
        }

        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.ANOTHER_ENCHANTMENT_IS_IN_PROGRESS_PLEASE_COMPLETE_THE_PREVIOUS_TASK_THEN_TRY_AGAIN);
            return false;
        }

        final BlessItemRequest request = new BlessItemRequest(player, item);
        if (!player.addRequest(request)) {
            return false;
        }

        playable.sendPacket(new ExOpenBlessOptionScroll(item.getId()));
        
        return true;
    }
}
