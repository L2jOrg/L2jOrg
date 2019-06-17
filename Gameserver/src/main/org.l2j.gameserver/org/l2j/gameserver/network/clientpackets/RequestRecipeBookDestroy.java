package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.model.L2RecipeList;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.RecipeBookItemList;

public final class RequestRecipeBookDestroy extends ClientPacket {
    private int _recipeID;

    @Override
    public void readImpl() {
        _recipeID = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("RecipeDestroy")) {
            return;
        }

        final L2RecipeList rp = RecipeData.getInstance().getRecipeList(_recipeID);
        if (rp == null) {
            return;
        }
        activeChar.unregisterRecipeList(_recipeID);

        final RecipeBookItemList response = new RecipeBookItemList(rp.isDwarvenRecipe(), activeChar.getMaxMp());
        if (rp.isDwarvenRecipe()) {
            response.addRecipes(activeChar.getDwarvenRecipeBook());
        } else {
            response.addRecipes(activeChar.getCommonRecipeBook());
        }

        activeChar.sendPacket(response);
    }
}