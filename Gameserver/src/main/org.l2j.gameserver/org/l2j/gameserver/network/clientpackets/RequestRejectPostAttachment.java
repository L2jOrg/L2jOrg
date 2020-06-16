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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 */
public final class RequestRejectPostAttachment extends ClientPacket {
    private int mailId;

    @Override
    public void readImpl() {
        mailId = readInt();
    }

    @Override
    public void runImpl() {
        if (!getSettings(GeneralSettings.class).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }

        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("rejectattach")) {
            return;
        }

        if (!player.isInsideZone(ZoneType.PEACE)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        final var mail = MailEngine.getInstance().getMail(mailId);
        if (mail == null) {
            return;
        }

        if (mail.getReceiver() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player + " tried to reject not own attachment!");
            return;
        }

        if (!mail.hasAttachments() || (mail.getType() != MailType.REGULAR)) {
            return;
        }

        MailEngine.getInstance().sendMail(mail.asReturned());

        client.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RETURNED);
        client.sendPacket(ExChangePostState.rejected(true, mailId));

        final Player sender = World.getInstance().findPlayer(mail.getSender());
        if (sender != null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_RETURNED_THE_MAIL);
            sm.addString(player.getName());
            sender.sendPacket(sm);
        }
    }
}
