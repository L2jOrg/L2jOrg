package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;
import org.l2j.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class SetPrivateStoreMsgSell extends IClientIncomingPacket {
    private static final int MAX_MSG_LENGTH = 29;

    private String _storeMsg;

    @Override
    public void readImpl() {
        _storeMsg = readString();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if ((player == null) || (player.getSellList() == null)) {
            return;
        }

        if ((_storeMsg != null) && (_storeMsg.length() > MAX_MSG_LENGTH)) {
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to overflow private store sell message", Config.DEFAULT_PUNISH);
            return;
        }

        player.getSellList().setTitle(_storeMsg);
        client.sendPacket(new PrivateStoreMsgSell(player));
    }
}
