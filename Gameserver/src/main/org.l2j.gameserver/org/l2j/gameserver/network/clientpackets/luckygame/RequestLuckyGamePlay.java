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
package org.l2j.gameserver.network.clientpackets.luckygame;

import org.l2j.commons.util.CommonUtil;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.LuckyGameData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.LuckyGameItemType;
import org.l2j.gameserver.enums.LuckyGameResultType;
import org.l2j.gameserver.enums.LuckyGameType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.LuckyGameDataHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.luckygame.ExBettingLuckyGameResult;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Sdw
 */
public class RequestLuckyGamePlay extends ClientPacket {
    private static final int FORTUNE_READING_TICKET = 23767;
    private static final int LUXURY_FORTUNE_READING_TICKET = 23768;
    private LuckyGameType _type;
    private int _reading;

    @Override
    public void readImpl() {
        final int type = CommonUtil.constrain(readInt(), 0, LuckyGameType.values().length);
        _type = LuckyGameType.values()[type];
        _reading = CommonUtil.constrain(readInt(), 0, 50); // max play is 50
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final int index = _type == LuckyGameType.LUXURY ? 102 : 2; // move to event config

        final LuckyGameDataHolder holder = LuckyGameData.getInstance().getLuckyGameDataByIndex(index);
        if (holder == null) {
            return;
        }

        final long tickets = _type == LuckyGameType.LUXURY ? player.getInventory().getInventoryItemCount(LUXURY_FORTUNE_READING_TICKET, -1) : player.getInventory().getInventoryItemCount(FORTUNE_READING_TICKET, -1);
        if (tickets < _reading) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_TICKETS_YOU_CANNOT_CONTINUE_THE_GAME);
            player.sendPacket(_type == LuckyGameType.LUXURY ? ExBettingLuckyGameResult.LUXURY_INVALID_ITEM_COUNT : ExBettingLuckyGameResult.NORMAL_INVALID_ITEM_COUNT);
            return;
        }

        int playCount = player.getVariables().getInt(PlayerVariables.FORTUNE_TELLING_VARIABLE, 0);
        boolean blackCat = player.getVariables().getBoolean(PlayerVariables.FORTUNE_TELLING_BLACK_CAT_VARIABLE, false);
        final EnumMap<LuckyGameItemType, List<ItemHolder>> rewards = new EnumMap<>(LuckyGameItemType.class);
        for (int i = 0; i < _reading; i++) {
            final double chance = 100 * Rnd.nextDouble();
            double totalChance = 0;

            for (ItemChanceHolder item : holder.getCommonReward()) {
                totalChance += item.getChance();
                if (totalChance >= chance) {
                    rewards.computeIfAbsent(LuckyGameItemType.COMMON, k -> new ArrayList<>()).add(item);
                    break;
                }
            }
            playCount++;
            if ((playCount >= holder.getMinModifyRewardGame()) && (playCount <= holder.getMaxModifyRewardGame()) && !blackCat) {
                final List<ItemChanceHolder> modifyReward = holder.getModifyReward();
                final double chanceModify = 100 * Rnd.nextDouble();
                totalChance = 0;

                for (ItemChanceHolder item : modifyReward) {
                    totalChance += item.getChance();
                    if (totalChance >= chanceModify) {
                        rewards.computeIfAbsent(LuckyGameItemType.RARE, k -> new ArrayList<>()).add(item);
                        blackCat = true;
                        break;
                    }
                }

                if (playCount == holder.getMaxModifyRewardGame()) {
                    rewards.computeIfAbsent(LuckyGameItemType.RARE, k -> new ArrayList<>()).add(modifyReward.get(Rnd.get(modifyReward.size())));
                    blackCat = true;
                }
            }
        }

        final int totalWeight = rewards.values().stream().mapToInt(list -> list.stream().mapToInt(item -> ItemEngine.getInstance().getTemplate(item.getId()).getWeight()).sum()).sum();

        // Check inventory capacity
        if ((rewards.size() > 0) && (!player.getInventory().validateCapacity(rewards.size()) || !player.getInventory().validateWeight(totalWeight))) {
            player.sendPacket(_type == LuckyGameType.LUXURY ? ExBettingLuckyGameResult.LUXURY_INVALID_CAPACITY : ExBettingLuckyGameResult.NORMAL_INVALID_CAPACITY);
            player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_EITHER_FULL_OR_OVERWEIGHT);
            return;
        }

        if (!player.destroyItemByItemId("LuckyGame", _type == LuckyGameType.LUXURY ? LUXURY_FORTUNE_READING_TICKET : FORTUNE_READING_TICKET, _reading, player, true)) {
            player.sendPacket(_type == LuckyGameType.LUXURY ? ExBettingLuckyGameResult.LUXURY_INVALID_ITEM_COUNT : ExBettingLuckyGameResult.NORMAL_INVALID_ITEM_COUNT);
            return;
        }

        for (int i = 0; i < _reading; i++) {
            final int serverGameNumber = LuckyGameData.getInstance().increaseGame();
            holder.getUniqueReward().stream().filter(reward -> reward.getPoints() == serverGameNumber).forEach(item -> rewards.computeIfAbsent(LuckyGameItemType.UNIQUE, k -> new ArrayList<>()).add(item));
        }

        player.sendPacket(new ExBettingLuckyGameResult(LuckyGameResultType.SUCCESS, _type, rewards, (int) (_type == LuckyGameType.LUXURY ? player.getInventory().getInventoryItemCount(LUXURY_FORTUNE_READING_TICKET, -1) : player.getInventory().getInventoryItemCount(FORTUNE_READING_TICKET, -1))));

        final InventoryUpdate iu = new InventoryUpdate();
        for (Entry<LuckyGameItemType, List<ItemHolder>> reward : rewards.entrySet()) {
            for (ItemHolder r : reward.getValue()) {
                final Item item = player.addItem("LuckyGame", r.getId(), r.getCount(), player, true);
                iu.addItem(item);
                if (reward.getKey() == LuckyGameItemType.UNIQUE) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_C1_HAS_OBTAINED_S2_OF_S3_THROUGH_FORTUNE_READING);
                    sm.addPcName(player);
                    sm.addLong(r.getCount());
                    sm.addItemName(item);
                    player.broadcastPacket(sm, 1000);
                    break;
                }

            }
        }

        player.sendInventoryUpdate(iu);

        player.getVariables().set(PlayerVariables.FORTUNE_TELLING_VARIABLE, playCount >= 50 ? (playCount - 50) : playCount);
        if (blackCat && (playCount < 50)) {
            player.getVariables().set(PlayerVariables.FORTUNE_TELLING_BLACK_CAT_VARIABLE, true);
        }
    }
}
