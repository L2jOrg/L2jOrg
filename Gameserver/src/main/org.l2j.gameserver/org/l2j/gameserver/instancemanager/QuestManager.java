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
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import org.l2j.gameserver.model.quest.Quest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Quests and scripts manager.
 *
 * @author Zoey76
 */
public final class QuestManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestManager.class);

    /**
     * Map containing all the quests.
     */
    private final Map<String, Quest> _quests = new ConcurrentHashMap<>();
    /**
     * Map containing all the scripts.
     */
    private final Map<String, Quest> _scripts = new ConcurrentHashMap<>();

    private QuestManager() {
    }

    public boolean reload(String questFolder) {
        final Quest q = getQuest(questFolder);
        if (q == null) {
            return false;
        }
        return q.reload();
    }

    /**
     * Reloads a the quest by ID.
     *
     * @param questId the ID of the quest to be reloaded
     * @return {@code true} if reload was successful, {@code false} otherwise
     */
    public boolean reload(int questId) {
        final Quest q = getQuest(questId);
        if (q == null) {
            return false;
        }
        return q.reload();
    }

    /**
     * Unload all quests and scripts and reload them.
     */
    public void reloadAllScripts() {
        unloadAllScripts();

        LOGGER.info("Reloading all server scripts.");
        try {
            ScriptEngineManager.getInstance().executeScriptLoader();
        } catch (Exception e) {
            LOGGER.error("Failed executing script list!", e);
        }

        getInstance().report();
    }

    /**
     * Unload all quests and scripts.
     */
    public void unloadAllScripts() {
        LOGGER.info("Unloading all server scripts.");

        // Unload quests.
        for (Quest quest : _quests.values()) {
            if (quest != null) {
                quest.unload(false);
            }
        }
        _quests.clear();
        // Unload scripts.
        for (Quest script : _scripts.values()) {
            if (script != null) {
                script.unload(false);
            }
        }
        _scripts.clear();
    }

    /**
     * Logs how many quests and scripts are loaded.
     */
    public void report() {
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _quests.size() + " quests.");
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _scripts.size() + " scripts.");
    }

    /**
     * Calls {@link Quest#onSave()} in all quests and scripts.
     */
    public void save() {
        // Save quests.
        for (Quest quest : _quests.values()) {
            quest.onSave();
        }

        // Save scripts.
        for (Quest script : _scripts.values()) {
            script.onSave();
        }
    }

    /**
     * Gets a quest by name.<br>
     * <i>For backwards compatibility, verifies scripts with the given name if the quest is not found.</i>
     *
     * @param name the quest name
     * @return the quest
     */
    public Quest getQuest(String name) {
        if (_quests.containsKey(name)) {
            return _quests.get(name);
        }
        return _scripts.get(name);
    }

    /**
     * Gets a quest by ID.
     *
     * @param questId the ID of the quest to get
     * @return if found, the quest, {@code null} otherwise
     */
    public Quest getQuest(int questId) {
        for (Quest q : _quests.values()) {
            if (q.getId() == questId) {
                return q;
            }
        }
        return null;
    }

    /**
     * Adds a new quest.
     *
     * @param quest the quest to be added
     */
    public void addQuest(Quest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("Quest argument cannot be null");
        }

        // FIXME: unloading the old quest at this point is a tad too late.
        // the new quest has already initialized itself and read the data, starting
        // an unpredictable number of tasks with that data. The old quest will now
        // save data which will never be read.
        // However, requesting the newQuest to re-read the data is not necessarily a
        // good option, since the newQuest may have already started timers, spawned NPCs
        // or taken any other action which it might re-take by re-reading the data.
        // the current solution properly closes the running tasks of the old quest but
        // ignores the data; perhaps the least of all evils...
        final Quest old = _quests.put(quest.getName(), quest);
        if (old != null) {
            old.unload();
            LOGGER.info("Replaced quest " + old.getName() + " (" + old.getId() + ") with a new version!");
        }

        if (Config.ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS) {
            final String questName = quest.getName().contains("_") ? quest.getName().substring(quest.getName().indexOf('_') + 1) : quest.getName();
            LOGGER.info("Loaded quest " + CommonUtil.splitWords(questName) + ".");
        }
    }

    /**
     * Removes a script.
     *
     * @param script the script to remove
     * @return {@code true} if the script was removed, {@code false} otherwise
     */
    public boolean removeScript(Quest script) {
        if (_quests.containsKey(script.getName())) {
            _quests.remove(script.getName());
            return true;
        } else if (_scripts.containsKey(script.getName())) {
            _scripts.remove(script.getName());
            return true;
        }
        return false;
    }

    public Map<String, Quest> getQuests() {
        return _quests;
    }

    public boolean unload(Quest ms) {
        ms.onSave();
        return removeScript(ms);
    }

    /**
     * Gets all the registered scripts.
     *
     * @return all the scripts
     */
    public Map<String, Quest> getScripts() {
        return _scripts;
    }

    /**
     * Adds a script.
     *
     * @param script the script to be added
     */
    public void addScript(Quest script) {
        final Quest old = _scripts.put(script.getClass().getSimpleName(), script);
        if (old != null) {
            old.unload();
            LOGGER.info("Replaced script " + old.getName() + " with a new version!");
        }

        if (Config.ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS) {
            LOGGER.info("Loaded script " + CommonUtil.splitWords(script.getClass().getSimpleName()) + ".");
        }
    }

    public static QuestManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final QuestManager INSTANCE = new QuestManager();
    }
}
