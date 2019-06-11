package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;

import java.nio.ByteBuffer;

public class RequestTutorialPassCmdToServer extends IClientIncomingPacket {
    private String _bypass = null;

    @Override
    public void readImpl() {
        _bypass = readString();
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
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerBypass(player, _bypass), player);
    }
}
