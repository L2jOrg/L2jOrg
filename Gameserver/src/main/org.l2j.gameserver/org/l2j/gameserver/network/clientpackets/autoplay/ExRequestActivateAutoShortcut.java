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
package org.l2j.gameserver.network.clientpackets.autoplay;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;
import org.l2j.gameserver.taskmanager.AutoUseTaskManager;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.enums.ShortcutType.ITEM;
import static org.l2j.gameserver.enums.ShortcutType.SKILL;

/**
 * @author JoeAlisson
 */
public class ExRequestActivateAutoShortcut extends ClientPacket {

    private boolean activate;
    private int room;

    @Override
    protected void readImpl()  {
        room = readShort();
        activate = readBoolean();
    }

    @Override
    protected void runImpl() {
        final Player player = client.getPlayer();

        var shortcut = AutoPlayEngine.getInstance().setActiveAutoShortcut(player, room, activate);

        if (isNull(shortcut)) {
            client.sendPacket(new ExActivateAutoShortcut(room, false));
            return;
        }

        var type = shortcut.getType();
        if(type == ITEM) {
            handleRequestAutoUseItem(player, shortcut);
        } else if(type == SKILL) {
            handleRequestAutoUseSkill(player, shortcut);
        }
    }

    private void handleRequestAutoUseSkill(Player player, Shortcut shortcut) {
        var skill = player.getKnownSkill(shortcut.getShortcutId());

        if(Config.AUTO_USE_BUFF && skill.isActive()) {
            if (activate) {
                AutoUseTaskManager.getInstance().addAutoSkill(player, skill.getId());
            } else {
                AutoUseTaskManager.getInstance().removeAutoSkill(player, skill.getId());
            }
        }
    }

    private void handleRequestAutoUseItem(Player player, Shortcut shortcut) {
        var item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());

        if(Config.AUTO_USE_ITEM && !item.isPotion()) {
            if(activate) {
                AutoUseTaskManager.getInstance().addAutoSupplyItem(player, item.getId());
            } else {
                AutoUseTaskManager.getInstance().removeAutoSupplyItem(player, item.getId());
            }
        }
    }
}