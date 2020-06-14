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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.NextAction;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExUseSharedGroupItem;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isBetween;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isItem;

/**
 * @author JoeAlisson
 */
public final class UseItem extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(UseItem.class);
    private int objectId;
    private boolean ctrlPressed;
    private int itemId;

    @Override
    public void readImpl() {
        objectId = readInt();
        ctrlPressed = readInt() != 0;
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        // Flood protect UseItem
        if (!client.getFloodProtectors().getUseItem().tryPerformAction("use item")) {
            return;
        }

        if (nonNull(player.getActiveTradeList())) {
            player.cancelActiveTrade();
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Item item = player.getInventory().getItemByObjectId(objectId);
        if (isNull(item)) {
            // gm can use other player item
            if (player.isGM()) {
                var obj = World.getInstance().findObject(objectId);
                if (isItem(obj)) {
                    AdminCommandHandler.getInstance().useAdminCommand(player, "admin_use_item " + objectId, true);
                }
            }
            return;
        }

        if (item.isQuestItem())  {
            player.sendPacket(YOU_CANNOT_USE_QUEST_ITEMS);
            return;
        }

        // No UseItem is allowed while the player is in special conditions
        if (player.hasBlockActions() || player.isControlBlocked() || player.isAlikeDead()) {
            return;
        }

        // Char cannot use item when dead
        if (player.isDead() || player.getInventory().isBlocked(item)) {
            var sm = getSystemMessage(S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addItemName(item);
            player.sendPacket(sm);
            return;
        }

        if (!item.isEquipped() && !item.getTemplate().checkCondition(player, player, true)) {
            return;
        }

        itemId = item.getId();
        if (player.isFishing() && !isBetween(itemId, 6535, 6540)) { // FIXME non existent ids
            // You cannot do anything else while fishing
            player.sendPacket(YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
            return;
        }

        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (player.getReputation() < 0)) {
            final List<ItemSkillHolder> skills = item.getSkills(ItemSkillType.NORMAL);
            if ((skills != null) && skills.stream().anyMatch(holder -> holder.getSkill().hasAnyEffectType(EffectType.TELEPORT))) {
                return;
            }
        }

        // If the item has reuse time and it has not passed.
        // Message from reuse delay must come from item.
        final int reuseDelay = item.getReuseDelay();
        final int sharedReuseGroup = item.getSharedReuseGroup();
        if (reuseDelay > 0) {
            final long reuse = player.getItemRemainingReuseTime(item.getObjectId());
            if (reuse > 0) {
                reuseData(player, item, reuse);
                sendSharedGroupUpdate(player, sharedReuseGroup, reuse, reuseDelay);
                return;
            }

            final long reuseOnGroup = player.getReuseDelayOnGroup(sharedReuseGroup);
            if (reuseOnGroup > 0) {
                reuseData(player, item, reuseOnGroup);
                sendSharedGroupUpdate(player, sharedReuseGroup, reuseOnGroup, reuseDelay);
                return;
            }
        }

        player.onActionRequest();
        if (item.isEquipable()) {
            handleEquipable(player, item);
        } else {
            final EtcItem etcItem = item.getEtcItem();
            final IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);
            if (isNull(handler)) {
                if(nonNull(etcItem) && Util.isNotEmpty(etcItem.getHandlerName())) {
                    LOGGER.warn("Unmanaged Item handler: {} for Item Id: {}!", etcItem.getHandlerName(), itemId);
                }
            } else if (handler.useItem(player, item, ctrlPressed)) {
                // Item reuse time should be added if the item is successfully used.
                // Skill reuse delay is done at handlers.itemhandlers.ItemSkillsTemplate;
                if (reuseDelay > 0) {
                    player.addTimeStampItem(item, reuseDelay);
                    sendSharedGroupUpdate(player, sharedReuseGroup, reuseDelay, reuseDelay);
                }
            }
        }
    }

    private void handleEquipable(Player player, Item item) {
        if (!checkCanUse(player, item)) {
            player.sendPacket(YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
            return;
        }

        if (player.isCastingNow()) {
            player.getAI().setNextAction(new NextAction(CtrlEvent.EVT_FINISH_CASTING, CtrlIntention.AI_INTENTION_CAST, () -> player.useEquippableItem(item, true)));
        } else if (player.isAttackingNow()) {
            ThreadPool.schedule(() -> {
                var usedItem = player.getInventory().getItemByObjectId(objectId);

                if(isNull(usedItem)) {
                    return;
                }
                // Equip or unEquip
                player.useEquippableItem(usedItem, false);
            }, player.getAttackEndTime() - TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()));
        } else {
            player.useEquippableItem(item, true);
        }
    }

    private boolean checkCanUse(Player player, Item item) {
        var bodyPart = item.getBodyPart();
        return checkUnlockedSlot(player, item, bodyPart) && (!item.isHeroItem() || player.isHero() || player.canOverrideCond(PcCondOverride.ITEM_CONDITIONS))
                &&  !player.getInventory().isItemSlotBlocked(bodyPart);
    }

    private boolean checkUnlockedSlot(Player player, Item item, BodyPart bodyPart) {
        switch (bodyPart) {
            case TWO_HAND, LEFT_HAND, RIGHT_HAND -> {
                if (player.isMounted() || player.isDisarmed()) {
                    return false;
                }
            }
            case TALISMAN -> {
                if (!item.isEquipped() && (player.getInventory().getTalismanSlots() == 0)) {
                    player.sendPacket(getSystemMessage(YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET).addItemName(item));
                    return false;
                }
            }
            case BROOCH_JEWEL -> {
                if (!item.isEquipped() && (player.getInventory().getBroochJewelSlots() == 0)) {
                    player.sendPacket(getSystemMessage(YOU_CANNOT_EQUIP_S1_WITHOUT_EQUIPPING_A_BROOCH).addItemName(item));
                    return false;
                }
            }
            case AGATHION -> {
                if (!item.isEquipped() && (player.getInventory().getAgathionSlots() == 0)) {
                    return false;
                }
            }
            case ARTIFACT -> {
                if (!item.isEquipped() && (player.getInventory().getArtifactSlots() == 0)) {
                    player.sendPacket(getSystemMessage(NO_ARTIFACT_BOOK_EQUIPPED_YOU_CANNOT_EQUIP_S1).addItemName(item));
                    return false;
                }
            }
        }
        return true;
    }

    private void reuseData(Player activeChar, Item item, long remainingTime) {
        final int hours = (int) (remainingTime / 3600000);
        final int minutes = (int) (remainingTime % 3600000) / 60000;
        final int seconds = (int) ((remainingTime / 1000) % 60);
        final SystemMessage sm;
        if (hours > 0) {
            sm = getSystemMessage(THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
            sm.addItemName(item);
            sm.addInt(hours);
            sm.addInt(minutes);
        } else if (minutes > 0) {
            sm = getSystemMessage(THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
            sm.addItemName(item);
            sm.addInt(minutes);
        } else {
            sm = getSystemMessage(THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
            sm.addItemName(item);
        }
        sm.addInt(seconds);
        activeChar.sendPacket(sm);
    }

    private void sendSharedGroupUpdate(Player activeChar, int group, long remaining, int reuse) {
        if (group > 0) {
            activeChar.sendPacket(new ExUseSharedGroupItem(itemId, group, remaining, reuse));
        }
    }
}
