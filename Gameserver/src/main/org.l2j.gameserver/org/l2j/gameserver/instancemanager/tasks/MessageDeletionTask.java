/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.instancemanager.tasks;

import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.network.SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * Message deletion task.
 *
 * @author xban1x
 * @author JoeAlisson
 */
public final class MessageDeletionTask implements Runnable {

    final int mailId;

    public MessageDeletionTask(int msgId) {
        mailId = msgId;
    }

    @Override
    public void run() {
        final var mail = MailEngine.getInstance().getMail(mailId);
        if (isNull(mail)) {
            return;
        }

        if (mail.hasAttachments()) {
            doIfNonNull(mail.getAttachment(), attachment -> {
                final var sender = World.getInstance().findPlayer(mail.getSender());
                if(nonNull(sender)) {
                    attachment.returnToWh(sender.getWarehouse());
                    sender.sendPacket(THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME);
                } else {
                    attachment.returnToWh(null);
                }
                attachment.deleteMe();
            });
            mail.removeAttachments();

            doIfNonNull(World.getInstance().findPlayer(mail.getReceiver()), receiver -> receiver.sendPacket(getSystemMessage(THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME)));
        }
        MailEngine.getInstance().deleteMailInDb(mail.getId());
    }
}
