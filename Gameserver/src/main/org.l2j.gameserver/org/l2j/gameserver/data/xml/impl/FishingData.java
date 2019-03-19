package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.FishingBaitData;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the Fishing information.
 *
 * @author bit
 */
public final class FishingData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FishingData.class);
    private final Map<Integer, FishingBaitData> _baitData = new HashMap<>();
    private int _baitDistanceMin;
    private int _baitDistanceMax;
    private double _expRateMin;
    private double _expRateMax;
    private double _spRateMin;
    private double _spRateMax;

    private FishingData() {
        load();
    }

    @Override
    public void load() {
        _baitData.clear();
        parseDatapackFile("data/Fishing.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded Fishing Data.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node listItem = n.getFirstChild(); listItem != null; listItem = listItem.getNextSibling()) {
                    switch (listItem.getNodeName()) {
                        case "baitDistance": {
                            _baitDistanceMin = parseInteger(listItem.getAttributes(), "min");
                            _baitDistanceMax = parseInteger(listItem.getAttributes(), "max");
                            break;
                        }
                        case "experienceRate": {
                            _expRateMin = parseDouble(listItem.getAttributes(), "min");
                            _expRateMax = parseDouble(listItem.getAttributes(), "max");
                            break;
                        }
                        case "skillPointsRate": {
                            _spRateMin = parseDouble(listItem.getAttributes(), "min");
                            _spRateMax = parseDouble(listItem.getAttributes(), "max");
                            break;
                        }
                        case "baits": {
                            for (Node bait = listItem.getFirstChild(); bait != null; bait = bait.getNextSibling()) {
                                if ("bait".equalsIgnoreCase(bait.getNodeName())) {
                                    final NamedNodeMap attrs = bait.getAttributes();
                                    final int itemId = parseInteger(attrs, "itemId");
                                    final int level = parseInteger(attrs, "level");
                                    final int minPlayerLevel = parseInteger(attrs, "minPlayerLevel");
                                    final double chance = parseDouble(attrs, "chance");
                                    final int timeMin = parseInteger(attrs, "timeMin");
                                    final int timeMax = parseInteger(attrs, "timeMax");
                                    final int waitMin = parseInteger(attrs, "waitMin");
                                    final int waitMax = parseInteger(attrs, "waitMax");
                                    final FishingBaitData baitData = new FishingBaitData(itemId, level, minPlayerLevel, chance, timeMin, timeMax, waitMin, waitMax);

                                    for (Node c = bait.getFirstChild(); c != null; c = c.getNextSibling()) {
                                        if ("catch".equalsIgnoreCase(c.getNodeName())) {
                                            baitData.addReward(parseInteger(c.getAttributes(), "itemId"));
                                        }
                                    }
                                    _baitData.put(baitData.getItemId(), baitData);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the fishing rod.
     *
     * @param baitItemId the item id
     * @return A list of reward item ids
     */
    public FishingBaitData getBaitData(int baitItemId) {
        return _baitData.get(baitItemId);
    }

    public int getBaitDistanceMin() {
        return _baitDistanceMin;
    }

    public int getBaitDistanceMax() {
        return _baitDistanceMax;
    }

    public double getExpRateMin() {
        return _expRateMin;
    }

    public double getExpRateMax() {
        return _expRateMax;
    }

    public double getSpRateMin() {
        return _spRateMin;
    }

    public double getSpRateMax() {
        return _spRateMax;
    }


    public static FishingData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final FishingData INSTANCE = new FishingData();
    }
}
