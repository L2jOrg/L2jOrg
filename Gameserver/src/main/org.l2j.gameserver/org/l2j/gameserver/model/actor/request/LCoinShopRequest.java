package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.actor.instance.Player;

public class LCoinShopRequest extends AbstractRequest {
    public LCoinShopRequest(Player activeChar) {
        super(activeChar);
    }

    @Override
    public boolean isUsing(int objectId) {
        return false;
    }
}
