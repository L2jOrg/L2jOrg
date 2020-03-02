package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 */
public final class RequestRejectPostAttachment extends ClientPacket {
    private int _msgId;

    @Override
    public void readImpl() {
        _msgId = readInt();
    }

    @Override
    public void runImpl() {
        if (!getSettings(GeneralSettings.class).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }

        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("rejectattach")) {
            return;
        }

        if (!activeChar.isInsideZone(ZoneType.PEACE)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        final Message msg = MailManager.getInstance().getMessage(_msgId);
        if (msg == null) {
            return;
        }

        if (msg.getReceiverId() != activeChar.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to reject not own attachment!");
            return;
        }

        if (!msg.hasAttachments() || (msg.getMailType() != MailType.REGULAR)) {
            return;
        }

        MailManager.getInstance().sendMessage(new Message(msg));

        client.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RETURNED);
        client.sendPacket(new ExChangePostState(true, _msgId, Message.REJECTED));

        final Player sender = World.getInstance().findPlayer(msg.getSenderId());
        if (sender != null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_RETURNED_THE_MAIL);
            sm.addString(activeChar.getName());
            sender.sendPacket(sm);
        }
    }
}
