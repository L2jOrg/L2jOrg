package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * This class ... cS
 *
 * @version $Revision: 1.1.2.2.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRecipeShopMessageSet extends IClientIncomingPacket {
    private static final int MAX_MSG_LENGTH = 29;

    private String _name;

    @Override
    public void readImpl(ByteBuffer packet) {
        _name = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if ((_name != null) && (_name.length() > MAX_MSG_LENGTH)) {
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to overflow recipe shop message", Config.DEFAULT_PUNISH);
            return;
        }

        if (player.hasManufactureShop()) {
            player.setStoreName(_name);
        }
    }
}
