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

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.world.World;

public final class AttackRequest extends ClientPacket {
    // cddddc
    private int _objectId;
    @SuppressWarnings("unused")
    private int _originX;
    @SuppressWarnings("unused")
    private int _originY;
    @SuppressWarnings("unused")
    private int _originZ;
    @SuppressWarnings("unused")
    private int _attackId;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _originX = readInt();
        _originY = readInt();
        _originZ = readInt();
        _attackId = readByte(); // 0 for simple click 1 for shift-click
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final BuffInfo info = activeChar.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(-1)) {
                    activeChar.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        // avoid using expensive operations if not needed
        final WorldObject target;
        if (activeChar.getTargetId() == _objectId) {
            target = activeChar.getTarget();
        } else {
            target = World.getInstance().findObject(_objectId);
        }

        if (target == null) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        } else if ((!target.isTargetable() || activeChar.isTargetingDisabled()) && !activeChar.canOverrideCond(PcCondOverride.TARGET_ALL)) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        // Players can't attack objects in the other instances
        // Only GMs can directly attack invisible characters
        else if (target.getInstanceWorld() != activeChar.getInstanceWorld() || !target.isVisibleFor(activeChar)) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (activeChar.getTarget() != target) {
            target.onAction(activeChar);
        } else if ((target.getObjectId() != activeChar.getObjectId()) && (activeChar.getPrivateStoreType() == PrivateStoreType.NONE) && (activeChar.getActiveRequester() == null)) {
            target.onForcedAttack(activeChar);
        } else {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
