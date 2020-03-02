package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.GameUtils;

public class RequestRecipeShopMessageSet extends ClientPacket {
    private static final int MAX_MSG_LENGTH = 29;

    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if ((_name != null) && (_name.length() > MAX_MSG_LENGTH)) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to overflow recipe shop message");
            return;
        }

        if (player.hasManufactureShop()) {
            player.setStoreName(_name);
        }
    }
}
