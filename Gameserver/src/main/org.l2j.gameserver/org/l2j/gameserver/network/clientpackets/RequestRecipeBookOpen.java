package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

public final class RequestRecipeBookOpen extends ClientPacket {
    private boolean _isDwarvenCraft;

    @Override
    public void readImpl() {
        _isDwarvenCraft = (readInt() == 0);
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isCastingNow()) {
            client.sendPacket(SystemMessageId.YOUR_RECIPE_BOOK_MAY_NOT_BE_ACCESSED_WHILE_USING_A_SKILL);
            return;
        }

        if (activeChar.getActiveRequester() != null) {
            activeChar.sendMessage("You may not alter your recipe book while trading.");
            return;
        }

        RecipeController.getInstance().requestBookOpen(activeChar, _isDwarvenCraft);
    }
}
