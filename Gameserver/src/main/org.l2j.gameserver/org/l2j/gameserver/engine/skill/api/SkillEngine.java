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
package org.l2j.gameserver.engine.skill.api;

import io.github.joealisson.primitive.*;
import io.github.joealisson.primitive.function.IntBiConsumer;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.SkillAutoUseType;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.BasicProperty;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.handler.*;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.*;
import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.stats.TraitType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.EffectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.*;

/**
 * @author JoeAlisson
 */
public class SkillEngine extends EffectParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillEngine.class);
    public static final String ATTR_LEVEL = "level";
    public static final String ATTR_INITIAL = "initial";

    private final LongMap<Skill> skills = new HashLongMap<>(36800);
    private IntIntMap skillsTime;
    private HashIntIntMap skillsDelay;

    private SkillEngine() {
    }

    public Skill getSkill(int id, int level) {
        return skills.get(skillHashCode(id, level));
    }

    public void addSiegeSkills(Player player) {
        player.addSkill(CommonSkill.IMPRIT_OF_LIGHT.getSkill(), false);
        player.addSkill(CommonSkill.IMPRIT_OF_DARKNESS.getSkill(), false);
        player.addSkill(CommonSkill.BUILD_HEADQUARTERS.getSkill(), false);
        player.addSkill(CommonSkill.BUILD_ADVANCED_HEADQUARTERS.getSkill(), false);

        if(player.getClan().getCastleId() > 0) {
            player.addSkill(CommonSkill.OUTPOST_CONSTRUCTION.getSkill(), false);
            player.addSkill(CommonSkill.OUTPOST_DEMOLITION.getSkill(), false);
        }
    }

    public void removeSiegeSkills(Player player) {
        player.removeSkill(CommonSkill.IMPRIT_OF_LIGHT.getSkill());
        player.removeSkill(CommonSkill.IMPRIT_OF_DARKNESS.getSkill());
        player.removeSkill(CommonSkill.BUILD_HEADQUARTERS.getSkill());
        player.removeSkill(CommonSkill.BUILD_ADVANCED_HEADQUARTERS.getSkill());
        player.removeSkill(CommonSkill.OUTPOST_CONSTRUCTION.getSkill());
        player.removeSkill(CommonSkill.OUTPOST_DEMOLITION.getSkill());
    }

    public int getMaxLevel(int skillId) {
        return zeroIfNullOrElse(skills.get(skillHashCode(skillId, 1)), Skill::getMaxLevel);
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/skills/skills.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/skills/skill-config.xml");
        parseDatapackDirectory("data/skills/", true);
        LOGGER.info("Loaded {} skills", skills.size());
        releaseResources();
    }

    @Override
    protected void releaseResources() {
        skillsTime = null;
        skillsDelay = null;
        super.releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        var list = doc.getFirstChild();
        for(var node = list.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            var name = node.getNodeName();
            if(name.equals("skill")) {
                parseSkill(node);
            } else if(name.equals("skill-config")) {
                parseSkillConfig(node);
            }
        }

    }

    private void parseSkillConfig(Node skillConfigNode) {
        skillsTime = new HashIntIntMap();
        skillsDelay = new HashIntIntMap();
        for (var node = skillConfigNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            var attributes = node.getAttributes();
            var name = node.getNodeName();

            if(name.equals("time")) {
                skillsTime.put(parseInt(attributes, ATTR_ID), parseInt(attributes, ATTR_VALUE));
            } else if(name.equals("delay")) {
                skillsDelay.put(parseInt(attributes, ATTR_ID), parseInt(attributes, ATTR_VALUE));
            }
        }
    }

    private void parseSkill(Node skillNode)  {
        Skill skill = null;
        try {
            var attr = skillNode.getAttributes();
            var id = parseInt(attr, ATTR_ID);
            var maxLevel = parseInt(attr, "max-level");

            skill = new Skill(id, parseString(attr, "name"), maxLevel, parseBoolean(attr, "debuff"), parseEnum(attr, SkillOperateType.class, "action"), parseEnum(attr, SkillType.class, "type"));
            skills.put(skillHashCode(id, 1), skill);

            parseSkillConstants(skill, skillNode);

            skill.computeSkillAttributes();

            for (var node = skillNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
                switch (node.getNodeName()) {
                    case "icon" -> parseIcon(node, skill, maxLevel);
                    case "attributes" -> parseSkillAttributes(node, skill, maxLevel);
                    case "consume" -> parseSkillConsume(node, skill, maxLevel);
                    case "abnormal" -> parseSkillAbnormal(node, skill, maxLevel);
                    case "conditions" -> parseConditions(node, skill);
                    case "effects" -> parseSkillEffects(node, skill, maxLevel);
                }
            }
            getOrCloneSkillBasedOnLast(id, maxLevel, true, true);
        } catch (Exception e) {
            LOGGER.error("Could not parse skill info {}", skill, e);
        }
    }

    private void parseConditions(Node conditionsNode, Skill skill)  {
        parseLeveledCondition(conditionsNode, skill, skill.getMaxLevel());

        for (var node = conditionsNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            if(nonNull(node.getAttributes().getNamedItem("on-level"))) {
                continue;
            }
            SkillCondition cond = "condition".equals(node.getNodeName()) ? parseNamedCondition(node) : parseCondition(node);
            if(nonNull(cond)) {
                var scope = parseEnum(node.getAttributes(), SkillConditionScope.class, "scope");
                var currentLevel = skill.getMaxLevel();
                var hash = skillHashCode(skill.getId(), currentLevel);
                while (currentLevel-- > 0) {
                    doIfNonNull(skills.get(hash--), s -> s.addCondition(scope, cond));
                }
            }  else {
                LOGGER.warn("Could not parse skill's ({}) condition {}", skill, node.getNodeName());
            }
        }
    }

    private void parseLeveledCondition(Node conditionsNode, Skill skill, int maxLevel)  {
        for (var node = conditionsNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            var level = parseInt(node.getAttributes(), "on-level", -1);
            if(level < 1 || level > maxLevel) {
                continue;
            }
            SkillCondition cond = "condition".equals(node.getNodeName()) ? parseNamedCondition(node) : parseCondition(node);
            if(nonNull(cond)) {
                var scope = parseEnum(node.getAttributes(), SkillConditionScope.class, "scope");
                var sk = getOrCloneSkillBasedOnLast(skill.getId(), level);
                sk.addCondition(scope, cond);
            }  else {
                LOGGER.warn("Could not parse skill's ({}) condition {}", skill, node.getNodeName());
            }
        }
    }

    private SkillCondition parseCondition(Node node) {
        var factory = SkillConditionHandler.getInstance().getHandlerFactory(node.getNodeName());
        return computeIfNonNull(factory, f -> f.apply(node));
    }

    private SkillCondition parseNamedCondition(Node node) {
        var factory = SkillConditionHandler.getInstance().getHandlerFactory(parseString(node.getAttributes(), "name"));
        return computeIfNonNull(factory, f -> f.apply(node));
    }

    private void parseSkillEffects(Node node, Skill skill, int maxLevel) {
        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            if("effect".equals(child.getNodeName())) {
                parseNamedEffect(child, skill, maxLevel);
            } else {
                parseEffect(child, skill, maxLevel);
            }
        }
    }

    private void parseEffect(Node node, Skill skill, int maxLevel) {
        var factory = EffectHandler.getInstance().getHandlerFactory(node.getNodeName());
        if(isNull(factory)) {
            LOGGER.error("could not parse skill's {} effect {}", skill, node.getNodeName());
            return;
        }
        createEffect(factory, node, skill, maxLevel);
    }

    private void parseNamedEffect(Node node, Skill skill, int maxLevel){
        var effectName = parseString(node.getAttributes(), "name");
        var factory = EffectHandler.getInstance().getHandlerFactory(effectName);

        if(isNull(factory)) {
            LOGGER.error("could not parse skill's {} effect {}", skill, effectName);
            return;
        }

        createEffect(factory, node, skill, maxLevel);
    }

    void createEffect(Function<StatsSet, AbstractEffect> factory, Node node, Skill skill, int maxLevel) {
        var attr = node.getAttributes();
        var startLevel = parseInt(attr, "start-level");
        var stopLevel = parseInt(attr, "stop-level", maxLevel);
        var scope = parseEnum(attr, EffectScope.class, "scope");

        var staticStatSet = new StatsSet(parseAttributes(node));

        if(node.hasChildNodes()) {
            IntMap<StatsSet> levelInfo = parseEffectChildNodes(node, startLevel, stopLevel, staticStatSet);

            if(levelInfo.isEmpty()) {
                addStaticEffect(factory, skill, startLevel, stopLevel, scope, staticStatSet);
            } else {
                parseLevelsInfo(factory, skill, startLevel, stopLevel, scope, staticStatSet, levelInfo);
            }
        } else {
            addStaticEffect(factory, skill, startLevel, stopLevel, scope, staticStatSet);
        }
    }

    private void parseLevelsInfo(Function<StatsSet, AbstractEffect> factory, Skill skill, int startLevel, int stopLevel, EffectScope scope, StatsSet staticStatSet, IntMap<StatsSet> levelInfo) {
        for (var i = startLevel; i <= stopLevel; i++) {
            var sk = getOrCloneSkillBasedOnLast(skill.getId(), i, false, true);
            var statsSet = levelInfo.computeIfAbsent(i, level -> {
                for (int j = level; j > 0; j--) {
                    if(levelInfo.containsKey(j)) {
                        return levelInfo.get(j);
                    }
                }
                return new StatsSet();
            });
            statsSet.merge(staticStatSet);
            sk.addEffect(scope, factory.apply(statsSet));
        }
    }

    private void addStaticEffect(Function<StatsSet, AbstractEffect> factory, Skill skill, int startLevel, int stopLevel, EffectScope scope, StatsSet staticStatSet)  {
        var effect = factory.apply(staticStatSet);
        for (int i = startLevel; i <= stopLevel ; i++) {
            var sk = getOrCloneSkillBasedOnLast(skill.getId(), i, false, true);
            sk.addEffect(scope, effect);
        }
    }

    private void parseSkillAbnormal(Node node, Skill skill, int maxLevel) {
        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            switch (child.getNodeName()) {
                case ATTR_LEVEL -> parseMappedInt(child, skill, maxLevel, (level, s) -> s.setAbnormalLevel(level));
                case "time" -> parseMappedInt(child, skill, maxLevel, (time, s) -> s.setAbnormalTime(time));
                case "chance" -> parseMappedInt(child, skill, maxLevel, (chance, s) -> s.setAbnormalChance(chance));
                case "visual" -> parseMappedAbnormalVisual(child, skill, maxLevel);
            }
        }
    }

    private void parseSkillConsume(Node node, Skill skill, int maxLevel) {
        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            switch (child.getNodeName()) {
                case "mana-init" -> parseMappedInt(child, skill, maxLevel, (consume, s) -> s.setManaInitConsume(consume));
                case "mana" -> parseMappedInt(child, skill, maxLevel, (consume, s) -> s.setManaConsume(consume));
                case "hp" -> parseMappedInt(child, skill, maxLevel,  (consume, s) -> s.setHpConsume(consume));
                case "item" -> parseMappedInt(child, skill, maxLevel,  (item, s) -> s.setItemConsume(item));
                case "item-count" -> parseMappedInt(child, skill, maxLevel, (count, s) -> s.setItemConsumeCount(count));
            }
        }
    }

    private void parseSkillConstants(Skill skill, Node skillNode) {
        for (var node = skillNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            switch (node.getNodeName()) {
                case "attributes" -> parseConstantsAttributes(node, skill);
                case "consume" -> parseConstantsConsume(node, skill);
                case "target" -> parseConstantsTarget(node, skill);
                case "abnormal" -> parseConstantAbnormal(node, skill);
                case "resist-abnormals" -> parseConstantResistAbnormals(node, skill);
                case "channeling" -> parseConstantsChanneling(node, skill);
            }
        }

        if(nonNull(skillsDelay) && skillsDelay.containsKey(skill.getId())) {
            skill.setCustomDelay(skillsDelay.get(skill.getId()));
        }

        if(nonNull(skillsTime) && skillsTime.containsKey(skill.getId())) {
            skill.setCustomTime(skillsTime.get(skill.getId()));
        }
    }


    private void parseConstantsChanneling(Node node, Skill skill) {
        var attr = node.getAttributes();
        skill.setChannelingSkill(parseInt(attr, "skill"));
        skill.setChannelingMpConsume(parseInt(attr, "mp-consume"));
        skill.setChannelingInitialDelay(parseInt(attr, "initial-delay") * 1000L);
        skill.setChannelingInterval(parseInt(attr, "interval") * 1000L);
    }

    private void parseConstantResistAbnormals(Node node, Skill skill) {
        skill.setResistAbnormals(Arrays.stream(node.getTextContent().split(SPACE)).map(AbnormalType::valueOf).collect(Collectors.toSet()));
    }

    private void parseConstantAbnormal(Node node, Skill skill) {
        var attr = node.getAttributes();

        skill.setAbnormalType(parseEnum(attr, AbnormalType.class, "type"));
        skill.setAbnormalSubordination(parseEnum(attr, AbnormalType.class, "subordination"));
        skill.setAbnormalInstant(parseBoolean(attr, "instant"));
    }

    private void parseConstantsTarget(Node node, Skill skill) {
        var attr = node.getAttributes();

        skill.setTargetType(parseEnum(attr, TargetType.class, "type"));
        skill.setAffectScope(parseEnum(attr, AffectScope.class, "scope"));
        skill.setAffectObject(parseEnum(attr, AffectObject.class, "object"));
        skill.setAffectRange(parseInt(attr, "range"));
        skill.setAffectMin(parseInt(attr, "affect-min"));
        skill.setAffectRandom(parseInt(attr, "affect-random"));

        forEach(node, "fan-range", fanRangeNode -> {
            var attrs = fanRangeNode.getAttributes();
            skill.setFanStartAngle(parseInt(attrs, "start-angle"));
            skill.setFanRadius(parseInt(attrs, "radius"));
            skill.setFanAngle(parseInt(attrs, "angle"));
        });
    }

    private void parseConstantsConsume(Node node, Skill skill) {
        var attr = node.getAttributes();
        skill.setSoulConsume(parseInt(attr, "soul"));
        skill.setChargeConsume(parseInt(attr, "charge"));
    }

    private void parseConstantsAttributes(Node nodeAttributes, Skill skill) {
        var attr = nodeAttributes.getAttributes();

        skill.setTrait(parseEnum(attr, TraitType.class, "trait"));
        skill.setNextAction(parseEnum(attr, NextActionType.class, "next-action"));
        skill.setProperty(parseEnum(attr, BasicProperty.class,"property"));
        skill.setStaticReuse(parseBoolean(attr, "static-reuse"));
        skill.setMagicCriticalRate(parseDouble(attr, "magic-critical-rate"));
        skill.setStayAfterDeath(parseBoolean(attr, "stay-after-death"));
        skill.setDisplayId(parseInt(attr, "display-id", skill.getId()));
        skill.setHitCancelTime(parseDouble(attr, "hit-cancel-time"));
        skill.setLevelBonusRate(parseInt(attr, "level-bonus-rate"));
        skill.setRemoveOnAction(parseBoolean(attr, "remove-on-action"));
        skill.setRemoveOnDamage(parseBoolean(attr, "remove-on-damage"));
        skill.setBlockedOnOlympiad(parseBoolean(attr, "blocked-on-olympiad"));
        skill.setSuicide(parseBoolean(attr, "suicide"));
        skill.setTriggered(parseBoolean(attr, "triggered"));
        skill.setDispellable(parseBoolean(attr, "dispellable"));
        skill.setCheck(parseBoolean(attr, "check"));
        skill.setWithoutAction(parseBoolean(attr, "without-action"));
        skill.setCanCastDisabled(parseBoolean(attr, "cast-disabled"));
        skill.setSummonShared(!parseBoolean(attr, "no-summon-shared"));
        skill.setRemoveAbnormalOnLeave(parseBoolean(attr, "remove-abnormal-on-leave"));
        skill.setIrreplacable(parseBoolean(attr, "irreplacable"));
        skill.setBlockActionSkill(parseBoolean(attr, "block-action-skill"));
        skill.setSkillAutoUseType(parseEnum(attr, SkillAutoUseType.class,"auto-use"));

        forEach(nodeAttributes, "element", elementNode -> {
            skill.setAttributeType(parseEnum(elementNode.getAttributes(), AttributeType.class, "type"));
            skill.setAttributeValue(parseInt(elementNode.getAttributes(), ATTR_VALUE));
        });

    }

    private void parseSkillAttributes(Node attributesNode, Skill skill, int maxLevel) {
        for(var node = attributesNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            switch (node.getNodeName()) {
                case "magic-level" -> parseMappedInt(node, skill, maxLevel, (value, s) -> s.setMagicLevel(value));
                case "cast-range" -> parseMappedInt(node, skill, maxLevel, (value, s) -> s.setCastRange(value));
                case "reuse" -> parseMappedInt(node, skill, maxLevel,  (value, s) -> s.setReuse(value));
                case "cool-time" -> parseMappedInt(node, skill, maxLevel, (value, s) -> s.setCoolTime(value));
                case "effect-point" -> parseMappedInt(node, skill, maxLevel, (value, s) -> s.setEffectPoint(value));
                case "effect-range" -> parseMappedInt(node, skill, maxLevel,  (value, s) -> s.setEffectRange(value));
                case "hit-time" -> parseMappedInt(node, skill, maxLevel, (value, s) -> s.setHitTime(value));
                case "activate-rate" -> parseMappedInt(node, skill, maxLevel,  (value, s) -> s.setActivateRate(value));
            }
        }
    }

    private void parseMappedInt(Node node, Skill skill, int maxLevel, IntBiConsumer<Skill> setter)  {
        int lastValue = parseInt(node.getAttributes(), ATTR_INITIAL);
        var lastLevel = skill.getLevel();
        setter.accept(lastValue, skill);

        for (var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            if (ATTR_VALUE.equals(child.getNodeName())) {
                var value = Integer.parseInt(child.getTextContent());
                if (lastValue != value) {
                    var level = parseInt(child.getAttributes(), ATTR_LEVEL);
                    lastLevel = setLastValueFromLastLevel(skill, setter, lastValue, lastLevel, level);

                    lastValue = value;
                    if (level <= maxLevel) {
                        var newSkill = getOrCloneSkillBasedOnLast(skill.getId(), level);
                        setter.accept(lastValue, newSkill);
                        lastLevel = level;
                    }
                }
            }
        }
        setValueUntilMaxLevel(skill, maxLevel, setter, lastValue, lastLevel);
    }

    private int setLastValueFromLastLevel(Skill skill, IntBiConsumer<Skill> setter, int lastValue, int lastLevel, int level) {
        var hash = skillHashCode(skill.getId(), ++lastLevel);
        var tmp = skills.get(hash);

        while (nonNull(tmp) && lastLevel++ < level) {
            setter.accept(lastValue, tmp);
            tmp = skills.get(++hash);
        }
        return lastLevel;
    }

    private void setValueUntilMaxLevel(Skill skill, int maxLevel, IntBiConsumer<Skill> setter, int lastValue, int lastLevel) {
        if(lastLevel < maxLevel) {
            var hash = skillHashCode(skill.getId(), ++lastLevel);
            var tmp = skills.get(hash);

            while (nonNull(tmp) && lastLevel++ <= maxLevel) {
                setter.accept(lastValue, tmp);
                tmp = skills.get(++hash);
            }
        }
    }

    private void parseMappedAbnormalVisual(Node node, Skill skill, int maxLevel) {
        var lastValue = parseEnumSet(node.getAttributes(), AbnormalVisualEffect.class, ATTR_INITIAL);
        var lastLevel = skill.getLevel();
        skill.setAbnormalVisualEffect(lastValue);

        for(var child = node.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
            if(ATTR_VALUE.equals(child.getNodeName())) {
                var value = parseEnumSet(child, AbnormalVisualEffect.class);
                if(!Objects.equals(lastValue, value)) {
                    var level = parseInt(child.getAttributes(), ATTR_LEVEL);
                    lastLevel = setLastValueFromlastLevel(skill, lastLevel, value, level);

                    lastValue = value;
                    if (level <= maxLevel) {
                        var newSkill = getOrCloneSkillBasedOnLast(skill.getId(), level);
                        newSkill.setAbnormalVisualEffect(lastValue);
                        lastLevel = level;
                    }
                }
            }
        }

        setValueUntilMaxLevel(skill, maxLevel, lastValue, lastLevel);
    }

    private void setValueUntilMaxLevel(Skill skill, int maxLevel, Set<AbnormalVisualEffect> lastValue, int lastLevel) {
        if(lastLevel < maxLevel) {
            var hash = skillHashCode(skill.getId(), ++lastLevel);
            var tmp = skills.get(hash);
            while (nonNull(tmp) && lastLevel++ <= maxLevel) {
                tmp.setAbnormalVisualEffect(lastValue);
                tmp = skills.get(++hash);
            }
        }
    }

    private int setLastValueFromlastLevel(Skill skill, int lastLevel, Set<AbnormalVisualEffect> value, int level) {
        var hash = skillHashCode(skill.getId(), ++lastLevel);
        var tmp = skills.get(hash);

        while(nonNull(tmp) && lastLevel++ < level) {
            tmp.setAbnormalVisualEffect(value);
            tmp = skills.get(hash++);
        }
        return lastLevel;
    }

    private void parseIcon(Node iconNode, Skill skill, int maxLevel) {
        var lastValue = parseString(iconNode.getAttributes(), ATTR_INITIAL);
        skill.setIcon(lastValue);
        for (var node = iconNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            if (ATTR_VALUE.equals(node.getNodeName())) {

                var value = node.getTextContent();
                if (!Objects.equals(lastValue, value)) {
                    lastValue = value;
                    var level = parseInt(node.getAttributes(), ATTR_LEVEL);
                    if(level <= maxLevel) {
                        var newSkill = getOrCloneSkillBasedOnLast(skill.getId(), level);
                        newSkill.setIcon(lastValue);
                    }
                }
            }
        }
    }

    private Skill getOrCloneSkillBasedOnLast(int id, int level) {
        return getOrCloneSkillBasedOnLast(id, level, false, false);
    }

    private Skill getOrCloneSkillBasedOnLast(int id, int level, boolean keepEffects, boolean keepConditions){
        var hash = skillHashCode(id, level);
        int currentLevel = level;

        Skill skill;
        do {
            skill = skills.get(hash);
            if (skill != null) {
                break;
            }
            currentLevel--;
            hash--;
        } while(currentLevel > 0);

        while (nonNull(skill) && currentLevel < level) {
            skill = skill.clone(keepEffects, keepConditions);
            skill.setLevel(++currentLevel);
            skills.put(++hash, skill);
        }
        return skill;
    }

    public static void init() {
        ServiceLoader.load(ITargetTypeHandler.class).forEach(TargetHandler.getInstance()::registerHandler);
        ServiceLoader.load(IAffectObjectHandler.class).forEach(AffectObjectHandler.getInstance()::registerHandler);
        ServiceLoader.load(IAffectScopeHandler.class).forEach(AffectScopeHandler.getInstance()::registerHandler);
        ServiceLoader.load(SkillConditionFactory.class).forEach(SkillConditionHandler.getInstance()::registerFactory);
        ServiceLoader.load(SkillEffectFactory.class).forEach(EffectHandler.getInstance()::registerFactory);
        getInstance().load();
        SkillTreesData.init();
        PetSkillEngine.init();
    }

    public void reload() {
        skills.clear();
        load();
        SkillTreesData.getInstance().load();
    }

    public static long skillHashCode(int id, int level) {
        return id * 65536L + level;
    }

    public static SkillEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final SkillEngine INSTANCE = new SkillEngine();
    }
}