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

import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Message deletion task.
 *
 * @author xban1x
 */
public final class MessageDeletionTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDeletionTask.class);

    final int _msgId;

    public MessageDeletionTask(int msgId) {
        _msgId = msgId;
    }

    @Override
    public void run() {
        final Message msg = MailManager.getInstance().getMessage(_msgId);
        if (msg == null) {
            return;
        }

        if (msg.hasAttachments()) {
            try {
                final Player sender = World.getInstance().findPlayer(msg.getSenderId());
                if (sender != null) {
                    msg.getAttachments().returnToWh(sender.getWarehouse());
                    sender.sendPacket(SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME);
                } else {
                    msg.getAttachments().returnToWh(null);
                }

                msg.getAttachments().deleteMe();
                msg.removeAttachments();

                final Player receiver = World.getInstance().findPlayer(msg.getReceiverId());
                if (receiver != null) {
                    receiver.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME));
                }
            } catch (Exception e) {
                LOGGER.warn(getClass().getSimpleName() + ": Error returning items:" + e.getMessage(), e);
            }
        }
        MailManager.getInstance().deleteMessageInDb(msg.getId());
    }
}
