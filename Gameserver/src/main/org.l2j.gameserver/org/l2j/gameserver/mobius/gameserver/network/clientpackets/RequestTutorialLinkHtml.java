package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.mobius.gameserver.handler.BypassHandler;
import org.l2j.gameserver.mobius.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

public class RequestTutorialLinkHtml extends IClientIncomingPacket {
    private String _bypass;

    @Override
    public void readImpl(ByteBuffer packet) {
        packet.getInt();
        _bypass = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (_bypass.startsWith("admin_")) {
            AdminCommandHandler.getInstance().useAdminCommand(player, _bypass, true);
        } else {
            final IBypassHandler handler = BypassHandler.getInstance().getHandler(_bypass);
            if (handler != null) {
                handler.useBypass(_bypass, player, null);
            }
        }
    }
}
