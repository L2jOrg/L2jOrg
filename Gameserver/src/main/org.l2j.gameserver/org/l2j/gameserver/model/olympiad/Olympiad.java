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
package org.l2j.gameserver.model.olympiad;

import io.github.joealisson.primitive.*;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.TimeInterpreter;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author godson
 * @author JoeAlisson
 */
public class Olympiad extends ListenersContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Olympiad.class);
    private static final Logger LOGGER_OLYMPIAD = LoggerFactory.getLogger("olympiad");

    public static final String OLYMPIAD_HTML_PATH = "data/html/olympiad/";
    private static final String OLYMPIAD_LOAD_DATA = "SELECT current_cycle, period, olympiad_end, validation_end, next_weekly_change FROM olympiad_data WHERE id = 0";
    private static final String OLYMPIAD_SAVE_DATA = "INSERT INTO olympiad_data (id, current_cycle, period, olympiad_end, validation_end, next_weekly_change) VALUES (0,?,?,?,?,?) ON DUPLICATE KEY UPDATE current_cycle=?, period=?, olympiad_end=?, validation_end=?, next_weekly_change=?";
    private static final String OLYMPIAD_LOAD_NOBLES = "SELECT olympiad_nobles.charId, olympiad_nobles.class_id, characters.char_name, olympiad_nobles.olympiad_points, olympiad_nobles.competitions_done, olympiad_nobles.competitions_won, olympiad_nobles.competitions_lost, olympiad_nobles.competitions_drawn, olympiad_nobles.competitions_done_week, olympiad_nobles.competitions_done_week_classed, olympiad_nobles.competitions_done_week_non_classed, olympiad_nobles.competitions_done_week_team FROM olympiad_nobles, characters WHERE characters.charId = olympiad_nobles.charId";
    private static final String OLYMPIAD_SAVE_NOBLES = "INSERT INTO olympiad_nobles (`charId`,`class_id`,`olympiad_points`,`competitions_done`,`competitions_won`,`competitions_lost`,`competitions_drawn`, `competitions_done_week`, `competitions_done_week_classed`, `competitions_done_week_non_classed`, `competitions_done_week_team`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String OLYMPIAD_UPDATE_NOBLES = "UPDATE olympiad_nobles SET olympiad_points = ?, competitions_done = ?, competitions_won = ?, competitions_lost = ?, competitions_drawn = ?, competitions_done_week = ?, competitions_done_week_classed = ?, competitions_done_week_non_classed = ?, competitions_done_week_team = ? WHERE charId = ?";
    private static final String OLYMPIAD_GET_HEROS = "SELECT olympiad_nobles.charId, characters.char_name FROM olympiad_nobles, characters WHERE characters.charId = olympiad_nobles.charId AND olympiad_nobles.class_id in (?, ?) AND olympiad_nobles.competitions_done >= " + Config.ALT_OLY_MIN_MATCHES + " AND olympiad_nobles.competitions_won > 0 ORDER BY olympiad_nobles.olympiad_points DESC, olympiad_nobles.competitions_done DESC, olympiad_nobles.competitions_won DESC";
    private static final String GET_ALL_CLASSIFIED_NOBLESS = "SELECT charId from olympiad_nobles_eom WHERE competitions_done >= " + Config.ALT_OLY_MIN_MATCHES + " ORDER BY olympiad_points DESC, competitions_done DESC, competitions_won DESC";
    private static final String GET_EACH_CLASS_LEADER = "SELECT characters.char_name from olympiad_nobles_eom, characters WHERE characters.charId = olympiad_nobles_eom.charId AND olympiad_nobles_eom.class_id = ? AND olympiad_nobles_eom.competitions_done >= " + Config.ALT_OLY_MIN_MATCHES + " ORDER BY olympiad_nobles_eom.olympiad_points DESC, olympiad_nobles_eom.competitions_done DESC, olympiad_nobles_eom.competitions_won DESC LIMIT 10";
    private static final String GET_EACH_CLASS_LEADER_CURRENT = "SELECT characters.char_name from olympiad_nobles, characters WHERE characters.charId = olympiad_nobles.charId AND olympiad_nobles.class_id = ? AND olympiad_nobles.competitions_done >= " + Config.ALT_OLY_MIN_MATCHES + " ORDER BY olympiad_nobles.olympiad_points DESC, olympiad_nobles.competitions_done DESC, olympiad_nobles.competitions_won DESC LIMIT 10";
    private static final String GET_EACH_CLASS_LEADER_SOULHOUND = "SELECT characters.char_name from olympiad_nobles_eom, characters WHERE characters.charId = olympiad_nobles_eom.charId AND (olympiad_nobles_eom.class_id = ? OR olympiad_nobles_eom.class_id = 133) AND olympiad_nobles_eom.competitions_done >= " + Config.ALT_OLY_MIN_MATCHES + " ORDER BY olympiad_nobles_eom.olympiad_points DESC, olympiad_nobles_eom.competitions_done DESC, olympiad_nobles_eom.competitions_won DESC LIMIT 10";
    private static final String GET_EACH_CLASS_LEADER_CURRENT_SOULHOUND = "SELECT characters.char_name from olympiad_nobles, characters WHERE characters.charId = olympiad_nobles.charId AND (olympiad_nobles.class_id = ? OR olympiad_nobles.class_id = 133) AND olympiad_nobles.competitions_done >= " + Config.ALT_OLY_MIN_MATCHES + " ORDER BY olympiad_nobles.olympiad_points DESC, olympiad_nobles.competitions_done DESC, olympiad_nobles.competitions_won DESC LIMIT 10";

    private static final String REMOVE_UNCLAIMED_POINTS = "DELETE FROM character_variables WHERE charId=? AND var=?";
    private static final String INSERT_UNCLAIMED_POINTS = "INSERT INTO character_variables (charId, var, val) VALUES (?, ?, ?)";
    public static final String UNCLAIMED_OLYMPIAD_POINTS_VAR = "UNCLAIMED_OLYMPIAD_POINTS";

    private static final String OLYMPIAD_DELETE_ALL = "TRUNCATE olympiad_nobles";
    private static final String OLYMPIAD_MONTH_CLEAR = "TRUNCATE olympiad_nobles_eom";
    private static final String OLYMPIAD_MONTH_CREATE = "INSERT INTO olympiad_nobles_eom SELECT charId, class_id, olympiad_points, competitions_done, competitions_won, competitions_lost, competitions_drawn FROM olympiad_nobles";

    private static final IntSet HERO_IDS = CategoryManager.getInstance().getCategoryByType(CategoryType.FOURTH_CLASS_GROUP);

    private static final int COMP_START = Config.ALT_OLY_START_TIME; // 6PM
    private static final int COMP_MIN = Config.ALT_OLY_MIN; // 00 mins
    private static final long COMP_PERIOD = Config.ALT_OLY_CPERIOD; // 6 hours
    protected static final long WEEKLY_PERIOD = Config.ALT_OLY_WPERIOD; // 1 week
    protected static final long VALIDATION_PERIOD = Config.ALT_OLY_VPERIOD; // 24 hours

    public static final int DEFAULT_POINTS = Config.ALT_OLY_START_POINTS;
    protected static final int WEEKLY_POINTS = Config.ALT_OLY_WEEKLY_POINTS;

    public static final String CHAR_ID = "charId";
    public static final String CLASS_ID = "class_id";
    public static final String CHAR_NAME = "char_name";
    public static final String POINTS = "olympiad_points";
    public static final String COMP_DONE = "competitions_done";
    public static final String COMP_WON = "competitions_won";
    public static final String COMP_LOST = "competitions_lost";
    public static final String COMP_DRAWN = "competitions_drawn";
    public static final String COMP_DONE_WEEK = "competitions_done_week";

    private final IntMap<StatsSet> nobles = new CHashIntMap<>();
    private final IntIntMap noblesRank = new HashIntIntMap();

    protected long _olympiadEnd;
    protected long _validationEnd;


    /**
     * The current period of the olympiad.<br>
     * <b>0 -</b> Competition period<br>
     * <b>1 -</b> Validation Period
     */
    protected int _period;
    protected long _nextWeeklyChange;
    protected int _currentCycle;
    private long _compEnd;
    private Calendar _compStart;
    protected static boolean _inCompPeriod;

    protected ScheduledFuture<?> _scheduledCompStart;
    protected ScheduledFuture<?> _scheduledCompEnd;
    protected ScheduledFuture<?> _scheduledOlympiadEnd;
    protected ScheduledFuture<?> _scheduledWeeklyTask;
    protected ScheduledFuture<?> _scheduledValdationTask;
    protected ScheduledFuture<?> _gameManager = null;
    protected ScheduledFuture<?> _gameAnnouncer = null;

    private Olympiad() {
        load();
        AntiFeedManager.getInstance().registerEvent(AntiFeedManager.OLYMPIAD_ID);

        if (_period == 0) {
            init();
        }
    }

    private void load() {
        nobles.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(OLYMPIAD_LOAD_DATA);
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                _currentCycle = rset.getInt("current_cycle");
                _period = rset.getInt("period");
                _olympiadEnd = rset.getLong("olympiad_end");
                _validationEnd = rset.getLong("validation_end");
                _nextWeeklyChange = rset.getLong("next_weekly_change");
            }
        } catch (Exception e) {
            LOGGER.warn("Error loading olympiad data from database: ", e);
        }

        switch (_period) {
            case 0 -> {
                if (_olympiadEnd == 0 || _olympiadEnd < System.currentTimeMillis()) {
                    setNewOlympiadEnd();
                } else {
                    scheduleWeeklyChange();
                }
            }
            case 1 -> {
                if (_validationEnd > Calendar.getInstance().getTimeInMillis()) {
                    loadNoblesRank();
                    _scheduledValdationTask = ThreadPool.schedule(new ValidationEndTask(), getMillisToValidationEnd());
                } else {
                    _currentCycle++;
                    _period = 0;
                    deleteNobles();
                    setNewOlympiadEnd();
                }
            }
            default -> {
                LOGGER.warn("Omg something went wrong in loading!! Period = {}", _period);
                return;
            }
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(OLYMPIAD_LOAD_NOBLES);
             ResultSet rset = statement.executeQuery()) {
            StatsSet statData;
            while (rset.next()) {
                statData = new StatsSet();
                statData.set(CLASS_ID, rset.getInt(CLASS_ID));
                statData.set(CHAR_NAME, rset.getString(CHAR_NAME));
                statData.set(POINTS, rset.getInt(POINTS));
                statData.set(COMP_DONE, rset.getInt(COMP_DONE));
                statData.set(COMP_WON, rset.getInt(COMP_WON));
                statData.set(COMP_LOST, rset.getInt(COMP_LOST));
                statData.set(COMP_DRAWN, rset.getInt(COMP_DRAWN));
                statData.set(COMP_DONE_WEEK, rset.getInt(COMP_DONE_WEEK));
                statData.set("to_save", false);

                addNobleStats(rset.getInt(CHAR_ID), statData);
            }
        } catch (Exception e) {
            LOGGER.warn("Error loading noblesse data from database: ", e);
        }

        synchronized (this) {
            LOGGER.info("Loading....");
            if (_period == 0) {
                LOGGER.info("Currently in Olympiad Period");
            } else {
                LOGGER.info("Currently in Validation Period");
            }

            long milliToEnd;
            if (_period == 0) {
                milliToEnd = getMillisToOlympiadEnd();
            } else {
                milliToEnd = getMillisToValidationEnd();
            }



            LOGGER.info("{} until period ends", TimeInterpreter.consolidate(milliToEnd, TimeUnit.MILLISECONDS, TimeUnit.MINUTES));

            if (_period == 0) {
                milliToEnd = getMillisToWeekChange();

                LOGGER.info("Next weekly change is in {}", TimeInterpreter.consolidate(milliToEnd, TimeUnit.MILLISECONDS, TimeUnit.MINUTES) );
            }
        }

        LOGGER.info("Loaded {} Nobles", nobles.size() );
    }

    private void setNewOlympiadEnd() {
        Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.ROUND_S1_OF_THE_OLYMPIAD_GAMES_HAS_STARTED).addInt(_currentCycle));

        Calendar currentTime = Calendar.getInstance();
        currentTime.set(Calendar.AM_PM, Calendar.AM);
        currentTime.set(Calendar.HOUR, 12);
        currentTime.set(Calendar.MINUTE, 0);
        currentTime.set(Calendar.SECOND, 0);

        Calendar nextChange = Calendar.getInstance();

        switch (Config.ALT_OLY_PERIOD) {
            case "DAY" -> {
                currentTime.add(Calendar.DAY_OF_MONTH, Config.ALT_OLY_PERIOD_MULTIPLIER);
                currentTime.add(Calendar.DAY_OF_MONTH, -1); // last day is for validation

                if (Config.ALT_OLY_PERIOD_MULTIPLIER >= 14) {
                    _nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
                } else if (Config.ALT_OLY_PERIOD_MULTIPLIER >= 7) {
                    _nextWeeklyChange = nextChange.getTimeInMillis() + (WEEKLY_PERIOD / 2);
                } else {
                    LOGGER.warn("Invalid config value for Config.ALT_OLY_PERIOD_MULTIPLIER, must be >= 7");
                }
            }
            case "WEEK" -> {
                currentTime.add(Calendar.WEEK_OF_MONTH, Config.ALT_OLY_PERIOD_MULTIPLIER);
                currentTime.add(Calendar.DAY_OF_MONTH, -1); // last day is for validation

                if (Config.ALT_OLY_PERIOD_MULTIPLIER > 1) {
                    _nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
                } else {
                    _nextWeeklyChange = nextChange.getTimeInMillis() + (WEEKLY_PERIOD / 2);
                }
            }
            case "MONTH" -> {
                currentTime.add(Calendar.MONTH, Config.ALT_OLY_PERIOD_MULTIPLIER);
                currentTime.add(Calendar.DAY_OF_MONTH, -1); // last day is for validation

                _nextWeeklyChange = nextChange.getTimeInMillis() + WEEKLY_PERIOD;
            }
        }
        _olympiadEnd = currentTime.getTimeInMillis();

        scheduleWeeklyChange();
    }

    public void loadNoblesRank() {
        noblesRank.clear();
        final Map<Integer, Integer> tmpPlace = new HashMap<>();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(GET_ALL_CLASSIFIED_NOBLESS);
             ResultSet rset = statement.executeQuery()) {
            int place = 1;
            while (rset.next()) {
                tmpPlace.put(rset.getInt(CHAR_ID), place++);
            }
        } catch (Exception e) {
            LOGGER.warn("Error loading noblesse data from database for Ranking: ", e);
        }

        int rank1 = (int) Math.round(tmpPlace.size() * 0.01);
        int rank2 = (int) Math.round(tmpPlace.size() * 0.10);
        int rank3 = (int) Math.round(tmpPlace.size() * 0.25);
        int rank4 = (int) Math.round(tmpPlace.size() * 0.50);
        if (rank1 == 0) {
            rank1 = 1;
            rank2++;
            rank3++;
            rank4++;
        }
        for (Entry<Integer, Integer> chr : tmpPlace.entrySet()) {
            if (chr.getValue() <= rank1) {
                noblesRank.put(chr.getKey(), 1);
            } else if (tmpPlace.get(chr.getKey()) <= rank2) {
                noblesRank.put(chr.getKey(), 2);
            } else if (tmpPlace.get(chr.getKey()) <= rank3) {
                noblesRank.put(chr.getKey(), 3);
            } else if (tmpPlace.get(chr.getKey()) <= rank4) {
                noblesRank.put(chr.getKey(), 4);
            } else {
                noblesRank.put(chr.getKey(), 5);
            }
        }

        // Store remaining hero reward points to player variables.

        nobles.keySet().forEach(noblesId -> {
            final int points = getOlympiadTradePoint(noblesId);
            if (points > 0)
            {
                final Player player = World.getInstance().findPlayer(noblesId);
                if (player != null)
                {
                    player.getVariables().set(UNCLAIMED_OLYMPIAD_POINTS_VAR, points);
                }
                else
                {
                    // Remove previous record.
                    try (Connection con = DatabaseFactory.getInstance().getConnection();
                         PreparedStatement statement = con.prepareStatement(REMOVE_UNCLAIMED_POINTS))
                    {
                        statement.setInt(1, noblesId);
                        statement.setString(2, UNCLAIMED_OLYMPIAD_POINTS_VAR);
                        statement.execute();
                    }
                    catch (SQLException e)
                    {
                        LOGGER.warn("Couldn't remove unclaimed olympiad points from DB!");
                    }
                    // Add new value.
                    try (Connection con = DatabaseFactory.getInstance().getConnection();
                         PreparedStatement statement = con.prepareStatement(INSERT_UNCLAIMED_POINTS))
                    {
                        statement.setInt(1, noblesId);
                        statement.setString(2, UNCLAIMED_OLYMPIAD_POINTS_VAR);
                        statement.setString(3, String.valueOf(points));
                        statement.execute();
                    }
                    catch (SQLException e)
                    {
                        LOGGER.warn("Couldn't store unclaimed olympiad points to DB!");
                    }
                }
            }
        });
    }

    protected void init() {
        if (_period == 1) {
            return;
        }

        setNewCompBegin();

        if (_scheduledOlympiadEnd != null) {
            _scheduledOlympiadEnd.cancel(true);
        }

        _scheduledOlympiadEnd = ThreadPool.schedule(new OlympiadEndTask(), getMillisToOlympiadEnd());

        updateCompStatus();
    }

	public StatsSet getNobleStats(int playerId)
	{
		return nobles.get(playerId);
	}

    private void updateCompStatus() {
        // _compStarted = false;

        synchronized (this) {
            final long milliToStart = getMillisToCompBegin();

            final double numSecs = (milliToStart / 1000d) % 60;
            double countDown = ((milliToStart / 1000.) - numSecs) / 60;
            final int numMins = (int) Math.floor(countDown % 60);
            countDown = (countDown - numMins) / 60;
            final int numHours = (int) Math.floor(countDown % 24);
            final int numDays = (int) Math.floor((countDown - numHours) / 24);

            LOGGER.info("Competition Period Starts in {} days, {} hours and {} minustes.", numDays, numHours, numMins);

            LOGGER.info("Event starts/started: {}", _compStart.getTime());
        }

        _scheduledCompStart = ThreadPool.schedule(() ->
        {
            if (isOlympiadEnd()) {
                return;
            }

            _inCompPeriod = true;

            Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_OLYMPIAD_MANAGER_BATTLES_IN_THE_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE));
            LOGGER.info("Olympiad Games have started.");
            LOGGER_OLYMPIAD.info("Result,Player1,Player2,Player1 HP,Player2 HP,Player1 Damage,Player2 Damage,Points,Classed");

            _gameManager = ThreadPool.scheduleAtFixedRate(OlympiadGameManager.getInstance(), 30000, 30000);
            if (Config.ALT_OLY_ANNOUNCE_GAMES) {
                _gameAnnouncer = ThreadPool.scheduleAtFixedRate(new OlympiadAnnouncer(), 30000, 500);
            }

            final long regEnd = getMillisToCompEnd() - 600000;
            if (regEnd > 0) {
                ThreadPool.schedule(() -> Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_OLYMPIAD_REGISTRATION_PERIOD_HAS_ENDED)), regEnd);
            }

            _scheduledCompEnd = ThreadPool.schedule(() ->
            {
                if (isOlympiadEnd()) {
                    return;
                }
                _inCompPeriod = false;
                Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.MUCH_CARNAGE_HAS_BEEN_LEFT_FOR_THE_CLEANUP_CREW_OF_THE_OLYMPIAD_STADIUM_BATTLES_IN_THE_OLYMPIAD_GAMES_ARE_NOW_OVER));
                LOGGER.info("Olympiad games have ended.");

                while (OlympiadGameManager.getInstance().isBattleStarted()) // cleared in game manager
                {
                    try {
                        // wait 1 minutes for end of pendings games
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                    }
                }

                if (_gameManager != null) {
                    _gameManager.cancel(false);
                    _gameManager = null;
                }

                if (_gameAnnouncer != null) {
                    _gameAnnouncer.cancel(false);
                    _gameAnnouncer = null;
                }

                saveOlympiadStatus();

                init();
            }, getMillisToCompEnd());
        }, getMillisToCompBegin());
    }

    private long getMillisToOlympiadEnd() {
        return _olympiadEnd - Calendar.getInstance().getTimeInMillis();
    }

    public void manualSelectHeroes() {
        if (_scheduledOlympiadEnd != null) {
            _scheduledOlympiadEnd.cancel(true);
        }

        _scheduledOlympiadEnd = ThreadPool.schedule(new OlympiadEndTask(), 0);
    }

    protected long getMillisToValidationEnd() {
        if (_validationEnd > Calendar.getInstance().getTimeInMillis()) {
            return _validationEnd - Calendar.getInstance().getTimeInMillis();
        }
        return 10;
    }

    public boolean isOlympiadEnd() {
        return _period != 0;
    }



    public boolean inCompPeriod() {
        return _inCompPeriod;
    }

    private long getMillisToCompBegin() {
        if ((_compStart.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) && (_compEnd > Calendar.getInstance().getTimeInMillis())) {
            return 10;
        }

        if (_compStart.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        }

        return setNewCompBegin();
    }

    private long setNewCompBegin() {
        _compStart = Calendar.getInstance();
        var currentHour = _compStart.get(Calendar.HOUR_OF_DAY);

        if(currentHour > COMP_START || (currentHour == COMP_START && _compStart.get(Calendar.MINUTE) >= COMP_MIN)) {
            _compStart.add(Calendar.DAY_OF_MONTH, 1);
        }

        var currentDay = _compStart.get(Calendar.DAY_OF_WEEK);
        var dayCounter = 0;
        for (var i = currentDay; dayCounter < 8; i++, dayCounter++) {
            if(Config.ALT_OLY_COMPETITION_DAYS.contains(i)) {
                break;
            }
            if(i == Calendar.SATURDAY)  {
                i = 0;
            }
        }
        if (dayCounter > 0) {
            _compStart.add(Calendar.DAY_OF_MONTH, dayCounter);
        }
        _compStart.set(Calendar.HOUR_OF_DAY, COMP_START);
        _compStart.set(Calendar.MINUTE, COMP_MIN);
        _compStart.add(Calendar.HOUR_OF_DAY, 24);
        _compEnd = _compStart.getTimeInMillis() + COMP_PERIOD;

        LOGGER.info("New Schedule @{}", _compStart.getTime());

        return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

    protected long getMillisToCompEnd() {
        return _compEnd - Calendar.getInstance().getTimeInMillis();
    }

    private long getMillisToWeekChange() {
        if (_nextWeeklyChange > Calendar.getInstance().getTimeInMillis()) {
            return _nextWeeklyChange - Calendar.getInstance().getTimeInMillis();
        }
        return 10;
    }

    private void scheduleWeeklyChange() {
        _scheduledWeeklyTask = ThreadPool.scheduleAtFixedRate(() ->
        {
            addWeeklyPoints();
            LOGGER.info("Added weekly points to nobles");
            resetWeeklyMatches();
            LOGGER.info("Reset weekly matches to nobles");

            _nextWeeklyChange = Calendar.getInstance().getTimeInMillis() + WEEKLY_PERIOD;
        }, getMillisToWeekChange(), WEEKLY_PERIOD);
    }


    protected synchronized void addWeeklyPoints() {
        if (_period == 1) {
            return;
        }

        int currentPoints;
        for (StatsSet nobleInfo : nobles.values()) {
            currentPoints = nobleInfo.getInt(POINTS);
            currentPoints += WEEKLY_POINTS;
            nobleInfo.set(POINTS, currentPoints);
        }
    }

    /**
     * Resets number of matches, classed matches, non classed matches, team matches done by noble characters in the week.
     */
    protected synchronized void resetWeeklyMatches() {
        if (_period == 1) {
            return;
        }

        for (StatsSet nobleInfo : nobles.values()) {
            nobleInfo.set(COMP_DONE_WEEK, 0);
        }
    }

    public int getCurrentCycle() {
        return _currentCycle;
    }

    public int getPeriod() {
        return _period;
    }

    /**
     * Save noblesse data to database
     */
    protected synchronized void saveNobleData() {
		if (nobles.isEmpty())
		{
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            for (var entry : nobles.entrySet()) {
                final StatsSet nobleInfo = entry.getValue();

                if (nobleInfo == null) {
                    continue;
                }

                final int charId = entry.getKey();
                final int classId = nobleInfo.getInt(CLASS_ID);
                final int points = nobleInfo.getInt(POINTS);
                final int compDone = nobleInfo.getInt(COMP_DONE);
                final int compWon = nobleInfo.getInt(COMP_WON);
                final int compLost = nobleInfo.getInt(COMP_LOST);
                final int compDrawn = nobleInfo.getInt(COMP_DRAWN);
                final int compDoneWeek = nobleInfo.getInt(COMP_DONE_WEEK);
                final boolean toSave = nobleInfo.getBoolean("to_save");

                try (PreparedStatement statement = con.prepareStatement(toSave ? OLYMPIAD_SAVE_NOBLES : OLYMPIAD_UPDATE_NOBLES)) {
                    if (toSave) {
                        statement.setInt(1, charId);
                        statement.setInt(2, classId);
                        statement.setInt(3, points);
                        statement.setInt(4, compDone);
                        statement.setInt(5, compWon);
                        statement.setInt(6, compLost);
                        statement.setInt(7, compDrawn);
                        statement.setInt(8, compDoneWeek);

                        nobleInfo.set("to_save", false);
                    } else {
                        statement.setInt(1, points);
                        statement.setInt(2, compDone);
                        statement.setInt(3, compWon);
                        statement.setInt(4, compLost);
                        statement.setInt(5, compDrawn);
                        statement.setInt(6, compDoneWeek);
						statement.setInt(7, charId);
                    }
                    statement.execute();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to save noblesse data to database: ", e);
        }
    }

    /**
     * Save olympiad.properties file with current olympiad status and update noblesse table in database
     */
    public void saveOlympiadStatus() {
        saveNobleData();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(OLYMPIAD_SAVE_DATA)) {
            statement.setInt(1, _currentCycle);
            statement.setInt(2, _period);
            statement.setLong(3, _olympiadEnd);
            statement.setLong(4, _validationEnd);
            statement.setLong(5, _nextWeeklyChange);
            statement.setInt(6, _currentCycle);
            statement.setInt(7, _period);
            statement.setLong(8, _olympiadEnd);
            statement.setLong(9, _validationEnd);
            statement.setLong(10, _nextWeeklyChange);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Failed to save olympiad data to database: ", e);
        }
    }

    protected void updateMonthlyData() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps1 = con.prepareStatement(OLYMPIAD_MONTH_CLEAR);
             PreparedStatement ps2 = con.prepareStatement(OLYMPIAD_MONTH_CREATE)) {
            ps1.execute();
            ps2.execute();
        } catch (SQLException e) {
            LOGGER.error("Failed to update monthly noblese data: ", e);
        }
    }

    protected List<StatsSet> sortHerosToBe() {
        if (_period != 1) {
            return Collections.emptyList();
        }

        LOGGER_OLYMPIAD.info("Noble,charid,classid,compDone,points");
        StatsSet nobleInfo;
        for (var entry : nobles.entrySet()) {
            nobleInfo = entry.getValue();
            if (nobleInfo == null) {
                continue;
            }

            final int charId = entry.getKey();
            final int classId = nobleInfo.getInt(CLASS_ID);
            final String charName = nobleInfo.getString(CHAR_NAME);
            final int points = nobleInfo.getInt(POINTS);
            final int compDone = nobleInfo.getInt(COMP_DONE);

            LOGGER_OLYMPIAD.info(charName + "," + charId + "," + classId + "," + compDone + "," + points);
        }

        final List<StatsSet> heroesToBe = new LinkedList<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(OLYMPIAD_GET_HEROS)) {
            StatsSet hero;
            var it = HERO_IDS.iterator();
            while (it.hasNext()) {
                var element = it.nextInt();
                // Classic can have 2nd and 3rd class competitors, but only 1 hero
                ClassId parent = ClassListData.getInstance().getClass(element).getParentClassId();
                statement.setInt(1, element);
                statement.setInt(2, parent.getId());

                try (ResultSet rset = statement.executeQuery()) {
                    if (rset.next()) {
                        hero = new StatsSet();
                        hero.set(CLASS_ID, element); // save the 3rd class title
                        hero.set(CHAR_ID, rset.getInt(CHAR_ID));
                        hero.set(CHAR_NAME, rset.getString(CHAR_NAME));

                        LOGGER_OLYMPIAD.info("Hero " + hero.getString(CHAR_NAME) + "," + hero.getInt(CHAR_ID) + "," + hero.getInt(CLASS_ID));
                        heroesToBe.add(hero);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.warn("Couldnt load heros from DB");
        }

        return heroesToBe;
    }

    public List<String> getClassLeaderBoard(int classId) {
        final List<String> names = new ArrayList<>();
        final String query = Config.ALT_OLY_SHOW_MONTHLY_WINNERS ? ((classId == 132) ? GET_EACH_CLASS_LEADER_SOULHOUND : GET_EACH_CLASS_LEADER) : ((classId == 132) ? GET_EACH_CLASS_LEADER_CURRENT_SOULHOUND : GET_EACH_CLASS_LEADER_CURRENT);
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, classId);
            try (ResultSet rset = ps.executeQuery()) {
                while (rset.next()) {
                    names.add(rset.getString(CHAR_NAME));
                }
            }
        } catch (SQLException e) {
            LOGGER.warn("Couldn't load olympiad leaders from DB!");
        }
        return names;
    }

    private int getOlympiadTradePoint(int objectId) {
        if ((_period != 1) || noblesRank.isEmpty())
        {
            return 0;
        }

        if (!noblesRank.containsKey(objectId))
        {
            return 0;
        }

        final StatsSet noble = nobles.get(objectId);
        if ((noble == null) || (noble.getInt(POINTS) == 0))
        {
            return 0;
        }

        // Hero point bonus
        int points = Hero.getInstance().isHero(objectId) || Hero.getInstance().isUnclaimedHero(objectId) ? Config.ALT_OLY_HERO_POINTS : 0;
        // Rank point bonus
        switch (noblesRank.get(objectId))
        {
            case 1:
            {
                points += Config.ALT_OLY_RANK1_POINTS;
                break;
            }
            case 2:
            {
                points += Config.ALT_OLY_RANK2_POINTS;
                break;
            }
            case 3:
            {
                points += Config.ALT_OLY_RANK3_POINTS;
                break;
            }
            case 4:
            {
                points += Config.ALT_OLY_RANK4_POINTS;
                break;
            }
            default:
            {
                points += Config.ALT_OLY_RANK5_POINTS;
            }
        }

        // Win/no win matches point bonus
        points += getCompetitionWon(objectId) > 0 ? 10 : 5;

        // This is a one time calculation.
        noble.set(POINTS, 0);

        return points;
    }

    public int getNoblePoints(Player player) {
        if (!nobles.containsKey(player.getObjectId())) {
            final StatsSet statDat = new StatsSet();
            statDat.set(CLASS_ID, player.getBaseClass());
            statDat.set(CHAR_NAME, player.getName());
            statDat.set(POINTS, DEFAULT_POINTS);
            statDat.set(COMP_DONE, 0);
            statDat.set(COMP_WON, 0);
            statDat.set(COMP_LOST, 0);
            statDat.set(COMP_DRAWN, 0);
            statDat.set(COMP_DONE_WEEK, 0);
            statDat.set("to_save", true);
            addNobleStats(player.getObjectId(), statDat);
        }
        return nobles.get(player.getObjectId()).getInt(POINTS);
    }

    public int getLastNobleOlympiadPoints(int objId) {
        int result = 0;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT olympiad_points FROM olympiad_nobles_eom WHERE charId = ?")) {
            ps.setInt(1, objId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.first()) {
                    result = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not load last olympiad points:", e);
        }
        return result;
    }

    public int getCompetitionDone(int objId) {
        if ((nobles == null) || !nobles.containsKey(objId)) {
            return 0;
        }
        return nobles.get(objId).getInt(COMP_DONE);
    }

    public int getCompetitionWon(int objId) {
        if ((nobles == null) || !nobles.containsKey(objId)) {
            return 0;
        }
        return nobles.get(objId).getInt(COMP_WON);
    }

    public int getCompetitionLost(int objId) {
        if ((nobles == null) || !nobles.containsKey(objId)) {
            return 0;
        }
        return nobles.get(objId).getInt(COMP_LOST);
    }

    /**
     * Gets how many matches a noble character did in the week
     *
     * @param objId id of a noble character
     * @return number of weekly competitions done
     */
    public int getCompetitionDoneWeek(int objId) {
        if ((nobles == null) || !nobles.containsKey(objId)) {
            return 0;
        }
        return nobles.get(objId).getInt(COMP_DONE_WEEK);
    }

    /**
     * Number of remaining matches a noble character can join in the week
     *
     * @param objId id of a noble character
     * @return difference between maximum allowed weekly matches and currently done weekly matches.
     */
    public int getRemainingWeeklyMatches(int objId) {
        return Math.max(Config.ALT_OLY_MAX_WEEKLY_MATCHES - getCompetitionDoneWeek(objId), 0);
    }

	protected void deleteNobles()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(OLYMPIAD_DELETE_ALL)) {
            statement.execute();
        } catch (SQLException e) {
            LOGGER.warn("Couldn't delete nobles from DB!");
        }
        nobles.clear();
    }

    /**
     * @param charId the noble object Id.
     * @param data the stats set data to add.
     * @return the old stats set if the noble is already present, null otherwise.
     */
    public StatsSet addNobleStats(int charId, StatsSet data)
    {
        return nobles.put(charId, data);
    }

    public static Olympiad getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final Olympiad INSTANCE = new Olympiad();
    }

    protected class OlympiadEndTask implements Runnable {
        @Override
        public void run()
        {
            final SystemMessage sm = getSystemMessage(SystemMessageId.ROUND_S1_OF_THE_OLYMPIAD_GAMES_HAS_NOW_ENDED);
            sm.addInt(_currentCycle);

            Broadcast.toAllOnlinePlayers(sm);

            if (_scheduledWeeklyTask != null)
            {
                _scheduledWeeklyTask.cancel(true);
            }

            saveNobleData();

            _period = 1;
            final List<StatsSet> heroesToBe = sortHerosToBe();
            Hero.getInstance().resetData();
            Hero.getInstance().computeNewHeroes(heroesToBe);

            saveOlympiadStatus();
            updateMonthlyData();

            final Calendar validationEnd = Calendar.getInstance();
            _validationEnd = validationEnd.getTimeInMillis() + VALIDATION_PERIOD;

            loadNoblesRank();
            _scheduledValdationTask = ThreadPool.schedule(new ValidationEndTask(), getMillisToValidationEnd());
        }
    }

    protected class ValidationEndTask implements Runnable
    {
        @Override
        public void run()
        {
            Broadcast.toAllOnlinePlayers("Olympiad Validation Period has ended");
            _period = 0;
            _currentCycle++;
            deleteNobles();
            setNewOlympiadEnd();
            init();
        }
    }

}
