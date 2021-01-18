package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;

public class RandomCraftRequest extends AbstractRequest
{
    public RandomCraftRequest(Player player)
    {
        super(player);
    }

    @Override
    public boolean isUsingItem(int objectId) {
        return false;
    }
}