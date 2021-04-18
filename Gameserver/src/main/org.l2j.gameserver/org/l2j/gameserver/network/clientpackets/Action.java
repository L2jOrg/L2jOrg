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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * @author JoeAlisson
 */
public final class Action extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);
    
    private int objectId;
    private int actionId;

    @Override
    public void readImpl() {
        objectId = readInt(); // Target object Identifier
        readInt(); // origin x
        readInt(); // origin y
        readInt(); // origin z
        actionId = readByte(); // Action identifier : 0-Simple click, 1-Shift click
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();

        if (!canPlayerInteract(player)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final WorldObject obj;
        if (player.getTargetId() == objectId) {
            obj = player.getTarget();
        } else {
            obj = World.getInstance().findObject(objectId);
        }

        if (!canPlayerInteractWith(player, obj)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.onActionRequest();

        switch (actionId) {
            case 0 -> obj.onAction(player);
            case 1 -> onShiftAction(player, obj);
            default -> onUnknownAction(player);
        }
    }

    private void onShiftAction(Player player, WorldObject obj) {
        if (!player.isGM() && (!(isNpc(obj) && Config.ALT_GAME_VIEWNPC))) {
            obj.onAction(player, false);
        } else {
            obj.onActionShift(player);
        }
    }

    private void onUnknownAction(Player player) {
        LOGGER.warn("{} requested invalid action: {}", player, actionId);
        client.sendPacket(ActionFailed.STATIC_PACKET);
    }

    private boolean canPlayerInteractWith(Player player, WorldObject obj) {
        if (isNull(obj) || (!obj.isTargetable() || player.isTargetingDisabled()) && !player.canOverrideCond(PcCondOverride.TARGET_ALL)) {
            return false;
        }

        return obj.getInstanceWorld() == player.getInstanceWorld() && obj.isVisibleFor(player);
    }

    private boolean canPlayerInteract(Player player) {
        if (player.isInObserverMode()) {
            player.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
            return false;
        }

        if (player.getActiveRequester() != null) {
            return false;
        }

        final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (nonNull(info)) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(-4)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    return false;
                }
            }
        }
        return true;
    }
}
