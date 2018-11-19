package l2s.gameserver.model.pledge;

import java.util.Calendar;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.ClanTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ClanWar
{
	public static enum ClanWarState
	{
		PREPARATION, 
		REJECTED, 
		MUTUAL, 
		WIN, 
		LOSS, 
		TIE;
	}

	public static enum WarProgress
	{
		VERY_LOW, 
		LOW, 
		NORMAL, 
		HIGH, 
		VERY_HIGH;
	}

	public static enum ClanWarPeriod
	{
		NEW, 
		PREPARATION, 
		MUTUAL, 
		PEACE;
	}

	private static final Logger _log = LoggerFactory.getLogger(ClanWar.class);

	public static final long PREPARATION_PERIOD_DURATION = TimeUnit.MILLISECONDS.convert(Config.CLAN_WAR_PREPARATION_DAYS_PERIOD, TimeUnit.DAYS);
	public static final long INACTIVITY_TIME_DURATION = TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
	public static final long PEACE_DURATION = TimeUnit.MILLISECONDS.convert(5, TimeUnit.DAYS);

	private final Clan _attackerClan;
	private final Clan _opposingClan;
	private ClanWarPeriod _period;
	private int _currentPeriodStartTime;
	private int _lastKillTime;
	private Future<?> _mutualStartTask;
	private Future<?> _inactivityCheckTask;
	private int _attackersKillCounter;
	private int _opposersKillCounter;

	public ClanWar(Clan attackerClan, Clan opposingClan, ClanWarPeriod period, int currentPeriodStartTime, int lastKillTime, int attackersKillCounter, int opposersKillCounter)
	{
		_attackerClan = attackerClan;
		_opposingClan = opposingClan;
		_period = period;
		_currentPeriodStartTime = currentPeriodStartTime;
		_lastKillTime = lastKillTime;
		_attackersKillCounter = attackersKillCounter;
		_opposersKillCounter = opposersKillCounter;

		_attackerClan.addClanWar(this);
		_opposingClan.addClanWar(this);

		onChange();
	}

	public void onKill(Player killer, Player victim)
	{
		if(_period != ClanWarPeriod.MUTUAL)
			return;

		Clan killerClan = killer.getClan();
		if(killerClan == null)
			return;

		Clan victimClan = victim.getClan();
		if(victimClan == null)
			return;

		if(victimClan.getReputationScore() > 0)
			killerClan.incReputation(Config.CLAN_WAR_REPUTATION_SCORE_PER_KILL, true, "ClanWar");

		if(killerClan.getReputationScore() > 0)
		{
			if(victim.getPledgeType() != Clan.SUBUNIT_ACADEMY)
				victimClan.incReputation(-Config.CLAN_WAR_REPUTATION_SCORE_PER_KILL, true, "ClanWar");
		}

		_lastKillTime = (int) (System.currentTimeMillis() / 1000L);

		if(killerClan == getAttackerClan())
			_attackersKillCounter += 1;
		else if(killerClan == getOpposingClan())
			_opposersKillCounter += 1;

		save(false);

		if(victim.getPledgeType() != Clan.SUBUNIT_ACADEMY) // TODO: Check this.
			victimClan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.BECAUSE_C1_WAS_KILLED_BY_A_CLAN_MEMBER_OF_S2_CLAN_FAME_POINTS_DECREASED_BY_1).addName(victim).addString(killerClan.getName()));
		killerClan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.BECAUSE_A_CLAN_MEMBER_OF_S1_WAS_KILLED_BY_C2_CLAN_FAME_POINTS_INCREASED_BY_1).addString(victimClan.getName()).addName(killer));
	}

	public int getAttackersKillCounter()
	{
		return _attackersKillCounter;
	}

	public int getPointDiff(Clan clan)
	{
		return getAttackerClan() == clan ? getAttackersKillCounter() - getOpposersKillCounter() : getOpposersKillCounter() - getAttackersKillCounter();
	}

	public WarProgress calculateWarProgress(int pointDiff)
	{
		if(pointDiff <= -50)
			return WarProgress.VERY_LOW;

		if(pointDiff > -50 && pointDiff <= -20)
			return WarProgress.LOW;

		if(pointDiff > -20 && pointDiff <= 19)
			return WarProgress.NORMAL;

		if(pointDiff > 19 && pointDiff <= 49)
			return WarProgress.HIGH;

		return WarProgress.VERY_HIGH;
	}

	public ClanWarState getClanWarState(Clan clan)
	{
		if(_period == ClanWarPeriod.NEW || _period == ClanWarPeriod.PREPARATION)
			return ClanWarState.PREPARATION;

		if(_period == ClanWarPeriod.MUTUAL)
			return ClanWarState.MUTUAL;

		if(_period == ClanWarPeriod.PEACE)
		{
			int points = getPointDiff(clan);
			if(points == 0)
				return ClanWarState.TIE;

			if(points < 0)
				return ClanWarState.LOSS;

			return ClanWarState.WIN;
		}

		return ClanWarState.REJECTED;
	}

	public boolean isAttacker(Clan clan)
	{
		return getAttackerClan() == clan;
	}

	public boolean isOpposing(Clan clan)
	{
		return getOpposingClan() == clan;
	}

	public Clan getAttackerClan()
	{
		return _attackerClan;
	}

	public Clan getOpposingClan()
	{
		return _opposingClan;
	}

	public int getOpposersKillCounter()
	{
		return _opposersKillCounter;
	}

	public int getLastKillTime()
	{
		return _lastKillTime;
	}

	public ClanWarPeriod getPeriod()
	{
		return _period;
	}

	public long getPeriodDuration()
	{
		switch(_period)
		{
			case NEW:
			case PREPARATION:
				return System.currentTimeMillis() - _currentPeriodStartTime * 1000L;
			case MUTUAL:
				return 0L;
			case PEACE:
				return System.currentTimeMillis() - _currentPeriodStartTime * 1000L;
		}
		return 0L;
	}

	public int getCurrentPeriodStartTime()
	{
		return _currentPeriodStartTime;
	}

	public void accept(Clan requestor)
	{
		if(requestor == getOpposingClan())
			setPeriod(ClanWarPeriod.MUTUAL);
	}

	public void cancel(Clan requester)
	{
		Clan winnerClan = requester == getAttackerClan() ? getOpposingClan() : getAttackerClan();
		Clan looserClan = requester == getAttackerClan() ? getAttackerClan() : getOpposingClan();

		looserClan.incReputation(-500, true, "ClanWar");
		looserClan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN).addString(winnerClan.getName()));

		winnerClan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN).addString(looserClan.getName()));

		setPeriod(ClanWarPeriod.PEACE);
	}

	public void setPeriod(ClanWarPeriod period)
	{
		if(_period == period)
			return;

		if(_period == ClanWarPeriod.MUTUAL && period == ClanWarPeriod.PREPARATION)
			_log.warn(getClass().getSimpleName() + ": Cannot change clan war period from mutual (when both sides fighting) to preparation.");

		_period = period;
		_currentPeriodStartTime = (int) (System.currentTimeMillis() / 1000L);

		if(period == ClanWarPeriod.MUTUAL)
		{
			getAttackerClan().updateClanWarStatus(this);
			getAttackerClan().broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED).addString(getOpposingClan().getName()));

			getOpposingClan().updateClanWarStatus(this);
			getOpposingClan().broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED).addString(getAttackerClan().getName()));
		}
		else if(period == ClanWarPeriod.PEACE)
		{
			getAttackerClan().updateClanWarStatus(this);
			getAttackerClan().broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.WAR_WITH_THE_S1_CLAN_HAS_ENDED).addString(getOpposingClan().getName()));

			getOpposingClan().updateClanWarStatus(this);
			getOpposingClan().broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.WAR_WITH_THE_S1_CLAN_HAS_ENDED).addString(getAttackerClan().getName()));

			ThreadPoolManager.getInstance().schedule(() ->
			{
				getAttackerClan().removeClanWar(ClanWar.this);
				//getAttackerClan().broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.THE_WAR_AGAINST_S1_CLAN_HAS_BEEN_STOPPED).addString(getOpposingClan().getName())); TODO: Надо ли?

				getOpposingClan().removeClanWar(ClanWar.this);
				//getOpposingClan().broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.THE_CLAN_S1_HAS_DECIDED_TO_STOP_THE_WAR).addString(getAttackerClan().getName())); TODO: Надо ли?

				ClanTable.getInstance().deleteClanWar(ClanWar.this);
			}
			, PEACE_DURATION - (System.currentTimeMillis() - _currentPeriodStartTime * 1000L));

			Calendar deletionTime = Calendar.getInstance();
			deletionTime.setTimeInMillis(System.currentTimeMillis() + PEACE_DURATION - (System.currentTimeMillis() - _currentPeriodStartTime * 1000L));

			_log.info(getClass().getSimpleName() + ": Clan war between clans " + getAttackerClan().getName() + " and " + getOpposingClan().getName() + " has end. CW scheduled for deletion at " + deletionTime.getTime() + ".");

		}
		onChange();
	}

	private void onChange()
	{
		if(_period == ClanWarPeriod.NEW)
		{
			_period = ClanWarPeriod.PREPARATION;

			getAttackerClan().broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.YOU_HAVE_DECLARED_A_CLAN_WAR_WITH_S1_CLAN_WAR_STARTS_IN_3_DAYS).addString(getOpposingClan().getName()));
			getOpposingClan().broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.S1_HAS_DECLARED_A_CLAN_WAR_CLAN_WAR_STARTS_IN_3_DAYS).addString(getAttackerClan().getName()));
		}

		if(_period == ClanWarPeriod.PREPARATION)
		{
			long mutualPeriodStartTime = _currentPeriodStartTime * 1000L + PREPARATION_PERIOD_DURATION - System.currentTimeMillis();

			if(mutualPeriodStartTime > 0)
			{
				_mutualStartTask = ThreadPoolManager.getInstance().schedule(() ->
				{
					if(_period != ClanWarPeriod.PREPARATION)
						return;

					setPeriod(ClanWarPeriod.MUTUAL);
				}
				, mutualPeriodStartTime);

				Calendar scheduleTime = Calendar.getInstance();
				scheduleTime.setTimeInMillis(System.currentTimeMillis() + mutualPeriodStartTime);
				_log.info(getClass().getSimpleName() + ": Clan war between clans with ID " + getAttackerClan().getClanId() + " and " + getOpposingClan().getClanId() + " in preparation mode. Scheduled for mutual period at " + scheduleTime.getTime());
			}
			else
				setPeriod(ClanWarPeriod.MUTUAL);
		}
		else if(_period == ClanWarPeriod.MUTUAL)
		{
			_inactivityCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
			{
				if(_period != ClanWarPeriod.MUTUAL)
				{
					if(_inactivityCheckTask != null)
					{
						_inactivityCheckTask.cancel(false);
						_inactivityCheckTask = null;
					}
				}
				else
				{
					long lastKillTimeDuration = System.currentTimeMillis() - _lastKillTime * 1000L;
					if(lastKillTimeDuration > INACTIVITY_TIME_DURATION)
						setPeriod(ClanWarPeriod.PEACE);
				}
			}
			, TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS), TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS));

			if(_lastKillTime > 0)
			{
				Calendar killTime = Calendar.getInstance();
				killTime.setTimeInMillis(_lastKillTime * 1000L);
				_log.info(getClass().getSimpleName() + ": Last kill in clan war between clans with ID " + getAttackerClan().getClanId() + " and " + getOpposingClan().getClanId() + " wat at " + killTime.getTime() + ". Scheduled inactivity check per each hour.");
			}
			else
				_log.info(getClass().getSimpleName() + ": Last kill in clan war between clans with ID " + getAttackerClan().getClanId() + " and " + getOpposingClan().getClanId() + " has never happened. Scheduled inactivity check per each hour.");
		}

		save(true);

		if(_period != ClanWarPeriod.PREPARATION && _mutualStartTask != null)
		{
			_mutualStartTask.cancel(true);
			_mutualStartTask = null;
		}
		else if(_period != ClanWarPeriod.MUTUAL && _inactivityCheckTask != null)
		{
			_inactivityCheckTask.cancel(true);
			_inactivityCheckTask = null;
		}

		if(_period == ClanWarPeriod.PREPARATION)
			return;

		Clan clan = getAttackerClan();
		if(clan != null)
		{
			for(Player member : clan.getOnlineMembers(-1))
				member.broadcastCharInfo();
		}

		clan = getOpposingClan();
		if(clan != null)
		{
			for(Player member : clan.getOnlineMembers(-1))
				member.broadcastCharInfo();
		}
	}

	private void save(boolean force)
	{
		ClanTable.getInstance().storeClanWar(this, force);
	}
}