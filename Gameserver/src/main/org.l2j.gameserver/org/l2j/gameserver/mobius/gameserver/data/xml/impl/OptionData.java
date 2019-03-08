package org.l2j.gameserver.mobius.gameserver.data.xml.impl;

import org.l2j.commons.util.IXmlReader;
import org.l2j.gameserver.mobius.gameserver.handler.EffectHandler;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.options.Options;
import org.l2j.gameserver.mobius.gameserver.model.options.OptionsSkillHolder;
import org.l2j.gameserver.mobius.gameserver.model.options.OptionsSkillType;
import org.l2j.gameserver.mobius.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public class OptionData implements IGameXmlReader
{
    private static final Logger LOGGER = Logger.getLogger(OptionData.class.getName());

    private final Map<Integer, Options> _optionData = new HashMap<>();

    protected OptionData()
    {
        load();
    }

    @Override
    public synchronized void load()
    {
        _optionData.clear();
        parseDatapackDirectory("data/stats/augmentation/options", false);
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _optionData.size() + " Options.");
    }

    @Override
    public void parseDocument(Document doc, File f)
    {
        forEach(doc, "list", listNode -> forEach(listNode, "option", optionNode ->
        {
            final int id = parseInteger(optionNode.getAttributes(), "id");
            final Options option = new Options(id);

            forEach(optionNode, IXmlReader::isNode, innerNode ->
            {
                switch (innerNode.getNodeName())
                {
                    case "effects":
                    {
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
                    case "active_skill":
                    {
                        option.addActiveSkill(new SkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level")));
                        break;
                    }
                    case "passive_skill":
                    {
                        option.addPassiveSkill(new SkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level")));
                        break;
                    }
                    case "attack_skill":
                    {
                        option.addActivationSkill(new OptionsSkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level"), parseDouble(innerNode.getAttributes(), "chance"), OptionsSkillType.ATTACK));
                        break;
                    }
                    case "magic_skill":
                    {
                        option.addActivationSkill(new OptionsSkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level"), parseDouble(innerNode.getAttributes(), "chance"), OptionsSkillType.MAGIC));
                        break;
                    }
                    case "critical_skill":
                    {
                        option.addActivationSkill(new OptionsSkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level"), parseDouble(innerNode.getAttributes(), "chance"), OptionsSkillType.CRITICAL));
                        break;
                    }
                }
            });
            _optionData.put(option.getId(), option);
        }));
    }

    public Options getOptions(int id)
    {
        return _optionData.get(id);
    }

    /**
     * Gets the single instance of OptionsData.
     * @return single instance of OptionsData
     */
    public static OptionData getInstance()
    {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder
    {
        protected static final OptionData _instance = new OptionData();
    }
}
