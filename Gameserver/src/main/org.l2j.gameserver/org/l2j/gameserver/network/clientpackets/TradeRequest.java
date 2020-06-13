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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SendTradeRequest;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * This packet manages the trade request.
 * @author JoeAlisson
 */
public final class TradeRequest extends ClientPacket {
    private int objectId;

    @Override
    public void readImpl() {
        objectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your current Access Level.");
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        BuffInfo info = player.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (nonNull(info)) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(ReportTable.TRADE_ACTION_BLOCK_ID)) {
                    client.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        var partner = World.getInstance().findPlayer(objectId);
        // If there is no target, target is far away or
        // they are in different instances
        // trade request is ignored and there is no system message.
        if (isNull(partner) || !player.isInSurroundingRegion(partner) || (partner.getInstanceWorld() != player.getInstanceWorld())) {
            return;
        }

        // If target and acting player are the same, trade request is ignored
        // and the following system message is sent to acting player.
        if (partner.equals(player)) {
            client.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        if (!isPlayer(partner)) {
            client.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }

        if (partner.isInOlympiadMode() || player.isInOlympiadMode()) {
            player.sendPacket(SystemMessageId.CANNOT_TRADE_ITEMS_WITH_THE_TARGETED_USER);
            return;
        }

        info = partner.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (nonNull(info)) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(ReportTable.TRADE_ACTION_BLOCK_ID)) {
                    final SystemMessage sm = getSystemMessage(SystemMessageId.C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_IS_CURRENTLY_BEING_INVESTIGATED);
                    sm.addString(partner.getName());
                    client.sendPacket(sm);
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        // L2J Customs: Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (player.getReputation() < 0)) {
            player.sendMessage("You cannot trade while you are in a chaotic state.");
            return;
        }

        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (partner.getReputation() < 0)) {
            player.sendMessage("You cannot request a trade while your target is in a chaotic state.");
            return;
        }

        if (Config.JAIL_DISABLE_TRANSACTION && (player.isJailed() || partner.isJailed())) {
            player.sendMessage("You cannot trade while you are in in Jail.");
            return;
        }

        if ((player.getPrivateStoreType() != PrivateStoreType.NONE) || (partner.getPrivateStoreType() != PrivateStoreType.NONE)) {
            client.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (player.isProcessingTransaction()) {
            client.sendPacket(SystemMessageId.YOU_ARE_ALREADY_TRADING_WITH_SOMEONE);
            return;
        }

        if (partner.isProcessingRequest() || partner.isProcessingTransaction()) {
            client.sendPacket(getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER).addString(partner.getName()));
            return;
        }

        if (partner.isTradeRefusing()) {
            player.sendMessage("That person is in trade refusal mode.");
            return;
        }

        if (BlockList.isBlocked(partner, player)) {
            client.sendPacket(getSystemMessage(SystemMessageId.C1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST).addString(partner.getName()));
            return;
        }

        if (!isInsideRadius3D(player, partner, 150)) {
            client.sendPacket(SystemMessageId.YOU_ARE_TOO_FAR_WAY_TO_TRADE);
            return;
        }

        player.onTransactionRequest(partner);
        partner.sendPacket(new SendTradeRequest(player.getObjectId()));
        client.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_REQUESTED_A_TRADE_WITH_C1).addString(partner.getName()));
    }
}
