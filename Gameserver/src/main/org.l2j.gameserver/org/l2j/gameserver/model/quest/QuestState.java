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
package org.l2j.gameserver.model.quest;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerQuestComplete;
import org.l2j.gameserver.network.serverpackets.ExShowQuestMark;
import org.l2j.gameserver.network.serverpackets.QuestList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * Quest state class.
 *
 * @author Luis Arias
 */
public final class QuestState {
    protected static final Logger LOGGER = LoggerFactory.getLogger(QuestState.class);

    /**
     * The name of the quest of this QuestState
     */
    private final String _questName;

    /**
     * The "owner" of this QuestState object
     */
    private final Player _player;

    /**
     * The current state of the quest
     */
    private byte _state;

    /**
     * Used for simulating Quest onTalk
     */
    private boolean _simulated = false;

    /**
     * A map of key->value pairs containing the quest state variables and their values
     */
    private Map<String, String> _vars;

    /**
     * boolean flag letting QuestStateManager know to exit quest when cleaning up
     */
    private boolean _isExitQuestOnCleanUp = false;

    /**
     * Constructor of the QuestState. Creates the QuestState object and sets the player's progress of the quest to this QuestState.
     *
     * @param quest  the {@link Quest} object associated with the QuestState
     * @param player the owner of this {@link QuestState} object
     * @param state  the initial state of the quest
     */
    public QuestState(Quest quest, Player player, byte state) {
        _questName = quest.getName();
        _player = player;
        _state = state;

        player.setQuestState(this);
    }

    /**
     * @return the name of the quest of this QuestState
     */
    public String getQuestName() {
        return _questName;
    }

    /**
     * @return the {@link Quest} object of this QuestState
     */
    public Quest getQuest() {
        return QuestManager.getInstance().getQuest(_questName);
    }

    /**
     * @return the {@link Player} object of the owner of this QuestState
     */
    public Player getPlayer() {
        return _player;
    }

    /**
     * @return the current State of this QuestState
     * @see org.l2j.gameserver.model.quest.State
     */
    public byte getState() {
        return _state;
    }

    /**
     * @return {@code true} if the State of this QuestState is CREATED, {@code false} otherwise
     * @see org.l2j.gameserver.model.quest.State
     */
    public boolean isCreated() {
        return _state == State.CREATED;
    }

    /**
     * @return {@code true} if the State of this QuestState is STARTED, {@code false} otherwise
     * @see org.l2j.gameserver.model.quest.State
     */
    public boolean isStarted() {
        return _state == State.STARTED;
    }

    /**
     * @return {@code true} if the State of this QuestState is COMPLETED, {@code false} otherwise
     * @see org.l2j.gameserver.model.quest.State
     */
    public boolean isCompleted() {
        return _state == State.COMPLETED;
    }

    /**
     * @param state the new state of the quest to set
     * @return {@code true} if state was changed, {@code false} otherwise
     * @see #setState(byte state, boolean saveInDb)
     * @see org.l2j.gameserver.model.quest.State
     */
    public boolean setState(byte state) {
        return setState(state, true);
    }

    /**
     * Change the state of this quest to the specified value.
     *
     * @param state    the new state of the quest to set
     * @param saveInDb if {@code true}, will save the state change in the database
     * @return {@code true} if state was changed, {@code false} otherwise
     * @see org.l2j.gameserver.model.quest.State
     */
    public boolean setState(byte state, boolean saveInDb) {
        if (_simulated) {
            return false;
        }
        if (_state == state) {
            return false;
        }
        final boolean newQuest = isCreated();
        _state = state;
        if (saveInDb) {
            if (newQuest) {
                Quest.createQuestInDb(this);
            } else {
                Quest.updateQuestInDb(this);
            }
        }

        _player.sendPacket(new QuestList(_player));
        return true;
    }

    /**
     * Add parameter used in quests.
     *
     * @param var String pointing out the name of the variable for quest
     * @param val String pointing out the value of the variable for quest
     * @return String (equal to parameter "val")
     */
    public String setInternal(String var, String val) {
        if (_simulated) {
            return null;
        }

        if (_vars == null) {
            _vars = new HashMap<>();
        }

        if (val == null) {
            val = "";
        }

        _vars.put(var, val);
        return val;
    }

    public String set(String var, int val) {
        if (_simulated) {
            return null;
        }
        return set(var, Integer.toString(val));
    }

    /**
     * Return value of parameter "val" after adding the couple (var,val) in class variable "vars".<br>
     * Actions:<br>
     * <ul>
     * <li>Initialize class variable "vars" if is null.</li>
     * <li>Initialize parameter "val" if is null</li>
     * <li>Add/Update couple (var,val) in class variable Map "vars"</li>
     * <li>If the key represented by "var" exists in Map "vars", the couple (var,val) is updated in the database.<br>
     * The key is known as existing if the preceding value of the key (given as result of function put()) is not null.<br>
     * If the key doesn't exist, the couple is added/created in the database</li>
     * <ul>
     *
     * @param var String indicating the name of the variable for quest
     * @param val String indicating the value of the variable for quest
     * @return String (equal to parameter "val")
     */
    public String set(String var, String val) {
        if (_simulated) {
            return null;
        }

        if (_vars == null) {
            _vars = new HashMap<>();
        }

        if (val == null) {
            val = "";
        }

        final String old = _vars.put(var, val);
        if (old != null) {
            Quest.updateQuestVarInDb(this, var, val);
        } else {
            Quest.createQuestVarInDb(this, var, val);
        }

        if ("cond".equals(var)) {
            try {
                int previousVal = 0;
                try {
                    previousVal = Integer.parseInt(old);
                } catch (Exception ignored) {
                }
                setCond(Integer.parseInt(val), previousVal);
                getQuest().sendNpcLogList(getPlayer());
            } catch (Exception e) {
                LOGGER.warn(_player.getName() + ", " + _questName + " cond [" + val + "] is not an integer.  Value stored, but no packet was sent: " + e.getMessage(), e);
            }
        }

        return val;
    }

    /**
     * Internally handles the progression of the quest so that it is ready for sending appropriate packets to the client.<br>
     * <u><i>Actions :</i></u><br>
     * <ul>
     * <li>Check if the new progress number resets the quest to a previous (smaller) step.</li>
     * <li>If not, check if quest progress steps have been skipped.</li>
     * <li>If skipped, prepare the variable completedStateFlags appropriately to be ready for sending to clients.</li>
     * <li>If no steps were skipped, flags do not need to be prepared...</li>
     * <li>If the passed step resets the quest to a previous step, reset such that steps after the parameter are not considered, while skipped steps before the parameter, if any, maintain their info.</li>
     * </ul>
     *
     * @param cond the current quest progress condition (0 - 31 including)
     * @param old  the previous quest progress condition to check against
     */
    private void setCond(int cond, int old) {
        if (_simulated) {
            return;
        }

        if (cond == old) {
            return;
        }

        int completedStateFlags = 0;
        // cond 0 and 1 do not need completedStateFlags. Also, if cond > 1, the 1st step must
        // always exist (i.e. it can never be skipped). So if cond is 2, we can still safely
        // assume no steps have been skipped.
        // Finally, more than 31 steps CANNOT be supported in any way with skipping.
        if ((cond < 3) || (cond > 31)) {
            unset("__compltdStateFlags");
        } else {
            completedStateFlags = getInt("__compltdStateFlags");
        }

        // case 1: No steps have been skipped so far...
        if (completedStateFlags == 0) {
            // check if this step also doesn't skip anything. If so, no further work is needed
            // also, in this case, no work is needed if the state is being reset to a smaller value
            // in those cases, skip forward to informing the client about the change...

            // ELSE, if we just now skipped for the first time...prepare the flags!!!
            if (cond > (old + 1)) {
                // set the most significant bit to 1 (indicates that there exist skipped states)
                // also, ensure that the least significant bit is an 1 (the first step is never skipped, no matter
                // what the cond says)
                completedStateFlags = 0x80000001;

                // since no flag had been skipped until now, the least significant bits must all
                // be set to 1, up until "old" number of bits.
                completedStateFlags |= (1 << old) - 1;

                // now, just set the bit corresponding to the passed cond to 1 (current step)
                completedStateFlags |= 1 << (cond - 1);
                set("__compltdStateFlags", String.valueOf(completedStateFlags));
            }
        }
        // case 2: There were exist previously skipped steps
        // if this is a push back to a previous step, clear all completion flags ahead
        else if (cond < old) {
            // note, this also unsets the flag indicating that there exist skips
            completedStateFlags &= (1 << cond) - 1;

            // now, check if this resulted in no steps being skipped any more
            if (completedStateFlags == ((1 << cond) - 1)) {
                unset("__compltdStateFlags");
            } else {
                // set the most significant bit back to 1 again, to correctly indicate that this skips states.
                // also, ensure that the least significant bit is an 1 (the first step is never skipped, no matter
                // what the cond says)
                completedStateFlags |= 0x80000001;
                set("__compltdStateFlags", String.valueOf(completedStateFlags));
            }
        }
        // If this moves forward, it changes nothing on previously skipped steps.
        // Just mark this state and we are done.
        else {
            completedStateFlags |= 1 << (cond - 1);
            set("__compltdStateFlags", String.valueOf(completedStateFlags));
        }

        // send a packet to the client to inform it of the quest progress (step change)
        _player.sendPacket(new QuestList(_player));

        final Quest q = getQuest();
        if (!q.isCustomQuest() && (cond > 0)) {
            _player.sendPacket(new ExShowQuestMark(q.getId(), getCond()));
        }
    }

    /**
     * Removes a quest variable from the list of existing quest variables.
     *
     * @param var the name of the variable to remove
     * @return the previous value of the variable or {@code null} if none were found
     */
    public String unset(String var) {
        if (_simulated) {
            return null;
        }

        if (_vars == null) {
            return null;
        }

        final String old = _vars.remove(var);
        if (old != null) {
            Quest.deleteQuestVarInDb(this, var);
        }
        return old;
    }

    /**
     * @param var the name of the variable to get
     * @return the value of the variable from the list of quest variables
     */
    public String get(String var) {
        if (_vars == null) {
            return null;
        }

        return _vars.get(var);
    }

    /**
     * @param var the name of the variable to get
     * @return the integer value of the variable or 0 if the variable does not exist or its value is not an integer
     */
    public int getInt(String var) {
        if (_vars == null) {
            return 0;
        }

        final String variable = _vars.get(var);
        if ((variable == null) || variable.isEmpty()) {
            return 0;
        }

        int varint = 0;
        try {
            varint = Integer.parseInt(variable);
        } catch (NumberFormatException nfe) {
            LOGGER.info("Quest " + _questName + ", method getInt(" + var + "), tried to parse a non-integer value (" + variable + "). Char Id: " + _player.getObjectId(), nfe);
        }

        return varint;
    }

    /**
     * Checks if the quest state progress ({@code cond}) is at the specified step.
     *
     * @param condition the condition to check against
     * @return {@code true} if the quest condition is equal to {@code condition}, {@code false} otherwise
     * @see #getInt(String var)
     */
    public boolean isCond(int condition) {
        return getInt("cond") == condition;
    }

    /**
     * @return the current quest progress ({@code cond})
     */
    public int getCond() {
        if (isStarted()) {
            int val = getInt("cond");
            if ((val & 0x80000000) != 0) {
                val &= 0x7fffffff;
                for (int i = 1; i < 32; i++) {
                    val = (val >> 1);
                    if (val == 0) {
                        val = i;
                        break;
                    }
                }
            }
            return val;
        }
        return 0;
    }

    /**
     * Sets the quest state progress ({@code cond}) to the specified step.
     *
     * @param value the new value of the quest state progress
     * @return this {@link QuestState} object
     * @see #set(String var, String val)
     * @see #setCond(int, boolean)
     */
    public QuestState setCond(int value) {
        if (_simulated) {
            return null;
        }

        if (isStarted()) {
            set("cond", Integer.toString(value));
        }
        return this;
    }

    /**
     * Check if a given variable is set for this quest.
     *
     * @param variable the variable to check
     * @return {@code true} if the variable is set, {@code false} otherwise
     * @see #get(String)
     * @see #getInt(String)
     * @see #getCond()
     */
    public boolean isSet(String variable) {
        return get(variable) != null;
    }

    /**
     * Sets the quest state progress ({@code cond}) to the specified step.
     *
     * @param value           the new value of the quest state progress
     * @param playQuestMiddle if {@code true}, plays "ItemSound.quest_middle"
     * @return this {@link QuestState} object
     * @see #setCond(int value)
     * @see #set(String var, String val)
     */
    public QuestState setCond(int value, boolean playQuestMiddle) {
        if (_simulated) {
            return null;
        }

        if (!isStarted()) {
            return this;
        }
        set("cond", String.valueOf(value));

        if (playQuestMiddle) {
            _player.sendPacket(QuestSound.ITEMSOUND_QUEST_MIDDLE.getPacket());
        }
        return this;
    }

    /**
     * @return the current Memo State
     */
    public int getMemoState() {
        if (isStarted()) {
            return getInt("memoState");
        }
        return 0;
    }

    public QuestState setMemoState(int value) {
        if (_simulated) {
            return null;
        }
        set("memoState", String.valueOf(value));
        return this;
    }

    public boolean isMemoState(int memoState) {
        return getInt("memoState") == memoState;
    }

    /**
     * Gets the memo state ex.
     *
     * @param slot the slot where the value was saved
     * @return the memo state ex
     */
    public int getMemoStateEx(int slot) {
        if (isStarted()) {
            return getInt("memoStateEx" + slot);
        }
        return 0;
    }

    /**
     * Sets the memo state ex.
     *
     * @param slot  the slot where the value will be saved
     * @param value the value
     * @return this QuestState
     */
    public QuestState setMemoStateEx(int slot, int value) {
        if (_simulated) {
            return null;
        }
        set("memoStateEx" + slot, String.valueOf(value));
        return this;
    }

    /**
     * Verifies if the given value is equal to the current memos state ex.
     *
     * @param slot        the slot where the value was saved
     * @param memoStateEx the value to verify
     * @return {@code true} if the values are equal, {@code false} otherwise
     */
    public boolean isMemoStateEx(int slot, int memoStateEx) {
        return (getMemoStateEx(slot) == memoStateEx);
    }

    /**
     * @return {@code true} if quest is to be exited on clean up by QuestStateManager, {@code false} otherwise
     */
    public final boolean isExitQuestOnCleanUp() {
        return _isExitQuestOnCleanUp;
    }

    /**
     * @param isExitQuestOnCleanUp {@code true} if quest is to be exited on clean up by QuestStateManager, {@code false} otherwise
     */
    public void setIsExitQuestOnCleanUp(boolean isExitQuestOnCleanUp) {
        if (_simulated) {
            return;
        }
        _isExitQuestOnCleanUp = isExitQuestOnCleanUp;
    }

    /**
     * Set condition to 1, state to STARTED and play the "ItemSound.quest_accept".<br>
     * Works only if state is CREATED and the quest is not a custom quest.
     *
     * @return the newly created {@code QuestState} object
     */
    public QuestState startQuest() {
        if (_simulated) {
            return null;
        }
        if (isCreated() && !getQuest().isCustomQuest()) {
            set("cond", "1");
            setState(State.STARTED);
            _player.sendPacket(QuestSound.ITEMSOUND_QUEST_ACCEPT.getPacket());
            getQuest().sendNpcLogList(getPlayer());
        }
        return this;
    }

    /**
     * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
     * If {@code type} is {@code QuestType.ONE_TIME}, also removes all other quest data associated with this quest.
     *
     * @param type the {@link QuestType} of the quest
     * @return this {@link QuestState} object
     * @see #exitQuest(QuestType type, boolean playExitQuest)
     * @see #exitQuest(boolean repeatable)
     * @see #exitQuest(boolean repeatable, boolean playExitQuest)
     */
    public QuestState exitQuest(QuestType type) {
        if (_simulated) {
            return null;
        }

        switch (type) {
            case DAILY: {
                exitQuest(false);
                setRestartTime();
                break;
            }
            // case ONE_TIME:
            // case REPEATABLE:
            default: {
                exitQuest(type == QuestType.REPEATABLE);
                break;
            }
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestComplete(_player, getQuest().getId(), type), _player);

        return this;
    }

    /**
     * Finishes the quest and removes all quest item associated with this quest from the player's inventory.<br>
     * If {@code type} is {@code QuestType.ONE_TIME}, also removes all other quest data associated with this quest.
     *
     * @param type          the {@link QuestType} of the quest
     * @param playExitQuest if {@code true}, plays "ItemSound.quest_finish"
     * @return this {@link QuestState} object
     * @see #exitQuest(QuestType type)
     * @see #exitQuest(boolean repeatable)
     * @see #exitQuest(boolean repeatable, boolean playExitQuest)
     */
    public QuestState exitQuest(QuestType type, boolean playExitQuest) {
        if (_simulated) {
            return null;
        }
        exitQuest(type);
        if (playExitQuest) {
            _player.sendPacket(QuestSound.ITEMSOUND_QUEST_FINISH.getPacket());
        }
        return this;
    }

    /**
     * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
     * If {@code repeatable} is set to {@code false}, also removes all other quest data associated with this quest.
     *
     * @param repeatable if {@code true}, deletes all data and variables of this quest, otherwise keeps them
     * @return this {@link QuestState} object
     * @see #exitQuest(QuestType type)
     * @see #exitQuest(QuestType type, boolean playExitQuest)
     * @see #exitQuest(boolean repeatable, boolean playExitQuest)
     */
    private QuestState exitQuest(boolean repeatable) {
        if (_simulated) {
            return null;
        }

        _player.removeNotifyQuestOfDeath(this);

        if (!isStarted()) {
            return this;
        }

        // Clean registered quest items
        getQuest().removeRegisteredQuestItems(_player);

        Quest.deleteQuestInDb(this, repeatable);
        if (repeatable) {
            _player.delQuestState(_questName);
            _player.sendPacket(new QuestList(_player));
        } else {
            setState(State.COMPLETED);
        }
        _vars = null;
        return this;
    }

    /**
     * Finishes the quest and removes all quest items associated with this quest from the player's inventory.<br>
     * If {@code repeatable} is set to {@code false}, also removes all other quest data associated with this quest.
     *
     * @param repeatable    if {@code true}, deletes all data and variables of this quest, otherwise keeps them
     * @param playExitQuest if {@code true}, plays "ItemSound.quest_finish"
     * @return this {@link QuestState} object
     * @see #exitQuest(QuestType type)
     * @see #exitQuest(QuestType type, boolean playExitQuest)
     * @see #exitQuest(boolean repeatable)
     */
    public QuestState exitQuest(boolean repeatable, boolean playExitQuest) {
        if (_simulated) {
            return null;
        }

        exitQuest(repeatable);
        if (playExitQuest) {
            _player.sendPacket(QuestSound.ITEMSOUND_QUEST_FINISH.getPacket());
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestComplete(_player, getQuest().getId(), repeatable ? QuestType.REPEATABLE : QuestType.ONE_TIME), _player);
        return this;
    }

    /**
     * Set the restart time for the daily quests.<br>
     * The time is hardcoded at {@link Quest#getResetHour()} hours, {@link Quest#getResetMinutes()} minutes of the following day.<br>
     * It can be overridden in scripts (quests).
     */
    public void setRestartTime() {
        if (_simulated) {
            return;
        }

        final Calendar reDo = Calendar.getInstance();
        if (reDo.get(Calendar.HOUR_OF_DAY) >= getQuest().getResetHour()) {
            reDo.add(Calendar.DATE, 1);
        }
        reDo.set(Calendar.HOUR_OF_DAY, getQuest().getResetHour());
        reDo.set(Calendar.MINUTE, getQuest().getResetMinutes());
        set("restartTime", String.valueOf(reDo.getTimeInMillis()));
    }

    /**
     * Check if a daily quest is available to be started over.
     *
     * @return {@code true} if the quest is available, {@code false} otherwise.
     */
    public boolean isNowAvailable() {
        final String val = get("restartTime");
        return (val != null) && (Long.parseLong(val) <= System.currentTimeMillis());
    }

    public void setSimulated(boolean simulated) {
        _simulated = simulated;
    }
}