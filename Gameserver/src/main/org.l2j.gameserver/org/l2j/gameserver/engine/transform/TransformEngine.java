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
package org.l2j.gameserver.engine.transform;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.settings.FeatureSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class TransformEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformEngine.class);

    private final IntMap<Transform> transformations = new HashIntMap<>();

    private TransformEngine() {
        // singleton
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/transformations.xsd");
    }

    @Override
    public synchronized void load() {
        transformations.clear();
        parseDatapackDirectory("data/stats/transformations", false);
        LOGGER.info("Loaded: {} transform templates.", transformations.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        var listNode = doc.getFirstChild();
        var transformNode = listNode.getFirstChild();
        parseTransformNode(transformNode);
    }

    private void parseTransformNode(Node node) {
        var attrs = node.getAttributes();
        var id = parseInt(attrs, "id");
        var displayId = parseInt(attrs, "display-id", id);
        var canSwim = parseBoolean(attrs, "can-swim");
        var canAttack = parseBoolean(attrs, "can-attack");
        var type = parseEnum(attrs, TransformType.class, "type");
        var name = parseString(attrs, "name", Util.STRING_EMPTY);
        var title = parseString(attrs, "title", Util.STRING_EMPTY);

        var attributesNode = node.getFirstChild();
        var actionsNode = attributesNode.getNextSibling();
        var skillsNode = actionsNode.getNextSibling();

        var attributes = parseTransformAttribute(attributesNode);
        var skills = parseSkills(skillsNode);
        var actions = parseIntArray(actionsNode);

        ExBasicActionList packetActionList = null;
        if (actions.length > 0) {
            packetActionList = new ExBasicActionList(actions);
         }

        transformations.put(id, new Transform(id, type, displayId, canSwim, canAttack, name, title, attributes, packetActionList, skills));
    }

    private List<Skill> parseSkills(Node skillsNode) {
        var skills = new ArrayList<Skill>(skillsNode.getChildNodes().getLength());

        for(var skillNode = skillsNode.getFirstChild(); skillNode != null; skillNode = skillsNode.getNextSibling()) {
            skills.add(parseSkillInfo(skillNode));
        }
        return skills;
    }

    private TransformAttributes parseTransformAttribute(Node attributesNode) {
        var baseNode = attributesNode.getFirstChild();
        var collisionNode = baseNode.getNextSibling();
        var movingNode = collisionNode.getNextSibling();

        var attrs = baseNode.getAttributes();
        var range = parseInt(attrs, "range");
        var attackSpeed = parseInt(attrs, "attack-speed");
        var attackType = parseEnum(attrs, WeaponType.class, "attack-type");
        var criticalRate = parseInt(attrs, "critical-rate");
        var magicAttack = parseInt(attrs, "magic-attack");
        var physicAttack = parseInt(attrs, "physic-attack");
        var randomDamage = parseInt(attrs, "random-damage");

        attrs = collisionNode.getAttributes();
        var radius = parseInt(attrs, "radius");
        var femaleRadius = parseInt(attrs, "female-radius", radius);
        var height = parseInt(attrs, "height");
        var femaleHeight = parseInt(attrs, "female-height", height);

        attrs = movingNode.getAttributes();
        var walk = parseInt(attrs, "walk");
        var run = parseInt(attrs, "run");
        var waterWalk = parseInt(attrs, "water-walk", walk);
        var waterRun = parseInt(attrs, "water-run", run);
        var flyWalk = parseInt(attrs, "fly-walk", walk);
        var flyRun = parseInt(attrs, "fly-run", run);

        return new TransformAttributes(range, attackSpeed, attackType, criticalRate, magicAttack, physicAttack, randomDamage,
                radius, femaleRadius, height, femaleHeight, walk, run, waterWalk, waterRun, flyWalk, flyRun);
    }

    public boolean transform(Creature creature, int transformId, boolean addSkills) {
        var transform = transformations.get(transformId);
        if(transform == null) {
            return false;
        }

        if (!FeatureSettings.allowRideInSiege() && transform.isRiding() && creature.isInsideZone(ZoneType.SIEGE)) {
            return false;
        }

        creature.setTransform(transform);
        transform.onTransform(creature, addSkills);
        return true;
    }

    public static void init() {
        getInstance().load();
    }

    public static TransformEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final TransformEngine INSTANCE = new TransformEngine();
    }
}
