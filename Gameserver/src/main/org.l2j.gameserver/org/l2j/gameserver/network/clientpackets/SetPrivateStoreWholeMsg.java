package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSetWholeMsg;
import org.l2j.gameserver.util.GameUtils;

/**
 * @author KenM
 */
public class SetPrivateStoreWholeMsg extends ClientPacket {
    private static final int MAX_MSG_LENGTH = 29;

    private String _msg;

    @Override
    public void readImpl() {
        _msg = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if ((player == null) || (player.getSellList() == null)) {
            return;
        }

        if ((_msg != null) && (_msg.length() > MAX_MSG_LENGTH)) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to overflow private store whole message");
            return;
        }

        player.getSellList().setTitle(_msg);
        client.sendPacket(new ExPrivateStoreSetWholeMsg(player));
    }

}
