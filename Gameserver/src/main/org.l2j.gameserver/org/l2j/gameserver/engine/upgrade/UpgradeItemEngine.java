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
package org.l2j.gameserver.engine.upgrade;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntKeyValue;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.StreamUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.api.item.UpgradeType;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.serverpackets.item.upgrade.ExUpgradeSystemNormalResult;
import org.l2j.gameserver.network.serverpackets.item.upgrade.ExUpgradeSystemResult;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.StreamUtil.collectToMap;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class UpgradeItemEngine extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpgradeItemEngine.class);

    private final EnumMap<UpgradeType, IntMap<Upgrade>> upgrades = new EnumMap<>(UpgradeType.class);

    private UpgradeItemEngine() {
        upgrades.put(UpgradeType.RARE, new HashIntMap<>(74));
        upgrades.put(UpgradeType.NORMAL, new HashIntMap<>(12000));
        upgrades.put(UpgradeType.SPECIAL, new HashIntMap<>(1850));
    }

    public void upgradeItem(Player player, int itemObjectid, UpgradeType upgradeType, int upgradeId) {
        final var upgrade = upgradeOf(upgradeType, upgradeId);

        if(isNull(upgrade)) {
            player.sendPacket(FAILED_THE_OPERATION);
            return;
        }

        final var inventory = player.getInventory();

        try {
            final var blockItems = StreamUtil.collectToSet(IntStream.concat(IntStream.of(57, upgrade.item()), upgrade.material().stream().mapToInt(ItemHolder::getId)));
            inventory.setInventoryBlock(blockItems, InventoryBlockType.BLACKLIST);
            if(validUpgradeRequirements(player, itemObjectid, upgrade)) {
                if(upgrade instanceof CommonUpgrade common) {
                    upgrade(player, common);
                } else {
                    rareUpgrade(player, (RareUpgrade) upgrade);
                }
            }
        } finally {
            inventory.unblock();
        }
    }

    private void rareUpgrade(Player player, RareUpgrade upgrade) {
        final var resultId = Util.zeroIfNullOrElse(player.addItem("Upgrade", upgrade.result(), 1, upgrade.resultEnchantment(), player, true), WorldObject::getObjectId);
        player.sendPacket(new ExUpgradeSystemResult(resultId));
    }

    private void upgrade(Player player, CommonUpgrade upgrade) {
        ExUpgradeSystemNormalResult result;
        if(Rnd.chance(upgrade.chance())) {
            result = ExUpgradeSystemNormalResult.success(upgrade)
                    .with(collectToMap( upgradeInfo(player, upgrade::results) ));

            if(!upgrade.bonusItems().isEmpty()) {
                result.withBonus(collectToMap( upgradeInfo(player, upgrade::bonusItems) ));
            }
        } else {
            result = ExUpgradeSystemNormalResult.fail(upgrade);

            if(!upgrade.failItems().isEmpty()) {
                result.with(collectToMap( upgradeInfo(player, upgrade::failItems) ));
            }

        }
        player.sendPacket(result);
    }

    private Stream<IntMap.Entry<ItemHolder>> upgradeInfo(Player player, Supplier<List<ItemHolder>> itemsSupplier) {
        return itemsSupplier.get().stream().map(item -> giveToPlayerAndRecord(player, item));
    }

    private IntKeyValue<ItemHolder> giveToPlayerAndRecord(Player player, ItemHolder holder) {
        final var item = player.addItem("Upgrade", holder, player, false);
        if(isNull(item)) {
            return null;
        }

        player.sendPacket(getSystemMessage(C1_YOU_OBTAINED_S2_THROUGH_EQUIPMENT_UPGRADE).addPcName(player).addItemName(item));
        return new IntKeyValue<>(item.getObjectId(), holder);
    }

    private boolean validUpgradeRequirements(Player player, int itemObjectid, Upgrade upgrade) {
        final var item = player.getInventory().getItemByObjectId(itemObjectid);
        if(isNull(item) || item.getId() != upgrade.item() || item.getEnchantLevel() != upgrade.enchantment()) {
            player.sendPacket(FAILED_BECAUSE_THE_TARGET_ITEM_DOES_NOT_EXIST);
            return false;
        }
        return consumeMaterial(player, itemObjectid, upgrade);
    }

    private boolean consumeMaterial(Player player, int itemObjectid, Upgrade upgrade) {
        final var material = upgrade.material();
        final var inventory = player.getInventory();

        if(inventory.getAdena() < upgrade.commission()) {
            player.sendPacket(FAILED_BECAUSE_THERE_S_NOT_ENOUGH_ADENA);
            return false;
        }

        for (var item : material) {
            if(inventory.getInventoryItemCount(item.getId(), -1) < item.getCount()) {
                player.sendPacket(FAILED_BECAUSE_THERE_ARE_NOT_ENOUGH_INGREDIENTS);
                return false;
            }
        }

        player.destroyItem("Upgrade", itemObjectid, 1, player, true);
        player.reduceAdena("Upgrade", upgrade.commission(), player, true);
        for (var item : material) {
            player.destroyItemByItemId("Upgrade", item.getId(), item.getCount(), player, true);
        }
        return true;
    }

    private Upgrade upgradeOf(UpgradeType upgradeType, int upgradeId) {
        return upgrades.getOrDefault(upgradeType, Containers.emptyIntMap()).get(upgradeId);
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/upgrade/upgrade.xsd");
    }

    @Override
    public void load() {
        parseDatapackDirectory("data/upgrade", false);
        releaseResources();
        LOGGER.info("Loaded item upgrade data");
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> {
            for (var node = listNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
                switch (node.getNodeName()) {
                    case "rare-upgrade" -> parseRareUpgrade(node);
                    case "upgrade" -> parseNormalUpgrade(node);
                }
            }
        });
    }

    private void parseNormalUpgrade(Node node) {
        final var attrs =  node.getAttributes();
        final var id = parseInt(attrs, "id");
        final var item = parseInt(attrs, "item");
        final var enchantment = parseInt(attrs, "enchantment");
        final var commission= parselong(attrs, "commission");
        final var chance = parseInt(attrs, "chance");
        final var type = parseEnum(attrs, UpgradeType.class, "type");

        final var material = parseMaterial(node);
        final var failItems = parseFailItems(node);
        final var bonusItems = parseBonus(node);
        final var results = parseResults(node);

        upgrades.get(type).put(id, new CommonUpgrade(id, item, enchantment, commission, results, chance, material, failItems, bonusItems));
    }

    private List<ItemHolder> parseResults(Node node) {
        return getItemList(node, "result");
    }

    private List<ItemHolder> parseBonus(Node node) {
        return getItemList(node, "bonus-item");
    }

    private List<ItemHolder> parseFailItems(Node upgradeNode) {
        return getItemList(upgradeNode, "item-on-fail");
    }

    private List<ItemHolder> getItemList(Node upgradeNode, String s) {
        if (!upgradeNode.hasChildNodes()) {
            return Collections.emptyList();
        }

        List<ItemHolder> itemList = new ArrayList<>();
        for (var node = upgradeNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            if (node.getNodeName().equals(s)) {
                itemList.add(parseItemHolder(node));
            }
        }
        return itemList.isEmpty() ? Collections.emptyList() : itemList;
    }

    private void parseRareUpgrade(Node node) {
        final var attrs =  node.getAttributes();
        final var id = parseInt(attrs, "id");
        final var item = parseInt(attrs, "item");
        final var enchantment = parseInt(attrs, "enchantment");
        final var commission= parselong(attrs, "commission");
        final var result = parseInt(attrs, "result");
        final var resultEnchantment = parseInt(attrs, "result-enchantment");
        final var material = parseMaterial(node);

        upgrades.get(UpgradeType.RARE).put(id, new RareUpgrade(id, item, enchantment, commission, result, resultEnchantment, material));
    }

    private List<ItemHolder> parseMaterial(Node upgradeNode) {
        return getItemList(upgradeNode, "material");
    }

    public static void init() {
        getInstance().load();
    }

    public static UpgradeItemEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final UpgradeItemEngine INSTANCE = new UpgradeItemEngine();
    }

}
