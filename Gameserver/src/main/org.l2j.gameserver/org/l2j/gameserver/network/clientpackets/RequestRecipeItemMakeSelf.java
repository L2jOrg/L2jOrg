package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author Administrator
 */
public final class RequestRecipeItemMakeSelf extends ClientPacket {
    private int _id;

    @Override
    public void readImpl() {
        _id = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getManufacture().tryPerformAction("RecipeMakeSelf")) {
            return;
        }

        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
            activeChar.sendMessage("You cannot create items while trading.");
            return;
        }

        if (activeChar.isCrafting()) {
            activeChar.sendMessage("You are currently in Craft Mode.");
            return;
        }

        RecipeController.getInstance().requestMakeItem(activeChar, _id);
    }
}
