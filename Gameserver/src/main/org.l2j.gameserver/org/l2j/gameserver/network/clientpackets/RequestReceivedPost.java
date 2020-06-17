/*
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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.network.serverpackets.ExReplyReceivedPost;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 */
public final class RequestReceivedPost extends ClientPacket {
    private int mailId;

    @Override
    public void readImpl() {
        mailId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (!getSettings(GeneralSettings.class).allowMail()) {
            return;
        }

        final var mail = MailEngine.getInstance().getMail(mailId);
        if (mail == null) {
            return;
        }

        if (!player.isInsideZone(ZoneType.PEACE) && mail.hasAttachments()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        if (mail.getReceiver() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to receive not own post!");
            return;
        }

        if (mail.isDeletedByReceiver()) {
            return;
        }

        client.sendPacket(new ExReplyReceivedPost(mail));
        client.sendPacket(ExChangePostState.reAdded(true, mailId));
        MailEngine.getInstance().markAsRead(player, mail);
    }
}
