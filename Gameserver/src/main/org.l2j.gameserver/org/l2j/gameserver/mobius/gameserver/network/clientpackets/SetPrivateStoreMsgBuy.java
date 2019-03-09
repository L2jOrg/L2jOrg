package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * This class ...
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class SetPrivateStoreMsgBuy extends IClientIncomingPacket
{
    private static final int MAX_MSG_LENGTH = 29;

    private String _storeMsg;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _storeMsg = readString(packet);
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance player = client.getActiveChar();
        if ((player == null) || (player.getBuyList() == null))
        {
            return;
        }

        if ((_storeMsg != null) && (_storeMsg.length() > MAX_MSG_LENGTH))
        {
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to overflow private store buy message", Config.DEFAULT_PUNISH);
            return;
        }

        player.getBuyList().setTitle(_storeMsg);
        client.sendPacket(new PrivateStoreMsgBuy(player));
    }
}
