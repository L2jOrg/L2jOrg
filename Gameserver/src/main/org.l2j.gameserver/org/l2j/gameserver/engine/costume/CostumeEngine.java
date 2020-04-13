package org.l2j.gameserver.engine.costume;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.api.costume.CostumeGrade;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.model.skills.AbnormalType.TURN_STONE;
import static org.l2j.gameserver.network.SystemMessageId.*;

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
        var costumes = parseIntSet(node.getFirstChild());

        collections.put(id, new CostumeCollection(id, skill, costumes));
    }

    private void parseCostume(Node node) {
        var attrs = node.getAttributes();
        var id = parseInt(attrs, "id");
        var skill = parseInt(attrs, "skill");
        var evolutionFee = parseInt(attrs, "evolution-fee");

        var extractNode = node.getFirstChild();
        var extractItem = parseInt(extractNode.getAttributes(), "item");
        var extractCost = parseExtractCost(extractNode);

        costumes.put(id, new Costume(id, skill, evolutionFee, extractItem, extractCost));

        var grade = parseEnum(attrs, CostumeGrade.class, "grade");
        costumesGrade.computeIfAbsent(grade, g -> new HashIntSet()).add(id);
    }

    private Set<ItemHolder> parseExtractCost(Node extractNode) {
        Set<ItemHolder> extractCost = new HashSet<>(extractNode.getChildNodes().getLength());
        forEach(extractNode, "cost", costNode -> {
            var costAttrs = costNode.getAttributes();
            extractCost.add(new ItemHolder(parseInt(costAttrs, "id"), parseLong(costAttrs, "count")));
        });
        return extractCost;
    }

    public Costume getCostume(int id) {
        return costumes.get(id);
    }

    public Costume getRandomCostume(EnumSet<CostumeGrade> grades) {
        if(grades.isEmpty()) {
            return null;
        }

        var available = costumesGrade.entrySet().stream()
                .filter(entry -> grades.contains(entry.getKey()))
                .flatMapToInt(entry -> entry.getValue().stream())
                .toArray();

        var costumeId = Rnd.get(available);
        return costumes.get(costumeId);
    }
    public boolean checkCostumeAction(Player player) {
        SystemMessageId errMsg = null;
        if(player.getPrivateStoreType() != PrivateStoreType.NONE) {
            errMsg = CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_USING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP;
        } else if(player.isDead()){
            errMsg = CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHEN_DEAD;
        } else if(player.hasAbnormalType(TURN_STONE)) {
            errMsg = CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_PETRIFIED;
        } else if(player.isFishing()) {
            errMsg = CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_FISHING;
        } else if(player.isSitting()) {
            errMsg = CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_SITTING;
        } else if(player.isMovementDisabled()) {
            errMsg = CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_FROZEN;
        } else if(player.isProcessingTransaction()) {
            errMsg = CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_DURING_EXCHANGE;
        } else if(AttackStanceTaskManager.getInstance().hasAttackStanceTask(player)) {
            errMsg = CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_DURING_A_BATTLE;
        }

        if(nonNull(errMsg)) {
            player.sendPacket(errMsg);
            return false;
        }
        return true;
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
