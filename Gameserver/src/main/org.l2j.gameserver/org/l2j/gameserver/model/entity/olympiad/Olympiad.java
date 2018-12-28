package org.l2j.gameserver.model.entity.olympiad;

import org.l2j.commons.configuration.ExProperties;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.dao.OlympiadParticipantsDAO;
import org.l2j.gameserver.instancemanager.OlympiadHistoryManager;
import org.l2j.gameserver.instancemanager.ServerVariables;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.ObservePoint;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.ClassLevel;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.entity.events.impl.SingleMatchEvent;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.utils.MultiValueIntegerMap;
import org.napile.pair.primitive.IntObjectPair;
import org.napile.primitive.maps.IntIntMap;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntIntMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import static org.l2j.commons.util.Util.isNullOrEmpty;

public class Olympiad
{
	private static final Logger _log = LoggerFactory.getLogger(Olympiad.class);

	private static final IntObjectMap<OlympiadParticipiantData> _participants = new CHashIntObjectMap<OlympiadParticipiantData>();

	public static final IntIntMap _participantRank = new CHashIntIntMap();

	public static List<Integer> _nonClassBasedRegisters = new CopyOnWriteArrayList<Integer>();
	public static MultiValueIntegerMap _classBasedRegisters = new MultiValueIntegerMap();
	public static HashMap<Integer, String> _playersHWID = new HashMap<Integer, String>();

	//public static final int DEFAULT_POINTS = 50;
	//private static final int WEEKLY_POINTS = 10;

	public static final String OLYMPIAD_HTML_PATH = "olympiad/";

	public static final int[][] BUFFS_LIST =
	{
		{ 4357, 2 },
		{ 4355, 3 },
		{ 4342, 2 },
		{ 4345, 3 },
		{ 4344, 3 },
		{ 4349, 2 },
		{ 4347, 4 },
		{ 4348, 4 },
		{ 4352, 2 }
	};

	private static long _olympiadPeriodStartTime;
	private static long _validationStartTime;
	private static long _weekStartTime;
	public static int _period;
	public static int _currentCycle;
	private static long _compEnd;
	private static final Calendar _compStart = Calendar.getInstance();
	public static boolean _inCompPeriod;
	public static boolean _isOlympiadEnd;

	private static ScheduledFuture<?> _scheduledOlympiadEnd;
	public static ScheduledFuture<?> _scheduledManagerTask;
	public static ScheduledFuture<?> _scheduledWeeklyTask;
	public static ScheduledFuture<?> _scheduledValdationTask;
	public static ScheduledFuture<?> _scheduledCompStartTask;
	public static ScheduledFuture<?> _scheduledCompEndTask;

	public static final Stadia[] STADIUMS = new Stadia[Config.OLYMPIAD_STADIAS_COUNT];

	public static OlympiadManager _manager;
	private static List<NpcInstance> _npcs = new ArrayList<NpcInstance>();

	public static void load()
	{
		_participants.clear();

		ExProperties olympiadProperties = Config.load(Config.OLYMPIAD);

		_currentCycle = ServerVariables.getInt("Olympiad_CurrentCycle", olympiadProperties.getProperty("CurrentCycle", 1));
		_period = ServerVariables.getInt("Olympiad_Period", olympiadProperties.getProperty("Period", 0));
		_olympiadPeriodStartTime = ServerVariables.getLong("olympiad_period_start_time", olympiadProperties.getProperty("period_start_time", System.currentTimeMillis()));
		_validationStartTime = ServerVariables.getLong("olympiad_validation_start_time", olympiadProperties.getProperty("validation_start_time", getOlympiadPeriodEndTime()));
		_weekStartTime = ServerVariables.getLong("olympiad_week_start_time", olympiadProperties.getProperty("week_start_time", 0L));

		initStadiums();

		OlympiadHistoryManager.getInstance();
		OlympiadParticipantsDAO.getInstance().select();
		OlympiadDatabase.loadParticipantsRank();

		switch(_period)
		{
			case 0:
				if(getOlympiadPeriodEndTime() < System.currentTimeMillis())
					OlympiadDatabase.setNewOlympiadStartTime();
				else
					_isOlympiadEnd = false;
				break;
			case 1:
				_isOlympiadEnd = true;
				_scheduledValdationTask = ThreadPoolManager.getInstance().schedule(new ValidationTask(), getMillisToValidationEnd());
				break;
			default:
				_log.warn("Olympiad System: Omg something went wrong in loading!! Period = " + _period);
				return;
		}

		_log.info("Olympiad System: Loading Olympiad System....");
		if(_period == 0)
			_log.info("Olympiad System: Currently in Olympiad Period");
		else
			_log.info("Olympiad System: Currently in Validation Period");

		_log.info("Olympiad System: Period Ends....");

		long milliToEnd;
		if(_period == 0)
			milliToEnd = getMillisToOlympiadEnd();
		else
			milliToEnd = getMillisToValidationEnd();

		double numSecs = milliToEnd / 1000 % 60;
		double countDown = (milliToEnd / 1000 - numSecs) / 60;
		int numMins = (int) Math.floor(countDown % 60);
		countDown = (countDown - numMins) / 60;
		int numHours = (int) Math.floor(countDown % 24);
		int numDays = (int) Math.floor((countDown - numHours) / 24);

		_log.info("Olympiad System: In " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");

		if(_period == 0)
		{
			_log.info("Olympiad System: Next Weekly Change is in....");

			milliToEnd = getMillisToWeekChange();

			double numSecs2 = milliToEnd / 1000 % 60;
			double countDown2 = (milliToEnd / 1000 - numSecs2) / 60;
			int numMins2 = (int) Math.floor(countDown2 % 60);
			countDown2 = (countDown2 - numMins2) / 60;
			int numHours2 = (int) Math.floor(countDown2 % 24);
			int numDays2 = (int) Math.floor((countDown2 - numHours2) / 24);

			_log.info("Olympiad System: In " + numDays2 + " days, " + numHours2 + " hours and " + numMins2 + " mins.");
		}

		_log.info("Olympiad System: Loaded " + _participants.size() + " participants.");

		if(_period == 0)
			init();
	}

	private static void initStadiums()
	{
		for(int i = 0; i < STADIUMS.length; i++)
			if(STADIUMS[i] == null)
				STADIUMS[i] = new Stadia();
	}

	public static void init()
	{
		if(isValidationPeriod())
			return;

		_compStart.setTimeInMillis(Config.OLYMPIAD_START_TIME.next(System.currentTimeMillis() - Config.ALT_OLY_CPERIOD));
		_compEnd = _compStart.getTimeInMillis() + Config.ALT_OLY_CPERIOD;
		if(_compEnd < System.currentTimeMillis())
		{
			_compStart.setTimeInMillis(Config.OLYMPIAD_START_TIME.next(System.currentTimeMillis()));
			_compEnd = _compStart.getTimeInMillis() + Config.ALT_OLY_CPERIOD;
		}

		startOlympiadEndTask(getMillisToOlympiadEnd());

		updateCompStatus();

		if(_scheduledWeeklyTask != null)
			_scheduledWeeklyTask.cancel(false);
		_scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WeeklyTask(), getMillisToWeekChange(), Config.ALT_OLY_WPERIOD);
	}

	public static void startOlympiadEndTask(long delay)
	{
		if(_scheduledOlympiadEnd != null)
			_scheduledOlympiadEnd.cancel(false);
		_scheduledOlympiadEnd = ThreadPoolManager.getInstance().schedule(new OlympiadEndTask(), delay);
	}

	public static void startCompStartTask(long delay)
	{
		if(_scheduledCompStartTask != null)
			_scheduledCompStartTask.cancel(false);
		_scheduledCompStartTask = ThreadPoolManager.getInstance().schedule(new CompStartTask(), delay);
	}

	public static void startCompEndTask(long delay)
	{
		if(_scheduledCompEndTask != null)
			_scheduledCompEndTask.cancel(false);
		_scheduledCompEndTask = ThreadPoolManager.getInstance().schedule(new CompEndTask(), delay);
	}

	public static int getCompWeek()
	{
		return _compStart.get(4);
	}

	public static boolean isClassedBattlesAllowed()
	{
		return Config.CLASSED_GAMES_ENABLED && getCompWeek() == 4;
	}

	public static boolean isRegistrationActive()
	{
		if(!_inCompPeriod || _isOlympiadEnd)
			return false;
		if(getMillisToOlympiadEnd() <= 600000)
			return false;
		if(getMillisToCompEnd() <= Config.OLYMPIAD_REGISTRATION_DELAY)
			return false;
		return true;
	}

	public static synchronized boolean registerParticipant(Player player, CompType type)
	{
		if(!isRegistrationActive())
		{
			player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}

		if(!validPlayer(player, player, type, false))
			return false;

		if(getParticipantPoints(player.getObjectId()) == 0)
			return false;

		if(player.getOlympiadGame() != null)
		{
			//
			return false;
		}

		switch(type)
		{
			case CLASSED:
			{
				if(isClassedBattlesAllowed())
				{
					_classBasedRegisters.put(ClassId.VALUES[getParticipantClass(player.getObjectId())].getType2().ordinal(), player.getObjectId());
					if(Config.ALT_OLY_BY_SAME_BOX_NUMBER > 0)
						_playersHWID.put(player.getObjectId(), player.getNetConnection().getHWID()); //put noble ID and HWID
					player.sendPacket(SystemMsg.YOU_HAVE_BEEN_REGISTERED_FOR_THE_GRAND_OLYMPIAD_WAITING_LIST_FOR_A_CLASS_SPECIFIC_MATCH);
				}
				break;
			}
			case NON_CLASSED:
			{
				_nonClassBasedRegisters.add(player.getObjectId());
				if(Config.ALT_OLY_BY_SAME_BOX_NUMBER > 0)
					_playersHWID.put(player.getObjectId(), player.getNetConnection().getHWID()); //put noble ID and HWID
				player.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_REGISTERED_FOR_A_1V1_CLASS_IRRELEVANT_MATCH);
				break;
			}
		}

		return true;
	}

	public static boolean validPlayer(Player sendPlayer, Player validPlayer, CompType type, boolean gameValidation)
	{
		if(validPlayer.getLevel() < Config.OLYMPIAD_MIN_LEVEL)
			return false;

		if(validPlayer.getClassId().getClassLevel().ordinal() < ClassLevel.SECOND.ordinal())
			return false;

		if(!validPlayer.isBaseClassActive())
		{
			sendPlayer.sendPacket(new SystemMessagePacket(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD).addName(validPlayer));
			return false;
		}

		if(validPlayer.isFishing())
		{
			if(validPlayer == sendPlayer)
				sendPlayer.sendPacket(SystemMsg.YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_WHILE_FISHING);
			return false;
		}

		if(validPlayer.isInTrainingCamp())
		{
			sendPlayer.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_CURRENTLY_IN_THE_ROYAL_TRAINING_CAMP_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD).addName(validPlayer));
			return false;
		}

		if(validPlayer.containsEvent(SingleMatchEvent.class))
		{
			if(validPlayer == sendPlayer)
				sendPlayer.sendPacket(SystemMsg.YOU_CANNOT_BE_SIMULTANEOUSLY_REGISTERED_FOR_PVP_MATCHES_SUCH_AS_THE_OLYMPIAD_UNDERGROUND_COLISEUM_AERIAL_CLEFT_KRATEIS_CUBE_AND_HANDYS_BLOCK_CHECKERS);
			return false;
		}

		if(validPlayer.isRegisteredInEvent())
		{
			sendPlayer.sendMessage(new CustomMessage("org.l2j.gameserver.model.entity.Olympiad.isRegisteredInEvent"));
			return false;
		}

		addParticipant(validPlayer);

		if(!gameValidation)
		{
			int[] ar = getWeekGameCounts(validPlayer.getObjectId());

			switch(type)
			{
				case CLASSED:
					if(_classBasedRegisters.containsValue(validPlayer.getObjectId()))
					{
						sendPlayer.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST).addName(validPlayer));
						return false;
					}

					if(ar[1] == 0)
					{
						validPlayer.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
						return false;
					}
					break;
				case NON_CLASSED:
					if(_nonClassBasedRegisters.contains(validPlayer.getObjectId()))
					{
						sendPlayer.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_CLASS_IRRELEVANT_INDIVIDUAL_MATCH).addName(validPlayer));
						return false;
					}
					if(ar[2] == 0)
					{
						validPlayer.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
						return false;
					}
					break;
			}

			if(ar[0] == 0)
			{
				validPlayer.sendPacket(SystemMsg.THE_MAXIMUM_MATCHES_YOU_CAN_PARTICIPATE_IN_1_WEEK_IS_70);
				return false;
			}

			if(isRegisteredInComp(validPlayer))
			{
				sendPlayer.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST).addName(validPlayer));
				return false;
			}
		}

		return true;
	}

	public static synchronized void logoutPlayer(Player player)
	{
		_classBasedRegisters.removeValue(player.getObjectId());
		_nonClassBasedRegisters.remove((Integer) player.getObjectId());
		_playersHWID.remove(player.getObjectId()); //obj id? remove by key

		OlympiadGame game = player.getOlympiadGame();
		if(game != null)
		{
			try
			{
				game.logoutPlayer(player);
				if(!game.validated)
					game.endGame(20000, true);
			}
			catch(Exception e)
			{
				_log.error("", e);
			}
		}
	}

	public static synchronized boolean unregisterParticipant(Player player)
	{
		if(!_inCompPeriod || _isOlympiadEnd)
		{
			player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}

		if(!isRegistered(player, true))
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_REGISTERED_FOR_THE_GRAND_OLYMPIAD);
			return false;
		}

		OlympiadGame game = player.getOlympiadGame();
		if(game != null)
		{
			if(game.getStatus() == BattleStatus.Begin_Countdown)
			{
				// TODO: System Message
				//TODO [VISTALL] узнать ли прерывается бой и если так ли это та мессага SystemMsg.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED
				player.sendMessage("Now you can't cancel participation in the Grand Olympiad.");
				return false;
			}

			try
			{
				game.logoutPlayer(player);
				if(!game.validated)
					game.endGame(20000, true);
			}
			catch(Exception e)
			{
				_log.error("", e);
			}
		}
		_classBasedRegisters.removeValue(player.getObjectId());
		_nonClassBasedRegisters.remove(player.getObjectId());
		_playersHWID.remove(player.getObjectId()); //obj id? remove by key

		player.sendPacket(SystemMsg.YOU_HAVE_BEEN_REMOVED_FROM_THE_GRAND_OLYMPIAD_WAITING_LIST);

		return true;
	}

	private static synchronized void updateCompStatus()
	{
		long milliToStart = getMillisToCompBegin();
		double numSecs = milliToStart / 1000 % 60;
		double countDown = (milliToStart / 1000 - numSecs) / 60;
		int numMins = (int) Math.floor(countDown % 60);
		countDown = (countDown - numMins) / 60;
		int numHours = (int) Math.floor(countDown % 24);
		int numDays = (int) Math.floor((countDown - numHours) / 24);

		_log.info("Olympiad System: Competition Period Starts in " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
		_log.info("Olympiad System: Event starts/started: " + _compStart.getTime());

		startCompStartTask(getMillisToCompBegin());
	}

	public static void setOlympiadPeriodStartTime(long value)
	{
		_olympiadPeriodStartTime = value;
	}

	public static long getOlympiadPeriodStartTime()
	{
		return _olympiadPeriodStartTime;
	}

	public static long getOlympiadPeriodEndTime()
	{
		return Config.OLYMIAD_END_PERIOD_TIME.next(_olympiadPeriodStartTime);
	}

	private static long getMillisToOlympiadEnd()
	{
		return Math.max(10L, getOlympiadPeriodEndTime() - System.currentTimeMillis());
	}

	public static void setValidationStartTime(long value)
	{
		_validationStartTime = value;
	}

	public static long getValidationStartTime()
	{
		return _validationStartTime;
	}

	public static long getValidationEndTime()
	{
		return _validationStartTime + Config.ALT_OLY_VPERIOD;
	}

	static long getMillisToValidationEnd()
	{
		return Math.max(10L, getValidationEndTime() - System.currentTimeMillis());
	}

	public static boolean isValidationPeriod()
	{
		return _period == 1;
	}

	public static boolean isOlympiadEnd()
	{
		return _isOlympiadEnd;
	}

	public static boolean inCompPeriod()
	{
		return _inCompPeriod;
	}

	private static long getMillisToCompBegin()
	{
		if(_compStart.getTimeInMillis() < System.currentTimeMillis())
		{
			if(_compEnd > System.currentTimeMillis())
				return 10L;

			return setNewCompBegin();
		}

		return _compStart.getTimeInMillis() - System.currentTimeMillis();
	}

	private static long setNewCompBegin()
	{
		_compStart.setTimeInMillis(Config.OLYMPIAD_START_TIME.next(System.currentTimeMillis() - Config.ALT_OLY_CPERIOD));
		_compEnd = _compStart.getTimeInMillis() + Config.ALT_OLY_CPERIOD;
		if(_compEnd < System.currentTimeMillis())
		{
			_compStart.setTimeInMillis(Config.OLYMPIAD_START_TIME.next(System.currentTimeMillis()));
			_compEnd = _compStart.getTimeInMillis() + Config.ALT_OLY_CPERIOD;
		}

		_log.info("Olympiad System: New Schedule @ " + Olympiad._compStart.getTime());

		return _compStart.getTimeInMillis() - System.currentTimeMillis();
	}

	public static long getMillisToCompEnd()
	{
		return _compEnd - System.currentTimeMillis();
	}

	public static void setWeekStartTime(long value)
	{
		_weekStartTime = value;
	}

	public static long getWeekStartTime()
	{
		return _weekStartTime;
	}

	public static long getWeekEndTime()
	{
		return _weekStartTime + Config.ALT_OLY_WPERIOD;
	}

	private static long getMillisToWeekChange()
	{
		return Math.max(10L, getWeekEndTime() - System.currentTimeMillis());
	}

	public static synchronized void doWeekTasks()
	{
		if(isValidationPeriod())
			return;

		for(IntObjectPair<OlympiadParticipiantData> entry : _participants.entrySet())
		{
			OlympiadParticipiantData data = entry.getValue();
			data.setPoints(data.getPoints() + Config.OLYMPIAD_POINTS_WEEKLY);
			data.setClassedGamesCount(0);
			data.setNonClassedGamesCount(0);

			Player player = GameObjectsStorage.getPlayer(entry.getKey());

			if(player != null)
				player.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addName(player).addInteger(Config.OLYMPIAD_POINTS_WEEKLY));
		}
	}

	public static int getCurrentCycle()
	{
		return _currentCycle;
	}

	public static synchronized void addObserver(int id, Player observer)
	{
		if(observer.getOlympiadGame() != null || isRegistered(observer, false) || Olympiad.isRegisteredInComp(observer))
		{
			observer.sendPacket(SystemMsg.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
			return;
		}

		if(_manager == null || _manager.getOlympiadInstance(id) == null || _manager.getOlympiadInstance(id).getStatus() == BattleStatus.Begining || _manager.getOlympiadInstance(id).getStatus() == BattleStatus.Begin_Countdown)
		{
			observer.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return;
		}

		if(observer.isInCombat() || observer.getPvpFlag() > 0 || observer.containsEvent(SingleMatchEvent.class))
		{
			observer.sendPacket(SystemMsg.YOU_CANNOT_OBSERVE_WHILE_YOU_ARE_IN_COMBAT);
			return;
		}

		for(Servitor servitor : observer.getServitors())
			servitor.unSummon(false);

		observer.enterArenaObserverMode(getOlympiadGame(id));
	}

	public static synchronized void removeObserver(int id, ObservePoint observer)
	{
		if(_manager == null || _manager.getOlympiadInstance(id) == null)
			return;

		_manager.getOlympiadInstance(id).removeObserver(observer);
	}

	public static List<ObservePoint> getObservers(int id)
	{
		if(_manager == null || _manager.getOlympiadInstance(id) == null)
			return null;
		return _manager.getOlympiadInstance(id).getObservers();
	}

	public static OlympiadGame getOlympiadGame(int gameId)
	{
		if(_manager == null || gameId < 0)
			return null;
		return _manager.getOlympiadGames().get(gameId);
	}

	public static synchronized int[] getWaitingList()
	{
		if(!inCompPeriod())
			return null;

		int[] array = new int[3];
		array[0] = _classBasedRegisters.totalSize();
		array[1] = _nonClassBasedRegisters.size();

		return array;
	}

	public static synchronized int getParticipantRewardCount(Player player, boolean remove)
	{
		int objId = player.getObjectId();

		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return 0;

		int points = participant.getPointsPast();
		if(points == 0) // Уже получил бонус
			return 0;

		int rank = _participantRank.get(objId) - 1;
		switch(rank)
		{
			case 0:
			case 1:
				points = Config.ALT_OLY_RANK1_POINTS;
				break;
			case 2:
				points = Config.ALT_OLY_RANK2_POINTS;
				break;
			case 3:
				points = Config.ALT_OLY_RANK3_POINTS;
				break;
			case 4:
				points = Config.ALT_OLY_RANK4_POINTS;
				break;
			default:
				points = Config.ALT_OLY_RANK5_POINTS;
		}

		if(Hero.getInstance().isInactiveHero(player.getObjectId()) || Hero.getInstance().isHero(player.getObjectId()))
			points += Config.ALT_OLY_HERO_POINTS;

		if(remove)
		{
			participant.setPointsPast(0);
			OlympiadDatabase.saveParticipantData(objId);
		}

		return points * Config.ALT_OLY_GP_PER_POINT;
	}
	public static synchronized int getRank(Player player)
	{
		if(_participantRank.containsKey(player.getObjectId()))
			return _participantRank.get(player.getObjectId());
		return 0;
	}
	
	public static synchronized boolean isRegistered(Player player)
	{
		return isRegistered(player, false);
	}	
	
	public static synchronized boolean isRegistered(Player player, boolean unregister)
	{
		if(_classBasedRegisters.containsValue(player.getObjectId()))
			return true;
		if(_nonClassBasedRegisters.contains(player.getObjectId()))
			return true;
			
		if(Config.ALT_OLY_BY_SAME_BOX_NUMBER > 0 && !unregister) //for unregister we won't count anything
		{
			GameClient client = player.getNetConnection();
			if(client == null)
				return true;

			String playerHWID = client.getHWID();
			if(isNullOrEmpty(playerHWID) || !_playersHWID.containsValue(playerHWID))
				return false; //nothing if we don't have his HWID

			int boxesCount = 0; //will count now
			for(String hwid : _playersHWID.values())
			{
				if(hwid != null && hwid.equals(playerHWID))
					boxesCount++;
			}
			if(boxesCount >= Config.ALT_OLY_BY_SAME_BOX_NUMBER)
			{
				player.sendMessage(new CustomMessage("org.l2j.gameserver.model.entity.Olympiad.isRegistered.ActiveBoxesLimit").addNumber(Config.ALT_OLY_BY_SAME_BOX_NUMBER));
				return true;
			}
		}
		return false;
	}

	public static synchronized boolean isRegisteredInComp(Player player)
	{
		if(isRegistered(player, false))
			return true;
		if(_manager == null || _manager.getOlympiadGames() == null)
			return false;
		for(OlympiadGame g : _manager.getOlympiadGames().values())
			if(g != null && g.isRegistered(player.getObjectId()))
				return true;
		return false;
	}

	/**
	 * Возвращает олимпийские очки за текущий период
	 * @param objId
	 * @return
	 */
	public static synchronized int getParticipantPoints(int objId)
	{
		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return 0;
		return participant.getPoints();
	}

	/**
	 * Возвращает олимпийские очки за прошлый период
	 * @param objId
	 * @return
	 */
	public static synchronized int getParticipantPointsPast(int objId)
	{
		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return 0;
		return participant.getPointsPast();
	}

	public static synchronized int getCompetitionDone(int objId)
	{
		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return 0;
		return participant.getCompDone();
	}

	public static synchronized int getCompetitionWin(int objId)
	{
		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return 0;
		return participant.getCompWin();
	}

	public static synchronized int getCompetitionLoose(int objId)
	{
		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return 0;
		return participant.getCompLoose();
	}

	public static synchronized int[] getWeekGameCounts(int objId)
	{
		int[] ar = new int[4];

		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return ar;

		ar[0] = Config.GAME_MAX_LIMIT - participant.getClassedGamesCount() - participant.getNonClassedGamesCount();
		ar[1] = Config.GAME_CLASSES_COUNT_LIMIT - participant.getClassedGamesCount();
		ar[2] = Config.GAME_NOCLASSES_COUNT_LIMIT - participant.getNonClassedGamesCount();

		return ar;
	}

	public static Stadia[] getStadiums()
	{
		return STADIUMS;
	}

	public static List<NpcInstance> getNpcs()
	{
		return _npcs;
	}

	public static void addOlympiadNpc(NpcInstance npc)
	{
		_npcs.add(npc);
	}

	public static IntObjectMap<OlympiadParticipiantData> getParticipantsMap()
	{
		return _participants;
	}

	public static OlympiadParticipiantData getParticipantInfo(int objectId)
	{
		return _participants.get(objectId);
	}

	public static void changeParticipantName(int objId, String newName)
	{
		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return;
		participant.setName(newName);
		OlympiadDatabase.saveParticipantData(objId);
	}

	public static String getParticipantName(int objId)
	{
		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return null;
		return participant.getName();
	}

	public static int getParticipantClass(int objId)
	{
		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return 0;
		return participant.getClassId();
	}

	public static void manualSetParticipantPoints(int objId, int points)
	{
		OlympiadParticipiantData participant = getParticipantInfo(objId);
		if(participant == null)
			return;
		participant.setPoints(points);
		OlympiadDatabase.saveParticipantData(objId);
	}

	public static int convertParticipantClassId(int baseClassId)
	{
		ClassId classId = ClassId.VALUES[baseClassId];
		if(classId.getClassLevel().ordinal() < ClassLevel.SECOND.ordinal())
		{
			for(ClassId id : ClassId.VALUES)
			{
				if(id.isOfLevel(ClassLevel.SECOND) && id.childOf(classId))
					return id.getId();
			}
		}

		return classId.getId();
	}

	public static synchronized void addParticipant(Player participant)
	{
		OlympiadParticipiantData participantData = _participants.get(participant.getObjectId());
		if(participantData == null)
		{
			participantData = new OlympiadParticipiantData(participant.getObjectId(), participant.getName(), participant.getBaseClassId());
			participantData.setPoints(Config.OLYMPIAD_POINTS_DEFAULT);

			_participants.put(participant.getObjectId(), participantData);

			OlympiadDatabase.saveParticipantData(participant.getObjectId());
		}
	}

	public static synchronized void removeParticipant(Player participant)
	{
		if(_participants.remove(participant.getObjectId()) != null)
			OlympiadDatabase.deleteParticipantData(participant.getObjectId());
	}

	public static int getParticipantsCount()
	{
		return _participants.size();
	}
}