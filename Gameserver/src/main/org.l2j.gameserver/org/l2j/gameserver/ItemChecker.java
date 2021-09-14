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
package org.l2j.gameserver;

import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.ExtractableProduct;
import org.l2j.gameserver.model.commission.CommissionItemType;
import org.l2j.gameserver.model.conditions.*;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.stats.functions.FuncTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNotEmpty;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public class ItemChecker {

    static Logger LOGGER = LoggerFactory.getLogger(ItemChecker.class);
    static IntMap<Item> items = new HashIntMap<>(13000);
    static IntIntMap autoUse = new HashIntIntMap();

    static Map<String, String>  bodyParts = new HashMap<>();
    static Map<String, String>  armorTypes = new HashMap<>();
    static Map<String, String>  crystalTypes = new HashMap<>();
    static Map<String, String>  itemTypes = new HashMap<>();
    static Map<String, String>  weaponTypes = new HashMap<>();
    static IntMap<String> autoUseTypes = new HashIntMap<>();

    static Item processingItem;
    private static ItemEngine itemEngine;

    static {
        autoUseTypes.put(0, "NONE");
        autoUseTypes.put(1, "SUPPLY");
        autoUseTypes.put(2, "HEALING");

        bodyParts.put("0","UNDERWEAR");
        bodyParts.put("1", "EAR");
        bodyParts.put("3", "NECK");
        bodyParts.put("4", "FINGER");
        bodyParts.put("6", "HEAD");
        bodyParts.put("7", "TWO_HAND");
        bodyParts.put("8", "FULL_ARMOR");
        bodyParts.put("9", "ALL_DRESS");
        bodyParts.put("10", "HAIR_ALL");
        bodyParts.put("11", "RIGHT_BRACELET");
        bodyParts.put("12", "LEFT_BRACELET");
        bodyParts.put("13", "TALISMAN");
        bodyParts.put("19", "BELT");
        bodyParts.put("21", "BROOCH_JEWEL");
        bodyParts.put("27", "AGATHION");
        bodyParts.put("54", "GLOVES");
        bodyParts.put("55", "CHEST");
        bodyParts.put("56", "LEGS");
        bodyParts.put("57", "FEET");
        bodyParts.put("58", "BACK");
        bodyParts.put("59", "HAIR");
        bodyParts.put("60", "HAIR2");
        bodyParts.put("61", "RIGHT_HAND");
        bodyParts.put("62", "LEFT_HAND");

        armorTypes.put("0", "NONE");
        armorTypes.put("1", "LIGHT");
        armorTypes.put("2", "HEAVY");
        armorTypes.put("3", "MAGIC");
        armorTypes.put("4", "SIGIL");

        crystalTypes.put("0", "NONE");
        crystalTypes.put("1", "D");
        crystalTypes.put("2", "C");
        crystalTypes.put("3", "B");
        crystalTypes.put("4", "A");
        crystalTypes.put("5", "S");

        itemTypes.put("0",  "NONE");
        itemTypes.put("1",  "SCROLL");
        itemTypes.put("2",  "ARROW");
        itemTypes.put("3",  "POTION");
        itemTypes.put("4",  "SPELLBOOK");
        itemTypes.put("5",  "RECIPE");
        itemTypes.put("6",  "MATERIAL");
        itemTypes.put("7",  "PET_COLLAR");
        itemTypes.put("8",  "CASTLE_GUARD");
        itemTypes.put("9",  "DYE");
        itemTypes.put("10", "SEED");
        itemTypes.put("11", "SEED2");
        itemTypes.put("12", "HARVEST");
        itemTypes.put("13", "LOTTO");
        itemTypes.put("14", "RACE_TICKET");
        itemTypes.put("15", "TICKET_OF_LORD");
        itemTypes.put("16", "LURE");
        itemTypes.put("17", "CROP");
        itemTypes.put("18", "MATURECROP");
        itemTypes.put("19", "ENCHANT_WEAPON");
        itemTypes.put("20", "ENCHANT_ARMOR");
        itemTypes.put("21", "BLESSED_ENCHANT_WEAPON");
        itemTypes.put("22", "BLESSED_ENCHANT_ARMOR");
        itemTypes.put("23", "COUPON");
        itemTypes.put("24", "ELIXIR");
        itemTypes.put("25", "ENCHT_ATTR");
        itemTypes.put("26", "ENCHT_ATTR_CURSED");
        itemTypes.put("27", "BOLT");
        itemTypes.put("28", "INC_PROP_ENCHANT_WEAPON");
        itemTypes.put("29", "INC_PROP_ENCHANT_ARMOR");
        itemTypes.put("30", "ENCHT_ATTR_CRYSTAL_ENCHANT_ARMOR");
        itemTypes.put("31", "ENCHT_ATTR_CRYSTAL_ENCHANT_WEAPON");
        itemTypes.put("32", "ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_ARMOR");
        itemTypes.put("33", "ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WEAPON");
        itemTypes.put("34", "RUNE");
        itemTypes.put("35", "RUNE_SELECT");
        itemTypes.put("36", "TELEPORT_BOOKMARK");
        itemTypes.put("37", "CHANGE_ATTR");
        itemTypes.put("38", "SOULSHOT");
        itemTypes.put("39", "SHAPE_SHIFTING_WEAPON");
        itemTypes.put("40", "BLESS_SHAPE_SHIFTING_WEAPON");
        itemTypes.put("41", "SHAPE_SHIFTING_WEAPON_FIXED");
        itemTypes.put("42", "SHAPE_SHIFTING_ARMOR");
        itemTypes.put("43", "BLESS_SHAPE_SHIFTING_ARMOR");
        itemTypes.put("44", "SHAPE_SHIFTING_ARMOR_FIXED");
        itemTypes.put("45", "SHAPE_SHIFTING_HAIR_ACC");
        itemTypes.put("46", "BLESS_SHAPE_SHIFTING_HAIR_ACC");
        itemTypes.put("47", "SHAPE_SHIFTING_HAIR_ACC_FIXED");
        itemTypes.put("48", "RESTORE_SHAPE_SHIFTING_WEAPON");
        itemTypes.put("49", "RESTORE_SHAPE_SHIFTING_ARMOR");
        itemTypes.put("50", "RESTORE_SHAPE_SHIFTING_HAIR_ACC");
        itemTypes.put("51", "RESTORE_SHAPE_SHIFTING_ALL_ITEM");
        itemTypes.put("52", "BLESS_INC_PROP_ENCHANT_WEAPON");
        itemTypes.put("53", "BLESS_INC_PROP_ENCHANT_ARMOR");
        itemTypes.put("54", "CARD_EVENT");
        itemTypes.put("55", "SHAPE_SHIFTING_ALL_ITEM_FIXED");
        itemTypes.put("56", "MULTI_ENCHANT_WEAPON");
        itemTypes.put("57", "MULTI_ENCHANT_ARMOR");
        itemTypes.put("58", "MULTI_INC_PROB_ENCHANT_WEAPON");
        itemTypes.put("59", "MULTI_INC_PROB_ENCHANT_ARMOR");
        itemTypes.put("60", "ENSOUL_STONE");
        itemTypes.put("61", "NICK_COLOR_OLD");
        itemTypes.put("62", "NICK_COLOR_NEW");
        itemTypes.put("63", "ENCHANT_AGATHION");
        itemTypes.put("64", "BLESS_ENCHANT_AGATHION");
        itemTypes.put("65", "MULTI_ENCHANT_AGATHION");
        itemTypes.put("66", "ANCIENT_CRYSTAL_ENCHANT_AGATHION");
        itemTypes.put("67", "INC_ENCHANT_PROP_AGATHION");
        itemTypes.put("68", "BLESS_INC_ENCHANT_PROP_AGATHION");
        itemTypes.put("69", "MULTI_INC_ENCHANT_PROB_AGATHION");
        itemTypes.put("70", "SEAL_SCROLL");
        itemTypes.put("71", "UNSEAL_SCROLL");
        itemTypes.put("72", "BULLET");
        itemTypes.put("73", "MAGICLAMP");
        itemTypes.put("74", "TRANSFORMATION_BOOK");
        itemTypes.put("75", "TRANSFORMATION_BOOK_BOX_RANDOM");
        itemTypes.put("76", "TRANSFORMATION_BOOK_BOX_RANDOM_RARE");
        itemTypes.put("77", "TRANSFORMATION_BOOK_BOX_STANDARD");
        itemTypes.put("78", "TRANSFORMATION_BOOK_BOX_HIGH_GRADE");
        itemTypes.put("79", "TRANSFORMATION_BOOK_BOX_RARE");
        itemTypes.put("80", "TRANSFORMATION_BOOK_BOX_LEGENDARY");
        itemTypes.put("81", "TRANSFORMATION_BOOK_BOX_MYTHIC");
        itemTypes.put("82", "POLY_ENCHANT_WEAPON");
        itemTypes.put("83", "POLY_ENCHANT_ARMOR");
        itemTypes.put("84", "POLY_INC_ENCHANT_PROP_WEAPON");
        itemTypes.put("85", "POLY_INC_ENCHANT_ARMOR");
        itemTypes.put("86", "CURSED_ENCHANT_WEAPON");
        itemTypes.put("87", "CURSED_ENCHANT_ARMOR");
        itemTypes.put("88", "VITAL_LEGACY_ITEM_1D");
        itemTypes.put("89", "VITAL_LEGACY_ITEM_7D");
        itemTypes.put("90", "VITAL_LEGACY_ITEM_30D");

        weaponTypes.put("0",  "NONE");
        weaponTypes.put("1",  "SWORD");
        weaponTypes.put("2",  "TWO_HAND_SWORD");
        weaponTypes.put("3",  "MAGIC_SWORD");
        weaponTypes.put("4",  "BLUNT");
        weaponTypes.put("5",  "HAMMER");
        weaponTypes.put("6",  "ROD");
        weaponTypes.put("7",  "STAFF");
        weaponTypes.put("8",  "DAGGER");
        weaponTypes.put("9",  "SPEAR");
        weaponTypes.put("10", "FIST");
        weaponTypes.put("11", "BOW");
        weaponTypes.put("12", "ETC");
        weaponTypes.put("13", "DUAL");
        weaponTypes.put("15", "FISHING_ROD");
        weaponTypes.put("16", "RAPIER");
        weaponTypes.put("17", "CROSSBOW");
        weaponTypes.put("18", "ANCIENT_SWORD");
        weaponTypes.put("20", "DUAL_DAGGER");
        weaponTypes.put("22", "TWO_HAND_CROSSBOW");
        weaponTypes.put("23", "DUAL_BLUNT");
    }
    static int step = 100;
    public static void test() {
        try {
            ItemEngine.init();
            itemEngine = ItemEngine.getInstance();
            SkillEngine.init();

            fillName();
            fillPrice();
            fillStat();
            fillItem();
            fillArmor();
            fillWeapon();

            Files.createDirectories(Path.of("new-items"));

            int start = 0;
            int end = start + step;

            var max  = items.keySet().stream().max().orElse(0);

            while (start < max) {
                processFile(start, end);
                start += step;
                end += step;
            }

            LOGGER.info("Parsed");
        } catch (Exception | StackOverflowError e) {
            LOGGER.error("ERROR PROCESSING ITEM {}", processingItem);
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void processFile(int start, int end) throws IOException {
        var file = String.format("%05d-%05d.xml", start, end-1);
        StringBuilder content = new StringBuilder(
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://l2j.org" xsi:schemaLocation="http://l2j.org items.xsd">
                """
        );
        var isNotEmpty = false;
        for (var i = start; i < end; i++) {
            var item = items.get(i);
            if(isNull(item)) {
                continue;
            }

            processingItem = item;
            var template = itemEngine.getTemplate(item.id);
            if(nonNull(template)) {
                if(template instanceof Weapon) {
                    processWeapon(item, content);
                } else if(template instanceof Armor) {
                    processArmor(item, content);
                } else if(template instanceof EtcItem) {
                    processItem(item, content);
                } else {
                    LOGGER.error("Item com tipo não encontrado {} with template {}", item, template);
                }
            } else {
                switch (item.itemProcessType) {
                    case NONE -> LOGGER.error("Item com tipo não encontrado {}", item);
                    case ETC -> processItem(item, content);
                    case ARMOR -> processArmor(item, content);
                    case WEAPON -> processWeapon(item, content);
                }
            }
            isNotEmpty = true;
        }

        if(isNotEmpty) {
            var writer = Files.newBufferedWriter(Path.of("new-items/", file), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            content.append("</list>");
            writer.write(content.toString());
            writer.flush();
            writer.close();
        }
    }

    private static void processItem(Item item, StringBuilder content) {
        var template = (EtcItem) itemEngine.getTemplate(item.id);

        if(nonNull(template)) {
            if (!item.name.equalsIgnoreCase(template.getName())) {
                LOGGER.info("Changing name of Item {} to {}", template, item.name);
            }

            if (!item.type.equalsIgnoreCase(template.getItemType().name())) {
                LOGGER.info("Changing type of Item {} ({}) to {}", template, template.getItemType(), item.type);
            }
        }

        content.append(String.format("\t<item id=\"%d\" name=\"%s\" type=\"%s\" icon=\"%s\">\n", item.id, item.name, item.type, item.icon));

        if(isNotEmpty(item.description)) {
            content.append("\t<!-- ").append(item.description.replace("--", "-")).append(" -->\n");
        }

        content.append("\t\t<restriction");
        if(nonNull(template)) {
            if (template.isOlyRestrictedItem()) {
                content.append(" olympiad-restricted=\"true\"");
            }
        }

        if(item.stackable) {
            content.append(" stackable=\"true\"");
        }

        if(!item.destroyable) {
            content.append(" destroyable=\"false\"");
        }

        if(!item.tradable) {
            content.append(" tradable=\"false\"");
        }

        if(!item.dropable) {
            content.append(" dropable=\"false\"");
        }

        if(!item.sellable) {
            content.append(" sellable=\"false\"");
        }

        if(!item.privateSellable) {
            content.append(" private-sellable=\"false\"");
        }

        if((item.keepType & 1) == 0) {
            content.append(" depositable=\"false\"");
        }

        if((item.keepType & 2) == 0) {
            content.append(" clan-depositable=\"false\"");
        }
        // keep_type = 0, no depositable. 1 private, 2 clan. 3 private and clan. 4 castle; 5 private and castle; 6 clan and castle; 7 ; 8 account shareable; 9 private e account


        if((item.keepType & 4) == 0) {
            content.append(" castle-depositable=\"false\"");
        }

        if((item.keepType & 4) == 0) {
            content.append(" freightable=\"false\"");
        }

        content.append("/>\n");

        if(nonNull(template) && !isNullOrEmpty(template.getConditions())) {
            content.append("\t\t<condition").append(messageFromConditions(template.getConditions())).append(">\n");
            parseConditions(template.getConditions(), content);
            content.append("\t\t</condition>\n");
        }

        content.append("\t\t<attributes");

        if(item.weight > 0) {
            content.append(" weight=\"").append(item.weight).append("\"");
        }

        if(item.price > 0) {
            content.append(" price=\"").append(item.price).append("\"");
        }

        if(nonNull(template)) {
            if (template.getCommissionItemType() != CommissionItemType.OTHER_ITEM) {
                content.append(String.format(" commission-type=\"%s\"", template.getCommissionItemType()));
            }

            if (template.getReuseDelay() > 0) {
                content.append(String.format(" reuse-delay=\"%d\"", template.getReuseDelay()));
            }

            if (template.getSharedReuseGroup() > 0) {
                content.append(String.format(" reuse-group=\"%d\"", template.getSharedReuseGroup()));
            }

            if (template.getTime() > 0) {
                content.append(String.format(" duration=\"%d\"", template.getTime()));
            }

            if (template.isForNpc()) {
                content.append(" for-npc=\"true\"");
            }

            if (template.hasImmediateEffect()) {
                content.append(" immediate-effect=\"true\"");
            }

            if (template.hasExImmediateEffect()) {
                content.append(" ex-immediate-effect=\"true\"");
            }
        }

        if(item.isQuest) {
            content.append(" quest-item=\"true\"");
        }

        if(nonNull(template)) {

            if (template.isInfinite()) {
                content.append(" infinite=\"true\"");
            }

            if (template.isSelfResurrection()) {
                content.append(" self-resurrection=\"true\"");
            }

            if (template.getDefaultAction() != ActionType.NONE) {
                content.append(" action=\"").append(template.getDefaultAction()).append("\"");
            }
        }

        if(autoUse.containsKey(item.id)) {
            content.append(" auto-use=\"").append(autoUseTypes.getOrDefault(autoUse.get(item.id),"NONE")).append("\"");
        }

        content.append("/>\n");
        if((item.type == "ARROW" || item.type == "BOLT")) {
            content.append("\t\t<crystal ").append("type=\"").append(item.grade).append("\"/>\n");
        }

        if(nonNull(template)) {
            if (nonNull(template.getHandlerName())) {
                switch (template.getHandlerName()) {
                    case "ExtractableItems" -> parseExtractable(template, content);
                    case "Elixir", "SummonItems", "ItemSkills", "SoulShots", "SpiritShot", "BlessedSoulShots", "BlessedSpiritShot", "BeastSpiritShot", "BeastSoulShot", "FishShots" -> parseSkillReducer(template, content);
                    default -> content.append("\t\t<action handler=\"").append(template.getHandlerName()).append("\"/>\n");
                }
            }
        }
        content.append("\t</item>\n\n");

    }

    private static void parseSkillReducer(EtcItem template, StringBuilder content) {
        content.append("\t\t<skill-reducer type=\"").append(template.getHandlerName()).append("\">\n");
        parseSkills(content, template.getAllSkills());
        content.append("\t\t</skill-reducer>\n");
    }

    private static void parseSkills(StringBuilder content, List<ItemSkillHolder> allSkills) {
        for (ItemSkillHolder skill : allSkills) {
            content.append("\t\t\t<skill id=\"").append(skill.getSkillId()).append("\" level=\"").append(skill.getLevel()).append("\"");

            if(skill.getType() != ItemSkillType.NORMAL) {
                content.append(" type=\"").append(skill.getType()).append("\"");
            }

            if(skill.getChance() != 100) {
                content.append(" value=\"").append(skill.getChance()).append("\"");
            }

            if(skill.getValue() != 0) {
                content.append(" value=\"").append(skill.getValue()).append("\"");
            }

            content.append("/>");

            if (nonNull(skill.getSkill())) {
                content.append("  <!-- ").append(skill.getSkill().getName()).append(" -->\n");
            } else {
                content.append("  <!-- TODO Skill not found -->\n");
            }
        }
    }

    private static void parseExtractable(EtcItem template, StringBuilder content) {
        content.append("\t\t<extract ");
        if(template.getExtractableCountMax() > 0) {
            content.append(" max=\"").append(template.getExtractableCountMax()).append("\"");
        }
        content.append(">\n");
        for (ExtractableProduct item : template.getExtractableItems()) {
            content.append("\t\t\t<item id=\"").append(item.getId()).append("\"");

            if(item.getMin() > 1) {
                content.append(" min-count=\"").append(item.getMin()).append("\"");
            }

            if(item.getMax() > 1) {
                content.append(" max-count=\"").append(item.getMax()).append("\"");
            }

            if(item.getChance() != 100) {
                content.append(" chance=\"").append(item.getChance()).append("\"");
            }

            if(item.getMinEnchant() > 0) {
                content.append(" min-enchant=\"").append(item.getMinEnchant()).append("\"");
            }

            if(item.getMaxEnchant() > 0) {
                content.append(" max-enchant=\"").append(item.getMaxEnchant()).append("\"");
            }

            content.append("/>  <!-- ").append(items.get(item.getId()).name).append(" -->\n");
        }
        content.append("\t\t</extract>\n");
    }

    private static void processArmor(Item item, StringBuilder content) {
        var template = (Armor) itemEngine.getTemplate(item.id);

        if(nonNull(template)) {
            if (!item.name.equalsIgnoreCase(template.getName())) {
                LOGGER.info("Changing name of Armor {} to {}", template, item.name);
            }

            if (!item.type.equalsIgnoreCase(template.getItemType().name())) {
                if(item.type.equalsIgnoreCase("NONE") && template.getItemType().equals(ArmorType.SHIELD)) {
                    item.type = ArmorType.SHIELD.name();
                } else {
                    LOGGER.info("Changing type of Item {} ({}) to {}", template, template.getItemType(), item.type);
                }
            }
        }

        content.append(String.format("\t<armor id=\"%d\" name=\"%s\" type=\"%s\" body-part=\"%s\" icon=\"%s\">\n", item.id, item.name, item.type, item.bodyPart, item.icon));

        if(isNotEmpty(item.description)) {
            content.append("\t<!-- ").append(item.description).append(" -->\n");
        }

        content.append("\t\t<restriction");

        if(nonNull(template)) {
            if (!template.isFreightable()) {
                content.append(" freightable=\"false\"");
            }

            if (template.isOlyRestrictedItem()) {
                content.append(" olympiad-restricted=\"true\"");
            }
        }

        if(item.stackable) {
            content.append(" stackable=\"true\"");
        }

        if(!item.destroyable) {
            content.append(" destroyable=\"false\"");
        }

        if(!item.tradable) {
            content.append(" tradable=\"false\"");
        }

        if(!item.dropable) {
            content.append(" dropable=\"false\"");
        }

        if(!item.sellable) {
            content.append(" sellable=\"false\"");
        }

        if(!item.privateSellable) {
            content.append(" private-sellable=\"false\"");
        }

        content.append("/>\n");

        if(nonNull(template) && !isNullOrEmpty(template.getConditions())) {
            content.append("\t\t<condition").append(messageFromConditions(template.getConditions())).append(">\n");
            parseConditions(template.getConditions(), content);
            content.append("\t\t</condition>\n");
        }

        content.append("\t\t<attributes");

        if(item.weight > 0) {
            content.append(" weight=\"").append(item.weight).append("\"");
        }

        if(item.price > 0) {
            content.append(" price=\"").append(item.price).append("\"");
        }

        if(nonNull(template)) {
            if (template.getCommissionItemType() != CommissionItemType.OTHER_ITEM) {
                content.append(String.format(" commission-type=\"%s\"", template.getCommissionItemType()));
            }

            if (template.getReuseDelay() > 0) {
                content.append(String.format(" reuse-delay=\"%d\"", template.getReuseDelay()));
            }

            if (template.getSharedReuseGroup() > 0) {
                content.append(String.format(" reuse-group=\"%d\"", template.getSharedReuseGroup()));
            }

            if (template.getTime() > 0) {
                content.append(String.format(" duration=\"%d\"", template.getTime()));
            }

            if (template.isForNpc()) {
                content.append(" for-npc=\"true\"");
            }

            if (!template.isEnchantable()) {
                content.append(" enchant-enabled=\"false\"");
            }

            if (template.getEquipReuseDelay() > 0) {
                content.append(String.format(" equip-reuse-delay=\"%d\"", template.getEquipReuseDelay()));
            }
        }

        content.append(" />\n");

        content.append("\t\t<crystal ");
        if(!item.grade.equalsIgnoreCase("NONE")) {
            content.append(String.format("type=\"%s\"", item.grade));
            if(nonNull(template) && template.getCrystalCount() > 0) {
                content.append(String.format(" count=\"%d\"", template.getCrystalCount()));
            }
        }
        content.append("/>\n");

        if(nonNull(template) && !isNullOrEmpty(template.getFunctionTemplates())) {
            for (FuncTemplate function : template.getFunctionTemplates()) {
                item.stats.putIfAbsent(function.getStat().name(), (float) function.getValue());
            }
        }

        if(item.stats.values().stream().anyMatch(v -> v != 0)) {
            content.append("\t\t<stats>\n");
            item.stats.entrySet().stream().filter(e -> e.getValue() != 0).sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                content.append("\t\t\t<stat type=\"").append(entry.getKey()).append("\" value=\"").append(entry.getValue()).append("\"/>\n");
            });
            content.append("\t\t</stats>\n");
        }


        if(nonNull(template)) {
            if (!isNullOrEmpty(template.getAllSkills())) {
                content.append("\t\t<skills>\n");
                parseSkills(content, template.getAllSkills());
                content.append("\t\t</skills>\n");
            }
        }
        content.append("\t</armor>\n\n");
    }

    private static void processWeapon(Item item, StringBuilder content) {
        var template = (Weapon) itemEngine.getTemplate(item.id);

        if(nonNull(template)) {
            if (!item.name.equalsIgnoreCase(template.getName())) {
                LOGGER.info("Changing name of weapon {} to {}", template, item.name);
            }

            if (!item.type.equalsIgnoreCase(template.getItemType().name())) {
                LOGGER.info("Changing type of weapon {} ({}) to {}", template, template.getItemType(), item.type);
            }
        }

        content.append(String.format("\t<weapon id=\"%d\" name=\"%s\" type=\"%s\" body-part=\"%s\" icon=\"%s\"", item.id, item.name, item.type, item.bodyPart, item.icon));

        if(nonNull(template) && item.isMagic != template.isMagicWeapon()) {
            LOGGER.info("Changing is magic of weapon {} to {}", template, item.type);
        }

        if(item.isMagic) {
            content.append(" magic=\"true\"");
        }

        if(item.manaConsume > 0 ) {
            content.append(String.format(" mana-consume=\"%d\"", item.manaConsume));
        }

        content.append(">\n");

        if(isNotEmpty(item.description)) {
            content.append("\t<!-- ").append(item.description).append(" -->\n");
        }

        content.append("\t\t<restriction");

        if(nonNull(template)) {
            if (!template.isFreightable()) {
                content.append(" freightable=\"false\"");
            }

            if (template.isOlyRestrictedItem()) {
                content.append(" olympiad-restricted=\"true\"");
            }
        }

        if(item.stackable) {
            content.append(" stackable=\"true\"");
        }

        if(!item.destroyable) {
            content.append(" destroyable=\"false\"");
        }

        if(!item.tradable) {
            content.append(" tradable=\"false\"");
        }

        if(!item.dropable) {
            content.append(" dropable=\"false\"");
        }

        if(!item.sellable) {
            content.append(" sellable=\"false\"");
        }

        if(!item.privateSellable) {
            content.append(" private-sellable=\"false\"");
        }

        if(item.isHero) {
            content.append(" hero=\"true\"");
        }

        content.append("/>\n");

        if(nonNull(template) && !isNullOrEmpty(template.getConditions())) {
            content.append("\t\t<condition").append(messageFromConditions(template.getConditions())).append(">\n");
            parseConditions(template.getConditions(), content);
            content.append("\t\t</condition>\n");
        }

        content.append("\t\t<attributes");

        if(item.weight > 0) {
            content.append(" weight=\"").append(item.weight).append("\"");
        }

        if(item.price > 0) {
            content.append(" price=\"").append(item.price).append("\"");
        }

        if(nonNull(template)) {
            if (template.getCommissionItemType() != CommissionItemType.OTHER_WEAPON) {
                content.append(String.format(" commission-type=\"%s\"", template.getCommissionItemType()));
            }

            if (template.getReuseDelay() > 0) {
                content.append(String.format(" reuse-delay=\"%d\"", template.getReuseDelay()));
            }

            if (template.getSharedReuseGroup() > 0) {
                content.append(String.format(" reuse-group=\"%d\"", template.getSharedReuseGroup()));
            }

            if (template.getTime() > 0) {
                content.append(String.format(" duration=\"%d\"", template.getTime()));
            }

            if (template.isForNpc()) {
                content.append(" for-npc=\"true\"");
            }

            if (!template.isEnchantable()) {
                content.append(" enchant-enabled=\"false\"");
            }

            if (template.getChangeWeaponId() > 0) {
                content.append(String.format(" change-weapon=\"%d\"", template.getChangeWeaponId()));
            }

            if (!template.isAttackWeapon()) {
                content.append(" can-attack=\"false\"");
            }

            if (template.useWeaponSkillsOnly()) {
                content.append(" restrict-skills=\"true\"");
            }

            if (template.getEquipReuseDelay() > 0) {
                content.append(String.format(" equip-reuse-delay=\"%d\"", template.getEquipReuseDelay()));
            }
        }

        content.append("/>\n");

        content.append("\t\t<crystal ");
        if(!item.grade.equalsIgnoreCase("NONE")) {
            content.append(String.format("type=\"%s\"", item.grade));
            if(nonNull(template) && template.getCrystalCount() > 0) {
                content.append(String.format(" count=\"%d\"", template.getCrystalCount()));
            }
        }
        content.append("/>\n");

        if(nonNull(template)) {
            content.append(String.format("""
                    \t\t<damage radius="%d" angle="%d"/>
                    """, template.getBaseAttackRadius(), template.getBaseAttackAngle()));
        }

        if(nonNull(template) && !isNullOrEmpty(template.getFunctionTemplates())) {
            for (FuncTemplate function : template.getFunctionTemplates()) {
                item.stats.putIfAbsent(function.getStat().name(), (float) function.getValue());
            }
        }

        if(item.stats.values().stream().anyMatch(v -> v != 0)) {
            content.append("\t\t<stats>\n");
            item.stats.entrySet().stream().filter(e -> e.getValue() != 0).sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                content.append("\t\t\t<stat type=\"").append(entry.getKey()).append("\" value=\"").append(entry.getValue()).append("\"/>\n");
            });
            content.append("\t\t</stats>\n");
        }

        if(nonNull(template)) {
            if (!isNullOrEmpty(template.getAllSkills())) {
                content.append("\t\t<skills>\n");
                parseSkills(content, template.getAllSkills());
                content.append("\t\t</skills>\n");
            }
        }
        content.append("\t</weapon>\n\n");
    }

    private static String messageFromConditions(List<Condition> conditions) {
        boolean addName = false;
        String msg = null;
        int msgId = 0;
        for (Condition condition : conditions) {
            addName |= condition.isAddName();
            if(isNotEmpty(condition.getMessage())){
                msg = condition.getMessage();
            }
            if(condition.getMessageId() > 0) {
                msgId = condition.getMessageId();
            }
        }
        StringBuilder builder = new StringBuilder();

        if(addName) {
            builder.append(" add-name=\"true\"");
        }

        if(isNotEmpty(msg)) {
            builder.append(" msg=\""+ msg +"\"");
        }

        if(msgId > 0) {
            builder.append(" msg-id=\"" + msgId + "\"");
        }

        return builder.toString();
    }

    private static void parseConditions(List<Condition> conditions, StringBuilder content) {
        // level, chaotic, ishero, pledgeclass, castle, sex, flymounted, insidezoneid
        StringBuilder conditionBuilder = new StringBuilder();
        StringBuilder playerConditions = new StringBuilder();
        for (Condition condition : conditions) {
            if(condition instanceof ConditionPlayerMinLevel) {
                playerConditions.append(" level-min=\"" + ((ConditionPlayerMinLevel) condition)._level+"\"");
            } else if(condition instanceof ConditionPlayerState) {
                var cond = (ConditionPlayerState) condition;
                switch (cond._check) {
                    case CHAOTIC -> playerConditions.append(" chaotic=\"" + cond._required + "\"");
                    default -> System.out.println("Not expected state " + cond._check);
                }

            } else if (condition instanceof ConditionPlayerIsHero) {
                playerConditions.append(" hero=\"" + ((ConditionPlayerIsHero) condition).isHero + "\"");
            } else if(condition instanceof ConditionPlayerPledgeClass) {
                playerConditions.append(" pledge-class=\"" + ((ConditionPlayerPledgeClass) condition)._pledgeClass + "\"");
            } else if(condition instanceof ConditionPlayerHasCastle) {
                playerConditions.append(" castle=\"" + ((ConditionPlayerHasCastle) condition)._castle + "\"");
            } else if(condition instanceof ConditionPlayerSex) {
                playerConditions.append(" sex=\"" + (((ConditionPlayerSex) condition)._sex == 1 ? "FEMALE" : "MALE") + "\"");            }
            else if(condition instanceof ConditionPlayerFlyMounted) {
                playerConditions.append(" flying=\"" + ((ConditionPlayerFlyMounted) condition)._val + "\"");
            } else if(condition instanceof ConditionPlayerInsideZoneId) {
                playerConditions.append(" zone=\"" + ((ConditionPlayerInsideZoneId) condition).zones.stream().mapToObj(String::valueOf).collect(Collectors.joining(" ")) + "\"");
            } else if(condition instanceof ConditionLogicAnd) {
                parseConditions(Arrays.asList(((ConditionLogicAnd) condition).conditions), conditionBuilder);
            } else if(condition instanceof ConditionLogicNot) {
                conditionBuilder.append("\t\t\t<not>\n\t");
                parseConditions(List.of(((ConditionLogicNot) condition)._condition), conditionBuilder);
                conditionBuilder.append("\t\t\t</not>\n");
            } else if(condition instanceof ConditionTargetLevelRange) {
                playerConditions.append(" level-min=\"" + ((ConditionTargetLevelRange) condition)._levels[0] + "\"");
                playerConditions.append(" level-max=\"" + ((ConditionTargetLevelRange) condition)._levels[1] + "\"");
            }
            else {
                LOGGER.error("Not parsed Condition {}", condition);
            }
        }
        if(playerConditions.length() > 0) {
            conditionBuilder.append("\t\t\t<player").append(playerConditions).append("/>\n");
        }

        content.append(conditionBuilder.toString());
    }

    private static void fillAuto() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Path.of("/home/alisson/autouseItem.csv"));
        String line = reader.readLine();
        while (nonNull(line = reader.readLine())) {
            var values = line.split("\t");
            autoUse.put(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
        }
    }

    static Pattern itemNamePattern =  Pattern.compile("^.*id=(\\d+?)\\sname=\\[(.*?)].*description=\\[(.*?)].*default_action=\\[(.*?)].*is_trade=(\\d+?)\\s.*is_drop=(\\d+?)\\s.*is_destruct=(\\d+?)\\s.*is_private_store=(\\d+)\\skeep_type=(\\d+)\\sis_npctrade=(\\d+)\\s.*is_commission_store=(\\d+).*");

    // keep_type = 0, no depositable. 1 private, 2 clan. 3 private and clan. 4 castle; 5 private and castle; 6 clan and castle; 7 ; 8 account shareable; 9 private e account
    // item_name_begin	id=1871	    name=[Charcoal]	additionalname=[]	description=[Sell it to a store or trade it in through the Aden Reconstruction Society for a new ingredient.]	popup=-1	default_action=[action_none]	use_order=99	name_class=-1	color=1	Tooltip_Texture=[None]	UNK int=1	UNK2 int=1	UNK3 int=1	is_trade=1	is_drop=1	is_destruct=1	is_private_store=1	keep_type=7	is_npctrade=1	is_commission_store=0	item_name_end
    // item_name_begin	id=91929	name=[Blessed Soulshot]	additionalname=[]	description=[Weapon containing the light of Spirits; instantly inflicts a very powerful attack on the enemy.\n\n<Use Effect>\nP. Atk. Bonus 130%]	popup=-1	default_action=[action_bless_soulshot]	use_order=11	name_class=-1	color=1	Tooltip_Texture=[None]	UNK int=1	UNK2 int=1	UNK3 int=1	is_trade=0	is_drop=0	is_destruct=1	is_private_store=0	keep_type=1	is_npctrade=0	is_commission_store=0	item_name_end
    // item_name_begin	id=92018	name=[Eva's Talisman]	additionalname=[]	description=[A talisman imbued with Eva’s energy. Can be enchanted up to +10 with the Enchant Scroll: Eva's Talisman. Enchantment adds various effects and increases stats of these option effects. If +1 or higher level enchantment fails, the talisman will disappear. Effects from two such Talismans do not stack.]	popup=-1	default_action=[action_equip]	use_order=99	name_class=-1	color=1	Tooltip_Texture=[None]	UNK int=1	UNK2 int=1	UNK3 int=1	is_trade=0	is_drop=0	is_destruct=1	is_private_store=0	keep_type=1	is_npctrade=0	is_commission_store=0	item_name_end
    //item_name_begin	id=17	name=[Wooden Arrow]	additionalname=[]	description=[Arrow made of wood. It is an arrow used for a No-grade bow.]	popup=-1	default_action=[action_equip]	use_order=99	name_class=-1	color=1	Tooltip_Texture=[None]	UNK int=1	UNK2 int=1	UNK3 int=1	is_trade=1	is_drop=1	is_destruct=1	is_private_store=1	keep_type=7	is_npctrade=1	is_commission_store=0	item_name_end
    //automatic_use_begin	Item_id=93750	Is_Use=1	automatic_use_end
    static Pattern autoUsePattern = Pattern.compile("^.*Item_id=(\\d+)\\sIs_Use=(\\d+)\\s.*");
    private static void fillName() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Path.of("res/itemname.txt"));
        String line;
        while (nonNull(line = reader.readLine())) {
            Matcher matcher = itemNamePattern.matcher(line);
            if (matcher.matches()) {
                var item = new Item();
                item.id = Integer.parseInt(matcher.group(1));
                item.name = matcher.group(2).replace("<", "(").replace(">", ")").replace("&", "&amp;").replaceAll("Lineage\\s?II\\s?", "").replace("NC ", "");
                item.description = matcher.group(3);
                item.action = matcher.group(4);
                item.tradable = !("0".equals(matcher.group(5)));
                item.dropable = !("0".equals(matcher.group(6)));
                item.destroyable = !("0".equals(matcher.group(7)));
                item.privateSellable = !("0".equals(matcher.group(8)));
                item.keepType = Integer.parseInt(matcher.group(9));
                item.sellable = !("0".equals(matcher.group(10)));
                item.commissionable = !("0".equals(matcher.group(11)));
                items.put(item.id, item);
            } else if((matcher = autoUsePattern.matcher(line)).matches()) {
                autoUse.put(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }
        }

    }

    // item_begin	tag=0	object_id=1	drop_type=1	drop_anim_type=1	drop_radius=7	drop_height=15	drop_texture={{[LineageWeapons.small_sword_m00_wp];{[LineageWeaponsTex.small_sword_t00_wp]}}}	icon={[icon.weapon_small_sword_i00];[None];[None];[None];[None]}	durability=-1	weight=1600	material_type=8	crystallizable=0	related_quest_id={}	color=1	is_attribution=0	property_params=0	icon_panel=[None]	complete_item_dropsound_type=[dropsound_complete_weapon]	inventory_type=1	body_part=61	handness=1	wp_mesh={{[LineageWeapons.small_sword_m00_wp]};{1}}	texture={[LineageWeaponsTex.small_sword_t00_wp]}	item_sound={[ItemSound.sword_small_1];[ItemSound.sword_big_7];[ItemSound.sword_mid_6];[ItemSound.public_sword_shing_4]}	drop_sound=[ItemSound.itemdrop_sword]	equip_sound=[ItemSound.itemequip_sword]	effect=[None]	random_damage=10	weapon_type=1	crystal_type=0	mp_consume=0	soulshot_count=1	spiritshot_count=1	curvature=1000	UNK_10=0	can_equip_hero=-1	is_magic_weapon=0	ertheia_fist_scale=1.0	junk=-1	Enchanted={{[LineageWeapons.rangesample];{0.95;0.55;0.55};{11.0;0.0;0.0};{0.0;0.0;0.0};0.0;0.0;0.0;{0.0;0.0;0.0};{0.0;0.0;0.0};{0.0;0.0;0.0}}}	variation_effect_type={-1;0;0;0;0;0}	variation_icon={[None]}	ensoul_slot_count=0	is_ensoul=0	item_end
    //
    static Pattern weaponPattern = Pattern.compile("^.*object_id=(\\d+).*?\\sicon=\\{\\[(.*?)].*\\sweight=(\\d+).*?\\scrystallizable=(\\d+).*\\sbody_part=(\\d+).*\\srandom_damage=(\\d+)\\sweapon_type=(\\d+)\\scrystal_type=(\\d+)\\smp_consume=(\\d+).*\\scan_equip_hero=(.*?)\\sis_magic_weapon=(\\d+).*");
    private static void fillWeapon() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Path.of("res/weapon.txt"));
        String line;
        while (nonNull(line = reader.readLine())) {
            Matcher matcher = weaponPattern.matcher(line);
            if(matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                var item = items.get(id);
                item.icon = matcher.group(2);
                item.weight = Integer.parseInt(matcher.group(3));
                item.crystallizable = !"0".equalsIgnoreCase(matcher.group(4));
                item.bodyPart = bodyParts.get(matcher.group(5));
                item.stats.put("RANDOM_DAMAGE", Float.parseFloat(matcher.group(6)));
                item.type = weaponTypes.get(matcher.group(7));

                item.grade = crystalTypes.get(matcher.group(8));
                if (isNull(item.grade)) {
                    LOGGER.warn("Weapon ({}) grade is null", id);
                    item.grade = "NONE";
                }

                item.manaConsume = Integer.parseInt(matcher.group(9));
                item.isHero = "1".equals(matcher.group(10));
                item.isMagic = "1".equals(matcher.group(11));

                item.itemProcessType = ItemProcessType.WEAPON;
            }
        }
    }

    //item_begin	object_id=1	pDefense=0	mDefense=0	pAttack=8	mAttack=6	pAttackSpeed=379	pHit=0.0	mHit=0.0	pCritical=8.0	mCritical=0.0	speed=0	ShieldDefense=0	ShieldDefenseRate=0	pavoid=0.0	mavoid=0.0	property_params=0	item_end
    static Pattern itemStatPattern = Pattern.compile("^.*object_id=(\\d+)\\spDefense=(\\d+)\\smDefense=(\\d+)\\spAttack=(\\d+)\\smAttack=(\\d+)\\spAttackSpeed=(\\d+)\\spHit=(.*?)\\smHit=(.*?)\\spCritical=(.*?)\\smCritical=(.*?)\\sspeed=(\\d+)\\sShieldDefense=(\\d+)\\sShieldDefenseRate=(\\d+)\\spavoid=(.*?)\\smavoid=(.*?)\\s.*");
    private static void fillStat() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Path.of("res/item_stats.txt"));
        String line;
        while (nonNull(line = reader.readLine())) {
            Matcher matcher = itemStatPattern.matcher(line);
            if(matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                var item = items.get(id);
                item.stats.put("PHYSICAL_DEFENCE", Float.parseFloat(matcher.group(2)));
                item.stats.put("MAGICAL_DEFENCE", Float.parseFloat(matcher.group(3)));
                item.stats.put("PHYSICAL_ATTACK", Float.parseFloat(matcher.group(4)));
                item.stats.put("MAGIC_ATTACK", Float.parseFloat(matcher.group(5)));
                item.stats.put("PHYSICAL_ATTACK_SPEED", Float.parseFloat(matcher.group(6)));
                item.stats.put("ACCURACY", Float.parseFloat(matcher.group(7)));
                item.stats.put("ACCURACY_MAGIC", Float.parseFloat(matcher.group(8)));
                item.stats.put("CRITICAL_RATE", Float.parseFloat(matcher.group(9)));
                item.stats.put("MAGIC_CRITICAL_RATE", Float.parseFloat(matcher.group(10)));
                item.stats.put("MAGIC_ATTACK_SPEED", Float.parseFloat(matcher.group(11)));
                item.stats.put("SHIELD_DEFENCE", Float.parseFloat(matcher.group(12)));
                item.stats.put("SHIELD_DEFENCE_RATE", Float.parseFloat(matcher.group(13)));
                item.stats.put("EVASION_RATE", Float.parseFloat(matcher.group(14)));
                item.stats.put("MAGIC_EVASION_RATE", Float.parseFloat(matcher.group(15)));
            }
        }
    }

    // item_begin	tag=2	object_id=17	drop_type=0	drop_anim_type=3	drop_radius=8	drop_height=4	drop_texture={{[dropitems.drop_quiver_m00];{[dropitemstex.drop_quiver_t00]}}}	icon={[icon.etc_wooden_quiver_i00];[None];[None];[None];[None]}	durability=-1	weight=6	material_type=13	crystallizable=0	related_quest_id={}	color=1	is_attribution=0	property_params=0	icon_panel=[None]	complete_item_dropsound_type=[None]	inventory_type=1	mesh={[LineageWeapons.wooden_arrow_m00_et]}	texture={[LineageWeaponsTex.wooden_arrow_t00_et]}	drop_sound=[ItemSound.itemdrop_arrow]	equip_sound=[None]	consume_type=2	etcitem_type=2	crystal_type=0	item_end
    //
    static Pattern etcItemPattern = Pattern.compile("^.*object_id=(\\d+)\\s.*icon=\\{\\[(.*?)].*weight=(\\d+)\\s.*crystallizable=(\\d+)\\srelated_quest_id=\\{(.*?)}.*consume_type=(\\d+)\\setcitem_type=(\\d+)\\scrystal_type=(\\d+).*");
    private static void fillItem() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Path.of("res/etcitem.txt"));
        String line; // ignore header line
        while (nonNull(line = reader.readLine())) {
            Matcher matcher = etcItemPattern.matcher(line);
            if(matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                var item = items.get(id);
                item.icon = matcher.group(2);
                item.weight = Integer.parseInt(matcher.group(3));
                item.crystallizable = false;
                item.isQuest = !matcher.group(5).isBlank();
                item.stackable = !"0".equals(matcher.group(6));
                item.type = itemTypes.get(matcher.group(7));

                if (isNull(item.type)) {
                    LOGGER.warn("item ({}) type is null", id);
                    item.type = "NONE";
                }
                if (item.type.equals("ARROW")) {
                    item.grade = crystalTypes.getOrDefault(matcher.group(8), "NONE");
                } else {
                    item.grade = "NONE";
                }
                item.itemProcessType = ItemProcessType.ETC;
            }
        }
    }

    //item_baseinfo_begin	id=1	default_price=768	grind_point=0	grind_commission=0	is_locked=0	item_baseinfo_end
    static Pattern itemInfoPattern = Pattern.compile("^.*id=(\\d+?)\\sdefault_price=(\\d+)\\s.*");
    private static void fillPrice() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Path.of("res/item_baseinfo.txt"));
        String line;
        while (nonNull(line = reader.readLine())) {
            Matcher matcher = itemInfoPattern.matcher(line);
            if(matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                var item = items.get(id);
                item.price = Long.parseLong(matcher.group(2));
            }
        }
    }

    // item_begin	tag=1	object_id=21	drop_type=0	drop_anim_type=3	drop_radius=7	drop_height=0	drop_texture={{[dropitems.drop_mfighter_m001_t02_u_m00];{[MFighter.MFighter_m001_t02_u]}}}	icon={[icon.armor_t02_u_i00];[None];[None];[None];[None]}	durability=-1	weight=4830	material_type=17	crystallizable=0	related_quest_id={}	color=1	is_attribution=0	property_params=0	icon_panel=[None]	complete_item_dropsound_type=[dropsound_complete_armor]	inventory_type=1	body_part=55	m_HumnFigh={{[Fighter.MFighter_m001_u]};{[MFighter.MFighter_m001_t02_u]}}	m_HumnFigh_add={{{[None]};{{0;-1}}};{[None]};[None]}	f_HumnFigh={{[Fighter.FFighter_m001_u]};{[FFighter.FFighter_m001_t02_u]}}	f_HumnFigh_add={{{[None]};{{0;-1}}};{[None]};[None]}	m_DarkElf={{[DarkElf.MDarkElf_m001_u]};{[MDarkElf.MDarkElf_m001_t02_u]}}	m_DarkElf_add={{{[None]};{{0;-1}}};{[None]};[None]}	f_DarkElf={{[DarkElf.FDarkElf_m001_u]};{[FDarkElf.FDarkElf_m001_t02_u]}}	f_DarkElf_add={{{[None]};{{0;-1}}};{[None]};[None]}	m_Dorf={{[Dwarf.MDwarf_m001_u]};{[MDwarf.MDwarf_m001_t02_u]}}	m_Dorf_add={{{[None]};{{0;-1}}};{[None]};[None]}	f_Dorf={{[Dwarf.FDwarf_m001_u]};{[FDwarf.FDwarf_m001_t02_u]}}	f_Dorf_add={{{[None]};{{0;-1}}};{[None]};[None]}	m_Elf={{[Elf.MElf_m001_u]};{[MElf.MElf_m001_t02_u]}}	m_Elf_add={{{[None]};{{0;-1}}};{[None]};[None]}	f_Elf={{[Elf.FElf_m001_u]};{[FElf.FElf_m001_t02_u]}}	f_Elf_add={{{[None]};{{0;-1}}};{[None]};[None]}	m_HumnMyst={{[Magic.MMagic_m005_u]};{[MMagic.MMagic_m005_t02_u]}}	m_HumnMyst_add={{{[None]};{{0;-1}}};{[None]};[None]}	f_HumnMyst={{[Magic.FMagic_m003_u]};{[FMagic.FMagic_m003_t02_u]}}	f_HumnMyst_add={{{[None]};{{0;-1}}};{[None]};[None]}	m_OrcFigh={{[Orc.MOrc_m001_u]};{[MOrc.MOrc_m001_t02_u]}}	m_OrcFigh_add={{{[None]};{{0;-1}}};{[None]};[None]}	f_OrcFigh={{[Orc.FOrc_m001_u]};{[FOrc.FOrc_m001_t02_u]}}	f_OrcFigh_add={{{[None]};{{0;-1}}};{[None]};[None]}	m_OrcMage={{[Shaman.MShaman_m001_u]};{[MShaman.MShaman_m001_t02_u]}}	m_OrcMage_add={{{[None]};{{0;-1}}};{[None]};[None]}	f_OrcMage={{[Shaman.FShaman_m001_u]};{[FShaman.FShaman_m001_t02_u]}}	f_OrcMage_add={{{[None]};{{0;-1}}};{[None]};[None]}	m_Kamael={{[Kamael.MKamael_m001_u]};{[MKamael.MKamael_m001_t02_u];[MKamael.MKamael_m001_t02_ut]}}	m_Kamael_add={{{[Kamael.MKamael_m001_w_ad00];[Kamael.MKamael_m001_l_ad00]};{{119;95};{108;95}}};{[MKamael.MKamael_m001_t00_w];[MKamael.MKamael_m001_t02_ut]};[None]}	f_Kamael={{[Kamael.FKamael_m001_u]};{[FKamael.FKamael_m001_t02_u];[FKamael.FKamael_m001_t02_ut]}}	f_Kamael_add={{{[Kamael.Fkamael_m001_w_ad00];[Kamael.Fkamael_m001_l_ad00]};{{119;95};{108;95}}};{[Fkamael.Fkamael_m001_t00_w];[FKamael.FKamael_m001_t02_ut]};[None]}	mertheia={{[Ertheia.Mertheia_m007_u]};{[Mertheia.Mertheia_m007_t02_u]}}	mertheia_mesh_add={{{[None]};{{0;-1}}};{[None]};[None]}	fertheia={{[ertheia.Fertheia_m004_u]};{[Fertheia.Fertheia_m004_t02_u]}}	fertheia_mesh_add={{{[None]};{{0;-1}}};{[None]};[None]}	NPC={{[None]};{[None]}}	NPC_add={{{};{}};{};[None]}	attack_effect=[LineageEffect.p_u002_a]	item_sound={[MonSound.Hit_normal_3];[MonSound.Hit_normal_12];[MonSound.Hit_wood_1];[MonSound.Hit_Shell_1]}	drop_sound=[ItemSound.itemdrop_armor_cloth]	equip_sound=[ItemSound.itemequip_armor_cloth]	UNK_7=257	UNK_6=0	armor_type=1	crystal_type=0	mp_bonus=0	hide_mask=520	underwear_body_part1=0	underwear_body_part2=0	full_armor_enchant_effect_type=-1	item_end
    static Pattern armorPattern = Pattern.compile("^.*object_id=(\\d+).*?\\sicon=\\{\\[(.*?)].*\\sweight=(\\d+).*\\scrystallizable=(\\d+).*\\sbody_part=(\\d+).*\\sarmor_type=(\\d+)\\scrystal_type=(\\d+)\\smp_bonus=(\\d+).*");
    private static void fillArmor() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Path.of("res/armor.txt"));
        String line;
        while (nonNull(line = reader.readLine())) {
            Matcher matcher = armorPattern.matcher(line);
            if(matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                var item = items.get(id);
                item.icon = matcher.group(2);
                item.weight = Integer.parseInt(matcher.group(3));
                item.crystallizable = !"0".equalsIgnoreCase(matcher.group(4));
                item.bodyPart = bodyParts.get(matcher.group(5));
                item.type = armorTypes.get(matcher.group(6));

                if (isNull(item.type)) {
                    item.type = "NONE";
                    LOGGER.warn("Armor ({}) type is null", id);

                }
                item.grade = crystalTypes.get(matcher.group(7));

                if (isNull(item.grade)) {
                    LOGGER.warn("Armor ({}) grade is null", id);
                    item.grade = "NONE";
                }

                item.stats.put("MAX_MP", Float.parseFloat(matcher.group(8)));

                item.itemProcessType = ItemProcessType.ARMOR;
            }

        }
    }


    static class Item {
        public int keepType;
        int id;
        String name;
        String description;
        String action;
        boolean tradable;
        public boolean privateSellable;
        boolean dropable;
        boolean destroyable;
        boolean depositable;
        boolean sellable;
        boolean commissionable;
        long price;
        String icon;
        int weight;
        boolean crystallizable;
        boolean isQuest;
        boolean isBlessed;
        boolean stackable;
        String grade;
        String type;
        String bodyPart;
        int randomDamage;
        int manaConsume;
        int ssCount;
        int spCount;
        boolean isHero;
        boolean isMagic;
        Map<String, Float> stats = new HashMap<>();
        ItemProcessType itemProcessType = ItemProcessType.NONE;

        public String toString() {
            return "id= " + id + " name= " + name;
        }
    }

    static enum ItemProcessType {
        WEAPON,
        ARMOR,
        ETC,
        NONE,
    }
}

