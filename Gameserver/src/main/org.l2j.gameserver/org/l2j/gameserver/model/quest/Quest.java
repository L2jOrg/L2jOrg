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
package org.l2j.gameserver.model.quest;

import io.github.joealisson.primitive.IntCollection;
import org.l2j.commons.util.CommonUtil;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.dao.QuestDAO;
import org.l2j.gameserver.data.database.data.QuestData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExQuestNpcLogList;
import org.l2j.gameserver.network.serverpackets.QuestList;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.html.NpcQuestHtmlMessage;
import org.l2j.gameserver.settings.PartySettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * Quest main class.
 *
 * @author Luis Arias
 * @author JoeAlisson
 */
public class Quest extends AbstractScript implements IIdentifiable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Quest.class);

    private static final String DEFAULT_NO_QUEST_MSG = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
    private static final int RESET_HOUR = 6;
    private static final int RESET_MINUTES = 30;
    private static final byte INITIAL_STATE = State.CREATED;

    private Set<QuestCondition> startConditions = null;
    private NpcStringId questNameNpcStringId;

    private final int questId;

    private boolean isCustom = false;
    private int[] questItemIds = null;

    public Quest(int questId) {
        this.questId = questId;
        if (questId > 0) {
            QuestManager.getInstance().addQuest(this);
        } else {
            QuestManager.getInstance().addScript(this);
        }

        onLoad();
    }

    /**
     * @return the reset hour for a daily quest, could be overridden on a script.
     */
    public int getResetHour() {
        return RESET_HOUR;
    }

    /**
     * @return the reset minutes for a daily quest, could be overridden on a script.
     */
    public int getResetMinutes() {
        return RESET_MINUTES;
    }

    /**
     * This method is, by default, called by the constructor of all scripts.<br>
     * Children of this class can implement this function in order to define what variables to load and what structures to save them in.<br>
     * By default, nothing is loaded.
     */
    protected void onLoad() {
    }

    /**
     * The function onSave is, by default, called at shutdown, for all quests, by the QuestManager.<br>
     * Children of this class can implement this function in order to convert their structures<br>
     * into <var, value> tuples and make calls to save them to the database, if needed.<br>
     * By default, nothing is saved.
     */
    public void onSave() {
    }

    /**
     * Gets the quest ID.
     *
     * @return the quest ID
     */
    @Override
    public int getId() {
        return questId;
    }

    /**
     * @return the NpcStringId of the current quest, used in Quest link bypass
     */
    public int getNpcStringId() {
        return questNameNpcStringId != null ? questNameNpcStringId.getId() / 100 : (questId > 10000 ? questId - 5000 : questId);
    }

    public void setQuestNameNpcStringId(NpcStringId npcStringId) {
        questNameNpcStringId = npcStringId;
    }

    /**
     * Add a new quest state of this quest to the database.
     *
     * @param player the owner of the newly created quest state
     * @return the newly created {@link QuestState} object
     */
    public QuestState newQuestState(Player player) {
        return new QuestState(this, player, INITIAL_STATE);
    }

    /**
     * Get the specified player's {@link QuestState} object for this quest.<br>
     * If the player does not have it and initIfNode is {@code true},<br>
     * create a new QuestState object and return it, otherwise return {@code null}.
     *
     * @param player     the player whose QuestState to get
     * @param initIfNone if true and the player does not have a QuestState for this quest,<br>
     *                   create a new QuestState
     * @return the QuestState object for this quest or null if it doesn't exist
     */
    public QuestState getQuestState(Player player, boolean initIfNone) {
        final QuestState qs = player.getQuestState(getName());
        if (nonNull(qs) || !initIfNone) {
            return qs;
        }
        return canStartQuest(player) ? newQuestState(player) : null;
    }

    /**
     * @return the name of the quest
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * @return the path of the quest script
     */
    public String getPath() {
        final String path = getClass().getName().replace("org.l2j.scripts.", "").replace('.', '/');
        return path.substring(0, path.lastIndexOf('/' + getClass().getSimpleName()));
    }

    /**
     * Add a timer to the quest (if it doesn't exist already) and start it.
     *
     * @param name   the name of the timer (also passed back as "event" in {@link #onAdvEvent(String, Npc, Player)})
     * @param time   time in ms for when to fire the timer
     * @param npc    the npc associated with this timer (can be null)
     * @param player the player associated with this timer (can be null)
     * @see #startQuestTimer(String, long, Npc, Player, boolean)
     */
    public void startQuestTimer(String name, long time, Npc npc, Player player) {
        startQuestTimer(name, time, npc, player, false);
    }

    /**
     * Add a timer to the quest (if it doesn't exist already) and start it.
     *
     * @param name      the name of the timer (also passed back as "event" in {@link #onAdvEvent(String, Npc, Player)})
     * @param time      time in ms for when to fire the timer
     * @param npc       the npc associated with this timer (can be null)
     * @param player    the player associated with this timer (can be null)
     * @param repeating indicates whether the timer is repeatable or one-time.<br>
     *                  If {@code true}, the task is repeated every {@code time} milliseconds until explicitly stopped.
     */
    public void startQuestTimer(String name, long time, Npc npc, Player player, boolean repeating) {
        var manager = QuestTimerManager.getInstance();
        if (isNull(manager.getQuestTimer(this, name, player, npc))) {
            manager.schedule(this, name, player, npc, time, repeating);
        }
    }

    protected boolean hasQuestTimer(String name, Player player, Npc npc) {
        return nonNull(QuestTimerManager.getInstance().getQuestTimer(this, name, player, npc));
    }

    protected void cancelQuestTimers() {
        QuestTimerManager.getInstance().cancelQuestTimers(this);
    }

    /**
     * Cancel all quest timers with the specified name.
     *
     * @param name the name of the quest timers to cancel
     */
    protected void cancelQuestTimers(String name) {
        QuestTimerManager.getInstance().cancelQuestTimers(this, name);
    }

    /**
     * Cancel the quest timer that matches the specified name and parameters.
     *
     * @param name   the name of the quest timer to cancel
     * @param npc    the NPC associated with the quest timer to cancel
     * @param player the player associated with the quest timer to cancel
     */
    protected void cancelQuestTimer(String name, Npc npc, Player player) {
        doIfNonNull(QuestTimerManager.getInstance().getQuestTimer(this, name, player, npc), QuestTimer::cancelAndRemove);
    }

    /**
     * @param npc      the NPC that was attacked
     * @param attacker the attacking player
     * @param damage   the damage dealt to the NPC by the player
     * @param isSummon if {@code true}, the attack was actually made by the player's summon
     * @param skill    the skill used to attack the NPC (can be null)
     */
    public final void notifyAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill) {
        String res;
        try {
            res = onAttack(npc, attacker, damage, isSummon, skill);
        } catch (Exception e) {
            showError(attacker, e);
            return;
        }
        showResult(attacker, res);
    }

    /**
     * @param killer the character that killed the {@code victim}
     * @param victim the character that was killed by the {@code killer}
     * @param qs     the quest state object of the player to be notified of this event
     */
    public final void notifyDeath(Creature killer, Creature victim, QuestState qs) {
        String res;
        try {
            res = onDeath(killer, victim, qs);
        } catch (Exception e) {
            showError(qs.getPlayer(), e);
            return;
        }
        showResult(qs.getPlayer(), res);
    }

    public final void notifySpellFinished(Npc instance, Player player, Skill skill) {
        String res;
        try {
            res = onSpellFinished(instance, player, skill);
        } catch (Exception e) {
            showError(player, e);
            return;
        }
        showResult(player, res);
    }

    /**
     * @param npc the spawned NPC
     */
    public final void notifySpawn(Npc npc) {
        try {
            onSpawn(npc);
        } catch (Exception e) {
            LOGGER.warn("Exception on onSpawn() in notifySpawn(): {}", e.getMessage(), e);
        }
    }

    public final void notifyEvent(String event, Npc npc, Player player) {
        String res;
        try {
            res = onAdvEvent(event, npc, player);
        } catch (Exception e) {
            showError(player, e);
            return;
        }
        showResult(player, res, npc);
    }

    public final void notifyKill(Npc npc, Player killer, boolean isSummon) {
        notifyKill(npc, killer, isSummon, null);
    }

    public final void notifyKill(Npc npc, Player killer, boolean isSummon, Object payload) {
        String res;
        try {
            if(payload == null)
                res = onKill(npc, killer, isSummon);
            else
                res = onKill(npc, killer, isSummon, payload);

        } catch (Exception e) {
            showError(killer, e);
            return;
        }
        showResult(killer, res);
    }

    public final void notifyTalk(Npc npc, Player player) {
        String res;
        try {
            //@formatter:off
            final Set<Quest> startingQuests = npc.getListeners(EventType.ON_NPC_QUEST_START).stream()
                    .map(AbstractEventListener::getOwner)
                    .filter(Quest.class::isInstance)
                    .map(Quest.class::cast)
                    .collect(Collectors.toSet());
            //@formatter:on

            final String startConditionHtml = getStartConditionHtml(player, npc);
            if (startingQuests.contains(this) && (startConditionHtml != null)) {
                res = startConditionHtml;
            } else {
                res = onTalk(npc, player);
            }
        } catch (Exception e) {
            showError(player, e);
            return;
        }
        player.setLastQuestNpcObject(npc.getObjectId());
        showResult(player, res, npc);
    }

    /**
     * Override the default NPC dialogs when a quest defines this for the given NPC.<br>
     * Note: If the default html for this npc needs to be shown, onFirstTalk should call npc.showChatWindow(player) and then return null.
     *
     * @param npc    the NPC whose dialogs to override
     * @param player the player talking to the NPC
     */
    public final void notifyFirstTalk(Npc npc, Player player) {
        try {
            String res = onFirstTalk(npc, player);
            showResult(player, res, npc);
        } catch (Exception e) {
            showError(player, e);
        }
    }

    public final void notifyItemTalk(Item item, Player player) {
        String res;
        try {
            res = onItemTalk(item, player);
        } catch (Exception e) {
            showError(player, e);
            return;
        }
        showResult(player, res);
    }

    public String onItemTalk(Item item, Player player) {
        return null;
    }

    public final void notifySkillSee(Npc npc, Player caster, Skill skill, WorldObject[] targets, boolean isSummon) {
        String res;
        try {
            res = onSkillSee(npc, caster, skill, targets, isSummon);
        } catch (Exception e) {
            showError(caster, e);
            return;
        }
        showResult(caster, res);
    }

    public final void notifyFactionCall(Npc npc, Npc caller, Player attacker, boolean isSummon) {
        String res;
        try {
            res = onFactionCall(npc, caller, attacker, isSummon);
        } catch (Exception e) {
            showError(attacker, e);
            return;
        }
        showResult(attacker, res);
    }

    public final void notifyAggroRangeEnter(Npc npc, Player player, boolean isSummon) {
        String res;
        try {
            res = onAggroRangeEnter(npc, player, isSummon);
        } catch (Exception e) {
            showError(player, e);
            return;
        }
        showResult(player, res);
    }

    /**
     * @param npc      the NPC that sees the creature
     * @param creature the creature seen by the NPC
     * @param isSummon
     */
    public final void notifySeeCreature(Npc npc, Creature creature, boolean isSummon) {
        Player player = null;
        if (isSummon || isPlayer(creature)) {
            player = creature.getActingPlayer();
        }
        String res;
        try {
            res = onSeeCreature(npc, creature, isSummon);
        } catch (Exception e) {
            if (player != null) {
                showError(player, e);
            }
            return;
        }
        if (player != null) {
            showResult(player, res);
        }
    }

    public final void notifyEnterZone(Creature character, Zone zone) {
        final Player player = character.getActingPlayer();
        String res;
        try {
            res = onEnterZone(character, zone);
        } catch (Exception e) {
            if (player != null) {
                showError(player, e);
            }
            return;
        }
        if (player != null) {
            showResult(player, res);
        }
    }

    public final void notifyExitZone(Creature character, Zone zone) {
        final Player player = character.getActingPlayer();
        String res;
        try {
            res = onExitZone(character, zone);
        } catch (Exception e) {
            if (player != null) {
                showError(player, e);
            }
            return;
        }
        if (player != null) {
            showResult(player, res);
        }
    }

    public final void notifyMoveFinished(Npc npc) {
        try {
            onMoveFinished(npc);
        } catch (Exception e) {
            LOGGER.warn("Exception on onMoveFinished() in notifyMoveFinished(): " + e.getMessage(), e);
        }
    }

    /**
     * This function is called in place of {@link #onAttack(Npc, Player, int, boolean, Skill)} if the former is not implemented.<br>
     * If a script contains both onAttack(..) implementations, then this method will never be called unless the script's {@link #onAttack(Npc, Player, int, boolean, Skill)} explicitly calls this method.
     *
     * @param npc      this parameter contains a reference to the exact instance of the NPC that got attacked the NPC.
     * @param attacker this parameter contains a reference to the exact instance of the player who attacked.
     * @param damage   this parameter represents the total damage that this attack has inflicted to the NPC.
     * @param isSummon this parameter if it's {@code false} it denotes that the attacker was indeed the player, else it specifies that the damage was actually dealt by the player's pet.
     * @return
     */
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon) {
        return null;
    }

    /**
     * This function is called whenever a player attacks an NPC that is registered for the quest.<br>
     * If is not overridden by a subclass, then default to the returned value of the simpler (and older) {@link #onAttack(Npc, Player, int, boolean)} override.<br>
     *
     * @param npc      this parameter contains a reference to the exact instance of the NPC that got attacked.
     * @param attacker this parameter contains a reference to the exact instance of the player who attacked the NPC.
     * @param damage   this parameter represents the total damage that this attack has inflicted to the NPC.
     * @param isSummon this parameter if it's {@code false} it denotes that the attacker was indeed the player, else it specifies that the damage was actually dealt by the player's summon
     * @param skill    parameter is the skill that player used to attack NPC.
     * @return
     */
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill) {
        return onAttack(npc, attacker, damage, isSummon);
    }

    /**
     * This function is called whenever an <b>exact instance</b> of a character who was previously registered for this event dies.<br>
     * The registration for {@link #onDeath(Creature, Creature, QuestState)} events <b>is not</b> done via the quest itself, but it is instead handled by the QuestState of a particular player.
     *
     * @param killer this parameter contains a reference to the exact instance of the NPC that <b>killed</b> the character.
     * @param victim this parameter contains a reference to the exact instance of the character that got killed.
     * @param qs     this parameter contains a reference to the QuestState of whomever was interested (waiting) for this kill.
     * @return
     */
    public String onDeath(Creature killer, Creature victim, QuestState qs) {
        return onAdvEvent("", (killer instanceof Npc) ? (Npc) killer : null, qs.getPlayer());
    }

    /**
     * This function is called whenever a player clicks on a link in a quest dialog and whenever a timer fires.<br>
     * If is not overridden by a subclass, then default to the returned value of the simpler (and older) {@link #onEvent(String, QuestState)} override.<br>
     * If the player has a quest state, use it as parameter in the next call, otherwise return null.
     *
     * @param event  this parameter contains a string identifier for the event.<br>
     *               Generally, this string is passed directly via the link.<br>
     *               For example:<br>
     *               <code>
     *               &lt;a action="bypass -h Quest 626_ADarkTwilight 31517-01.htm"&gt;hello&lt;/a&gt;
     *               </code><br>
     *               The above link sets the event variable to "31517-01.htm" for the quest 626_ADarkTwilight.<br>
     *               In the case of timers, this will be the name of the timer.<br>
     *               This parameter serves as a sort of identifier.
     * @param npc    this parameter contains a reference to the instance of NPC associated with this event.<br>
     *               This may be the NPC registered in a timer, or the NPC with whom a player is speaking, etc.<br>
     *               This parameter may be {@code null} in certain circumstances.
     * @param player this parameter contains a reference to the player participating in this function.<br>
     *               It may be the player speaking to the NPC, or the player who caused a timer to start (and owns that timer).<br>
     *               This parameter may be {@code null} in certain circumstances.
     * @return the text returned by the event (may be {@code null}, a filename or just text)
     */
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (player != null) {
            final QuestState qs = player.getQuestState(getName());
            if (qs != null) {
                return onEvent(event, qs);
            }
        }
        return null;
    }

    /**
     * This function is called in place of {@link #onAdvEvent(String, Npc, Player)} if the former is not implemented.<br>
     * If a script contains both {@link #onAdvEvent(String, Npc, Player)} and this implementation, then this method will never be called unless the script's {@link #onAdvEvent(String, Npc, Player)} explicitly calls this method.
     *
     * @param event this parameter contains a string identifier for the event.<br>
     *              Generally, this string is passed directly via the link.<br>
     *              For example:<br>
     *              <code>
     *              &lt;a action="bypass -h Quest 626_ADarkTwilight 31517-01.htm"&gt;hello&lt;/a&gt;
     *              </code><br>
     *              The above link sets the event variable to "31517-01.htm" for the quest 626_ADarkTwilight.<br>
     *              In the case of timers, this will be the name of the timer.<br>
     *              This parameter serves as a sort of identifier.
     * @param qs    this parameter contains a reference to the quest state of the player who used the link or started the timer.
     * @return the text returned by the event (may be {@code null}, a filename or just text)
     */
    public String onEvent(String event, QuestState qs) {
        return null;
    }

    /**
     * This function is called whenever a player kills a NPC that is registered for the quest.
     *
     * @param npc      this parameter contains a reference to the exact instance of the NPC that got killed.
     * @param killer   this parameter contains a reference to the exact instance of the player who killed the NPC.
     * @param isSummon this parameter if it's {@code false} it denotes that the attacker was indeed the player, else it specifies that the killer was the player's pet.
     * @return the text returned by the event (may be {@code null}, a filename or just text)
     */
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        return onKill(npc, killer, isSummon, null);
    }

    /**
     * This function is called whenever a player kills a NPC that is registered for the quest.
     *
     * @param npc      this parameter contains a reference to the exact instance of the NPC that got killed.
     * @param killer   this parameter contains a reference to the exact instance of the player who killed the NPC.
     * @param isSummon this parameter if it's {@code false} it denotes that the attacker was indeed the player, else it specifies that the killer was the player's pet.
     * @param payload  this parameter contains a payload value for special cases, It may be {@code null} if no payload sent.
     * @return the text returned by the event (may be {@code null}, a filename or just text)
     */
    public String onKill(Npc npc, Player killer, boolean isSummon, Object payload) {
        if (!getNpcLogList(killer).isEmpty()) {
            sendNpcLogList(killer);
        }
        return null;
    }

    /**
     * This function is called whenever a Creature that is registered for the quest is killed.
     *
     * @param creature this parameter contains a reference to the exact instance of the Creature that got killed.
     * @param killer   this parameter contains a reference to the exact instance of the Creature who killed the Creature.
     */
    public void onCreatureKill(Creature creature, Creature killer) {}

    /**
     * This function is called whenever a player clicks to the "Quest" link of an NPC that is registered for the quest.
     *
     * @param npc    this parameter contains a reference to the exact instance of the NPC that the player is talking with.
     * @param talker this parameter contains a reference to the exact instance of the player who is talking to the NPC.
     * @return the text returned by the event (may be {@code null}, a filename or just text)
     */
    public String onTalk(Npc npc, Player talker) {
        return null;
    }

    /**
     * This function is called whenever a player talks to an NPC that is registered for the quest.<br>
     * That is, it is triggered from the very first click on the NPC, not via another dialog.<br>
     * <b>Note 1:</b><br>
     * Each NPC can be registered to at most one quest for triggering this function.<br>
     * In other words, the same one NPC cannot respond to an "onFirstTalk" request from two different quests.<br>
     * Attempting to register an NPC in two different quests for this function will result in one of the two registration being ignored.<br>
     * <b>Note 2:</b><br>
     * Since a Quest link isn't clicked in order to reach this, a quest state can be invalid within this function.<br>
     * The coder of the script may need to create a new quest state (if necessary).<br>
     * <b>Note 3:</b><br>
     * The returned value of onFirstTalk replaces the default HTML that would have otherwise been loaded from a sub-folder of DatapackRoot/game/data/html/.<br>
     * If you wish to show the default HTML, within onFirstTalk do npc.showChatWindow(player) and then return ""<br>
     *
     * @param npc    this parameter contains a reference to the exact instance of the NPC that the player is talking with.
     * @param player this parameter contains a reference to the exact instance of the player who is talking to the NPC.
     * @return the text returned by the event (may be {@code null}, a filename or just text)
     */
    public String onFirstTalk(Npc npc, Player player) {
        return null;
    }

    /**
     * This function is called whenever a player request a skill list.<br>
     * TODO: Re-implement, since Skill Trees rework it's support was removed.
     *
     * @param npc    this parameter contains a reference to the exact instance of the NPC that the player requested the skill list.
     * @param player this parameter contains a reference to the exact instance of the player who requested the skill list.
     * @return
     */
    public String onAcquireSkillList(Npc npc, Player player) {
        return null;
    }

    /**
     * This function is called whenever a player request a skill info.
     *
     * @param npc    this parameter contains a reference to the exact instance of the NPC that the player requested the skill info.
     * @param player this parameter contains a reference to the exact instance of the player who requested the skill info.
     * @param skill  this parameter contains a reference to the skill that the player requested its info.
     * @return
     */
    public String onAcquireSkillInfo(Npc npc, Player player, Skill skill) {
        return null;
    }

    /**
     * This function is called whenever a player casts a skill near a registered NPC (1000 distance).<br>
     * <b>Note:</b><br>
     * If a skill does damage, both onSkillSee(..) and onAttack(..) will be triggered for the damaged NPC!<br>
     * However, only onSkillSee(..) will be triggered if the skill does no damage,<br>
     * or if it damages an NPC who has no onAttack(..) registration while near another NPC who has an onSkillSee registration.<br>
     * TODO: confirm if the distance is 1000 and unhardcode.
     *
     * @param npc      the NPC that saw the skill
     * @param caster   the player who cast the skill
     * @param skill    the actual skill that was used
     * @param targets  an array of all objects (can be any type of object, including mobs and players) that were affected by the skill
     * @param isSummon if {@code true}, the skill was actually cast by the player's summon, not the player himself
     * @return
     */
    public String onSkillSee(Npc npc, Player caster, Skill skill, WorldObject[] targets, boolean isSummon) {
        return null;
    }

    /**
     * This function is called whenever an NPC finishes casting a skill.
     *
     * @param npc    the NPC that casted the skill.
     * @param player the player who is the target of the skill. Can be {@code null}.
     * @param skill  the actual skill that was used by the NPC.
     * @return
     */
    public String onSpellFinished(Npc npc, Player player, Skill skill) {
        return null;
    }

    /**
     * This function is called whenever an NPC spawns or re-spawns and passes a reference to the newly (re)spawned NPC.<br>
     * Currently the only function that has no reference to a player.<br>
     * It is useful for initializations, starting quest timers, displaying chat (NpcSay), and more.
     *
     * @param npc this parameter contains a reference to the exact instance of the NPC who just (re)spawned.
     * @return
     */
    public String onSpawn(Npc npc) {
        return null;
    }

    /**
     * This function is called whenever an NPC is called by another NPC in the same faction.
     *
     * @param npc      this parameter contains a reference to the exact instance of the NPC who is being asked for help.
     * @param caller   this parameter contains a reference to the exact instance of the NPC who is asking for help.<br>
     * @param attacker this parameter contains a reference to the exact instance of the player who attacked.
     * @param isSummon this parameter if it's {@code false} it denotes that the attacker was indeed the player, else it specifies that the attacker was the player's summon.
     * @return
     */
    public String onFactionCall(Npc npc, Npc caller, Player attacker, boolean isSummon) {
        return null;
    }

    /**
     * This function is called whenever a player enters an NPC aggression range.
     *
     * @param npc      this parameter contains a reference to the exact instance of the NPC whose aggression range is being transgressed.
     * @param player   this parameter contains a reference to the exact instance of the player who is entering the NPC's aggression range.
     * @param isSummon this parameter if it's {@code false} it denotes that the character that entered the aggression range was indeed the player, else it specifies that the character was the player's summon.
     * @return
     */
    public String onAggroRangeEnter(Npc npc, Player player, boolean isSummon) {
        return null;
    }

    /**
     * This function is called whenever a NPC "sees" a creature.
     *
     * @param npc      the NPC who sees the creature
     * @param creature the creature seen by the NPC
     * @param isSummon this parameter if it's {@code false} it denotes that the character seen by the NPC was indeed the player, else it specifies that the character was the player's summon
     * @return
     */
    public String onSeeCreature(Npc npc, Creature creature, boolean isSummon) {
        return null;
    }

    /**
     * This function is called whenever a character enters a registered zone.
     *
     * @param character this parameter contains a reference to the exact instance of the character who is entering the zone.
     * @param zone      this parameter contains a reference to the zone.
     * @return
     */
    public String onEnterZone(Creature character, Zone zone) {
        return null;
    }

    /**
     * This function is called whenever a character exits a registered zone.
     *
     * @param character this parameter contains a reference to the exact instance of the character who is exiting the zone.
     * @param zone      this parameter contains a reference to the zone.
     * @return
     */
    public String onExitZone(Creature character, Zone zone) {
        return null;
    }

    /**
     * @param eventName - name of event
     * @param sender    - NPC, who sent event
     * @param receiver  - NPC, who received event
     * @param reference - WorldObject to pass, if needed
     * @return
     */
    public String onEventReceived(String eventName, Npc sender, Npc receiver, WorldObject reference) {
        return null;
    }

    /**
     * This function is called whenever a NPC finishes moving
     *
     * @param npc registered NPC
     */
    public void onMoveFinished(Npc npc) {
    }

    public void onSummonSpawn(Summon summon) {

    }

    public void onSummonTalk(Summon summon) {
    }

    /**
     * This listener is called when player enter into instance.
     *
     * @param player   player who enter
     * @param instance instance where player enter
     */
    public void onInstanceEnter(Player player, Instance instance) {
    }

    /**
     * This listener is called when player leave instance.
     *
     * @param player   player who leaved
     * @param instance instance which player leaved
     */
    public void onInstanceLeave(Player player, Instance instance) {
    }


    /**
     * Show an error message to the specified player.
     *
     * @param player the player to whom to send the error (must be a GM)
     * @param t      the {@link Throwable} to get the message/stacktrace from
     * @return {@code false}
     */
    public boolean showError(Player player, Throwable t) {
        LOGGER.warn(getScriptName(), t);
        if ((player != null) && player.getAccessLevel().isGM()) {
            final String res = "<html><body><title>Script error</title>" + CommonUtil.getStackTrace(t) + "</body></html>";
            return showResult(player, res);
        }
        return false;
    }

    /**
     * @param player the player to whom to show the result
     * @param res    the message to show to the player
     * @return {@code false} if the message was sent, {@code true} otherwise
     * @see #showResult(Player, String, Npc)
     */
    public boolean showResult(Player player, String res) {
        return showResult(player, res, null);
    }

    /**
     * Show a message to the specified player.<br>
     * <u><i>Concept:</i></u><br>
     * Three cases are managed according to the value of the {@code res} parameter:<br>
     * <ul>
     * <li><u>{@code res} ends with ".htm" or ".html":</u> the contents of the specified HTML file are shown in a dialog window</li>
     * <li><u>{@code res} starts with "&lt;html&gt;":</u> the contents of the parameter are shown in a dialog window</li>
     * <li><u>all other cases :</u> the text contained in the parameter is shown in chat</li>
     * </ul>
     *
     * @param player the player to whom to show the result
     * @param npc    npc to show the result for
     * @param res    the message to show to the player
     * @return {@code false} if the message was sent, {@code true} otherwise
     */
    public boolean showResult(Player player, String res, Npc npc) {
        if (isNullOrEmpty(res) || isNull(player)) {
            return true;
        }

        if (res.endsWith(".htm") || res.endsWith(".html")) {
            showHtmlFile(player, res, npc);
        } else if (res.startsWith("<html>")) {
            final NpcHtmlMessage npcReply = new NpcHtmlMessage(npc != null ? npc.getObjectId() : 0, res);
            if (npc != null) {
                npcReply.replace("%objectId%", npc.getObjectId());
            }
            npcReply.replace("%playername%", player.getName());
            player.sendPacket(npcReply);
            player.sendPacket(ActionFailed.STATIC_PACKET);
        } else {
            player.sendMessage(res);
        }
        return false;
    }

    // TODO: Clean up these methods
    public void addStartNpc(int npcId) {
        setNpcQuestStartId(npcId);
    }

    public void addFirstTalkId(int npcId) {
        setNpcFirstTalkId(event -> notifyFirstTalk(event.getNpc(), event.getActiveChar()), npcId);
    }

    public void addTalkId(int npcId) {
        setNpcTalkId(npcId);
    }

    public void addKillId(int npcId) {
        setAttackableKillId(kill -> notifyKill(kill.getTarget(), kill.getAttacker(), kill.isSummon(), kill.getPayload()), npcId);
    }

    public void addDeathId(int npcId) {
        setCreatureKillId(kill -> onCreatureKill(kill.getTarget(), kill.getAttacker()), npcId);
    }

    public void addAttackId(int npcId) {
        setAttackableAttackId(attack -> notifyAttack(attack.getTarget(), attack.getAttacker(), attack.getDamage(), attack.isSummon(), attack.getSkill()), npcId);
    }

    /**
     * Add the quest to the NPC's startQuest
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addStartNpc(int... npcIds) {
        setNpcQuestStartId(npcIds);
    }

    /**
     * Add the quest to the NPC's startQuest
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addStartNpc(IntCollection npcIds) {
        setNpcQuestStartId(npcIds);
    }

    /**
     * Add the quest to the NPC's first-talk (default action dialog).
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addFirstTalkId(int... npcIds) {
        setNpcFirstTalkId(event -> notifyFirstTalk(event.getNpc(), event.getActiveChar()), npcIds);
    }

    /**
     * Add the quest to the NPC's first-talk (default action dialog).
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addFirstTalkId(IntCollection npcIds) {
        setNpcFirstTalkId(event -> notifyFirstTalk(event.getNpc(), event.getActiveChar()), npcIds);
    }

    /**
     * Add the Item to the notify when player speaks with it.
     *
     * @param itemIds the IDs of the Item to register
     */
    public void addItemTalkId(int... itemIds) {
        setItemTalkId(event -> notifyItemTalk(event.getItem(), event.getActiveChar()), itemIds);
    }

    /**
     * Add this quest to the list of quests that the passed mob will respond to for attack events.
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addAttackId(int... npcIds) {
        setAttackableAttackId(attack -> notifyAttack(attack.getTarget(), attack.getAttacker(), attack.getDamage(), attack.isSummon(), attack.getSkill()), npcIds);
    }

    /**
     * Add this quest to the list of quests that the passed mob will respond to for attack events.
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addAttackId(IntCollection npcIds) {
        setAttackableAttackId(attack -> notifyAttack(attack.getTarget(), attack.getAttacker(), attack.getDamage(), attack.isSummon(), attack.getSkill()), npcIds);
    }

    /**
     * Add this quest to the list of quests that the passed mob will respond to for kill events.
     *
     * @param npcIds
     */
    public void addKillId(int... npcIds) {
        setAttackableKillId(kill -> notifyKill(kill.getTarget(), kill.getAttacker(), kill.isSummon(), kill.getPayload()), npcIds);
    }

    /**
     * Add this quest event to the collection of NPC IDs that will respond to for on kill events.
     *
     * @param npcIds the collection of NPC IDs
     */
    public void addKillId(IntCollection npcIds) {
        setAttackableKillId(kill -> notifyKill(kill.getTarget(), kill.getAttacker(), kill.isSummon(), kill.getPayload()), npcIds);
    }

    /**
     * Add this quest to the list of quests that the passed npc will respond to for Talk Events.
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addTalkId(int... npcIds) {
        setNpcTalkId(npcIds);
    }

    public void addTalkId(IntCollection npcIds) {
        setNpcTalkId(npcIds);
    }

    /**
     * Add this quest to the list of quests that the passed npc will respond to for spawn events.
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addSpawnId(int... npcIds) {
        setNpcSpawnId(event -> notifySpawn(event.getNpc()), npcIds);
    }

    /**
     * Add this quest to the list of quests that the passed npc will respond to for spawn events.
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addSpawnId(IntCollection npcIds) {
        setNpcSpawnId(event -> notifySpawn(event.getNpc()), npcIds);
    }

    /**
     * Add this quest to the list of quests that the passed npc will respond to for skill see events.
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addSkillSeeId(int... npcIds) {
        setNpcSkillSeeId(event -> notifySkillSee(event.getTarget(), event.getCaster(), event.getSkill(), event.getTargets(), event.isSummon()), npcIds);
    }

    /**
     * Add this quest to the list of quests that the passed npc will respond to for skill see events.
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addSkillSeeId(IntCollection npcIds) {
        setNpcSkillSeeId(event -> notifySkillSee(event.getTarget(), event.getCaster(), event.getSkill(), event.getTargets(), event.isSummon()), npcIds);
    }

    /**
     * @param npcIds the IDs of the NPCs to register
     */
    public void addSpellFinishedId(int... npcIds) {
        setNpcSkillFinishedId(event -> notifySpellFinished(event.getCaster(), event.getTarget(), event.getSkill()), npcIds);
    }

    /**
     * Add this quest to the list of quests that the passed npc will respond to for faction call events.
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addFactionCallId(int... npcIds) {
        setAttackableFactionIdId(event -> notifyFactionCall(event.getNpc(), event.getCaller(), event.getAttacker(), event.isSummon()), npcIds);
    }

    /**
     * Add this quest to the list of quests that the passed npc will respond to for character see events.
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addAggroRangeEnterId(int... npcIds) {
        setAttackableAggroRangeEnterId(event -> notifyAggroRangeEnter(event.getNpc(), event.getActiveChar(), event.isSummon()), npcIds);
    }

    /**
     * @param npcIds the IDs of the NPCs to register
     */
    public void addSeeCreatureId(int... npcIds) {
        setNpcCreatureSeeId(event -> notifySeeCreature(event.getNpc(), event.getCreature(), event.isSummon()), npcIds);
    }

    /**
     * Register onEnterZone trigger for zone
     *
     * @param zoneId the ID of the zone to register
     */
    public void addEnterZoneId(int zoneId) {
        setCreatureZoneEnterId(event -> notifyEnterZone(event.getCreature(), event.getZone()), zoneId);
    }

    /**
     * Register onEnterZone trigger for zones
     *
     * @param zoneIds the IDs of the zones to register
     */
    public void addEnterZoneId(int... zoneIds) {
        setCreatureZoneEnterId(event -> notifyEnterZone(event.getCreature(), event.getZone()), zoneIds);
    }

    /**
     * Register onExitZone trigger for zones
     *
     * @param zoneIds the IDs of the zones to register
     */
    public void addExitZoneId(int... zoneIds) {
        setCreatureZoneExitId(event -> notifyExitZone(event.getCreature(), event.getZone()), zoneIds);
    }

    /**
     * Register onMoveFinished trigger for NPC
     *
     * @param npcIds the IDs of the NPCs to register
     */
    public void addMoveFinishedId(int... npcIds) {
        setNpcMoveFinishedId(event -> notifyMoveFinished(event.getNpc()), npcIds);
    }

    /**
     * Register onSummonSpawn trigger when summon is spawned.
     *
     * @param npcIds
     */
    public void addSummonSpawnId(int... npcIds) {
        setPlayerSummonSpawnId(event -> onSummonSpawn(event.getSummon()), npcIds);
    }

    /**
     * Register onSummonTalk trigger when master talked to summon.
     *
     * @param npcIds
     */
    public void addSummonTalkId(int... npcIds) {
        setPlayerSummonTalkId(event -> onSummonTalk(event.getSummon()), npcIds);
    }

    /**
     * Register onInstanceEnter trigger when player enter into instance.
     *
     * @param templateIds
     */
    public void addInstanceEnterId(int... templateIds) {
        setInstanceEnterId(event -> onInstanceEnter(event.getPlayer(), event.getInstanceWorld()), templateIds);
    }

    /**
     * Register onInstanceEnter trigger when player leave from instance.
     *
     * @param templateIds
     */
    public void addInstanceLeaveId(int... templateIds) {
        setInstanceLeaveId(event -> onInstanceLeave(event.getPlayer(), event.getInstanceWorld()), templateIds);
    }

    /**
     * Use this method to get a random party member from a player's party.<br>
     * Useful when distributing rewards after killing an NPC.
     *
     * @param player this parameter represents the player whom the party will taken.
     * @return {@code null} if {@code player} is {@code null}, {@code player} itself if the player does not have a party, and a random party member in all other cases
     */
    public Player getRandomPartyMember(Player player) {
        if (player == null) {
            return null;
        }
        final Party party = player.getParty();
        if ((party == null) || (party.getMembers().isEmpty())) {
            return player;
        }
        return party.getMembers().get(Rnd.get(party.getMembers().size()));
    }

    /**
     * Get a random party member with required cond value.
     *
     * @param player the instance of a player whose party is to be searched
     * @param cond   the value of the "cond" variable that must be matched
     * @return a random party member that matches the specified condition, or {@code null} if no match was found
     */
    public Player getRandomPartyMember(Player player, int cond) {
        return getRandomPartyMember(player, "cond", String.valueOf(cond));
    }

    /**
     * Auxiliary function for party quests.<br>
     * Note: This function is only here because of how commonly it may be used by quest developers.<br>
     * For any variations on this function, the quest script can always handle things on its own.
     *
     * @param player the instance of a player whose party is to be searched
     * @param var    the quest variable to look for in party members. If {@code null}, it simply unconditionally returns a random party member
     * @param value  the value of the specified quest variable the random party member must have
     * @return a random party member that matches the specified conditions or {@code null} if no match was found.<br>
     * If the {@code var} parameter is {@code null}, a random party member is selected without any conditions.<br>
     * The party member must be within a range of 1500 ingame units of the target of the reference player, or, if no target exists, within the same range of the player itself
     */
    public Player getRandomPartyMember(Player player, String var, String value) {
        // if no valid player instance is passed, there is nothing to check...
        if (player == null) {
            return null;
        }

        // for null var condition, return any random party member.
        if (var == null) {
            return getRandomPartyMember(player);
        }

        // normal cases...if the player is not in a party, check the player's state
        QuestState temp;
        final Party party = player.getParty();
        // if this player is not in a party, just check if this player instance matches the conditions itself
        if ((party == null) || (party.getMembers().isEmpty())) {
            temp = player.getQuestState(getName());
            if ((temp != null) && temp.isSet(var) && temp.get(var).equalsIgnoreCase(value)) {
                return player; // match
            }
            return null; // no match
        }

        // if the player is in a party, gather a list of all matching party members (possibly including this player)
        final List<Player> candidates = new ArrayList<>();
        // get the target for enforcing distance limitations.
        WorldObject target = player.getTarget();
        if (target == null) {
            target = player;
        }

        for (Player partyMember : party.getMembers()) {
            if (partyMember == null) {
                continue;
            }
            temp = partyMember.getQuestState(getName());
            if ((temp != null) && (temp.get(var) != null) && (temp.get(var)).equalsIgnoreCase(value) && isInsideRadius3D(partyMember, target, PartySettings.partyRange())) {
                candidates.add(partyMember);
            }
        }
        // if there was no match, return null...
        if (candidates.isEmpty()) {
            return null;
        }
        // if a match was found from the party, return one of them at random.
        return candidates.get(Rnd.get(candidates.size()));
    }

    /**
     * Get a random party member from the specified player's party.<br>
     * If the player is not in a party, only the player himself is checked.<br>
     * The lucky member is chosen by standard loot roll rules -<br>
     * each member rolls a random number, the one with the highest roll wins.
     *
     * @param player the player whose party to check
     * @param npc    the NPC used for distance and other checks (if {@link #checkPartyMember(Player, Npc)} is overriden)
     * @return the random party member or {@code null}
     */
    public Player getRandomPartyMember(Player player, Npc npc) {
        if ((player == null) || !checkDistanceToTarget(player, npc)) {
            return null;
        }
        final Party party = player.getParty();
        Player luckyPlayer = null;
        if (party == null) {
            if (checkPartyMember(player, npc)) {
                luckyPlayer = player;
            }
        } else {
            int highestRoll = 0;

            for (Player member : party.getMembers()) {
                final int rnd = Rnd.get(1000);

                if ((rnd > highestRoll) && checkPartyMember(member, npc)) {
                    highestRoll = rnd;
                    luckyPlayer = member;
                }
            }
        }
        return (luckyPlayer != null) && checkDistanceToTarget(luckyPlayer, npc) ? luckyPlayer : null;
    }

    private boolean checkDistanceToTarget(Player player, Npc target) {
        return (target == null) || GameUtils.checkIfInRange(PartySettings.partyRange(), player, target, true);
    }

    /**
     * This method is called for every party member in {@link #getRandomPartyMember(Player, Npc)}.<br>
     * It is intended to be overriden by the specific quest implementations.
     *
     * @param player the player to check
     * @param npc    the NPC that was passed to {@link #getRandomPartyMember(Player, Npc)}
     * @return {@code true} if this party member passes the check, {@code false} otherwise
     */
    public boolean checkPartyMember(Player player, Npc npc) {
        return true;
    }

    /**
     * Get a random party member from the player's party who has this quest at the specified quest progress.<br>
     * If the player is not in a party, only the player himself is checked.
     *
     * @param player       the player whose random party member state to get
     * @param condition    the quest progress step the random member should be at (-1 = check only if quest is started)
     * @param playerChance how many times more chance does the player get compared to other party members (3 - 3x more chance).<br>
     *                     On retail servers, the killer usually gets 2-3x more chance than other party members
     * @param target       the NPC to use for the distance check (can be null)
     * @return the {@link QuestState} object of the random party member or {@code null} if none matched the condition
     */
    public QuestState getRandomPartyMemberState(Player player, int condition, int playerChance, Npc target) {
        if ((player == null) || (playerChance < 1)) {
            return null;
        }

        QuestState qs = player.getQuestState(getName());
        if (!player.isInParty()) {
            return !checkPartyMemberConditions(qs, condition, target) || !checkDistanceToTarget(player, target) ? null : qs;
        }

        final List<QuestState> candidates = new ArrayList<>();
        if (checkPartyMemberConditions(qs, condition, target)) {
            for (int i = 0; i < playerChance; i++) {
                candidates.add(qs);
            }
        }

        for (Player member : player.getParty().getMembers()) {
            if (member == player) {
                continue;
            }

            qs = member.getQuestState(getName());
            if (checkPartyMemberConditions(qs, condition, target)) {
                candidates.add(qs);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        qs = Rnd.get(candidates);
        return !checkDistanceToTarget(qs.getPlayer(), target) ? null : qs;
    }

    private boolean checkPartyMemberConditions(QuestState qs, int condition, Npc npc) {
        return (qs != null) && ((condition == -1) ? qs.isStarted() : qs.isCond(condition)) && checkPartyMember(qs, npc);
    }

    /**
     * This method is called for every party member in {@link #getRandomPartyMemberState(Player, int, int, Npc)} if/after all the standard checks are passed.<br>
     * It is intended to be overriden by the specific quest implementations.<br>
     * It can be used in cases when there are more checks performed than simply a quest condition check,<br>
     * for example, if an item is required in the player's inventory.
     *
     * @param qs  the {@link QuestState} object of the party member
     * @param npc the NPC that was passed as the last parameter to {@link #getRandomPartyMemberState(Player, int, int, Npc)}
     * @return {@code true} if this party member passes the check, {@code false} otherwise
     */
    public boolean checkPartyMember(QuestState qs, Npc npc) {
        return true;
    }

    /**
     * Send an HTML file to the specified player.
     *
     * @param player   the player to send the HTML to
     * @param filename the name of the HTML file to show
     * @return the contents of the HTML file that was sent to the player
     * @see #showHtmlFile(Player, String, Npc)
     */
    public String showHtmlFile(Player player, String filename) {
        return showHtmlFile(player, filename, null);
    }

    /**
     * Send an HTML file to the specified player.
     *
     * @param player   the player to send the HTML file to
     * @param filename the name of the HTML file to show
     * @param npc      the NPC that is showing the HTML file
     * @return the contents of the HTML file that was sent to the player
     */
    public String showHtmlFile(Player player, String filename, Npc npc) {
        final boolean questwindow = !filename.endsWith(".html");

        String content = getHtml(player, filename);

        // Send message to client if message not empty
        if (content != null) {
            if (npc != null) {
                content = content.replaceAll("%objectId%", String.valueOf(npc.getObjectId()));
            }

            if (questwindow && (questId > 0) && (questId < 20000) && (questId != 999)) {
                final NpcQuestHtmlMessage npcReply = new NpcQuestHtmlMessage(npc != null ? npc.getObjectId() : 0, questId);
                npcReply.setHtml(content);
                npcReply.replace("%playername%", player.getName());
                player.sendPacket(npcReply);
            } else {
                final NpcHtmlMessage npcReply = new NpcHtmlMessage(npc != null ? npc.getObjectId() : 0, content);
                npcReply.replace("%playername%", player.getName());
                player.sendPacket(npcReply);
            }
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }

        return content;
    }

    /**
     * @param player   for language prefix.
     * @param fileName the html file to be get.
     * @return the HTML file contents
     */
    public String getHtml(Player player, String fileName) {
        String path;
        if(fileName.startsWith("data/")) {
            path = fileName;
        } else if(fileName.startsWith("./")) {
            path = "data/extension/" + getPath() + fileName.substring(1);
        } else {
            path = "data/extension/html/" + getPath() + "/" + fileName;
        }
        return HtmCache.getInstance().getHtm(player, path);
    }

    /**
     * @return the registered quest items IDs.
     */
    public int[] getRegisteredItemIds() {
        return questItemIds;
    }

    /**
     * Registers all items that have to be destroyed in case player abort the quest or finish it.
     *
     * @param items
     */
    public void registerQuestItems(int... items) {
        for (int id : items) {
            if ((id != 0) && (ItemEngine.getInstance().getTemplate(id) == null)) {
                LOGGER.error("Found registerQuestItems for non existing item: {}!", id);
            }
        }
        questItemIds = items;
    }

    /**
     * Remove all quest items associated with this quest from the specified player's inventory.
     *
     * @param player the player whose quest items to remove
     */
    public void removeRegisteredQuestItems(Player player) {
        takeItems(player, -1, questItemIds);
    }

    @Override
    public String getScriptName() {
        return getName();
    }

    @Override
    public Path getScriptPath() {
        return ScriptEngineManager.getInstance().getCurrentLoadingScript();
    }


    @Override
    public boolean reload() {
        unload();
        return super.reload();
    }

    @Override
    public boolean unload() {
        return unload(true);
    }

    public boolean unload(boolean removeFromList) {
        onSave();
        // cancel all pending timers before reloading.
        // if timers ought to be restarted, the quest can take care of it
        // with its code (example: save global data indicating what timer must be restarted).
        cancelQuestTimers();

        if (removeFromList) {
            return QuestManager.getInstance().removeScript(this) && super.unload();
        }
        return super.unload();
    }

    /**
     * Verifies if this is a custom quest.
     *
     * @return {@code true} if the quest script is a custom quest, {@code false} otherwise.
     */
    public boolean isCustomQuest() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public Set<NpcLogListHolder> getNpcLogList(Player activeChar) {
        return Collections.emptySet();
    }

    public void sendNpcLogList(Player activeChar) {
        if (activeChar.getQuestState(getName()) != null) {
            final ExQuestNpcLogList packet = new ExQuestNpcLogList(questId);
            getNpcLogList(activeChar).forEach(packet::add);
            activeChar.sendPacket(packet);
        }
    }

    /**
     * Gets the start conditions.
     *
     * @return the start conditions
     */
    private Set<QuestCondition> getStartConditions() {
        if (startConditions == null) {
            synchronized (this) {
                if (startConditions == null) {
                    startConditions = ConcurrentHashMap.newKeySet(1);
                }
            }
        }
        return startConditions;
    }

    /**
     * Verifies if the player meets all the start conditions.
     *
     * @param player the player
     * @return {@code true} if all conditions are met
     */
    public boolean canStartQuest(Player player) {
        if (startConditions == null) {
            return true;
        }

        for (QuestCondition cond : startConditions) {
            if (!cond.test(player)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the HTML for the first starting condition not met.
     *
     * @param player the player
     * @param npc
     * @return the HTML
     */
    public String getStartConditionHtml(Player player, Npc npc) {
        final QuestState st = getQuestState(player, false);
        if ((startConditions == null) || ((st != null) && !st.isCreated())) {
            return null;
        }

        for (QuestCondition cond : startConditions) {
            if (!cond.test(player)) {
                return cond.getHtml(npc);
            }
        }
        return null;
    }

    /**
     * Adds a predicate to the start conditions.
     *
     * @param questStartRequirement the predicate condition
     * @param html                  the HTML to display if that condition is not met
     */
    public void addCondStart(Predicate<Player> questStartRequirement, String html) {
        getStartConditions().add(new QuestCondition(questStartRequirement, html));
    }

    /**
     * Adds a minimum/maximum level start condition to the quest.
     *
     * @param minLevel the minimum player's level to start the quest
     * @param maxLevel the maximum player's level to start the quest
     * @param html     the HTML to display if the condition is not met
     */
    public void addCondLevel(int minLevel, int maxLevel, String html) {
        addCondStart(p -> (p.getLevel() >= minLevel) && (p.getLevel() <= maxLevel), html);
    }

    /**
     * Adds a minimum level start condition to the quest.
     *
     * @param minLevel the minimum player's level to start the quest
     * @param html     the HTML to display if the condition is not met
     */
    public void addCondMinLevel(int minLevel, String html) {
        addCondStart(p -> p.getLevel() >= minLevel, html);
    }

    /**
     * Adds a minimum/maximum level start condition to the quest.
     *
     * @param maxLevel the maximum player's level to start the quest
     * @param html     the HTML to display if the condition is not met
     */
    public void addCondMaxLevel(int maxLevel, String html) {
        addCondStart(p -> p.getLevel() <= maxLevel, html);
    }

    public final void addCondClassIds(ClassId... classIds) {
        addCondStart(p -> Util.contains(classIds, p.getClassId()), "");
    }

    /**
     * Adds a category start condition to the quest.
     *
     * @param categoryType the category type
     * @param html         the HTML to display if the condition is not met
     */
    public void addCondInCategory(CategoryType categoryType, String html) {
        addCondStart(p -> p.isInCategory(categoryType), html);
    }

    public void onQuestAborted(Player player) {

    }

    /**
     * Loads all quest states and variables for the specified player.
     *
     * @param player the player who is entering the world
     */
    public static void playerEnter(Player player) {
        var questDAO = getDAO(QuestDAO.class);
        for (QuestData data : questDAO.findPlayerQuestsByState(player.getObjectId())) {
            final Quest quest = QuestManager.getInstance().getQuest(data.getName());
            if (isNull(quest)) {
                LOGGER.debug("Unknown quest {} for {}", data.getName(), player);

                if (Config.AUTODELETE_INVALID_QUEST_DATA) {
                    questDAO.deleteQuest(player.getObjectId(), data.getName());
                }
                continue;
            }
            new QuestState(quest, player, State.getStateId(data.getValue()));

        }

        for (QuestData data : questDAO.findPlayerQuestsByNonState(player.getObjectId())) {
            var questState = player.getQuestState(data.getName());
            if(isNull(questState)) {
                LOGGER.debug("Lost variable {} in quest {} for {}", data.getVar(), data.getName(), player);
                if (Config.AUTODELETE_INVALID_QUEST_DATA) {
                    questDAO.deleteQuestVar(player.getObjectId(), data.getName(), data.getVar());
                }
                continue;
            }
            questState.setInternal(data.getVar(), data.getValue());
        }

        player.sendPacket(new QuestList(player));
    }

    /**
     * Insert in the database the quest for the player.
     *
     * @param qs    the {@link QuestState} object whose variable to insert
     * @param var   the name of the variable
     * @param value the value of the variable
     */
    static void createQuestVarInDb(QuestState qs, String var, String value) {
        getDAO(QuestDAO.class).saveQuestVar(qs.getPlayer().getObjectId(), qs.getQuestName(), var, value);
    }

    /**
     * Update the value of the variable "var" for the specified quest in database
     *
     * @param qs    the {@link QuestState} object whose variable to update
     * @param var   the name of the variable
     * @param value the value of the variable
     */
    static void updateQuestVarInDb(QuestState qs, String var, String value) {
        getDAO(QuestDAO.class).updateQuestVar(qs.getPlayer().getObjectId(), qs.getQuestName(), var, value);
    }

    /**
     * Delete a variable of player's quest from the database.
     *
     * @param qs  the {@link QuestState} object whose variable to delete
     * @param var the name of the variable to delete
     */
    static void deleteQuestVarInDb(QuestState qs, String var) {
        getDAO(QuestDAO.class).deleteQuestVar(qs.getPlayer().getObjectId(), qs.getQuestName(), var);
    }

    /**
     * Delete from the database all variables and states of the specified quest state.
     *
     * @param qs         the {@link QuestState} object whose variables to delete
     * @param repeatable if {@code false}, the state variable will be preserved, otherwise it will be deleted as well
     */
    public static void deleteQuestInDb(QuestState qs, boolean repeatable) {
        if(repeatable){
            getDAO(QuestDAO.class).deleteQuest(qs.getPlayer().getObjectId(), qs.getQuestName());
        } else  {
            getDAO(QuestDAO.class).deleteNonRepeatable(qs.getPlayer().getObjectId(), qs.getQuestName());
        }
    }

    /**
     * Create a database record for the specified quest state.
     *
     * @param qs the {@link QuestState} object whose data to write in the database
     */
    static void createQuestInDb(QuestState qs) {
        createQuestVarInDb(qs, "<state>", State.getStateName(qs.getState()));
    }

    /**
     * Update a quest state record of the specified quest state in database.
     *
     * @param qs the {@link QuestState} object whose data to update in the database
     */
    static void updateQuestInDb(QuestState qs) {
        updateQuestVarInDb(qs, "<state>", State.getStateName(qs.getState()));
    }

    /**
     * @param player the player whose language settings to use in finding the html of the right language
     * @return the default html for when no quest is available: "You are either not on a quest that involves this NPC.."
     */
    public static String getNoQuestMsg(Player player) {
        final String result = HtmCache.getInstance().getHtm(player, "data/html/noquest.htm");
        if ((result != null) && (result.length() > 0)) {
            return result;
        }
        return DEFAULT_NO_QUEST_MSG;
    }

    /**
     * @param player the player whose language settings to use in finding the html of the right language
     * @return the default html for when quest is already completed
     */
    protected static String getAlreadyCompletedMsg(Player player) {
        return getAlreadyCompletedMsg(player, QuestType.ONE_TIME);
    }

    /**
     * @param player the player whose language settings to use in finding the html of the right language
     * @param type   the Quest type
     * @return the default html for when quest is already completed
     */
    private static String getAlreadyCompletedMsg(Player player, QuestType type) {
        return HtmCache.getInstance().getHtm(player, (type == QuestType.ONE_TIME ? "data/html/alreadyCompleted.html" : "data/html/alreadyCompletedDaily.html"));
    }
}
