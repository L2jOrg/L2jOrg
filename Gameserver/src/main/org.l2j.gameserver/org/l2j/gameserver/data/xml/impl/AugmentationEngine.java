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
package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.handler.EffectHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.options.Options;
import org.l2j.gameserver.model.options.OptionsSkillHolder;
import org.l2j.gameserver.model.options.OptionsSkillType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.EffectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

import static java.util.Objects.*;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class AugmentationEngine extends EffectParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AugmentationEngine.class);

    private final IntMap<Options> augmentations = new HashIntMap<>();

    private AugmentationEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/augmentation/options/options.xsd");
    }

    @Override
    public synchronized void load() {
        augmentations.clear();
        parseDatapackDirectory("data/augmentation/options", false);
        LOGGER.info("Loaded {} Augmentations Options.", augmentations.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "option", optionNode -> {
            final int id = parseInteger(optionNode.getAttributes(), "id");
            final Options option = new Options(id);

            forEach(optionNode, XmlReader::isNode, innerNode -> {
                switch (innerNode.getNodeName()) {
                    case "effects" -> parseEffects(innerNode, option);
                    case "skill" -> parseSkills(option, innerNode);
                }
            });
            augmentations.put(option.getId(), option);
        }));
    }

    private void parseSkills(Options option, Node innerNode) {
        var attr = innerNode.getAttributes();
        var skill = new SkillHolder(parseInt(attr, "id"), parseInt(attr, "level"));
        var type = parseEnum(attr, OptionsSkillType.class, "type");
        switch (type) {
            case ACTIVE -> option.addActiveSkill(skill);
            case PASSIVE -> option.addPassiveSkill(skill);
            default -> option.addActivationSkill(new OptionsSkillHolder(skill, parseDouble(attr, "chance"), type));
        }
    }

    private void parseEffects(Node node, Options option) {
        for(var child = node.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            String effectName;
            if("effect".equals(child.getNodeName())) {
                effectName = parseString(child.getAttributes(), "name");
            } else {
                effectName = child.getNodeName();
            }

            var factory = EffectHandler.getInstance().getHandlerFactory(effectName);

            if(isNull(factory)) {
                LOGGER.error("could not parse options' {} effect {}", option, effectName);
                continue;
            }
            option.addEffect(createEffect(factory, child));
        }
    }

    private AbstractEffect createEffect(Function<StatsSet, AbstractEffect> factory, Node node) {
        var statsSet = new StatsSet(parseAttributes(node));
        StatsSet stats = null;
        if (node.hasChildNodes()) {
            IntMap<StatsSet> levelInfo = parseEffectChildNodes(node, 1, 1, statsSet);
            if (!levelInfo.isEmpty()) {
                stats = levelInfo.get(1);
                stats.merge(statsSet);
            }
        }
        return factory.apply(requireNonNullElse(stats, statsSet));
    }

    public Options getOptions(int id) {
        return augmentations.get(id);
    }

    public static void init() {
        getInstance().load();
    }

    public static AugmentationEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AugmentationEngine INSTANCE = new AugmentationEngine();
    }
}
