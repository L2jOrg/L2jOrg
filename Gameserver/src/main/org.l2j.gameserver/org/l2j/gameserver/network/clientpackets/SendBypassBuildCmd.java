package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * This class handles all GM commands triggered by //command
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:29 $
 */
public final class SendBypassBuildCmd extends IClientIncomingPacket {
    public static final int GM_MESSAGE = 9;
    public static final int ANNOUNCEMENT = 10;

    private String _command;

    @Override
    public void readImpl() {
        _command = readString();
        if (_command != null) {
            _command = _command.trim();
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        AdminCommandHandler.getInstance().useAdminCommand(activeChar, "admin_" + _command, true);
    }
}
