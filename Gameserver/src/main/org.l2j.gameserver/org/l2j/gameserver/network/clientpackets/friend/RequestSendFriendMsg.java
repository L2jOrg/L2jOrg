package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.FriendSay;
import org.l2j.gameserver.settings.ChatSettings;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.configuration.Configurator.getSettings;


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
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if ((_message == null) || _message.isEmpty() || (_message.length() > 300)) {
            return;
        }

        final Player targetPlayer = World.getInstance().findPlayer(_reciever);
        if ((targetPlayer == null) || !targetPlayer.getFriendList().contains(player.getObjectId())) {
            player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
            return;
        }

        if (getSettings(ChatSettings.class).logChat()) {
            LOGGER_CHAT.info("PRIV_MSG [{} to {}] {}", player, targetPlayer, _message);
        }

        targetPlayer.sendPacket(new FriendSay(player.getName(), _reciever, _message));
    }
}
