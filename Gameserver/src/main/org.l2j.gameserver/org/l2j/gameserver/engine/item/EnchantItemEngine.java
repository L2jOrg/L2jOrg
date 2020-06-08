package org.l2j.gameserver.engine.item;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.enchant.*;
import org.l2j.gameserver.enums.InventoryBlockType;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    case "group" -> parseScrollGroup(node, chanceGroups, scrollGroups);
                    case "scroll" -> parseScroll(node, scrollGroups);
                }
            }
        });
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
        scrolls.put(id, new EnchantScroll(group, grade,type, items, minEnchant, maxEnchant, chanceBonus, random, maxEnchantRandom));
    }

    private boolean checkScroll(int id, ItemTemplate scrollTemplate) {
        if(isNull(scrollTemplate)) {
            LOGGER.warn("No valid item to scroll {}", id);
            return false;
        }

        if(!isEnchantmentItem(scrollTemplate)) {
            LOGGER.warn("Scroll doesn't have valid enchantment type {}", scrollTemplate);
            return false;
        }
        return true;
    }

    private boolean isEnchantmentItem(ItemTemplate item) {
        return  item.getItemType() instanceof EtcItemType itemType &&
        switch (itemType) {
            case ENCHANT_WEAPON,
                ENCHANT_ARMOR,
                BLESSED_ENCHANT_WEAPON,
                BLESSED_ENCHANT_ARMOR,
                INC_PROP_ENCHANT_WEAPON,
                INC_PROP_ENCHANT_ARMOR,
                ENCHT_ATTR_CRYSTAL_ENCHANT_ARMOR,
                ENCHT_ATTR_CRYSTAL_ENCHANT_WEAPON,
                ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_ARMOR,
                ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WEAPON,
                BLESS_INC_PROP_ENCHANT_WEAPON,
                BLESS_INC_PROP_ENCHANT_ARMOR,
                MULTI_ENCHANT_WEAPON,
                MULTI_ENCHANT_ARMOR,
                MULTI_INC_PROB_ENCHANT_WEAPON,
                MULTI_INC_PROB_ENCHANT_ARMOR,
                ENCHANT_AGATHION,
                BLESS_ENCHANT_AGATHION,
                MULTI_ENCHANT_AGATHION,
                ANCIENT_CRYSTAL_ENCHANT_AGATHION,
                INC_ENCHANT_PROP_AGATHION,
                BLESS_INC_ENCHANT_PROP_AGATHION,
                MULTI_INC_ENCHANT_PROB_AGATHION,
                POLY_ENCHANT_WEAPON,
                POLY_ENCHANT_ARMOR,
                POLY_INC_ENCHANT_PROP_WEAPON,
                POLY_INC_ENCHANT_ARMOR,
                CURSED_ENCHANT_WEAPON,
                CURSED_ENCHANT_ARMOR -> true;
                default -> false;
        };
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

            min = Math.min(from, min);
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
        }
        return scroll.canEnchant(item);
    }

    public void enchant(Player player, Item item, Item enchantmentItem) {
        final var scroll = scrolls.get(enchantmentItem.getId());
        final var inventory = player.getInventory();
        inventory.setInventoryBlock(IntSet.of(item.getId()), InventoryBlockType.BLACKLIST);

        try {
            if (!checkEnchantmentCondition(player, item, enchantmentItem, scroll)) {
                player.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
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
            player.sendPacket(SystemMessageId.ENCHANT_FAILED_THE_ENCHANT_SKILL_FOR_THE_CORRESPONDING_ITEM_WILL_BE_EXACTLY_RETAINED);
            player.sendPacket(new EnchantResult(EnchantResult.SAFE_FAIL, item));

            if (Config.LOG_ITEM_ENCHANTS) {
                LOGGER.info("Safe Fail, Player: {}, +{} {}, {}", player, item.getEnchantLevel(), item, scroll);
            }

        } else if(scroll.isBlessed()) {
            player.sendPacket(SystemMessageId.THE_BLESSED_ENCHANT_FAILED_THE_ENCHANT_VALUE_OF_THE_ITEM_BECAME_0);
            item.setEnchantLevel(0);
            item.updateDatabase();
            player.sendPacket(new EnchantResult(EnchantResult.BLESSED_FAIL, 0, 0));

            if(item.isEquipped()) {
                player.getInventory().reloadEquippedItem(item);
                player.broadcastUserInfo();
            }
            inventoryUpdate.addModifiedItem(item);

            if (Config.LOG_ITEM_ENCHANTS) {
                LOGGER.info("Blessed Fail, Player: {}, +{} {}, {}", player, item.getEnchantLevel(), item, scroll);
            }
        } else {
            if (item.isEquipped()) {
                if (item.getEnchantLevel() > 0) {
                    player.sendPacket(getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(item.getEnchantLevel()).addItemName(item));
                } else {
                    player.sendPacket(getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED).addItemName(item));
                }

                player.getInventory().unEquipItemInBodySlot(item.getBodyPart());
                player.broadcastUserInfo();
            }

            if(isNull(player.getInventory().destroyItem("Enchant", item, player, null))) {
                LOGGER.warn("Unable to destroy, Player: {}, +{} {}, {}", player, item.getEnchantLevel(), item, scroll);
            }
            inventoryUpdate.addRemovedItem(item);
            final var crystalId = item.getCrystalType().getCrystalId();
            int count = item.getCrystalCount();

            if(crystalId != 0 && count > 0) {
                var crystal = player.getInventory().addItem("Enchant", crystalId, count, player, item);
                player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S).addItemName(crystal).addLong(count));
                player.sendPacket(new EnchantResult(EnchantResult.FAIL, crystalId, count));
                inventoryUpdate.addItem(crystal);
            } else {
                player.sendPacket(new EnchantResult(EnchantResult.NO_CRYSTAL, 0, 0));
            }

            if (Config.LOG_ITEM_ENCHANTS) {
                LOGGER.info("Fail, Player: {}, +{} {}, {}", player, item.getEnchantLevel(), item, scroll);
            }
        }
    }

    private void onSuccess(Player player, Item item, EnchantScroll scroll) {
        item.updateEnchantLevel(scroll.enchantStep(item));
        item.updateDatabase();

        player.sendPacket(new EnchantResult(EnchantResult.SUCCESS, item));

        if (Config.LOG_ITEM_ENCHANTS) {
            LOGGER.info("Success, {} Enchant {} {}, with scroll {}", player, item.getEnchantLevel(), item, scroll);
        }

        announceEnchantment(player, item);
        if(item.isEquipped()) {
            player.getInventory().reloadEquippedItem(item);
            player.broadcastUserInfo();
        }
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
