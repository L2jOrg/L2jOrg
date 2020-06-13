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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * TODO: This class is a copy of AttackRequest, we should get proper structure for both.
 */
public final class Attack extends ClientPacket {
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
        final Player player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        // Avoid Attacks in Boat.
        if (isPlayable(player) && player.isInBoat()) {
            player.sendPacket(SystemMessageId.THIS_IS_NOT_ALLOWED_WHILE_RIDING_A_FERRY_OR_BOAT);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (nonNull(info)) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(-1)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        // avoid using expensive operations if not needed
        final WorldObject target;
        if (player.getTargetId() == _objectId) {
            target = player.getTarget();
        } else {
            target = World.getInstance().findObject(_objectId);
        }

        if (isNull(target )) {
            return;
        }

        if ((!target.isTargetable() || player.isTargetingDisabled()) && !player.canOverrideCond(PcCondOverride.TARGET_ALL)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Players can't attack objects in the other instances
        // Only GMs can directly attack invisible characters
        else if (target.getInstanceWorld() != player.getInstanceWorld() || !target.isVisibleFor(player)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.onActionRequest();

        if (player.getTarget() != target) {
            target.onAction(player);
        } else if ((target.getObjectId() != player.getObjectId()) && (player.getPrivateStoreType() == PrivateStoreType.NONE) && (player.getActiveRequester() == null)) {
            target.onForcedAttack(player);
        } else {
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
