package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.handler.UserCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * This class ...
 *
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class BypassUserCmd extends ClientPacket {
    private int _command;

    @Override
    public void readImpl() {
        _command = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final IUserCommandHandler handler = UserCommandHandler.getInstance().getHandler(_command);
        if (handler == null) {
            if (player.isGM()) {
                player.sendMessage("User commandID " + _command + " not implemented yet.");
            }
        } else {
            handler.useUserCommand(_command, client.getActiveChar());
        }
    }
}
