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
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 */
public final class RequestDeleteReceivedPost extends ClientPacket {
    private static final int BATCH_LENGTH = 4; // length of the one item

    int[] _msgIds = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        final int count = readInt();
        if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != available())) {
            throw new InvalidDataPacketException();
        }

        _msgIds = new int[count];
        for (int i = 0; i < count; i++) {
            _msgIds[i] = readInt();
        }
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (isNull(activeChar) || isNull(_msgIds) || !getSettings(GeneralSettings.class).allowMail()) {
            return;
        }

        if (!activeChar.isInsideZone(ZoneType.PEACE)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        for (int msgId : _msgIds) {
            final Message msg = MailManager.getInstance().getMessage(msgId);
            if (msg == null) {
                continue;
            }
            if (msg.getReceiverId() != activeChar.getObjectId()) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to delete not own post!");
                return;
            }

            if (msg.hasAttachments() || msg.isDeletedByReceiver()) {
                return;
            }

            msg.setDeletedByReceiver();
        }
        client.sendPacket(new ExChangePostState(true, _msgIds, Message.DELETED));
    }
}
