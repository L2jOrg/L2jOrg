package org.l2j.gameserver.data.xml.impl;

import org.l2j.commons.util.IXmlReader;
import org.l2j.gameserver.handler.EffectHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.options.Options;
import org.l2j.gameserver.model.options.OptionsSkillHolder;
import org.l2j.gameserver.model.options.OptionsSkillType;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class OptionData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionData.class.getName());

    private final Map<Integer, Options> _optionData = new HashMap<>();

    private OptionData() {
        load();
    }

    @Override
    public synchronized void load() {
        _optionData.clear();
        parseDatapackDirectory("data/stats/augmentation/options", false);
        LOGGER.info("Loaded: {} Options.", _optionData.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "option", optionNode ->
        {
            final int id = parseInteger(optionNode.getAttributes(), "id");
            final Options option = new Options(id);

            forEach(optionNode, IXmlReader::isNode, innerNode ->
            {
                switch (innerNode.getNodeName()) {
                    case "effects": {
                        forEach(innerNode, "effect", effectNode ->
                        {
                            final String name = parseString(effectNode.getAttributes(), "name");
                            final StatsSet params = new StatsSet();
                            forEach(effectNode, IXmlReader::isNode, paramNode ->
                            {
                                params.set(paramNode.getNodeName(), SkillData.getInstance().parseValue(paramNode, true, false, Collections.emptyMap()));
                            });
                            option.addEffect(EffectHandler.getInstance().getHandlerFactory(name).apply(params));
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
