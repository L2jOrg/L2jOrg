/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.instancemanager.tasks.UpdateSoDStateTask;
import org.l2j.gameserver.model.quest.Quest;

import java.util.Calendar;
import java.util.logging.Logger;

public final class GraciaSeedsManager {
    private static final Logger LOGGER = Logger.getLogger(GraciaSeedsManager.class.getName());
    private static final byte SOITYPE = 2;
    private static final byte SOATYPE = 3;
    // Seed of Destruction
    private static final byte SODTYPE = 1;
    public static String ENERGY_SEEDS = "EnergySeeds";
    private final Calendar _SoDLastStateChangeDate;
    private int _SoDTiatKilled = 0;
    private int _SoDState = 1;

    protected GraciaSeedsManager() {
        _SoDLastStateChangeDate = Calendar.getInstance();
        loadData();
        handleSodStages();
    }

    /**
     * Gets the single instance of {@code GraciaSeedsManager}.
     *
     * @return single instance of {@code GraciaSeedsManager}
     */
    public static GraciaSeedsManager getInstance() {
        return SingletonHolder._instance;
    }

    public void saveData(byte seedType) {
        switch (seedType) {
            case SODTYPE: {
                // Seed of Destruction
                GlobalVariablesManager.getInstance().set("SoDState", _SoDState);
                GlobalVariablesManager.getInstance().set("SoDTiatKilled", _SoDTiatKilled);
                GlobalVariablesManager.getInstance().set("SoDLSCDate", _SoDLastStateChangeDate.getTimeInMillis());
                break;
            }
            case SOITYPE: {
                // Seed of Infinity
                break;
            }
            case SOATYPE: {
                // Seed of Annihilation
                break;
            }
            default: {
                LOGGER.warning(getClass().getSimpleName() + ": Unknown SeedType in SaveData: " + seedType);
                break;
            }
        }
    }

    public void loadData() {
        // Seed of Destruction variables
        if (GlobalVariablesManager.getInstance().hasVariable("SoDState")) {
            _SoDState = GlobalVariablesManager.getInstance().getInt("SoDState");
            _SoDTiatKilled = GlobalVariablesManager.getInstance().getInt("SoDTiatKilled");
            _SoDLastStateChangeDate.setTimeInMillis(GlobalVariablesManager.getInstance().getLong("SoDLSCDate"));
        } else {
            // save Initial values
            saveData(SODTYPE);
        }
    }

    private void handleSodStages() {
        switch (_SoDState) {
            case 1: {
                // do nothing, players should kill Tiat a few times
                break;
            }
            case 2: {
                // Conquest Complete state, if too much time is passed than change to defense state
                final long timePast = System.currentTimeMillis() - _SoDLastStateChangeDate.getTimeInMillis();
                if (timePast >= Config.SOD_STAGE_2_LENGTH) {
                    // change to Attack state because Defend statet is not implemented
                    setSoDState(1, true);
                } else {
                    ThreadPoolManager.getInstance().schedule(new UpdateSoDStateTask(), Config.SOD_STAGE_2_LENGTH - timePast);
                }
                break;
            }
            case 3: {
                // not implemented
                setSoDState(1, true);
                break;
            }
            default: {
                LOGGER.warning(getClass().getSimpleName() + ": Unknown Seed of Destruction state(" + _SoDState + ")! ");
            }
        }
    }

    public void updateSodState() {
        final Quest quest = QuestManager.getInstance().getQuest(ENERGY_SEEDS);
        if (quest == null) {
            LOGGER.warning(getClass().getSimpleName() + ": missing EnergySeeds Quest!");
        } else {
            quest.notifyEvent("StopSoDAi", null, null);
        }
    }

    public void increaseSoDTiatKilled() {
        if (_SoDState == 1) {
            _SoDTiatKilled++;
            if (_SoDTiatKilled >= Config.SOD_TIAT_KILL_COUNT) {
                setSoDState(2, false);
            }
            saveData(SODTYPE);
            final Quest esQuest = QuestManager.getInstance().getQuest(ENERGY_SEEDS);
            if (esQuest == null) {
                LOGGER.warning(getClass().getSimpleName() + ": missing EnergySeeds Quest!");
            } else {
                esQuest.notifyEvent("StartSoDAi", null, null);
            }
        }
    }

    public int getSoDTiatKilled() {
        return _SoDTiatKilled;
    }

    public void setSoDState(int value, boolean doSave) {
        LOGGER.info(getClass().getSimpleName() + ": New Seed of Destruction state -> " + value + ".");
        _SoDLastStateChangeDate.setTimeInMillis(System.currentTimeMillis());
        _SoDState = value;
        // reset number of Tiat kills
        if (_SoDState == 1) {
            _SoDTiatKilled = 0;
        }

        handleSodStages();

        if (doSave) {
            saveData(SODTYPE);
        }
    }

    public long getSoDTimeForNextStateChange() {
        switch (_SoDState) {
            case 1: {
                return -1;
            }
            case 2: {
                return ((_SoDLastStateChangeDate.getTimeInMillis() + Config.SOD_STAGE_2_LENGTH) - System.currentTimeMillis());
            }
            case 3: {
                // not implemented yet
                return -1;
            }
            default: {
                // this should not happen!
                return -1;
            }
        }
    }

    public Calendar getSoDLastStateChangeDate() {
        return _SoDLastStateChangeDate;
    }

    public int getSoDState() {
        return _SoDState;
    }

    private static class SingletonHolder {
        protected static final GraciaSeedsManager _instance = new GraciaSeedsManager();
    }
}