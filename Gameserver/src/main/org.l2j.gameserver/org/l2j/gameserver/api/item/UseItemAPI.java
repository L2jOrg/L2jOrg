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
package org.l2j.gameserver.api.item;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.NextAction;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.network.serverpackets.ExUseSharedGroupItem;
import org.l2j.gameserver.settings.CharacterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class UseItemAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(UseItemAPI.class);

    public static boolean useItem(Player player, int itemObjectId, boolean force) {
        var item = player.getInventory().getItemByObjectId(itemObjectId);
        if(item == null) {
            return false;
        }
        return useItem(player,  item, force);
    }

    public static boolean useItem(Player player, Item item, boolean force) {
        player.onActionRequest();
        if(player.getActiveTradeList() != null) {
            player.cancelActiveTrade();
        }

        if(player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return false;
        }

        if(!checkUseItem(player, item)) {
            return false;
        }

        if(item.isEquipable()) {
            return handleEquipItem(player, item);
        }
        return handleUseItem(player, item, force);
    }

    private static boolean handleUseItem(Player player, Item item, boolean force) {
        var etcItem = item.getEtcItem();
        var handler = ItemHandler.getInstance().getHandler(etcItem);

        if (isNull(handler)) {
            if(nonNull(etcItem) && Util.isNotEmpty(etcItem.getHandlerName())) {
                LOGGER.warn("Unmanaged Item handler: {} for Item: {}!", etcItem.getHandlerName(), item);
            }
            return false;
        }

        if (handler.useItem(player, item, force)) {
            addReuseDelay(player, item);
            return true;
        }
        return false;
    }

    private static void addReuseDelay(Player player, Item item) {
        var reuseDelay = item.getReuseDelay();
        if (reuseDelay > 0) {
            player.addTimeStampItem(item, reuseDelay);
            if(item.getSharedReuseGroup() > 0) {
                player.sendPacket(new ExUseSharedGroupItem(item.getId(), item.getSharedReuseGroup(), reuseDelay, item.getReuseDelay()));
            }
        }
    }

    public static boolean handleEquipItem(Player player, Item item) {
        if(!checkEquipItem(player, item)) {
            player.sendPacket(YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
            return false;
        }
        if (player.isCastingNow()) {
            player.getAI().setNextAction(new NextAction(CtrlEvent.EVT_FINISH_CASTING, CtrlIntention.AI_INTENTION_CAST, () -> delayedEquipItem(player, item.getObjectId())));
        } else if (player.isAttackingNow()) {
            player.getAI().setNextAction(new NextAction(CtrlEvent.EVT_READY_TO_ACT, CtrlIntention.AI_INTENTION_ATTACK, () -> delayedEquipItem(player, item.getObjectId())));
        } else {
            player.useEquippableItem(item,true);
        }
        return true;
    }

    private static void delayedEquipItem(Player player, int itemObjectId) {
        var item = player.getInventory().getItemByObjectId(itemObjectId);
        if(item != null) {
            player.useEquippableItem(item, true);
        }
    }

    private static boolean checkEquipItem(Player player, Item item) {
        if(item.isEquipped()) {
            return true;
        }
        var bodyPart = item.getBodyPart();
        return checkUnlockedSlot(player, item, bodyPart) && (!item.isHeroItem() || player.isHero() || player.canOverrideCond(PcCondOverride.ITEM_CONDITIONS))
                &&  !player.getInventory().isItemSlotBlocked(bodyPart);
    }

    private static boolean checkUnlockedSlot(Player player, Item item, BodyPart bodyPart) {
        return switch (bodyPart) {
            case TWO_HAND, LEFT_HAND, RIGHT_HAND -> checkUnlockedHands(player);
            case TALISMAN -> checkUnlockedTalisman(player, item);
            case BROOCH_JEWEL -> checkUnlockedJewels(player, item);
            case AGATHION -> checkUnlockedAgathion(player);
            case ARTIFACT -> checkUnlockedArtifact(player, item);
            default -> true;
        };
    }

    private static boolean checkUnlockedArtifact(Player player, Item item) {
        if (player.getInventory().getArtifactSlots() == 0) {
            player.sendPacket(getSystemMessage(NO_ARTIFACT_BOOK_EQUIPPED_YOU_CANNOT_EQUIP_S1).addItemName(item));
            return false;
        }
        return true;
    }

    private static boolean checkUnlockedAgathion(Player player) {
        return player.getInventory().getAgathionSlots() != 0;
    }

    private static boolean checkUnlockedJewels(Player player, Item item) {
        if (player.getInventory().getBroochJewelSlots() == 0) {
            player.sendPacket(getSystemMessage(YOU_CANNOT_EQUIP_S1_WITHOUT_EQUIPPING_A_BROOCH).addItemName(item));
            return false;
        }
        return true;
    }

    private static boolean checkUnlockedTalisman(Player player, Item item) {
        if (player.getInventory().getTalismanSlots() == 0) {
            player.sendPacket(getSystemMessage(YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET).addItemName(item));
            return false;
        }
        return true;
    }


    private static boolean checkUnlockedHands(Player player) {
        return !(player.isMounted() || player.isDisarmed());
    }

    private static boolean checkUseItem(Player player, Item item) {
        if (player.hasBlockActions() || player.isControlBlocked() || player.isAlikeDead()) {
            return false;
        }

        if (item.isQuestItem())  {
            player.sendPacket(YOU_CANNOT_USE_QUEST_ITEMS);
            return false;
        }

        if (player.getInventory().isBlocked(item)) {
            player.sendPacket(getSystemMessage(S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item));
            return false;
        }

        if (!item.isEquipped() && !item.checkConditions(player, true)) {
            return false;
        }

        if (player.isFishing()) {
            player.sendPacket(YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
            return false;
        }

        return CharacterSettings.allowPKTeleport() || player.getReputation() >= 0 || !item.hasSkills(ItemSkillType.NORMAL, s -> s.hasAnyEffectType(EffectType.TELEPORT));
    }

}
