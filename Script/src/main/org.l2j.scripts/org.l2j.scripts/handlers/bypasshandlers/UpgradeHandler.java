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
package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.api.item.UpgradeAPI;
import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.MathUtil;

import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.SPACE;

/**
 * @author JoeAlisson
 */
public class UpgradeHandler implements IBypassHandler {

    @Override
    public boolean useBypass(String command, Player player, Creature npc) {
        if(isNull(npc) || !MathUtil.isInsideRadius3D(player, npc, Npc.INTERACTION_DISTANCE) || !command.contains(SPACE)) {
            return false;
        }

        final var typeName = command.split(SPACE)[1];
        return UpgradeAPI.showUpgradeUI(player, UpgradeType.valueOf(typeName));
    }

    @Override
    public String[] getBypassList() {
        return new String[] { "upgrade_item" };
    }
}
