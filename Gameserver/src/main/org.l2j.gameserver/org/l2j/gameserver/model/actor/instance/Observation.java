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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author NightMarez
 * @version $Revision: 1.3.2.2.2.5 $ $Date: 2005/03/27 15:29:32 $
 */
public final class Observation extends Npc {
    public Observation(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2ObservationInstance);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        String filename;

        if (MathUtil.isInsideRadius2D(this,-79884, 86529, 50) || MathUtil.isInsideRadius2D(this,-78858, 111358,  50) || MathUtil.isInsideRadius2D(this,-76973, 87136, 50) || MathUtil.isInsideRadius2D(this, -75850, 111968, 50)) {
            if (val == 0) {
                filename = "data/html/observation/" + getId() + "-Oracle.htm";
            } else {
                filename = "data/html/observation/" + getId() + "-Oracle-" + val + ".htm";
            }
        } else if (val == 0) {
            filename = "data/html/observation/" + getId() + ".htm";
        } else {
            filename = "data/html/observation/" + getId() + "-" + val + ".htm";
        }

        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(player, filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        player.sendPacket(html);
    }
}