package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.enums.CrystallizationType;
import org.l2j.gameserver.model.holders.CrystallizationDataHolder;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.items.Armor;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.Weapon;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 */
public final class ItemCrystallizationData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCrystallizationData.class);

    private final Map<CrystalType, Map<CrystallizationType, List<ItemChanceHolder>>> _crystallizationTemplates = new EnumMap<>(CrystalType.class);
    private final Map<Integer, CrystallizationDataHolder> _items = new HashMap<>();

    private ItemCrystallizationData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/CrystallizableItems.xsd");
    }

    @Override
    public void load() {
        _crystallizationTemplates.clear();
        for (CrystalType crystalType : CrystalType.values()) {
            _crystallizationTemplates.put(crystalType, new EnumMap<>(CrystallizationType.class));
        }
        _items.clear();
        parseDatapackFile("data/CrystallizableItems.xml");
        LOGGER.info("Loaded {} crystallization templates.", _crystallizationTemplates.size());
        LOGGER.info("Loaded {} pre-defined crystallizable items.", _items.size());

        // Generate remaining data.
        generateCrystallizationData();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node o = n.getFirstChild(); o != null; o = o.getNextSibling()) {
                    if ("templates".equalsIgnoreCase(o.getNodeName())) {
                        for (Node d = o.getFirstChild(); d != null; d = d.getNextSibling()) {
                            if ("crystallizable_template".equalsIgnoreCase(d.getNodeName())) {
                                final CrystalType crystalType = parseEnum(d.getAttributes(), CrystalType.class, "crystalType");
                                final CrystallizationType crystallizationType = parseEnum(d.getAttributes(), CrystallizationType.class, "crystallizationType");
                                final List<ItemChanceHolder> crystallizeRewards = new ArrayList<>();
                                for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                                    if ("item".equalsIgnoreCase(c.getNodeName())) {
                                        NamedNodeMap attrs = c.getAttributes();
                                        final int itemId = parseInteger(attrs, "id");
                                        final long itemCount = parseLong(attrs, "count");
                                        final double itemChance = parseDouble(attrs, "chance");
                                        crystallizeRewards.add(new ItemChanceHolder(itemId, itemChance, itemCount));
                                    }
                                }

                                _crystallizationTemplates.get(crystalType).put(crystallizationType, crystallizeRewards);
                            }
                        }
                    } else if ("items".equalsIgnoreCase(o.getNodeName())) {
                        for (Node d = o.getFirstChild(); d != null; d = d.getNextSibling()) {
                            if ("crystallizable_item".equalsIgnoreCase(d.getNodeName())) {
                                final int id = parseInteger(d.getAttributes(), "id");
                                final List<ItemChanceHolder> crystallizeRewards = new ArrayList<>();
                                for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                                    if ("item".equalsIgnoreCase(c.getNodeName())) {
                                        NamedNodeMap attrs = c.getAttributes();
                                        final int itemId = parseInteger(attrs, "id");
                                        final long itemCount = parseLong(attrs, "count");
                                        final double itemChance = parseDouble(attrs, "chance");
                                        crystallizeRewards.add(new ItemChanceHolder(itemId, itemChance, itemCount));
                                    }
                                }
                                _items.put(id, new CrystallizationDataHolder(id, crystallizeRewards));
                            }
                        }
                    }
                }
            }
        }
    }

    public int getLoadedCrystallizationTemplateCount() {
        return _crystallizationTemplates.size();
    }

    private List<ItemChanceHolder> calculateCrystallizeRewards(ItemTemplate item, List<ItemChanceHolder> crystallizeRewards) {
        if (crystallizeRewards == null) {
            return null;
        }

        final List<ItemChanceHolder> rewards = new ArrayList<>();

        for (ItemChanceHolder reward : crystallizeRewards) {
            double chance = reward.getChance() * item.getCrystalCount();
            long count = reward.getCount();

            if (chance > 100.) {
                double countMul = Math.ceil(chance / 100.);
                chance /= countMul;
                count *= countMul;
            }

            rewards.add(new ItemChanceHolder(reward.getId(), chance, count));
        }

        return rewards;
    }

    private void generateCrystallizationData() {
        final int previousCount = _items.size();

        for (ItemTemplate item : ItemTable.getInstance().getAllItems()) {
            // Check if the data has not been generated.
            if (((item instanceof Weapon) || (item instanceof Armor)) && item.isCrystallizable() && !_items.containsKey(item.getId())) {
                final List<ItemChanceHolder> holder = _crystallizationTemplates.get(item.getCrystalType()).get((item instanceof Weapon) ? CrystallizationType.WEAPON : CrystallizationType.ARMOR);
                if (holder != null) {
                    _items.put(item.getId(), new CrystallizationDataHolder(item.getId(), calculateCrystallizeRewards(item, holder)));
                }
            }
        }

        LOGGER.info("Generated {} crystallizable items from templates.", _items.size() - previousCount);
    }

    public List<ItemChanceHolder> getCrystallizationTemplate(CrystalType crystalType, CrystallizationType crystallizationType) {
        return _crystallizationTemplates.get(crystalType).get(crystallizationType);
    }

    /**
     * @param itemId
     * @return {@code CrystallizationData} for unenchanted items (enchanted items just have different crystal count, but same rewards),<br>
     * or {@code null} if there is no such data registered.
     */
    public CrystallizationDataHolder getCrystallizationData(int itemId) {
        return _items.get(itemId);
    }

    /**
     * @param item to calculate its worth in crystals.
     * @return List of {@code ItemChanceHolder} for the rewards with altered crystal count.
     */
    public List<ItemChanceHolder> getCrystallizationRewards(Item item) {
        final List<ItemChanceHolder> result = new ArrayList<>();
        final CrystallizationDataHolder data = getCrystallizationData(item.getId());
        if (data != null) {
            // If there are no crystals on the template, add such.
            if (data.getItems().stream().noneMatch(i -> i.getId() == item.getItem().getCrystalItemId())) {
                result.add(new ItemChanceHolder(item.getItem().getCrystalItemId(), 100, item.getCrystalCount()));
            }

            result.addAll(data.getItems());
        } else {
            // Add basic crystal reward.
            result.add(new ItemChanceHolder(item.getItem().getCrystalItemId(), 100, item.getCrystalCount()));
        }

        return result;
    }

    public static ItemCrystallizationData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemCrystallizationData INSTANCE = new ItemCrystallizationData();
    }
}
