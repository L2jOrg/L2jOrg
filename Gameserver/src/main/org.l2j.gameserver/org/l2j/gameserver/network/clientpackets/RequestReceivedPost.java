package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.network.serverpackets.ExReplyReceivedPost;
import org.l2j.gameserver.util.GameUtils;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 */
public final class RequestReceivedPost extends ClientPacket {
    private int _msgId;

    @Override
    public void readImpl() {
        _msgId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (isNull(activeChar) || !getSettings(GeneralSettings.class).allowMail()) {
            return;
        }

        final Message msg = MailManager.getInstance().getMessage(_msgId);
        if (msg == null) {
            return;
        }

        if (!activeChar.isInsideZone(ZoneType.PEACE) && msg.hasAttachments()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        if (msg.getReceiverId() != activeChar.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to receive not own post!");
            return;
        }

        if (msg.isDeletedByReceiver()) {
            return;
        }

        client.sendPacket(new ExReplyReceivedPost(msg));
        client.sendPacket(new ExChangePostState(true, _msgId, Message.READED));
        msg.markAsRead();
    }
}
