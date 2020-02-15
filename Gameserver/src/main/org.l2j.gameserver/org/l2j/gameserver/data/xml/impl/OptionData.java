package org.l2j.gameserver.data.xml.impl;

import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.handler.EffectHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.options.Options;
import org.l2j.gameserver.model.options.OptionsSkillHolder;
import org.l2j.gameserver.model.options.OptionsSkillType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.util.exp4j.ExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OptionData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionData.class.getName());

    private final Map<Integer, Options> _optionData = new HashMap<>();

    private OptionData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/optionsData.xsd");
    }

    @Override
    public synchronized void load() {
        _optionData.clear();
        parseDatapackDirectory("data/stats/augmentation/options", false);
        LOGGER.info("Loaded {} Options.", _optionData.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "option", optionNode -> {
            final int id = parseInteger(optionNode.getAttributes(), "id");
            final Options option = new Options(id);

            forEach(optionNode, XmlReader::isNode, innerNode -> {
                switch (innerNode.getNodeName()) {
                    case "effects": {
                        forEach(innerNode, "effect", effectNode ->
                        {
                            final String name = parseString(effectNode.getAttributes(), "name");
                            final StatsSet params = new StatsSet();
                            forEach(effectNode, XmlReader::isNode, paramNode ->
                            {
                                params.set(paramNode.getNodeName(), parseValue(paramNode, true, false, Collections.emptyMap()));
                            });
                            var factory = EffectHandler.getInstance().getHandlerFactory(name);
                            if(nonNull(factory)) {
                                option.addEffect(factory.apply(params));
                            } else {
                                LOGGER.error("Could not parse effect factory {} on option {}", name, id);
                            }
                        });
                        break;
                    }
                    case "active_skill": {
                        option.addActiveSkill(new SkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level")));
                        break;
                    }
                    case "passive_skill": {
                        option.addPassiveSkill(new SkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level")));
                        break;
                    }
                    case "attack_skill": {
                        option.addActivationSkill(new OptionsSkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level"), parseDouble(innerNode.getAttributes(), "chance"), OptionsSkillType.ATTACK));
                        break;
                    }
                    case "magic_skill": {
                        option.addActivationSkill(new OptionsSkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level"), parseDouble(innerNode.getAttributes(), "chance"), OptionsSkillType.MAGIC));
                        break;
                    }
                    case "critical_skill": {
                        option.addActivationSkill(new OptionsSkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level"), parseDouble(innerNode.getAttributes(), "chance"), OptionsSkillType.CRITICAL));
                        break;
                    }
                }
            });
            _optionData.put(option.getId(), option);
        }));
    }

    Object parseValue(Node node, boolean blockValue, boolean parseAttributes, Map<String, Double> variables) {
        StatsSet statsSet = null;
        List<Object> list = null;
        Object text = null;
        if (parseAttributes && (!node.getNodeName().equals("value") || !blockValue) && (node.getAttributes().getLength() > 0)) {
            statsSet = new StatsSet();
            parseAttributes(node.getAttributes(), "", statsSet, variables);
        }
        for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
            final String nodeName = node.getNodeName();
            switch (node.getNodeName()) {
                case "#text": {
                    final String value = node.getNodeValue().trim();
                    if (!value.isEmpty()) {
                        text = parseNodeValue(value, variables);
                    }
                    break;
                }
                case "item": {
                    if (list == null) {
                        list = new LinkedList<>();
                    }

                    final Object value = parseValue(node, false, true, variables);
                    if (value != null) {
                        list.add(value);
                    }
                    break;
                }
                case "value": {
                    if (blockValue) {
                        break;
                    }
                }
                default: {
                    final Object value = parseValue(node, false, true, variables);
                    if (value != null) {
                        if (statsSet == null) {
                            statsSet = new StatsSet();
                        }

                        statsSet.set(nodeName, value);
                    }
                }
            }
        }
        if (list != null) {
            if (text != null) {
                throw new IllegalArgumentException("Text and list in same node are not allowed. Node[" + node + "]");
            }
            if (statsSet != null) {
                statsSet.set(".", list);
            } else {
                return list;
            }
        }
        if (text != null) {
            if (list != null) {
                throw new IllegalArgumentException("Text and list in same node are not allowed. Node[" + node + "]");
            }
            if (statsSet != null) {
                statsSet.set(".", text);
            } else {
                return text;
            }
        }
        return statsSet;
    }

    private void parseAttributes(NamedNodeMap attributes, String prefix, StatsSet statsSet, Map<String, Double> variables) {
        for (int i = 0; i < attributes.getLength(); i++) {
            final Node attributeNode = attributes.item(i);
            statsSet.set(prefix + "." + attributeNode.getNodeName(), parseNodeValue(attributeNode.getNodeValue(), variables));
        }
    }

    private Object parseNodeValue(String value, Map<String, Double> variables) {
        if (value.startsWith("{") && value.endsWith("}")) {
            return new ExpressionBuilder(value).variables(variables.keySet()).build().setVariables(variables).evaluate();
        }
        return value;
    }

    public Options getOptions(int id) {
        return _optionData.get(id);
    }

    public static OptionData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final OptionData INSTANCE = new OptionData();
    }
}
