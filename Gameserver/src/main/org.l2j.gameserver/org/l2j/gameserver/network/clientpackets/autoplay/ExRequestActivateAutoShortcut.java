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
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.autoplay.ExActivateAutoShortcut;
import org.l2j.gameserver.taskmanager.AutoUseTaskManager;

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
        if (player == null)
        {
            return;
        }
        final Shortcut shortcut = player.getShortcut(room);
        if (shortcut == null)
        {
            return;
        }
        final Item item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
        Skill skill = null;
        if (item == null)
        {
            skill = player.getKnownSkill(shortcut.getShortcutId());
        }
        if(AutoPlayEngine.getInstance().setActiveAutoShortcut(client.getPlayer(), room, activate)) {
            client.sendPacket(new ExActivateAutoShortcut(room, activate));
        } else {
            client.sendPacket(new ExActivateAutoShortcut(room, false));
        }
        if (!activate)
        {
            if (skill != null)
            {
                AutoUseTaskManager.getInstance().removeAutoSkill(player, skill.getId());
            }
            if (item != null) {
                if (!item.isPotion()) {
                    AutoUseTaskManager.getInstance().removeAutoSupplyItem(player, item.getId());
                }
            }
        }
        else {
            if ((item != null) && !item.isPotion())
            {
                // auto supply
                if (Config.AUTO_USE_ITEM)
                {
                    AutoUseTaskManager.getInstance().addAutoSupplyItem(player, item.getId());
                }
            }
            else {
                if (Config.AUTO_USE_BUFF && (shortcut.getType() == ShortcutType.SKILL)) {
                    assert skill != null;
                    if (skill.isAutoUse() && skill.isAutoBuff()) {
                        AutoUseTaskManager.getInstance().addAutoSkill(player, skill.getId());
                    }
                }
            }
        }
    }
}