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
package org.l2j.gameserver.engine.item;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.enchant.*;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.EnchantResult;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.item.ExItemAnnounce;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static java.lang.Math.min;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.network.serverpackets.item.ItemAnnounceType.ENHANCEMENT;

/**
 * @author JoeAlisson
 */
public class EnchantItemEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemEngine.class);

    private final IntMap<EnchantScroll> scrolls = new HashIntMap<>();
    private final Map<CrystalType, EnchantFailReward> weaponRewards = new EnumMap<>(CrystalType.class);
    private final Map<CrystalType, EnchantFailReward> armorRewards = new EnumMap<>(CrystalType.class);
    private final Map<CrystalType, ArmorHpBonus> armorHpBonuses = new EnumMap<>(CrystalType.class);
    private float fullArmorHpBonusMult = 1f;

    private EnchantItemEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/enchantment.xsd");
    }

    @Override
    public synchronized void load() {
        scrolls.clear();
        parseDatapackFile("data/enchantment.xml");
        LOGGER.info("Loaded {} Enchant Scrolls.", scrolls.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        final Map<String, RangedChanceGroup> chanceGroups = new HashMap<>(5);
        final IntMap<ScrollGroup> scrollGroups = new HashIntMap<>(2);
        forEach(doc, "enchantment", enchantment -> {
            for (var node = enchantment.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
                switch (node.getNodeName()) {
                    case "chance-group" -> parseChanceGroup(node, chanceGroups);
                    case "fail-rewards" -> parseFailRewards(node);
                    case "armor-hp-bonus" -> parseArmorHpBonus(node);
                    case "group" -> parseScrollGroup(node, chanceGroups, scrollGroups);
                    case "scroll" -> parseScroll(node, scrollGroups);
                }
            }
        });
    }

    private void parseArmorHpBonus(Node node) {
        fullArmorHpBonusMult = parseFloat(node.getAttributes(), "full-armor-multiplier");
        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            var attr = child.getAttributes();
            var grade = parseEnum(attr, CrystalType.class, "grade");
            var fullArmorMaxBonus = parseInt(attr, "full-armor-max");
            var set = parseChildEnchantValues(child);
            armorHpBonuses.put(grade, new ArmorHpBonus(fullArmorMaxBonus, set));
        }
    }

    private void parseFailRewards(Node node) {
        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            if(child.getNodeName().equalsIgnoreCase("weapon")) {
                parseEnchantFailReward(child, weaponRewards);
            } else {
                parseEnchantFailReward(child, armorRewards);
            }
        }
    }

    private void parseEnchantFailReward(Node node, Map<CrystalType, EnchantFailReward> rewards) {
        final var id = parseInt(node.getAttributes(), "reward-id");
        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            var grade = parseEnum(child.getAttributes(), CrystalType.class, "grade");
            Set<RangedEnchantValue> values = parseChildEnchantValues(child);
            rewards.put(grade, new EnchantFailReward(id, values));
        }
    }

    private Set<RangedEnchantValue> parseChildEnchantValues(Node node) {
        final var length =  node.getChildNodes().getLength();
        if(length == 0) {
            return Collections.emptySet();
        }

        Set<RangedEnchantValue> set = new HashSet<>(length);
        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            var attr = child.getAttributes();
            var from = parseInt(attr, "from");
            var until = parseInt(attr, "until");
            var amount = parseInt(attr, "amount");
            var bonusPerLevel = parseInt(attr, "bonus-per-level");
            set.add(new RangedEnchantValue(from, until, amount, bonusPerLevel));
        }
        return set;
    }

    private void parseScroll(Node node, IntMap<ScrollGroup> scrollGroups) {
        final var attr = node.getAttributes();
        final var id = parseInt(attr, "id");
        final var scrollTemplate = ItemEngine.getInstance().getTemplate(id);

        if (!checkScroll(id, scrollTemplate)) {
            return;
        }

        final var items = parseIntSet(node.getFirstChild());
        final var group = scrollGroups.get(parseInt(attr, "group"));
        final var grade = parseEnum(attr, CrystalType.class, "grade");

        final var type = (EtcItemType) scrollTemplate.getItemType();
        final var maxEnchant = parseInt(attr, "max-enchant") - 1;
        final var minEnchant = parseInt(attr, "min-enchant");
        final var chanceBonus = 1 + parseFloat(attr, "chance-percent-bonus") / 100;
        final var random = parseInt(attr, "random");
        final var maxEnchantRandom = parseInt(attr, "max-enchant-random");
        final var safeFailStep = parseInt(attr, "safe-fail-step");
        scrolls.put(id, new EnchantScroll(group, grade,type, items, minEnchant, maxEnchant, chanceBonus, random, maxEnchantRandom, safeFailStep));
    }

    private boolean checkScroll(int id, ItemTemplate scrollTemplate) {
        if(isNull(scrollTemplate)) {
            LOGGER.warn("The item is not a valid scroll {}", id);
            return false;
        }

        if(!isEnchantmentItem(scrollTemplate)) {
            LOGGER.warn("Scroll doesn't have valid enchantment type {}", scrollTemplate);
            return false;
        }
        return true;
    }

    private boolean isEnchantmentItem(ItemTemplate item) {
        return  item.getItemType() instanceof EtcItemType itemType && itemType.isEnchantment();
    }

    private void parseScrollGroup(Node node, Map<String, RangedChanceGroup> chanceGroups, final IntMap<ScrollGroup> scrollGroups) {
        List<EnchantChance> enchantChances = new ArrayList<>(node.getChildNodes().getLength());

        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()){
            var attr = child.getAttributes();
            var slots = parseEnumSet(child.getFirstChild(), BodyPart.class);
            var group = chanceGroups.get(parseString(attr, "group"));
            enchantChances.add(new EnchantChance(group, slots, parseBoolean(attr, "magic-weapon", null)));
        }

        scrollGroups.put(parseInt(node.getAttributes(), "id"), new ScrollGroup(enchantChances));
    }

    private void parseChanceGroup(Node node, final Map<String, RangedChanceGroup> chanceGroups) {
        List<RangedChance> chances = new ArrayList<>(node.getChildNodes().getLength());
        int min = Short.MAX_VALUE;
        int max = -1;

        for (var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()){
            var attr = child.getAttributes();

            var from = parseInt(attr, "from");
            var until = parseInt(attr, "until");
            chances.add(new RangedChance(from, until, parseFloat(attr, "chance")));

            min = min(from, min);
            max = Math.max(until, max);
        }
        chanceGroups.put(parseString(node.getAttributes(), "name"), new RangedChanceGroup(min, max, chances));
    }

    public boolean existsScroll(Item scroll) {
        return nonNull(scrolls.get(scroll.getId()));
    }

    public boolean canEnchant(Item item, Item enchantmentItem) {
        final var scroll = scrolls.get(enchantmentItem.getId());
        if(isNull(scroll)) {
            LOGGER.warn("Undefined scroll have been used id: {}", enchantmentItem.getId());
            return false;
        }
        return scroll.canEnchant(item);
    }

    public void enchant(Player player, Item item, Item enchantmentItem) {
        final var scroll = scrolls.get(enchantmentItem.getId());
        final var inventory = player.getInventory();
        inventory.setInventoryBlock(IntSet.of(item.getId()), InventoryBlockType.BLACKLIST);

        try {
            if (!checkEnchantmentCondition(player, item, enchantmentItem, scroll)) {
                player.sendPacket(EnchantResult.error());
                player.removeRequest(EnchantItemRequest.class);
                return;
            }
            InventoryUpdate inventoryUpdate = new InventoryUpdate(enchantmentItem);
            doEnchantment(player, item, scroll, inventoryUpdate);
            player.sendInventoryUpdate(inventoryUpdate);
        } finally {
            inventory.unblock();
        }
    }

    private void doEnchantment(Player player, Item item, EnchantScroll scroll, InventoryUpdate inventoryUpdate) {
        final var success = scroll.calcEnchantmentSuccess(item, player.getStats().getEnchantRateBonus());
        if(success) {
            onSuccess(player, item, scroll);
            inventoryUpdate.addModifiedItem(item);
        } else {
            onFailed(player, item, scroll, inventoryUpdate);
        }
    }

    private void onFailed(Player player, Item item, EnchantScroll scroll, InventoryUpdate inventoryUpdate) {
        if(scroll.isSafe()) {
            EnchantResult result;
            if(scroll.safeFailStep() > 0) {
                item.updateEnchantLevel(-scroll.safeFailStep());
                onEnchantEquippedItem(player, item);
                inventoryUpdate.addModifiedItem(item);
                result = EnchantResult.safeReduced(item);
            } else {
                result = EnchantResult.safe(item);
            }

            player.sendPacket(result);

            if (Config.LOG_ITEM_ENCHANTS) {
                LOGGER.info("Safe Fail, Player: {}, +{} {}, {}", player, item.getEnchantLevel(), item, scroll);
            }

        } else if(scroll.isBlessed()) {
            item.setEnchantLevel(0);
            item.updateDatabase();
            onEnchantEquippedItem(player, item);

            inventoryUpdate.addModifiedItem(item);
            player.sendPacket(EnchantResult.blessed(item));

            if (Config.LOG_ITEM_ENCHANTS) {
                LOGGER.info("Blessed Fail, Player: {}, +{} {}, {}", player, item.getEnchantLevel(), item, scroll);
            }
        } else {
            if (item.isEquipped()) {
                var modifiedItems = player.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(item.getLocationSlot()));
                player.sendInventoryUpdate(new InventoryUpdate(modifiedItems));
            }

            if(isNull(player.getInventory().destroyItem("Enchant", item, player, null))) {
                LOGGER.warn("Unable to destroy, Player: {}, +{} {}, {}", player, item.getEnchantLevel(), item, scroll);
            }
            inventoryUpdate.addRemovedItem(item);

            final var crystalId = item.getCrystalType().getCrystalId();
            int count = item.getCrystalCount();

            EnchantResult result;
            if(crystalId != 0 && count > 0) {
                var crystal = player.getInventory().addItem("Enchant", crystalId, count, player, item);
                player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S).addItemName(crystal).addLong(count));
                inventoryUpdate.addItem(crystal);
                result = EnchantResult.fail(crystalId, count);
            } else {
                result = EnchantResult.fail();
            }

            doIfNonNull(item.isWeapon() ? weaponRewards.get(item.getCrystalType()) : armorRewards.get(item.getCrystalType()), failReward -> {
                long rewardAmount = failReward.amount(item.getEnchantLevel());
                if(rewardAmount > 0) {
                    var reward = player.getInventory().addItem("Enchant",  failReward.id(), rewardAmount, player, item);
                    player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S).addItemName(reward).addLong(rewardAmount));
                    inventoryUpdate.addItem(reward);
                    result.withStone(failReward.id(), rewardAmount);
                }
            });

            player.sendPacket(result);

            if (Config.LOG_ITEM_ENCHANTS) {
                LOGGER.info("Fail, Player: {}, +{} {}, {}", player, item.getEnchantLevel(), item, scroll);
            }
        }
    }

    private void onEnchantEquippedItem(Player player, Item item) {
        if (item.isEquipped()) {
            player.getInventory().reloadEquippedItem(item);
            player.broadcastUserInfo();
        }
    }

    private void onSuccess(Player player, Item item, EnchantScroll scroll) {
        item.updateEnchantLevel(scroll.enchantStep(item));
        item.updateDatabase();

        player.sendPacket(EnchantResult.success(item));


        if (Config.LOG_ITEM_ENCHANTS) {
            LOGGER.info("Success, {} Enchant {} {}, with scroll {}", player, item.getEnchantLevel(), item, scroll);
        }

        announceEnchantment(player, item);
        onEnchantEquippedItem(player, item);
    }

    private void announceEnchantment(Player player, Item item) {
        final int minEnchantAnnounce = item.isArmor() ? getSettings(CharacterSettings.class).minimumEnchantAnnounceArmor() : getSettings(CharacterSettings.class).minimumEnchantAnnounceWeapon();
        if (minEnchantAnnounce > 0 && item.getEnchantLevel() >= minEnchantAnnounce) {

            Broadcast.toAllOnlinePlayers(new ExItemAnnounce(ENHANCEMENT, player, item),
                    getSystemMessage(SystemMessageId.C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3).addPcName(player).addInt(item.getEnchantLevel()).addItemName(item));

            doIfNonNull(CommonSkill.FIREWORK.getSkill(), skill ->
                    player.broadcastPacket(new MagicSkillUse(player, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay())));
        }
    }

    private boolean checkEnchantmentCondition(Player player, Item item, Item enchantmentItem, EnchantScroll scroll) {
        if(isNull(scroll) || !scroll.canEnchant(item) || item.getOwnerId() != player.getObjectId()) {
            player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            return false;
        }

        if (isNull(player.getInventory().destroyItem("Enchant", enchantmentItem.getObjectId(), 1, player, item))) {
            player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            GameUtils.handleIllegalPlayerAction(player, player + " tried to enchant with a scroll he doesn't have");
            return false;
        }
        return true;
    }

    public int getArmorHpBonus(Item item) {
        if(item.isArmor()) {
            var armorHpBonus = armorHpBonuses.get(item.getCrystalType());
            if(nonNull(armorHpBonus)) {
                var bonus = armorHpBonus.get(item.getEnchantLevel());
                if(item.getBodyPart() == BodyPart.FULL_ARMOR) {
                    bonus = min((int) (bonus * fullArmorHpBonusMult), armorHpBonus.fullArmorMaxBonus());
                }
                return bonus;
            }
        }
        return 0;
    }

    public static void init() {
        getInstance().load();
    }

    public static EnchantItemEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final EnchantItemEngine INSTANCE = new EnchantItemEngine();
    }
}
