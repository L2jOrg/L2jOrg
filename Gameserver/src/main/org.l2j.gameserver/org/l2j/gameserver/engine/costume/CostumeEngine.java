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
package org.l2j.gameserver.engine.costume;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.api.costume.CostumeGrade;
import org.l2j.gameserver.data.database.data.CostumeCollectionData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.costume.ExSendCostumeListFull;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.computeIfNonNull;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.model.skills.AbnormalType.TURN_STONE;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class CostumeEngine extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CostumeEngine.class);

    private final IntMap<Costume> costumes = new HashIntMap<>(159);
    private final EnumMap<CostumeGrade, IntSet> costumesGrade = new EnumMap<>(CostumeGrade.class);
    private final IntMap<CostumeCollection> collections = new HashIntMap<>(12);
    private final IntMap<Set<Skill>> stackedBonus = new HashIntMap<>(12);

    private CostumeEngine() {
        var listeners = Listeners.players();
        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) e -> onPlayLogin(e.getPlayer()), this));
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/costumes.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/costumes.xml");
        LOGGER.info("Loaded {} costumes and {} collections", costumes.size(), collections.size());
        releaseResources();
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
        Set<Skill> bonus = new HashSet<>(node.getChildNodes().getLength());
        forEach(node, "skill", skillNode -> {
            var attr = skillNode.getAttributes();
            var skill = SkillEngine.getInstance().getSkill(parseInt(attr, "id"), parseInt(attr, "level"));
            bonus.add(skill);
        });
        stackedBonus.put(count, bonus);
    }

    private void parseCollection(Node node) {
        var attrs = node.getAttributes();
        var id = parseInt(attrs, "id");
        var skillId = parseInt(attrs, "skill");
        var costumes = parseIntSet(node.getFirstChild());
        var skill = SkillEngine.getInstance().getSkill(skillId, 1);
        collections.put(id, new CostumeCollection(id, skill, costumes));
    }

    private void parseCostume(Node node) {
        var attrs = node.getAttributes();
        var id = parseInt(attrs, "id");
        var skillId = parseInt(attrs, "skill");
        var evolutionFee = parseInt(attrs, "evolution-fee");

        var extractNode = node.getFirstChild();
        var extractItem = parseInt(extractNode.getAttributes(), "item");
        var extractCost = parseExtractCost(extractNode);
        var skill = SkillEngine.getInstance().getSkill(skillId, 1);

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

    private void onPlayLogin(Player player) {
        processCollections(player);
        player.sendPacket(new ExSendCostumeListFull());
        checkStackedEffects(player, 0);

        doIfNonNull(collections.get(player.getActiveCostumeCollection().getId()),
                c -> player.addSkill(c.skill(), false));
    }

    private void checkStackedEffects(Player player, int previousStack) {
        var currentBonus = player.getCostumeCollectionAmount();
        if(currentBonus != previousStack) {
            doIfNonNull(stackedBonus.get(previousStack),
                bonus -> bonus.forEach(skill -> player.removeSkill(skill, false)));

            doIfNonNull(stackedBonus.get(currentBonus),
                bonus -> bonus.forEach(skill -> player.addSkill(skill, false)));
        }
    }

    public void processCollections(Player player) {
        collections.values().stream()
            .filter(c -> hasAllCostumes(player, c))
            .forEach(c -> player.addCostumeCollection(c.id()));
    }

    private boolean hasAllCostumes(Player player, CostumeCollection costumeCollection) {
        return costumeCollection.costumes().stream().mapToObj(player::getCostume).allMatch(Objects::nonNull);
    }

    public Skill getCostumeSkill(int costumeId) {
        return computeIfNonNull(getCostume(costumeId), Costume::skill);
    }

    public boolean activeCollection(Player player, int collectionId) {
        var activeCollection = player.getActiveCostumeCollection();

        if(activeCollection.getId() == collectionId) {
            player.sendPacket(THIS_COLLECTION_EFFECT_IS_ALREADY_ACTIVE);
            return false;
        }

        if (!checkReuseTime(player, activeCollection)) {
            return false;
        }

        var collection = collections.get(collectionId);
        if(nonNull(collection) && player.setActiveCostumesCollection(collectionId)) {
            doIfNonNull(collections.get(activeCollection.getId()), c -> player.removeSkill(c.skill(), false));
            player.addSkill(collection.skill(), false);
            return true;
        }
        player.sendPacket(CANNOT_ACTIVATE_THE_EFFECT_THE_COLLECTION_IS_INCOMPLETE);
        return false;
    }

    protected boolean checkReuseTime(Player player, CostumeCollectionData activeCollection) {
        if(activeCollection.getReuseTime() > 0) {
            var duration = Duration.between(Instant.now(), Instant.ofEpochSecond(activeCollection.getReuseTime()));
            SystemMessage msg;
            if(duration.toMinutes() >= 1) {
                msg = getSystemMessage(YOU_CAN_COLLECT_A_COLLECTION_EFFECT_AGAIN_AFTER_S1_MINUTES).addInt((int) duration.toMinutes());
            } else {
                msg = getSystemMessage(YOU_CAN_SELECT_ANOTHER_COLLECTION_EFFECT_S1_SECONDS_LATER).addInt((int) duration.toSeconds());
            }

            player.sendPacket(msg);
            return false;
        }
        return true;
    }

    public void checkCostumeCollection(Player player, int id) {
        var stackBonus = player.getCostumeCollectionAmount();
        collections.values().stream().filter(c -> c.costumes().contains(id)).forEach(c -> {
            if(hasAllCostumes(player, c)) {
                player.addCostumeCollection(c.id());
            } else {
                player.removeCostumeCollection(c.id());
                if(player.getActiveCostumeCollection().getId() == c.id()) {
                    player.removeSkill(c.skill(), false);
                }
            }
        });
        checkStackedEffects(player, stackBonus);
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
