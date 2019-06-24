package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.ActionDataHolder;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 */
public class ActionData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionData.class);

    private final Map<Integer, ActionDataHolder> _actionData = new HashMap<>();
    private final Map<Integer, Integer> _actionSkillsData = new HashMap<>(); // skillId, actionId

    private ActionData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/ActionData.xsd");
    }

    @Override
    public void load() {
        _actionData.clear();
        _actionSkillsData.clear();
        parseDatapackFile("data/ActionData.xml");
        _actionData.values().stream().filter(h -> h.getHandler().equals("PetSkillUse") || h.getHandler().equals("ServitorSkillUse")).forEach(h -> _actionSkillsData.put(h.getOptionId(), h.getId()));
        LOGGER.info("Loaded {} player actions.", _actionData.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "action", actionNode ->
        {
            final ActionDataHolder holder = new ActionDataHolder(new StatsSet(parseAttributes(actionNode)));
            _actionData.put(holder.getId(), holder);
        }));
    }

    /**
     * @param id
     * @return the ActionDataHolder for specified id
     */
    public ActionDataHolder getActionData(int id) {
        return _actionData.get(id);
    }

    /**
     * @param skillId
     * @return the actionId corresponding to the skillId or -1 if no actionId is found for the specified skill.
     */
    public int getSkillActionId(int skillId) {
        return _actionSkillsData.getOrDefault(skillId, -1);
    }


    public static ActionData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ActionData INSTANCE = new ActionData();
    }
}
