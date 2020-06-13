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
package org.l2j.gameserver.network.clientpackets.crystalization;

import org.l2j.gameserver.data.xml.impl.ItemCrystallizationData;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.crystalization.ExGetCrystalizingEstimation;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author UnAfraid
 */
public class RequestCrystallizeEstimate extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestCrystallizeEstimate.class);

    private int _objectId;
    private long _count;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _count = readLong();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if ((activeChar == null) || activeChar.isInCrystallize()) {
            return;
        }

        // if (!client.getFloodProtectors().getTransaction().tryPerformAction("crystallize"))
        // {
        // activeChar.sendMessage("You are crystallizing too fast.");
        // return;
        // }

        if (_count <= 0) {
            GameUtils.handleIllegalPlayerAction(activeChar, "[RequestCrystallizeItem] count <= 0! ban! oid: " + _objectId + " owner: " + activeChar.getName());
            return;
        }

        if ((activeChar.getPrivateStoreType() != PrivateStoreType.NONE) || activeChar.isInCrystallize()) {
            client.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        final int skillLevel = activeChar.getSkillLevel(CommonSkill.CRYSTALLIZE.getId());
        if (skillLevel <= 0) {
            client.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Item item = activeChar.getInventory().getItemByObjectId(_objectId);
        if ((item == null) || item.isTimeLimitedItem() || item.isHeroItem()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!item.getTemplate().isCrystallizable() || (item.getTemplate().getCrystalCount() <= 0) || (item.getTemplate().getCrystalType() == CrystalType.NONE)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            LOGGER.warn("{} tried to crystallize {}", activeChar, item.getTemplate());
            return;
        }

        if (_count > item.getCount()) {
            _count = activeChar.getInventory().getItemByObjectId(_objectId).getCount();
        }

        if (activeChar.getInventory().isBlocked(item)) {
            activeChar.sendMessage("You cannot use this item.");
            return;
        }

        // Check if the char can crystallize items and return if false;
        boolean canCrystallize = true;

        switch (item.getTemplate().getCrystalType()) {
            case D: {
                if (skillLevel < 1) {
                    canCrystallize = false;
                }
                break;
            }
            case C: {
                if (skillLevel < 2) {
                    canCrystallize = false;
                }
                break;
            }
            case B: {
                if (skillLevel < 3) {
                    canCrystallize = false;
                }
                break;
            }
            case A: {
                if (skillLevel < 4) {
                    canCrystallize = false;
                }
                break;
            }
            case S: {
                if (skillLevel < 5) {
                    canCrystallize = false;
                }
                break;
            }
        }

        if (!canCrystallize) {
            client.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Show crystallization rewards window.
        final List<ItemChanceHolder> crystallizationRewards = ItemCrystallizationData.getInstance().getCrystallizationRewards(item);
        if ((crystallizationRewards != null) && !crystallizationRewards.isEmpty()) {
            activeChar.setInCrystallize(true);
            client.sendPacket(new ExGetCrystalizingEstimation(crystallizationRewards));
        } else {
            client.sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
        }

    }
}
