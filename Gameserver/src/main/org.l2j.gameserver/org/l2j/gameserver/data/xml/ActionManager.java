package org.l2j.gameserver.data.xml;

import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.falseIfNullOrElse;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class ActionManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);

    private final IntMap<ActionData> actions = new HashIntMap<>();
    private final IntIntMap actionSkills = new HashIntIntMap(); // skillId, actionId

    private ActionManager() {
    }


    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/actions.xsd");
    }

    @Override
    public void load() {
        actions.clear();
        actionSkills.clear();
        parseDatapackFile("data/actions.xml");
        LOGGER.info("Loaded {} player actions.", actions.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "action", actionNode -> {
            var attrs = actionNode.getAttributes();
            var id = parseInteger(attrs, "id");
            var optionId = parseInteger(attrs, "option");

            final ActionData action = new ActionData(id, parseString(attrs, "handler"), optionId, parseBoolean(attrs, "auto-use"));
            actions.put(id, action);

            if(isActionSkill(action)) {
                actionSkills.put(optionId, id);
            }
        }));
    }

    private boolean isActionSkill(ActionData h) {
        return h.getHandler().equals("PetSkillUse") || h.getHandler().equals("ServitorSkillUse");
    }

    /**
     * @param id the action identifier
     * @return the ActionData for specified id
     */
    public ActionData getActionData(int id) {
        return actions.get(id);
    }

    /**
     * @param skillId the skill identifier
     * @return the actionId corresponding to the skillId or -1 if no actionId is found for the specified skill.
     */
    public int getSkillActionId(int skillId) {
        return actionSkills.getOrDefault(skillId, -1);
    }

    public boolean isAutoUseAction(int actionId) {
        return falseIfNullOrElse(actions.get(actionId), ActionData::isAutoUse);
    }

    public static void init() {
        getInstance().load();
    }

    public static ActionManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ActionManager INSTANCE = new ActionManager();
    }
}
