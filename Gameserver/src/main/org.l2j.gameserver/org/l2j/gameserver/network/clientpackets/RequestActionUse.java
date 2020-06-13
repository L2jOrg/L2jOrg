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

import org.l2j.gameserver.data.xml.ActionManager;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.PlayerActionHandler;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.serverpackets.RecipeShopManageList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static java.util.Objects.nonNull;

/**
 * This class manages the action use request packet.
 *
 * @author Zoey76
 * @author JoeAlisson
 */
public final class RequestActionUse extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestActionUse.class);

    private int actionId;
    private boolean ctrlPressed;
    private boolean shiftPressed;

    @Override
    public void readImpl() {
        actionId = readInt();
        ctrlPressed = readIntAsBoolean();
        shiftPressed = readBoolean();
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();

        // Don't do anything if player is dead or confused
        if ((player.isFakeDeath() && (actionId != 0)) || player.isDead() || player.isControlBlocked()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (nonNull(info)) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(actionId)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    player.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        final int[] allowedActions = player.isTransformed() ? ExBasicActionList.ACTIONS_ON_TRANSFORM : ExBasicActionList.DEFAULT_ACTION_LIST;
        if (!(Arrays.binarySearch(allowedActions, actionId) >= 0)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            LOGGER.warn("Player {} used action which he does not have! Id = {} transform: {}", player, actionId, player.getTransformation().orElse(null));
            return;
        }

        var action = ActionManager.getInstance().getActionData(actionId);
        if (nonNull(action)) {
            var handler = PlayerActionHandler.getInstance().getHandler(action.getHandler());
            if (nonNull(handler)) {
                handler.useAction(player, action, ctrlPressed, shiftPressed);
                return;
            }
            LOGGER.warn("Couldn't find handler with name: {}", action.getHandler());
            return;
        }

        if (actionId == 51) { // General Manufacture
            // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
            if (player.isAlikeDead()) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            if (player.isSellingBuffs()) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
                player.setPrivateStoreType(PrivateStoreType.NONE);
                player.broadcastUserInfo();
            }
            if (player.isSitting()) {
                player.standUp();
            }

            client.sendPacket(new RecipeShopManageList(player, false));
        } else {
            LOGGER.warn("{}: unhandled action type {}", player, actionId);
        }
    }
}
