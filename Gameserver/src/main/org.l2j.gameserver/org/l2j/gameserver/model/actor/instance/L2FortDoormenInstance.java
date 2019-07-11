/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.StringTokenizer;

public class L2FortDoormenInstance extends L2DoormenInstance {
    public L2FortDoormenInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2FortDoormenInstance);
    }

    @Override
    public void showChatWindow(Player player) {
        player.sendPacket(ActionFailed.STATIC_PACKET);

        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

        if (!isOwnerClan(player)) {
            html.setFile(player, "data/html/doormen/" + getTemplate().getId() + "-no.htm");
        } else if (isUnderSiege()) {
            html.setFile(player, "data/html/doormen/" + getTemplate().getId() + "-busy.htm");
        } else {
            html.setFile(player, "data/html/doormen/" + getTemplate().getId() + ".htm");
        }

        html.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(html);
    }

    @Override
    protected final void openDoors(Player player, String command) {
        final StringTokenizer st = new StringTokenizer(command.substring(10), ", ");
        st.nextToken();

        while (st.hasMoreTokens()) {
            getFort().openDoor(player, Integer.parseInt(st.nextToken()));
        }
    }

    @Override
    protected final void closeDoors(Player player, String command) {
        final StringTokenizer st = new StringTokenizer(command.substring(11), ", ");
        st.nextToken();

        while (st.hasMoreTokens()) {
            getFort().closeDoor(player, Integer.parseInt(st.nextToken()));
        }
    }

    @Override
    protected final boolean isOwnerClan(Player player) {
        if ((player.getClan() != null) && (getFort() != null) && (getFort().getOwnerClan() != null)) {
            if (player.getClanId() == getFort().getOwnerClan().getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected final boolean isUnderSiege() {
        return getFort().getZone().isActive();
    }
}