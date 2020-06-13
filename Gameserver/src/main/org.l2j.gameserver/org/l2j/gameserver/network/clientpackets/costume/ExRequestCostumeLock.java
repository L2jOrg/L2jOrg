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
package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeLock;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;

import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.network.SystemMessageId.CANNOT_EDIT_THE_LOCK_TRANSFORMATION_SETTING_DURING_A_BATTLE;

/**
 * @author JoeAlisson
 */
public class ExRequestCostumeLock extends ClientPacket {

    private int id;
    private boolean lock;

    @Override
    protected void readImpl() throws Exception {
        id = readInt();
        lock = readBoolean();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        if(AttackStanceTaskManager.getInstance().hasAttackStanceTask(player)) {
            client.sendPacket(CANNOT_EDIT_THE_LOCK_TRANSFORMATION_SETTING_DURING_A_BATTLE);
            return;
        }

        doIfNonNull(player.getCostume(id), costume -> {
            costume.setLocked(lock);
            client.sendPacket(new ExCostumeLock(id, lock, true));
        });
    }
}
