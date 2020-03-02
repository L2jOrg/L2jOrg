package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;
import org.l2j.gameserver.util.GameUtils;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class SetPrivateStoreMsgSell extends ClientPacket {
    private static final int MAX_MSG_LENGTH = 29;

    private String _storeMsg;

    @Override
    public void readImpl() {
        _storeMsg = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (isNull(player) || isNull(player.getSellList())) {
            return;
        }

        if (nonNull(_storeMsg) && (_storeMsg.length() > MAX_MSG_LENGTH)) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player + " tried to overflow private store sell message");
            return;
        }

        player.getSellList().setTitle(_storeMsg);
        client.sendPacket(new PrivateStoreMsgSell(player));
    }
}
