package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import org.l2j.gameserver.util.GameUtils;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class SetPrivateStoreMsgBuy extends ClientPacket {
    private static final int MAX_MSG_LENGTH = 29;

    private String _storeMsg;

    @Override
    public void readImpl() {
        _storeMsg = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if ((player == null) || (player.getBuyList() == null)) {
            return;
        }

        if ((_storeMsg != null) && (_storeMsg.length() > MAX_MSG_LENGTH)) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to overflow private store buy message");
            return;
        }

        player.getBuyList().setTitle(_storeMsg);
        client.sendPacket(new PrivateStoreMsgBuy(player));
    }
}
