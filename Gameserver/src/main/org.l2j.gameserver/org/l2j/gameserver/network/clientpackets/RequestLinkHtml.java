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

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lets drink to code!
 *
 * @author zabbix, HorridoJoho
 */
public final class RequestLinkHtml extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLinkHtml.class);
    private String _link;

    @Override
    public void readImpl() {
        _link = readString();
    }

    @Override
    public void runImpl() {
        final Player actor = client.getPlayer();
        if (actor == null) {
            return;
        }

        if (_link.isEmpty()) {
            LOGGER.warn("Player " + actor.getName() + " sent empty html link!");
            return;
        }

        if (_link.contains("..")) {
            LOGGER.warn("Player " + actor.getName() + " sent invalid html link: link " + _link);
            return;
        }

        final int htmlObjectId = actor.validateHtmlAction("link " + _link);
        if (htmlObjectId == -1) {
            LOGGER.warn("Player " + actor.getName() + " sent non cached  html link: link " + _link);
            return;
        }

        if ((htmlObjectId > 0) && !GameUtils.isInsideRangeOfObjectId(actor, htmlObjectId, Npc.INTERACTION_DISTANCE)) {
            // No logging here, this could be a common case
            return;
        }

        final String filename = "data/html/" + _link;
        final NpcHtmlMessage msg = new NpcHtmlMessage(htmlObjectId);
        msg.setFile(actor, filename);
        actor.sendPacket(msg);
    }
}
