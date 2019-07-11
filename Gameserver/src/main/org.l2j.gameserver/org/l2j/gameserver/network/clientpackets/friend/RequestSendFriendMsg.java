package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.L2FriendSay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Recieve Private (Friend) Message - 0xCC Format: c SS S: Message S: Receiving Player
 *
 * @author Tempy
 */
public final class RequestSendFriendMsg extends ClientPacket {
    private static Logger LOGGER_CHAT = LoggerFactory.getLogger("chat");

    private String _message;
    private String _reciever;

    @Override
    public void readImpl() {
        _message = readString();
        _reciever = readString();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if ((_message == null) || _message.isEmpty() || (_message.length() > 300)) {
            return;
        }

        final Player targetPlayer = L2World.getInstance().getPlayer(_reciever);
        if ((targetPlayer == null) || !targetPlayer.getFriendList().contains(activeChar.getObjectId())) {
            activeChar.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
            return;
        }

        if (Config.LOG_CHAT) {
            LOGGER_CHAT.info("PRIV_MSG [" + activeChar + " to " + targetPlayer + "] " + _message);
        }

        targetPlayer.sendPacket(new L2FriendSay(activeChar.getName(), _reciever, _message));
    }
}
