package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.instance.Player;

public class RequestTutorialLinkHtml extends ClientPacket {
    private String _bypass;

    @Override
    public void readImpl() {
        readInt();
        _bypass = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
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
