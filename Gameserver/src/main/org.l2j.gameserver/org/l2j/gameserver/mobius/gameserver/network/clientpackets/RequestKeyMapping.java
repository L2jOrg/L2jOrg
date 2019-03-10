package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExUISetting;

import java.nio.ByteBuffer;

/**
 * @author KenM / mrTJO
 */
public class RequestKeyMapping extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (Config.STORE_UI_SETTINGS) {
            client.sendPacket(new ExUISetting(activeChar));
        }
    }
}
