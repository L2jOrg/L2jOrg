package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.AppearanceHolder;
import org.l2j.gameserver.model.items.appearance.AppearanceStone;
import org.l2j.gameserver.model.items.appearance.AppearanceTargetType;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class AppearanceItemData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppearanceItemData.class);

    private final Map<Integer, AppearanceStone> _stones = new HashMap<>();

    private AppearanceItemData() {
        load();
    }

    @Override
    public void load() {
        parseDatapackFile("data/AppearanceStones.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _stones.size() + " Stones");

        //@formatter:off
		/*
		for (L2Item item : ItemTable.getInstance().getAllItems())
		{
			if ((item == null) || !item.getName().contains("Appearance Stone"))
			{
				continue;
			}
			if (item.getName().contains("Pack") || _stones.containsKey(item.getId()))
			{
				continue;
			}

			System.out.println("Unhandled appearance stone: " + item);
		}
		*/
        //@formatter:on
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("appearance_stone".equalsIgnoreCase(d.getNodeName())) {
                        final AppearanceStone stone = new AppearanceStone(new StatsSet(parseAttributes(d)));
                        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                            switch (c.getNodeName()) {
                                case "grade": {
                                    final CrystalType type = CrystalType.valueOf(c.getTextContent());
                                    stone.addCrystalType(type);
                                    break;
                                }
                                case "targetType": {
                                    final AppearanceTargetType type = AppearanceTargetType.valueOf(c.getTextContent());
                                    stone.addTargetType(type);
                                    break;
                                }
                                case "bodyPart": {
                                    final long part = ItemTable.SLOTS.get(c.getTextContent());
                                    stone.addBodyPart(part);
                                    break;
                                }
                                case "race": {
                                    final Race race = Race.valueOf(c.getTextContent());
                                    stone.addRace(race);
                                    break;
                                }
                                case "raceNot": {
                                    final Race raceNot = Race.valueOf(c.getTextContent());
                                    stone.addRaceNot(raceNot);
                                    break;
                                }
                                case "visual": {
                                    stone.addVisualId(new AppearanceHolder(new StatsSet(parseAttributes(c))));
                                }
                            }
                        }
                        if (ItemTable.getInstance().getTemplate(stone.getId()) != null) {
                            _stones.put(stone.getId(), stone);
                        } else {
                            LOGGER.info(getClass().getSimpleName() + ": Could not find appearance stone item " + stone.getId());
                        }
                    }
                }
            }
        }
    }

    public int getLoadedElementsCount() {
        return _stones.size();
    }

    public AppearanceStone getStone(int stone) {
        return _stones.get(stone);
    }

    public static AppearanceItemData getInstance() {
        return Singleton.INSTANCE;
    }
    private static class Singleton {

        protected static final AppearanceItemData INSTANCE = new AppearanceItemData();
    }
}
