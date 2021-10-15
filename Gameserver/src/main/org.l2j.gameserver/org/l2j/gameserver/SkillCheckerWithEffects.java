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
    package org.l2j.gameserver;

    import io.github.joealisson.primitive.Containers;
    import io.github.joealisson.primitive.HashIntMap;
    import io.github.joealisson.primitive.IntMap;
    import org.l2j.commons.util.Util;
    import org.l2j.gameserver.engine.skill.api.Skill;
    import org.l2j.gameserver.engine.skill.api.SkillEngine;
    import org.l2j.gameserver.enums.AttributeType;
    import org.l2j.gameserver.enums.NextActionType;
    import org.l2j.gameserver.model.item.type.WeaponType;
    import org.l2j.gameserver.model.skills.AbnormalType;
    import org.l2j.gameserver.model.skills.SkillOperateType;
    import org.l2j.gameserver.model.skills.targets.AffectObject;
    import org.l2j.gameserver.model.skills.targets.AffectScope;
    import org.l2j.gameserver.model.skills.targets.TargetType;
    import org.l2j.gameserver.model.stats.TraitType;
    import org.l2j.gameserver.settings.ServerSettings;
    import org.l2j.gameserver.util.GameXmlReader;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.w3c.dom.Document;
    import org.w3c.dom.Element;
    import org.w3c.dom.Node;
    import org.w3c.dom.NodeList;
    import org.xml.sax.InputSource;
    import org.xml.sax.SAXException;

    import javax.xml.parsers.DocumentBuilderFactory;
    import javax.xml.parsers.ParserConfigurationException;
    import javax.xml.transform.*;
    import javax.xml.transform.dom.DOMSource;
    import javax.xml.transform.stream.StreamResult;
    import javax.xml.transform.stream.StreamSource;
    import javax.xml.xpath.XPath;
    import javax.xml.xpath.XPathConstants;
    import javax.xml.xpath.XPathFactory;
    import java.io.*;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.StandardOpenOption;
    import java.util.*;
    import java.util.function.IntSupplier;
    import java.util.function.ToIntFunction;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;
    import java.util.stream.Collectors;

    import static java.util.Objects.isNull;
    import static java.util.Objects.nonNull;

    public class SkillCheckerWithEffects {

        static Logger logger = LoggerFactory.getLogger(SkillCheckerWithEffects.class);
        static IntMap<String> autoSkills = new HashIntMap<>();
        static IntMap<IntMap<SkillModel>> skills = new HashIntMap<>();

        static IntMap<String> skillTypes = new HashIntMap<>(4);
        static Map<String, String> skillProperty = new HashMap<>(2);

        static {
            skillTypes.put(0, "PHYSIC");
            skillTypes.put(1, "MAGIC");
            skillTypes.put(2, "STATIC");
            skillTypes.put(3, "DANCE");

            skillProperty.put("default", "DEFAULT");
            skillProperty.put("alter", "ALTER");
        }

        static int step = 100;
        static SkillEngine skillData;
        static SkillEffectReader effectReader;

        public static void doCheck() throws Exception, TransformerConfigurationException {
            effectReader = new SkillEffectReader();
            SkillEngine.init();
            skillData = SkillEngine.getInstance();

            fillSkillName();
            fillSkillData();
            fillSkillConditions();

            Files.createDirectories(Path.of("new-skills"));
            Files.createDirectories(Path.of("new-effects"));
            Files.createDirectories(Path.of("new-effects/stat"));

            int start = 0;
            int end = start + step;
            int maxId = skills.keySet().stream().max().orElse(0);

            while (start < maxId) {
                processFile(start, end);
                start += step;
                end += step;
            }
        }

        private static void processFile(int start, int end) throws Exception {
            var file = String.format("%05d-%05d.xml", start, end - 1);
            StringBuilder content = new StringBuilder(
                    """
                            <?xml version="1.0" encoding="UTF-8"?>
                            <list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://l2j.org" xsi:schemaLocation="http://l2j.org skills.xsd">
                            """
            );
            var isNotEmpty = false;
            for (var i = start; i < end; i++) {
                var skillLevels = skills.get(i);

                if (isNull(skillLevels)) {
                    continue;
                }
                var skillContent = processSkill(skillLevels);
                if (skillContent.length() > 0) {
                    content.append(skillContent).append("\n");
                    isNotEmpty = true;
                }
            }

            if (isNotEmpty) {
                var writer = Files.newBufferedWriter(Path.of("new-skills/", file), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                content.append("</list>");

                // dummy replace
                var text = content.toString();
                text = text.replaceAll("0.0E1", "0.0");
                text = text.replaceAll("1.0E0", "1.0");
                text = text.replaceAll("1.0E2", "100");
                text = text.replaceAll(" scope=\"GENERAL\"", "");
                text = text.replaceAll(" mode=\"DIFF\"", "");
                text = text.replaceAll(" start-level=\"1\"", "");
                text = text.replaceAll(" ticks=\"0\"", "");
                text = text.replaceAll(" attack-mod=\"1.0\"", "");
                text = text.replaceAll(" defense-mod=\"1.0\"", "");
                text = text.replaceAll(" charges=\"1\"", "");
                text = text.replaceAll(" experience-multiplier=\"1.0\"", "");
                text = text.replaceAll(" can-kill=\"false\"", "");
                text = text.replaceAll(" max-level=\"1\"", "");
                text = text.replaceAll(" affect=\"CASTER\"", "");
                text = text.replaceAll(" lower=\"true\"", "");
                text = text.replaceAll(" heal=\"false\"", "");
                text = text.replaceAll("\"0.0\"", "\"0\"");
                text = text.replaceAll(" critical-chance=\"0\"", "");
                text = text.replaceAll(" initial=\"0\"", "");
                text = text.replaceAll(" pve-only=\"false\"", "");
                text = text.replaceAll(" check-3d=\"false\"", "");
                text = text.replaceAll(" is-shine=\"true\"", "");
                text = text.replaceAll(" advanced=\"false\"", "");
                text = text.replaceAll(" value=\"0\"", "");
                text = text.replaceAll(" attacker=\"Creature\"", "");
                text = text.replaceAll(" learned=\"false\"", "");
                text = text.replaceAll(" include-caster=\"true\"", "");
                text = text.replaceAll(" min-damage=\"1\"", "");
                text = text.replaceAll(" any-type=\"false\"", "");
                text = text.replaceAll(" knock-down=\"false\"", "");
                text = text.replaceAll(" use-request=\"false\"", "");
                text = text.replaceAll("<location heading=\"0\"", "<location");
                text = text.replaceAll("<location level=\"1\"", "<location");
                text = text.replaceAll("<magic-cost type=\"MAGIC\">", "<magic-cost>");
                text = text.replaceAll("<trigger-skill-by-change-exp chance=\"100\" gain=\"true\"", "<trigger-skill-by-change-exp");
                Document document = convertStringToDocument(text);
                var data = toPrettyXmlString(4, document);
                data = data.replaceAll("aaa_", ""); // dummy sort hack
                data = data.replaceAll("aab_", ""); // dummy sort hack
                data = data.replace(" xmlns=\"http://l2j.org\"", "");
                data = data.replace("instance\"", "instance\" xmlns=\"http://l2j.org\"");
                data = data.replaceAll("<trigger-skill-by-attack critical=\"false\"", "<trigger-skill-by-attack");
                writer.write(data);
                writer.flush();
                writer.close();
            }
        }

        private static StringBuilder processSkill(IntMap<SkillModel> skillLevels) throws Exception {
            var content = new StringBuilder();
            var maxLevel = skillLevels.keySet().stream().max().orElse(0);
            if (maxLevel > 0) {
                var baseSkill = skillLevels.get(1);
                content.append("\t<skill aaa_id=\"").append(baseSkill.id).append("\" ")
                        .append("aaa_name=\"").append(baseSkill.name).append("\" ");
                if (maxLevel != 1)
                    content.append("aab_max-level=\"").append(maxLevel).append("\" ");

                var refSkill = skillData.getSkill(baseSkill.id, 1);

                if (baseSkill.debuff) {
                    content.append("debuff=\"true\" ");
                }

                if (nonNull(refSkill) && refSkill.getOperateType() != SkillOperateType.P) {
                    content.append("action=\"").append(refSkill.getOperateType()).append("\"");
                }

                var skillType = skillTypes.getOrDefault(baseSkill.isMagic, "PHYSIC");
                if (!skillType.equals("PHYSIC")) {
                    content.append(" type=\"").append(skillType).append("\"");
                }
                content.append(">\n");

                content.append(parseSkillDescriptions(skillLevels, false));
                content.append(parseSkillsIcons(baseSkill, skillLevels, maxLevel));
                content.append(parseSkillAttributes(baseSkill, skillLevels, maxLevel, refSkill));
                content.append(parseSkillConsume(baseSkill, skillLevels, maxLevel, refSkill));
                parseSkillTarget(content, refSkill);

                if (nonNull(refSkill)) {
                    parseSkillAbnormal(content, maxLevel, refSkill);

                    if (!Util.isNullOrEmpty(refSkill.getAbnormalResists())) {
                        content.append("\t\t<resist-abnormals>").append(refSkill.getAbnormalResists().stream().sorted(Comparator.comparing(Enum::name)).map(Objects::toString).collect(Collectors.joining(" "))).append("</resist-abnormals>\n");
                    }

                    if (refSkill.isChanneling()) {
                        content.append("\t\t<channeling skill=\"").append(refSkill.getChannelingSkillId()).append("\" mp-consume=\"").append(refSkill.getMpPerChanneling()).append("\" initial-delay=\"").append(refSkill.getChannelingTickInitialDelay() / 1000).append("\" interval=\"").append(refSkill.getChannelingTickInterval() / 1000).append("\"/>\n");
                    }
                    try {
                        content.append(parseSkillConditions(baseSkill, skillLevels, maxLevel));
                        content.append(parseSkillEffects(baseSkill, maxLevel));
                    } catch (TransformerException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                content.append("\t</skill>");
            }
            return content;
        }

        private static StringBuilder parseSkillEffects(SkillModel baseSkill, int maxLevel) throws Exception {
            StringBuilder builder = new StringBuilder();
            //var effects = effectReader.toNewEffectsArch(baseSkill, maxLevel).toString();
            var effects = effectReader.toEffectsXmlString(baseSkill.id);
            if (Util.isNotEmpty(effects)) {
                effects = effects.replaceAll("0.0E1", "0.0");
                effects = effects.replaceAll("1.0E0", "1.0");
                effects = effects.replaceAll("1.0E2", "100");

                if (baseSkill.defense_mod != 0)
                    effects = doReplaceAttribute(effects, baseSkill, "defense-mod", baseSkill.defense_mod, 1.0, true, "<physical-attack ");

                if (baseSkill.critical_chance != 0)
                    effects = doReplaceAttribute(effects, baseSkill, "critical-chance", baseSkill.critical_chance, 0, false, "<physical-attack ", "<physical-soul-attack ", "<backstab ", "<energy-attack ");

                effects = doReplaceAttribute(effects, baseSkill, "over-hit", baseSkill.over_hit, false, true, "<physical-attack ", "<physical-soul-attack ", "<backstab ", "<energy-attack ", "<magical-attack ", "<physical-attack-hp-link ", "<soul-blow ");
                effects = doReplaceAttribute(effects, baseSkill, "ignore-shield", baseSkill.ignore_shield, false, true, "<physical-attack ", "<physical-soul-attack ",  "<energy-attack ");

                effects = effects.replace(" physical-defense-mod=\"1.0\"", "");

                //physical-soul-attack physical-attack energy-attack
                builder.append("\t\t").append(effects);
            }
            return builder;
        }

        private static String doReplaceAttribute(String effects, SkillModel baseSkill, String attribute, Object obj, Object base, boolean overwrite, String... data) throws IOException {
            if (!effects.contains(attribute)) {
                for (String att : data)
                    effects = effects.replaceAll(att, att + "" + attribute + "=\"" + obj + "\"");
            } else {
                var pattern = Pattern.compile("^.*" + attribute + "=\"(.*?)\".*");
                var reader = new BufferedReader(new StringReader(effects));
                String line;
                var read = false;
                while (nonNull(line = reader.readLine())) {
                    var matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        var match = matcher.group(1);
                        if (overwrite) {
                            if (!obj.toString().equalsIgnoreCase(match)) {
                                effects = effects.replaceAll(attribute + "=\"" + match + "\"", attribute + "=\"" + obj + "\"");
                                System.out.println("Skill " + baseSkill.id + " " + baseSkill.level + " overwrite " + attribute + " -> " + match + " to " + obj);
                            }
                            if (base.toString().equalsIgnoreCase(match)) {
                                effects = effects.replaceAll(attribute + "=\"" + match + "\"", "");
                                System.out.println("Skill " + baseSkill.id + " " + baseSkill.level + " remove default value " + attribute + " -> " + match);
                            }
                        }
                        read = true;
                    }
                }
                if (!read)
                    System.out.println("Can't find attribute: " + attribute + " -> " + baseSkill.id + " " + baseSkill.level);
            }

            return effects;
        }

        private static StringBuilder parseSkillConditions(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) throws TransformerException {
            StringBuilder builder = new StringBuilder();

            var conditions = effectReader.toConditionsXmlString(baseSkill.id).replace("<conditions>", "").replace("</conditions>", "").replaceAll("(^\\s+|\\s+$)", "");

            StringBuilder parsedConditions = new StringBuilder();

            if ("shield".equals(baseSkill.equipType) && !conditions.contains("EquipShield")) {
                parsedConditions.append("<condition name=\"EquipShield\"/>\n");
            }

            if (!baseSkill.attackItemTypes.isEmpty()) {
                if (!conditions.contains("<weapon")) {
                    parsedConditions.append("<weapon>\n\t\t\t\t<type>")
                            .append(baseSkill.attackItemTypes.stream().map(Objects::toString).collect(Collectors.joining(" ")))
                            .append("</type>\n\t\t\t</weapon>");
                } else {
                    conditions = conditions.replaceFirst("(<type>).*?(</type>)", "$1" + baseSkill.attackItemTypes.stream().map(Objects::toString).collect(Collectors.joining(" ")) + "$2");
                }
            }

            if (baseSkill.statType != null && !"none".equalsIgnoreCase(baseSkill.statType) && !conditions.contains("remain-status")) {
                switch (baseSkill.statType) {
                    case "myhp" -> {
                        parsedConditions.append("<remain-status amount=\"").append(baseSkill.statPercent).append("\" ");
                        if (baseSkill.statUP) {
                            parsedConditions.append("lower=\"false\"");
                        }
                        parsedConditions.append(" stat=\"HP\"/>\n");
                    }
                    case "mymp" -> {
                        parsedConditions.append("<remain-status amount=\"").append(baseSkill.statPercent).append("\" ");
                        if (baseSkill.statUP) {
                            parsedConditions.append("lower=\"false\"");
                        }
                        parsedConditions.append(" stat=\"MP\"/>\n");
                    }
                    case "none" -> {
                        // CP?
//                    parsedConditions.append("<remain-status amount=\"").append(baseSkill.statPercent).append("\" ");
//                    if (baseSkill.statUP) {
//                        parsedConditions.append("lower=\"false\"");
//                    }
//                    parsedConditions.append(" stat=\"CP\"/>\n");
                    }
                }
            }
//            if (baseSkill.statType == 1 && !conditions.contains("remain-status")) {
//                parsedConditions.append("<remain-status amount=\"").append(baseSkill.statPercent).append("\" ");
//                if (baseSkill.statUP) {
//                    parsedConditions.append("lower=\"false\"");
//                }
//                parsedConditions.append(" stat=\"HP\"/>\n");
//            } else if (baseSkill.statType == 2 && !conditions.contains("remain-status")) {
//                parsedConditions.append("<remain-status amount=\"").append(baseSkill.statPercent).append("\" ");
//                if (baseSkill.statUP) {
//                    parsedConditions.append("lower=\"false\"");
//                }
//                parsedConditions.append(" stat=\"MP\"/>\n");
//            } else if (baseSkill.statType == 3 && !conditions.contains("remain-status")) {
//                parsedConditions.append("<remain-status amount=\"").append(baseSkill.statPercent).append("\" ");
//                if (baseSkill.statUP) {
//                    parsedConditions.append("lower=\"false\"");
//                }
//                parsedConditions.append(" stat=\"CP\"/>\n");
//            }

            parsedConditions.append(conditions);

            if (parsedConditions.length() > 0) {
                builder.append("\t\t").append("<conditions>\n\t\t\t").append(parsedConditions).append("\n\t\t</conditions>\n");
            }
            return builder;
        }

        private static StringBuilder parseSkillConsume(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel, Skill refSkill) {
            var content = new StringBuilder();
            var hasAttr = false;

            if (nonNull(refSkill) && (refSkill.getMaxSoulConsumeCount() > 0 || refSkill.getChargeConsumeCount() > 0)) {
                hasAttr = true;
            }

            var elements = new StringBuilder();

            elements.append(parseSkillsManaInitConsume(baseSkill, skillLevels, maxLevel));
            elements.append(parseSkillManaConsume(baseSkill, skillLevels, maxLevel));
            elements.append(parseSkillHpConsume(baseSkill, skillLevels, maxLevel));
            elements.append(parseSkillDpConsume(baseSkill, skillLevels, maxLevel));
            elements.append(parseSkillItemConsume(baseSkill, skillLevels, maxLevel));
            elements.append(parseSkillItemCountConsume(baseSkill, skillLevels, maxLevel));

            if (hasAttr) {
                content.append("\t\t<consume");

                if (refSkill.getMaxSoulConsumeCount() > 0) {
                    content.append(" soul=\"").append(refSkill.getMaxSoulConsumeCount()).append("\"");
                }

                if (refSkill.getChargeConsumeCount() > 0) {
                    content.append(" charge=\"").append(refSkill.getChargeConsumeCount()).append("\"");
                }

                if (elements.length() == 0) {
                    content.append("/>\n");
                } else {
                    content.append(">\n").append(elements).append("\t\t</consume>\n");
                }
            } else if (elements.length() > 0) {
                content.append("\t\t<consume>\n").append(elements).append("\t\t</consume>\n");
            }
            return content;
        }

        private static StringBuilder parseSkillItemCountConsume(SkillModel refSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("item-count", refSkill, skillLevels, maxLevel, () -> refSkill.itemCount, skill -> skill.itemCount);
        }

        private static StringBuilder parseSkillMappedInt(String name, Skill baseSkill, int maxLevel, IntSupplier initialSupplier, ToIntFunction<Skill> valueFunction) {
            var content = new StringBuilder();
            var lastValue = initialSupplier.getAsInt();
            if (lastValue == 0) {
                var goAhead = false;
                for (int i = baseSkill.getLevel() + 1; i <= maxLevel; i++) {
                    if (skillData.getMaxLevel(baseSkill.getId()) < i) {
                        return content;
                    }
                    var skill = skillData.getSkill(baseSkill.getId(), i);
                    if (nonNull(skill)) {
                        if (valueFunction.applyAsInt(skill) != lastValue) {
                            goAhead = true;
                            break;
                        }
                    }
                }
                if (!goAhead) {
                    return content;
                }
            }

            content.append("\t\t\t<").append(name).append(" initial=\"").append(lastValue).append("\"");

            var levelInfo = new StringBuilder();
            for (int i = baseSkill.getLevel() + 1; i <= maxLevel; i++) {
                if (skillData.getMaxLevel(baseSkill.getId()) < i) {
                    break;
                }
                var skill = skillData.getSkill(baseSkill.getId(), i);
                if (nonNull(skill)) {
                    if (valueFunction.applyAsInt(skill) == lastValue) {
                        continue;
                    }
                    lastValue = valueFunction.applyAsInt(skill);
                    levelInfo.append("\t\t\t\t<value level=\"").append(i).append("\">").append(lastValue).append("</value>\n");
                }
            }

            if (levelInfo.length() > 0) {
                content.append(">\n").append(levelInfo).append("\t\t\t</").append(name).append(">\n");
            } else {
                content.append("/>\n");
            }

            return content;
        }

        private static StringBuilder parseSkillItemConsume(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("item", baseSkill, skillLevels, maxLevel, () -> baseSkill.itemId, skill -> skill.itemId);
        }

        private static void parseSkillAbnormal(StringBuilder content, int maxLevel, Skill refSkill) {
            if (nonNull(refSkill) && refSkill.getAbnormalTime() != 0) {
                content.append("\t\t<abnormal");
                if (refSkill.getAbnormalType() != AbnormalType.NONE) {
                    content.append(" type=\"").append(refSkill.getAbnormalType()).append("\"");
                }

                if (nonNull(refSkill.getAbnormalVisualEffect())) {
                    content.append(" visual=\"").append(refSkill.getAbnormalVisualEffect().stream().map(Objects::toString).collect(Collectors.joining(" "))).append("\"");
                }

                if (refSkill.getSubordinationAbnormalType() != AbnormalType.NONE) {
                    content.append(" subordination=\"").append(refSkill.getSubordinationAbnormalType()).append("\"");
                }

                if (refSkill.isAbnormalInstant()) {
                    content.append(" instant=\"true\"");
                }

                content.append(">\n");
                content.append(parseSkillAbnormalLevel(refSkill, maxLevel));
                content.append(parseSkillAbnormalTime(refSkill, maxLevel));
                content.append(parseSkillAbnormalChance(refSkill, maxLevel));
                content.append("\t\t</abnormal>\n");
            }
        }

        private static StringBuilder parseSkillAbnormalChance(Skill baseSkill, int maxLevel) {
            return parseSkillMappedInt("chance", baseSkill, maxLevel, baseSkill::getActivateRate, Skill::getActivateRate);
        }

        private static StringBuilder parseSkillAbnormalTime(Skill baseSkill, int maxLevel) {
            return parseSkillMappedInt("time", baseSkill, maxLevel, baseSkill::getAbnormalTime, Skill::getAbnormalTime);
        }

        private static StringBuilder parseSkillAbnormalLevel(Skill baseSkill, int maxLevel) {
            return parseSkillMappedInt("level", baseSkill, maxLevel, baseSkill::getAbnormalLvl, Skill::getAbnormalLvl);
        }

        private static void parseSkillTarget(StringBuilder content, Skill refSkill) {
            content.append("\t\t<target ");
            var hasFanRange = false;
            if (nonNull(refSkill)) {
                if (refSkill.getTargetType() != TargetType.SELF) {
                    content.append("type=\"").append(refSkill.getTargetType()).append("\" ");
                }

                if (refSkill.affectScope != AffectScope.SINGLE) {
                    content.append("scope=\"").append(refSkill.affectScope).append("\" ");
                }

                if (refSkill.getAffectObject() != AffectObject.ALL) {
                    content.append("object=\"").append(refSkill.getAffectObject()).append("\" ");
                }

                if (refSkill.getAffectRange() > 0) {
                    content.append("range=\"").append(refSkill.getAffectRange()).append("\" ");
                }

                if (refSkill.affectMin > 0 || refSkill.affectRandom > 0) {
                    content.append("affect-min=\"").append(refSkill.affectMin).append("\" affect-random=\"").append(refSkill.affectRandom).append("\" ");
                }

                if (refSkill.getFanStartAngle() != 0 || refSkill.getFanRadius() != 0 || refSkill.getFanAngle() != 0) {
                    hasFanRange = true;

                    content.append(">\n\t\t\t<fan-range start-angle=\"").append(refSkill.getFanStartAngle()).append("\" radius=\"").append(refSkill.getFanRadius()).append("\" angle=\"").append(refSkill.getFanAngle()).append("\"/>\n");
                }

            }
            if (hasFanRange) {
                content.append("\t\t</target>\n");
            } else {
                content.append("/>\n");
            }
        }

        private static StringBuilder parseSkillAttributes(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel, Skill refSkill) {
            var content = new StringBuilder();
            content.append("\t\t<attributes ");

            var property = skillProperty.getOrDefault(baseSkill.magicType, "DEFAULT");
            if (!property.equals("DEFAULT")) {
                content.append("property=\"").append(property).append("\" ");
            }

            if (nonNull(refSkill)) {

                if (refSkill.isStaticReuse() || baseSkill.static_reuse) {
                    content.append("static-reuse=\"true\" ");
                }

                if (refSkill.getNextAction() != NextActionType.NONE) {
                    content.append("next-action=\"").append(refSkill.getNextAction()).append("\" ");
                }

                if (refSkill.getMagicCriticalRate() != 0) {
                    content.append("magic-critical-rate=\"").append(refSkill.getMagicCriticalRate()).append("\" ");
                }

                if (refSkill.getTrait() != TraitType.NONE) {
                    content.append("trait=\"").append(refSkill.getTrait()).append("\" ");
                }

                if (refSkill.isStayAfterDeath()) {
                    content.append("stay-after-death=\"true\" ");
                }

                if (refSkill.getDisplayId() != baseSkill.id) {
                    content.append("display-id=\"").append(refSkill.getDisplayId()).append("\" ");
                }

                if (refSkill.getHitCancelTime() > 0) {
                    content.append("hit-cancel-time=\"").append(refSkill.getHitCancelTime()).append("\" ");
                }

                if (refSkill.getLevelBonusRate() > 0) {
                    content.append("level-bonus-rate=\"").append(refSkill.getLevelBonusRate()).append("\" ");
                }

                if (refSkill.isRemovedOnAnyActionExceptMove()) {
                    content.append("remove-on-action=\"true\" ");
                }

                if (refSkill.isRemovedOnDamage()) {
                    content.append("remove-on-damage=\"true\" ");
                }

                if (refSkill.isBlockedInOlympiad()) {
                    content.append("blocked-on-olympiad=\"true\" ");
                }

                if (refSkill.isSuicideAttack()) {
                    content.append("suicide=\"true\" ");
                }

                if (refSkill.isTriggeredSkill()) {
                    content.append("triggered=\"true\" ");
                }

                if (!refSkill.canBeDispelled()) {
                    content.append("dispellable=\"false\" ");
                }

                if (refSkill.isExcludedFromCheck()) {
                    content.append("check=\"false\" ");
                }

                if (refSkill.isWithoutAction()) {
                    content.append("without-action=\"true\" ");
                }

                if (refSkill.canCastWhileDisabled()) {
                    content.append("cast-disabled=\"true\"");
                }

                if (!refSkill.isSharedWithSummon()) {
                    content.append("no-summon-shared=\"true\" ");
                }

                if (refSkill.isDeleteAbnormalOnLeave()) {
                    content.append("remove-abnormal-on-leave=\"true\" ");
                }

                if (refSkill.isIrreplacableBuff()) {
                    content.append("irreplacable=\"true\" ");
                }

                if (refSkill.isBlockActionUseSkill()) {
                    content.append("block-action-skill=\"true\" ");
                }
            }

            if (autoSkills.containsKey(baseSkill.id)) {
                content.append("auto-use=\"").append(autoSkills.get(baseSkill.id)).append("\"");
            }

            content.append(">\n");
            if (nonNull(refSkill) && !refSkill.isPassive()) {
                content.append(parseSkillsMagicLevel(refSkill, maxLevel));
            }

            if (nonNull(refSkill) && !refSkill.isPassive()) {
                content.append(parseSkillCastRange(baseSkill, skillLevels, maxLevel));
            }

            if (nonNull(refSkill) && !refSkill.isPassive()) {
                content.append(parseSkillReuse(baseSkill, skillLevels, maxLevel));
            }

            if (nonNull(refSkill) && !refSkill.isPassive()) {
                content.append(parseSkillCoolTime(baseSkill, skillLevels, maxLevel));
            }

            if (nonNull(refSkill) && !refSkill.isPassive()) {
                content.append(parseSkillEffectPoint(baseSkill, skillLevels, maxLevel));
            }

            if (nonNull(refSkill) && !refSkill.isPassive()) {
                content.append(parseSkillEffectRange(refSkill, maxLevel));
            }

            if (nonNull(refSkill) && !refSkill.isPassive()) {
                content.append(parseSkillHitTime(baseSkill, skillLevels, maxLevel));
            }

            if (nonNull(refSkill) && !refSkill.isPassive() && (isNull(refSkill.getAbnormalType()) || refSkill.getAbnormalType() == AbnormalType.NONE)) {
                content.append(parseActivateRate(refSkill, maxLevel));
            }

            if (nonNull(refSkill) && refSkill.getAttributeType() != AttributeType.NONE) {
                content.append("\n\t\t\t<element type=\"").append(refSkill.getAttributeType()).append("\" value=\"").append(refSkill.getAttributeValue()).append("\"/>\n");
            }

            content.append("\t\t</attributes>\n");
            return content;
        }

        private static StringBuilder parseSkillReuse(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("reuse", baseSkill, skillLevels, maxLevel, () -> baseSkill.reuseDelay, skill -> skill.reuseDelay);
        }

        private static StringBuilder parseActivateRate(Skill refSkill, int maxLevel) {
            return parseSkillMappedInt("activate-rate", refSkill, maxLevel, refSkill::getActivateRate, Skill::getActivateRate);
        }

        private static StringBuilder parseSkillHitTime(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("hit-time", baseSkill, skillLevels, maxLevel, () -> baseSkill.hitTime, skill -> skill.hitTime);
        }

        private static StringBuilder parseSkillEffectRange(Skill baseSkill, int maxLevel) {
            return parseSkillMappedInt("effect-range", baseSkill, maxLevel, baseSkill::getEffectRange, Skill::getEffectRange);
        }

        private static StringBuilder parseSkillEffectPoint(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("effect-point", baseSkill, skillLevels, maxLevel, () -> baseSkill.effectPoint, skill -> skill.effectPoint);
        }

        private static StringBuilder parseSkillCoolTime(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("cool-time", baseSkill, skillLevels, maxLevel, () -> baseSkill.coolTime, skill -> skill.coolTime);
        }

        private static StringBuilder parseSkillMappedInt(String name, SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel, IntSupplier initialSupplier, ToIntFunction<SkillModel> valueFunction) {
            var content = new StringBuilder();
            if (initialSupplier.getAsInt() == 0 && skillLevels.values().stream().noneMatch(s -> valueFunction.applyAsInt(s) != initialSupplier.getAsInt())) {
                return content;
            }

            content.append("\t\t\t<").append(name).append(" initial=\"").append(initialSupplier.getAsInt()).append("\"");

            if (skillLevels.values().stream().anyMatch(s -> valueFunction.applyAsInt(s) != initialSupplier.getAsInt())) {
                var lastValue = initialSupplier.getAsInt();
                content.append(">\n");

                for (int i = 2; i <= maxLevel; i++) {
                    var refSkill = skillLevels.get(i);
                    if (lastValue == valueFunction.applyAsInt(refSkill)) {
                        continue;
                    }
                    lastValue = valueFunction.applyAsInt(refSkill);
                    content.append("\t\t\t\t<value level=\"").append(i).append("\">").append(lastValue).append("</value>\n");
                }
                content.append("\t\t\t</").append(name).append(">\n");

            } else {
                content.append("/>\n");
            }
            return content;
        }

        private static StringBuilder parseSkillCastRange(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("cast-range", baseSkill, skillLevels, maxLevel, () -> baseSkill.castRange, skill -> skill.castRange);
        }

        private static StringBuilder parseSkillHpConsume(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("hp", baseSkill, skillLevels, maxLevel, () -> baseSkill.hpConsume, skill -> skill.hpConsume);
        }

        private static StringBuilder parseSkillDpConsume(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("dp", baseSkill, skillLevels, maxLevel, () -> baseSkill.dpConsume, skill -> skill.dpConsume);
        }

        private static StringBuilder parseSkillManaConsume(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("mana", baseSkill, skillLevels, maxLevel, () -> baseSkill.manaConsume, skill -> skill.manaConsume);
        }

        private static StringBuilder parseSkillsHpInitConsume(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("hp-init", baseSkill, skillLevels, maxLevel, () -> baseSkill.hpInitial, skill -> skill.hpInitial);
        }

        private static StringBuilder parseSkillsManaInitConsume(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            return parseSkillMappedInt("mana-init", baseSkill, skillLevels, maxLevel, () -> baseSkill.manaInitial, skill -> skill.manaInitial);
        }

        private static StringBuilder parseSkillsMagicLevel(Skill baseSkill, int maxLevel) {
            return parseSkillMappedInt("magic-level", baseSkill, maxLevel, baseSkill::getMagicLevel, Skill::getMagicLevel);
        }

        private static StringBuilder parseSkillsIcons(SkillModel baseSkill, IntMap<SkillModel> skillLevels, int maxLevel) {
            var content = new StringBuilder();
            content.append("\t\t<icon initial=\"").append(baseSkill.icon).append("\"");
            if (skillLevels.values().stream().anyMatch(s -> !s.icon.equals(baseSkill.icon))) {
                var baseIcon = baseSkill.icon;
                content.append(">\n");
                for (int i = 2; i <= maxLevel; i++) {
                    var refSkill = skillLevels.get(i);
                    if (baseIcon.equals(refSkill.icon)) {
                        continue;
                    }
                    content.append("\t\t\t<value level=\"").append(i).append("\">").append(refSkill.icon).append("</value>\n");
                    baseIcon = refSkill.icon;
                }
                content.append("\t\t</icon>\n");
            } else {
                content.append("/>\n");
            }
            return content;
        }

        private static StringBuilder parseSkillDescriptions(IntMap<SkillModel> skillLevels, boolean skip) {
            var content = new StringBuilder();

            var baseSkill = skillLevels.get(1);
            if (skillLevels.values().stream().anyMatch(s -> !s.desc.equals(baseSkill.desc))) {
                if (!skip)
                    content.append("\t<!-- \n");
                var maxLevel = skillLevels.keySet().stream().max().orElse(0);
                var baseDesc = "";
                for (int i = 1; i <= maxLevel; i++) {
                    var refSkill = skillLevels.get(i);

                    if (refSkill.itemId == 0 && refSkill.desc.contains("Consumes")) {
                        var desc = refSkill.desc.substring(refSkill.desc.indexOf("Consumes"));
                        refillConsume(refSkill, desc.replace("\\n", " "));
                    }
                    if (refSkill.desc.equals(baseDesc)) {
                        continue;
                    }
                    if (!skip)
                        content.append("\t\t\t").append("level ").append(i).append(": ").append(refSkill.desc.replace("\\n", " ")).append("\n");
                    baseDesc = refSkill.desc;
                }
                if (!skip)
                    content.append("\t\t-->\n");
            } else {
                if (!skip)
                    content.append("\t<!-- ").append(baseSkill.desc).append(" -->\n");
            }

            checkOtherParamsFromDescription(baseSkill);
            return content;
        }


        private static final Pattern DEFENSE_MODE = Pattern.compile("^.*Ignores (\\d+?) of the target's defense.*");


        private static void checkOtherParamsFromDescription(SkillModel model) {
            var desc = model.desc;
            if (desc.contains("Skill with fixed cooldown"))
                model.static_reuse = true;
            if (desc.contains("Ignores Shield Defense"))
                model.ignore_shield = true;
            if (desc.contains("Over-hit"))
                model.over_hit = true;
            if (desc.contains("\\nCritical"))
                model.critical_chance = 25;

            var matcher = DEFENSE_MODE.matcher(desc);
            if (matcher.matches()) {
                var match = matcher.group(1).replace("%", "").replace("\n", "").trim();
                model.defense_mod = (100 - Double.parseDouble(match)) / 100;
            }
        }

        private static void refillConsume(SkillModel model, String data) {
            var array = data.split(" ");
            var type = array[2];
            switch (type) {
                case "DP", "DP." -> model.dpConsume = Integer.parseInt(array[1]);
                case "Spirit" -> {
                    model.itemId = 3031;
                    model.itemCount = Integer.parseInt(array[1]);
                }
                case "MP", "HP", "large", "own", "depends", "" -> { // Skip
                    // Consumes a large amount of MP
                    // Consumes your HP
                    // Consumes {} MP
                    // Consumes one's own HP
                    // Consumes MP depends on your level
                }
                default -> {
                    System.out.println("UNK type -> " + type + ", data: " + data);
                }
            }
        }

        /*
         *
         *  13 => DualSword
         *  9 => Spear
         *
         *  equiptype
         *      1       => armor/shield
         *      2       => weapon
         *
         * Conditions
         *
         *
         *       equiptype attackitemtype
         *           2          13              => dualsword.
         *           2     1;3;2;4;5;6;7;13;9   => sword, blunt weapon, spear or dualsword
         *           2     1;3;2;4;5;6;7;13     => Sword, Blunt Weapon, Dualsword
         *           2     10                   => Fist
         *           2     11                   => Bow
         *           2     8                    => Dagger
         *           2     2                    => Spear
         *           0                          =>
         *           1                          => Shield
         *
         *
         *      Stattype    up
         *          1               => < HP
         *          2               => < MP
         *          3       1       => > CP
         *
         *
         *      mpConsume2 => consume mana
         *      mpConsume1 => consume mana-init
         *      hpconsume => consume hp
         *      itemid    => consume item
         *      itemnum   => consume item-count
         *
         */
        private static final Pattern SKILL_CONDITIONS_PATTERN = Pattern.compile("^.*id=(\\d+)\\tlevel=(\\d+)\\t.*?\\tequiptype=(.*?)\\tattackitemtype=\\{(.*?)}\\tstattype=(.*?)\\tstatpercentage=(\\d+)\\tup=(.*?)\\thpconsume=(\\d+)\\tmpconsume1=(\\d+)\\tmpconsume2=(\\d+)\\titemid=(\\d+)\\titemnum=(\\d+)\\t.*");

        private static void fillSkillConditions() throws IOException {
            BufferedReader reader = Files.newBufferedReader(Path.of("dat_decoded/skills_conditions.txt"));
            String line;
            while (nonNull(line = reader.readLine())) {
                Matcher matcher = SKILL_CONDITIONS_PATTERN.matcher(line);
                if (matcher.matches()) {
                    var id = Integer.parseInt(matcher.group(1));
                    var level = Integer.parseInt(matcher.group(2));

                    var skill = skills.getOrDefault(id, Containers.emptyIntMap()).get(level);
                    if (isNull(skill)) {
                        logger.warn("skill name not found {}:{}", id, level);
                        continue;
                    }

                    skill.equipType = matcher.group(3);
                    skill.attackItemTypes = parseWeaponTypes(matcher.group(4));

                    skill.statType = matcher.group(5);
                    skill.statPercent = Integer.parseInt(matcher.group(6));
                    skill.statUP = !matcher.group(7).equals("less");

                    skill.hpInitial = Integer.parseInt(matcher.group(8));
                    skill.manaInitial = Integer.parseInt(matcher.group(9));
                    var a = Integer.parseInt(matcher.group(10));
                    skill.itemId = Integer.parseInt(matcher.group(11));
                    skill.itemCount = Integer.parseInt(matcher.group(12));
                }

            }
        }

        private static EnumSet<WeaponType> parseWeaponTypes(String weapons) {
            EnumSet<WeaponType> weaponsType = EnumSet.noneOf(WeaponType.class);
            if (Util.isNotEmpty(weapons)) {
                for (String weapon : weapons.split(";")) {
                    switch (weapon) {
                        case "dual" -> weaponsType.add(WeaponType.DUAL);
                        case "sword" -> weaponsType.add(WeaponType.SWORD);
                        case "buster" -> weaponsType.add(WeaponType.MAGIC_SWORD);
                        case "twohandsword" -> weaponsType.add(WeaponType.TWO_HAND_SWORD);
                        case "blunt" -> weaponsType.add(WeaponType.BLUNT);
                        case "twohandblunt" -> weaponsType.add(WeaponType.HAMMER);
                        case "staff" -> weaponsType.add(WeaponType.STAFF);
                        case "twohandstaff" -> weaponsType.add(WeaponType.ROD);
                        case "pole" -> weaponsType.add(WeaponType.SPEAR);
                        case "dagger" -> weaponsType.add(WeaponType.DAGGER);
                        case "dualfist" -> weaponsType.add(WeaponType.DUAL_FIST);
                        case "bow" -> weaponsType.add(WeaponType.BOW);
                        case "ancientsword" -> weaponsType.add(WeaponType.ANCIENT_SWORD);
                        case "rapier" -> weaponsType.add(WeaponType.RAPIER);
                    }
                }
            }
            return weaponsType;
        }

        private static final WeaponType[] WEAPON_TYPES = WeaponType.values();

        private static WeaponType weaponTypeByOrdinal(int ordinal) {
            return WEAPON_TYPES[ordinal];
        }

        private static final Pattern SKILL_PATTERN = Pattern.compile("^.*skill_id=(\\d+)\\tskill_level=(\\d+)\\t.*?\\tMagicType=(.*?)\\toperate_type=(\\d+)\\tmp_consume=(\\d+)\\tcast_range=(-?\\d+)\\t.*?\\thit_time=(.*?)\\tcool_time=(.*?)\\treuse_delay=(.*?)\\teffect_point=(.*?)\\tis_magic=(\\d+)\\t.*?\\ticon=\\[(.*?)]\\t.*?\\tdebuff=(.*?)\\t.*?\\thp_consume=(\\d+)\\t.*");
        private static final Pattern AUTO_SKILL_PATTERN = Pattern.compile("^.*skill_id=(\\d+)\\tautomatic_use=(\\d+)\\t.*");

        // skill_begin	skill_id=1	skill_level=1	skill_sublevel=0\toperate_type=0	icon_hide=0	icon_type=0	MagicType=1	mp_consume=42	cast_range=40	cast_style=9	hit_time=1.733	cool_time=0.5	reuse_delay=3.0	effect_point=-107	is_magic=0	origin_skill=0	is_double=0	animation={[Mix05]}	skill_visual_effect=[1]	icon=[icon.skill0001]	icon_panel=[None]	debuff=1	resist_cast=0	enchant_skill_level=0	enchant_icon=[None]	hp_consume=0	rumble_self=9	rumble_target=11	skill_end
        private static void fillSkillData() throws IOException {
            BufferedReader reader = Files.newBufferedReader(Path.of("dat_decoded/skills.txt"));
            String line;
            while (nonNull(line = reader.readLine())) {
                Matcher matcher = SKILL_PATTERN.matcher(line);
                if (matcher.matches()) {
                    var id = Integer.parseInt(matcher.group(1));
                    var level = Integer.parseInt(matcher.group(2));
                    var magicType = matcher.group(3);
                    var operateType = Integer.parseInt(matcher.group(4));
                    var mpConsume = Integer.parseInt(matcher.group(5));
                    var castRange = Integer.parseInt(matcher.group(6));
                    var hitTime = matcher.group(7).contains(".") ? (int) (Float.parseFloat(matcher.group(7)) * 1000) : Integer.parseInt(matcher.group(7));
                    var coolTime = (int) (Float.parseFloat(matcher.group(8)) * 1000);
                    int reuseDelay = (int) (Float.parseFloat(matcher.group(9)) * 1000);
                    var effectPoint = Integer.parseInt(matcher.group(10));
                    var isMagic = Integer.parseInt(matcher.group(11));
                    var icon = matcher.group(12);
                    var debuff = matcher.group(13).equals("1");
                    var hpConsume = Integer.parseInt(matcher.group(14));

                    var skill = skills.getOrDefault(id, Containers.emptyIntMap()).get(level);
                    if (isNull(skill)) {
                        logger.warn("skill name not found {}:{}", id, level);
                        continue;
                    }

                    skill.operateType = operateType;

                    skill.magicType = magicType;
                    skill.manaConsume = mpConsume;
                    skill.castRange = castRange;
                    skill.hitTime = hitTime;
                    skill.coolTime = coolTime;
                    skill.reuseDelay = reuseDelay;
                    skill.effectPoint = effectPoint;
                    skill.isMagic = isMagic;
                    skill.icon = icon;
                    skill.debuff = debuff;
                    skill.hpConsume = hpConsume;
                } else if ((matcher = AUTO_SKILL_PATTERN.matcher(line)).matches()) {
                    autoSkills.put(Integer.parseInt(matcher.group(1)), matcher.group(2).equals("1") ? "BUFF" : matcher.group(2).equals("2") ? "ACTIVE" : "TRANSFORM");
                }
            }
        }

        private static final Pattern SKILL_NAME_PATTERN = Pattern.compile("^.*skill_id=(\\d+)\\tskill_level=(\\d+).*?\\tname=\\[(.*?)]\\tdesc=\\[(.*?)]\\tdesc_param=\\[(.*?)].*");

        private static void fillSkillName() throws IOException {
            BufferedReader reader = Files.newBufferedReader(Path.of("dat_decoded/skills_name.txt"));
            String line;
            var descTemplate = "";
            var lastId = -1;
            while (nonNull(line = reader.readLine())) {
                Matcher matcher = SKILL_NAME_PATTERN.matcher(line);
                if (matcher.matches()) {
                    var id = Integer.parseInt(matcher.group(1));
                    if (id != lastId) {
                        descTemplate = "";
                        lastId = id;
                    }
                    var level = Integer.parseInt(matcher.group(2));
                    var name = matcher.group(3).trim().replace("<", "(").replace(">", ")").replace("&", "&amp;");
                    String desc = "";
                    if (Util.isNotEmpty(matcher.group(4).trim())) {
                        descTemplate = matcher.group(4).trim();
                    }

                    if (Util.isNotEmpty(descTemplate)) {
                        if (Util.isNotEmpty(matcher.group(5).trim())) {
                            desc = parseDesc(descTemplate, matcher.group(5).trim());
                        } else {
                            desc = descTemplate;
                        }
                    }
                    final String des = desc;
                    skills.computeIfAbsent(id, i -> new HashIntMap<>()).computeIfAbsent(level, l -> new SkillModel(id, level, name, des));
                }
            }
        }

        private static String parseDesc(String descTemplate, String params) {
            var data = params.split(";");
            for (int i = 0; i < data.length; i++) {
                descTemplate = descTemplate.replace("$s" + (i + 1), data[i]);
            }
            return descTemplate;
        }

        static class SkillEffectReader extends GameXmlReader {

            private final Transformer transformer;
            IntMap<Node> effectsNode = new HashIntMap<>();
            IntMap<Node> conditionsNode = new HashIntMap<>();
            Map<String, Node> effects = new HashMap<>();
            Map<String, List<String>> effectsBaseNames = new HashMap<>();

            SkillEffectReader() throws TransformerConfigurationException {
                transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                load();
            }

            @Override
            protected Path getSchemaFilePath() {
                return  ServerSettings.dataPackDirectory().resolve("data/skills/skills.xsd");
            }

            @Override
            public void load() {
                parseDatapackDirectory("data/skills/", true);
            }

            @Override
            protected void parseDocument(Document doc, File f) {
                forEach(doc, "list", listNode -> forEach(listNode, "skill", skillNode -> {
                    var id = parseInt(skillNode.getAttributes(), "id");
                    for (Node node = skillNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
                        if (node.getNodeName().equalsIgnoreCase("effects")) {
                            effectsNode.put(id, node);
                        } else if (node.getNodeName().equalsIgnoreCase("conditions")) {
                            conditionsNode.put(id, node);
                        }
                    }
                }));
            }

            StringBuilder toNewEffectsArch(SkillModel skill, int maxLevel) {
                var node = effectsNode.get(skill.id);
                StringBuilder effectBuilder = new StringBuilder();
                if (isNull(node)) {
                    return effectBuilder;
                }
                for (var effectNode = node.getFirstChild(); nonNull(effectNode); effectNode = effectNode.getNextSibling()) {
                    effectBuilder.append(proccessToNewArch(effectNode, maxLevel));
                }
                return effectBuilder;
            }

            private StringBuilder proccessToNewArch(Node effectNode, int maxLevel) {
                boolean hasLeveledInfo = hasLeveledinfo(effectNode);
                if (hasLeveledInfo) {
                    return processLeveledEffect(effectNode, maxLevel);
                }
                String effectName = extractEffectName(effectNode);
                var newEffect = effectNode.cloneNode(true);
                ((Element) newEffect).setAttribute("ref-id", effectName);
                effects.putIfAbsent(effectName, newEffect);
                effectsBaseNames.computeIfAbsent(newEffect.getNodeName(), k -> new ArrayList<>()).add(effectName);
                return new StringBuilder("<effect reference=\"").append(effectName).append("\"/>\n");
            }

            private StringBuilder processLeveledEffect(Node effectNode, int maxLevel) {
                var startLevel = parseInt(effectNode.getAttributes(), "start-level");
                var stopLevel = parseInt(effectNode.getAttributes(), "stop-level", maxLevel);

                StringBuilder createdEffects = new StringBuilder();

                Map<String, Node> lastUsedNodes = new HashMap<>();
                for (int i = startLevel; i < stopLevel; i++) {
                    var newEffect = effectNode.cloneNode(false);
                    for (var child = effectNode.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
                        if (isLeveledNode(child)) {
                            Node newNode;
                            if (i == startLevel) {
                                newNode = child.cloneNode(false);
                            } else {
                                newNode = leveledToInitial(child, i);
                                if (isNull(newNode)) {
                                    newNode = lastUsedNodes.get(child.getNodeName());
                                }
                            }
                            if (nonNull(newNode)) {
                                lastUsedNodes.put(newNode.getNodeName(), newNode);
                                newEffect.appendChild(newNode);
                            }
                        } else {
                            newEffect.appendChild(child);
                        }
                    }
                    var effectName = extractEffectName(newEffect);

                    ((Element) newEffect).setAttribute("ref-id", effectName);
                    effects.putIfAbsent(effectName, newEffect);
                    effectsBaseNames.computeIfAbsent(newEffect.getNodeName(), k -> new ArrayList<>()).add(effectName);
                    createdEffects.append("<effect reference=\"").append(effectName).append("\"/>\n");
                }
                return createdEffects;
            }

            private Node leveledToInitial(Node leveledNode, int level) {
                for (var value = leveledNode.getFirstChild(); nonNull(value); value = value.getNextSibling()) {
                    if (parseInt(value.getAttributes(), "level") == level) {
                        var newNode = leveledNode.cloneNode(false);
                        newNode.getAttributes().getNamedItem("initial").setNodeValue(value.getTextContent());
                        return newNode;
                    }
                }
                return null;
            }

            private boolean isLeveledNode(Node child) {
                return nonNull(child.getAttributes().getNamedItem("initial")) || Util.falseIfNullOrElse(child.getFirstChild(), c -> c.getNodeName().equals("value"));
            }

            private boolean hasLeveledinfo(Node effectNode) {
                for (var child = effectNode.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
                    if (isLeveledNode(child)) {
                        return true;
                    }
                }
                return false;
            }

            private static final List<String> ignoredAttrs = List.of("ref-id", "ticks", "scope", "start-level", "stop-level");

            private String extractEffectName(Node effectNode) {
                StringBuilder name = new StringBuilder(effectNode.getNodeName().replace("-", "_").toUpperCase());
                var attrs = effectNode.getAttributes();

                for (int i = 0; i < attrs.getLength(); i++) {
                    var attrNode = attrs.item(i);
                    if (ignoredAttrs.contains(attrNode.getNodeName())) {
                        continue;
                    }
                    name.append("_").append(attrNode.getNodeName().replace("-", "_").toUpperCase());
                    name.append("_").append(attrNode.getNodeValue());
                }

                for (var child = effectNode.getFirstChild(); nonNull(child); child = child.getNextSibling()) {
                    name.append("_").append(child.getNodeName().replace("-", "_").toUpperCase());
                    if (isLeveledNode(child)) {
                        name.append("_").append(parseDouble(child.getAttributes(), "initial"));
                    } else {
                        name.append("_").append(child.getTextContent().toUpperCase());
                    }
                }
                return name.toString();
            }

            String toEffectsXmlString(int id) throws TransformerException {
                StringWriter writer = new StringWriter();
                var node = effectsNode.get(id);
                if (isNull(node)) {
                    return "";
                }
                transformer.transform(new DOMSource(node), new StreamResult(writer));
                return writer.toString().replace(" xmlns=\"http://l2j.org\"", "");
            }

            String toConditionsXmlString(int id) throws TransformerException {
                StringWriter writer = new StringWriter();
                var node = conditionsNode.get(id);
                if (isNull(node)) {
                    return "";
                }
                transformer.transform(new DOMSource(node), new StreamResult(writer));
                return writer.toString().replace(" xmlns=\"http://l2j.org\"", "");
            }
        }

        public static class SkillModel {
            public boolean statUP;
            public int manaInitial;
            public int hpInitial;
            public int itemId;
            public int itemCount;
            boolean static_reuse;
            boolean ignore_shield;
            boolean over_hit;
            double defense_mod;
            double critical_chance;
            int statPercent;
            String statType;
            EnumSet<WeaponType> attackItemTypes = EnumSet.noneOf(WeaponType.class);
            String equipType;
            int id;
            int level;
            int operateType;
            String magicType;
            int isMagic;
            String icon;
            boolean debuff;
            int reuseDelay;
            String name;
            String desc;
            int manaConsume;
            int hpConsume;
            int dpConsume;
            int castRange;
            int coolTime;
            int effectPoint;
            int hitTime;

            public SkillModel(int id, int level, String name, String desc) {
                this.id = id;
                this.level = level;
                this.name = name;
                this.desc = desc;
            }

            public String getDesc() {
                return desc;
            }
        }

        interface EffectNameExtractor {
            String extract(Node effectNode);

            String handledEffect();
        }

        public static String toPrettyXmlString(int indent, Document document) {
            try {
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList nodeList = (NodeList) xPath.evaluate(
                        "//text()[normalize-space()='']",
                        document,
                        XPathConstants.NODESET
                );

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    node.getParentNode().removeChild(node);
                }

                StringWriter stringWriter = new StringWriter();
                StreamResult xmlOutput = new StreamResult(stringWriter);
                var factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer();
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(new DOMSource(document), xmlOutput);
                return stringWriter.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static Document convertStringToDocument(String xml) {
            try {
                return DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
            } catch (SAXException | IOException | ParserConfigurationException e) {
                e.printStackTrace();
            }
            return null;
        }
    }