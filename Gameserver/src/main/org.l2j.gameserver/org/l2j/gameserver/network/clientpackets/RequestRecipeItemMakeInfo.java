package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.RecipeItemMakeInfo;

public final class RequestRecipeItemMakeInfo extends ClientPacket {
    private int _id;

    @Override
    public void readImpl() {
        _id = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        client.sendPacket(new RecipeItemMakeInfo(_id, player));
    }
}
