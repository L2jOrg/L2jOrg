/*
 * Copyright © 2019-2021 L2JOrg
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

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zoey76
 */
public final class RequestHennaRemove extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHennaRemove.class);
    private int _symbolId;


    @Override
    public void readImpl() {
        _symbolId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("HennaRemove")) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        Henna henna;
        boolean found = false;
        for (int i = 1; i <= 3; i++) {
            henna = activeChar.getHenna(i);
            if ((henna != null) && (henna.getDyeId() == _symbolId)) {
                if (activeChar.getAdena() >= henna.getCancelFee()) {
                    activeChar.removeHenna(i);
                } else {
                    activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                }
                found = true;
                break;
            }
        }
        // TODO: Test.
        if (!found) {
            LOGGER.warn(getClass().getSimpleName() + ": Player " + activeChar + " requested Henna Draw remove without any henna.");
            client.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
