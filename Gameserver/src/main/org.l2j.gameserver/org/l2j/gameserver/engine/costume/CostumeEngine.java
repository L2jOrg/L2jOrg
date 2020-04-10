package org.l2j.gameserver.engine.costume;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.api.costume.CostumeGrade;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author JoeAlisson
 */
public class CostumeEngine extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CostumeEngine.class);

    private final IntMap<Costume> costumes = new HashIntMap<>(159);
    private final EnumMap<CostumeGrade, IntSet> costumesGrade = new EnumMap<>(CostumeGrade.class);
    private final IntMap<CostumeCollection> collections = new HashIntMap<>(12);
    private final IntMap<Set<SkillHolder>> stackedBonus = new HashIntMap<>(12);

    private CostumeEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/costumes.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/costumes.xml");
        LOGGER.info("Loaded {} costumes and {} collections", costumes.size(), collections.size());
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> {
            for(var node = listNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
                switch (node.getNodeName()) {
                    case "costume" -> parseCostume(node);
                    case "collection" -> parseCollection(node);
                    case "stack-bonus" -> parseCollectionStackBonus(node);
                }
            }
        });
    }

    private void parseCollectionStackBonus(Node node) {
        var count = parseInt(node.getAttributes(), "count");
        Set<SkillHolder> bonus = new HashSet<>(node.getChildNodes().getLength());
        forEach(node, "skill", skillNode -> {
            var attr = skillNode.getAttributes();
            bonus.add(new SkillHolder(parseInt(attr, "id"), parseInt(attr, "level") ));
        });
        stackedBonus.put(count, bonus);
    }

    private void parseCollection(Node node) {
        var attrs = node.getAttributes();
        var id = parseInt(attrs, "id");
        var skill = parseInt(attrs, "skill");

        var collection = new CostumeCollection(id, skill);
        collections.put(id, collection);

        collection.setCostumes(parseIntSet(node.getFirstChild()));
    }

    private void parseCostume(Node node) {
        var attrs = node.getAttributes();
        var id = parseInt(attrs, "id");
        var skill = parseInt(attrs, "skill");
        var evolutionFee = parseInt(attrs, "evolution-fee");

        final var costume = new Costume(id, skill, evolutionFee);

        costumes.put(id, costume);

        var grade = parseEnum(attrs, CostumeGrade.class, "grade");
        costumesGrade.computeIfAbsent(grade, g -> new HashIntSet()).add(id);

        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            switch (child.getNodeName()) {
                case "consume" -> parseCostumeConsume(costume, child);
                case "extract" -> parseCostumeExtract(costume, child);
            }
        }
    }

    private void parseCostumeExtract(Costume costume, Node node) {
        var extractItem = new ItemHolder(parseInt(node.getAttributes(), "item"), 1);
        costume.setExtractItem(extractItem);
        forEach(node, "cost", cost -> {
            var attrs = cost.getAttributes();
            costume.addExtractCost(new ItemHolder( parseInt(attrs, "id"), parselong(attrs, "count")));
        });
    }

    private void parseCostumeConsume(Costume costume, Node node) {
        var attrs = node.getAttributes();
        costume.setConsumeItem(new ItemHolder(parseInt(attrs, "id"), parselong(attrs, "count")));
    }

    public Costume getCostume(int id) {
        return costumes.get(id);
    }

    public Costume getRandomCostume(EnumSet<CostumeGrade> grades) {
        if(grades.isEmpty()) {
            return null;
        }

        var availables = costumesGrade.entrySet().stream()
                .filter(entry -> grades.contains(entry.getKey()))
                .flatMapToInt(entry -> entry.getValue().stream())
                .toArray();

        var costumeId = Rnd.get(availables);
        return costumes.get(costumeId);
    }

    public static void init() {
        getInstance().load();
    }

    public static CostumeEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final CostumeEngine INSTANCE = new CostumeEngine();
    }
}
