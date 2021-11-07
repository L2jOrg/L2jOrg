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
package org.l2j.gameserver.engine.fishing;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the Fishing information.
 *
 * @author bit
 * @author JoeAlisson
 */
public final class FishingEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FishingEngine.class);

    private final IntMap<FishingBait> baits = new HashIntMap<>();
    private int baitMinDistance;
    private int baitMaxDistance;
    private double expMinRate;
    private double expMaxRate;
    private double spMinRate;
    private double spMaxRate;

    private FishingEngine() {
        // singleton
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/fishing.xsd");
    }

    @Override
    public void load() {
        baits.clear();
        parseDatapackFile("data/fishing.xml");
        LOGGER.info("Loaded {} Fishing Data.", baits.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (var listNode = doc.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
            if ("list".equalsIgnoreCase(listNode.getNodeName())) {
                for (var node = listNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                    switch (node.getNodeName()) {
                        case "distance" -> parseDistance(node);
                        case "experience" -> parseExperience(node);
                        case "skill-points-rate" -> parseSkillPointsRate(node);
                        case "bait" -> parseBait(node);
                        default -> LOGGER.warn("Unknown Attribute {}", node.getNodeName());
                    }
                }
            }
        }
    }

    private void parseBait(Node node) {
        final var attrs = node.getAttributes();
        final var id = parseInt(attrs, "id");
        final var level = parseInt(attrs, "level");
        final var minPlayerLevel = parseInt(attrs, "min-player-level");
        final var chance = parseDouble(attrs, "chance");
        final var minTime = parseInt(attrs, "min-time");
        final var maxTime = parseInt(attrs, "max-time", minTime);
        final var minWait = parseInt(attrs, "min-wait");
        final var maxWait = parseInt(attrs, "max-wait", minWait);

        var rewards = parseRewards(node);

        final var baitData = new FishingBait(id, level, minPlayerLevel, chance, minTime, maxTime, minWait, maxWait, rewards);
        baits.put(id, baitData);
    }

    private List<ItemChanceHolder> parseRewards(Node node) {
        List<ItemChanceHolder> rewards = new ArrayList<>(node.getChildNodes().getLength());
        for (var catchNode = node.getFirstChild(); catchNode != null; catchNode = catchNode.getNextSibling()) {
            if ("catch".equalsIgnoreCase(catchNode.getNodeName())) {
                var atrr = catchNode.getAttributes();
                var id = parseInt(atrr, "id");
                var chance = parseFloat(atrr, "chance");
                var count = parseInt(atrr, "count");
                rewards.add(new ItemChanceHolder(id, chance, count));
            }
        }
        return rewards;
    }

    private void parseSkillPointsRate(Node node) {
        var attr = node.getAttributes();
        spMinRate = parseDouble(attr, "min");
        spMaxRate = parseDouble(attr, "max");
    }

    private void parseExperience(Node node) {
        var attr = node.getAttributes();
        expMinRate = parseDouble(attr, "min");
        expMaxRate = parseDouble(attr, "max");
    }

    private void parseDistance(Node node) {
        var attr = node.getAttributes();
        baitMinDistance = parseInt(attr, "min");
        baitMaxDistance = parseInt(attr, "max");
    }

    /**
     * Gets the fishing rod.
     *
     * @param baitItemId the item id
     * @return A list of reward item ids
     */
    public FishingBait getBaitData(int baitItemId) {
        return baits.get(baitItemId);
    }

    public boolean isFishingShot(Item shot) {
        return shot.getAction() == ActionType.FISHINGSHOT;
    }

    public int getBaitDistanceMin() {
        return baitMinDistance;
    }

    public int getBaitDistanceMax() {
        return baitMaxDistance;
    }

    public double getExpRateMin() {
        return expMinRate;
    }

    public double getExpRateMax() {
        return expMaxRate;
    }

    public double getSpRateMin() {
        return spMinRate;
    }

    public double getSpRateMax() {
        return spMaxRate;
    }

    public static void init() {
        getInstance().load();
    }

    public static FishingEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final FishingEngine INSTANCE = new FishingEngine();
    }
}
