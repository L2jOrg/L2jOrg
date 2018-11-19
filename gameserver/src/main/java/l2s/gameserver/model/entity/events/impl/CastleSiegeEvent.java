package l2s.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.Config;
import l2s.gameserver.dao.CastleDamageZoneDAO;
import l2s.gameserver.dao.CastleDoorUpgradeDAO;
import l2s.gameserver.dao.CastleHiredGuardDAO;
import l2s.gameserver.dao.SiegeClanDAO;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.actor.OnKillListener;
import l2s.gameserver.model.*;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.HeroDiary;
import l2s.gameserver.model.entity.events.objects.DoorObject;
import l2s.gameserver.model.entity.events.objects.SiegeClanObject;
import l2s.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import l2s.gameserver.model.entity.events.objects.SpawnExObject;
import l2s.gameserver.model.entity.events.objects.SpawnSimpleObject;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.ResidenceSide;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.support.MerchantGuard;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.SiegeUtils;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

/**
 * @author VISTALL
 * @date 15:12/14.02.2011
 */
public class CastleSiegeEvent extends SiegeEvent<Castle, SiegeClanObject>
{
    public class KillListener implements OnKillListener
    {
        @Override
        public void onKill(Creature actor, Creature victim)
        {
            Player winner = actor.getPlayer();
            if(winner == null || !victim.isPlayer() || winner == victim || !checkIfInZone(victim) || !((Player) victim).isUserRelationActive() || !victim.containsEvent(CastleSiegeEvent.this))
                return;
            List<Player> players;
            if(winner.getParty() == null)
                players = Collections.singletonList(winner);
            else
                players = winner.getParty().getPartyMembers();

            double bonus = Config.ALT_PARTY_BONUS[Math.min(Config.ALT_PARTY_BONUS.length, players.size()) - 1];
            int value = (int) (Math.round(BASE_SIEGE_FAME * bonus) / players.size());
            for(Player temp : players)
            {
                if(!temp.containsEvent(CastleSiegeEvent.this) || temp.getLevel() < 40 || !temp.isInRange(winner, Config.ALT_PARTY_DISTRIBUTION_RANGE))
                	continue;

                temp.setFame(temp.getFame() + value, CastleSiegeEvent.this.toString(), true);
            }

            ((Player) victim).startEnableUserRelationTask(BLOCK_FAME_TIME, CastleSiegeEvent.this);
            _blockedFameOnKill.put(victim.getObjectId(), System.currentTimeMillis() + BLOCK_FAME_TIME);
        }

        @Override
        public boolean ignorePetOrSummon()
        {
            return true;
        }
    }

	public static final int MAX_SIEGE_CLANS = Config.MAX_SIEGE_CLANS;
    public static final int BASE_SIEGE_FAME = 72;

	public static final String DEFENDERS_WAITING = "defenders_waiting";
	public static final String DEFENDERS_REFUSED = "defenders_refused";

	public static final String CONTROL_TOWERS = "control_towers";
	public static final String FLAME_TOWERS = "flame_towers";
	public static final String BOUGHT_ZONES = "bought_zones";
	public static final String GUARDS = "guards";
	public static final String HIRED_GUARDS = "hired_guards";

    private static final String LIGHT_SIDE = "light_side";
    private static final String DARK_SIDE = "dark_side";

	private boolean _firstStep = false;

    private final Calendar _validationDate;
    private final IntSet _visitedParticipants = new HashIntSet();

	public CastleSiegeEvent(MultiValueSet<String> set)
	{
		super(set);
        _killListener = new KillListener();

        int[] validationTimeArray = set.getIntegerArray("validation_date", new int[] { 2, 4, 2003 });
        _validationDate = Calendar.getInstance();
        _validationDate.set(Calendar.DAY_OF_MONTH, validationTimeArray[0]);
        _validationDate.set(Calendar.MONTH, validationTimeArray[1] - 1);
        _validationDate.set(Calendar.YEAR, validationTimeArray[2]);
        _validationDate.set(Calendar.HOUR_OF_DAY, 0);
        _validationDate.set(Calendar.MINUTE, 0);
        _validationDate.set(Calendar.SECOND, 0);
        _validationDate.set(Calendar.MILLISECOND, 0);
	}

	//========================================================================================================================================================================
	//                                                    Главные методы осады
	//========================================================================================================================================================================
	@Override
	public void initEvent()
	{
		super.initEvent();

		List<DoorObject> doorObjects = getObjects(DOORS);

		addObjects(BOUGHT_ZONES, CastleDamageZoneDAO.getInstance().load(getResidence()));

		for(DoorObject doorObject : doorObjects)
		{
			doorObject.setUpgradeValue(this, CastleDoorUpgradeDAO.getInstance().load(doorObject.getUId()));
			doorObject.getDoor().addListener(_doorDeathListener);
		}
	}

	public void takeCastle(Clan newOwnerClan, ResidenceSide side)
	{
		processStep(newOwnerClan);

		getResidence().setResidenceSide(side, false);
		getResidence().broadcastResidenceState();
	}

	@Override
	public void processStep(Clan newOwnerClan)
	{
		Clan oldOwnerClan = getResidence().getOwner();

		getResidence().changeOwner(newOwnerClan);

		// если есть овнер в резиденции, делаем его аттакером
		if(oldOwnerClan != null)
		{
			SiegeClanObject ownerSiegeClan = getSiegeClan(DEFENDERS, oldOwnerClan);
			if(ownerSiegeClan != null)
			{
				removeObject(DEFENDERS, ownerSiegeClan);

				ownerSiegeClan.setType(ATTACKERS);
				addObject(ATTACKERS, ownerSiegeClan);
			}
		}
		else
		{
			// Если атакуется замок, принадлежащий NPC, и только 1 атакующий - закончить осаду
			if(getObjects(ATTACKERS).size() == 1)
			{
				stopEvent(false);
				return;
			}

			// Если атакуется замок, принадлежащий NPC, и все атакующие в одном альянсе - закончить осаду
			int allianceObjectId = newOwnerClan.getAllyId();
			if(allianceObjectId > 0)
			{
				List<SiegeClanObject> attackers = getObjects(ATTACKERS);
				boolean sameAlliance = true;
				for(SiegeClanObject sc : attackers)
					if(sc != null && sc.getClan().getAllyId() != allianceObjectId)
						sameAlliance = false;
				if(sameAlliance)
				{
					stopEvent(false);
					return;
				}
			}
		}

		// ставим нового овнера защитником
		SiegeClanObject newOwnerSiegeClan = getSiegeClan(ATTACKERS, newOwnerClan);
		newOwnerSiegeClan.deleteFlag();
		newOwnerSiegeClan.setType(DEFENDERS);

		removeObject(ATTACKERS, newOwnerSiegeClan);

		// у нас защитник ток овнер
		List<SiegeClanObject> defenders = removeObjects(DEFENDERS);
		for(SiegeClanObject siegeClan : defenders)
			siegeClan.setType(ATTACKERS);

		// новый овнер это защитник
		addObject(DEFENDERS, newOwnerSiegeClan);

		// При захвате замка, удаляем из списка участвующих на других осадах замков.
		for(CastleSiegeEvent castleSiege : EventHolder.getInstance().getEvents(CastleSiegeEvent.class))
		{
			if(castleSiege == this)
				continue;

			if(!castleSiege.isInProgress())
				continue;

			SiegeClanObject siegeClan = castleSiege.getSiegeClan(ATTACKERS, newOwnerClan);
			if(siegeClan != null)
			{
				siegeClan.deleteFlag();
				castleSiege.removeObject(ATTACKERS, siegeClan);

				for(Player player : newOwnerClan.getOnlineMembers(0))
				{
					player.removeEvent(castleSiege);
					player.broadcastCharInfo();
				}
			}

			siegeClan = castleSiege.getSiegeClan(DEFENDERS, newOwnerClan);
			if(siegeClan != null)
			{
				siegeClan.deleteFlag();
				castleSiege.removeObject(DEFENDERS, siegeClan);

				for(Player player : newOwnerClan.getOnlineMembers(0))
				{
					player.removeEvent(castleSiege);
					player.broadcastCharInfo();
				}
			}
		}

		// все дефендеры, стают аттакующими
		addObjects(ATTACKERS, defenders);

		updateParticles(true, ATTACKERS, DEFENDERS);

        teleportPlayers(FROM_RESIDENCE_TO_TOWN);

		// ток при первом захвате обнуляем мерчант гвардов и убираем апгрейд дверей
		if(!_firstStep)
		{
			_firstStep = true;

			broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED, ATTACKERS, DEFENDERS);

			if(_oldOwner != null)
			{
				if(containsObjects(HIRED_GUARDS))
					spawnAction(HIRED_GUARDS, false);
				damageZoneAction(false);
				removeObjects(HIRED_GUARDS);
				removeObjects(BOUGHT_ZONES);

				CastleDamageZoneDAO.getInstance().delete(getResidence());
			}
			else
				spawnAction(GUARDS, false);

			List<DoorObject> doorObjects = getObjects(DOORS);
			for(DoorObject doorObject : doorObjects)
			{
				doorObject.setWeak(true);
				doorObject.setUpgradeValue(this, 0);

				CastleDoorUpgradeDAO.getInstance().delete(doorObject.getUId());
			}
		}

		spawnAction(DOORS, true);
		despawnSiegeSummons();
	}

	@Override
	public void startEvent()
	{
        List<SiegeClanObject> attackers = getObjects(ATTACKERS);
        if(attackers.isEmpty())
        {
            if(getResidence().getOwner() == null)
                broadcastToWorld(new SystemMessagePacket(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(getResidence()));
            else
            {
                broadcastToWorld(new SystemMessagePacket(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addResidenceName(getResidence()));

                getResidence().getOwner().setCastleDefendCount(getResidence().getOwner().getCastleDefendCount() + 1);
                getResidence().getOwner().updateClanInDB();
            }

            getResidence().getOwnDate().setTimeInMillis(getResidence().getOwner() == null ? 0 : System.currentTimeMillis());
            reCalcNextTime(false);
            return;
        }

		_oldOwner = getResidence().getOwner();
		if(_oldOwner != null)
		{
			addObject(DEFENDERS, new SiegeClanObject(DEFENDERS, _oldOwner, 0));

			if(getResidence().getSpawnMerchantTickets().size() > 0)
			{
				for(ItemInstance item : getResidence().getSpawnMerchantTickets())
				{
					MerchantGuard guard = getResidence().getMerchantGuard(item.getItemId());

					addObject(HIRED_GUARDS, new SpawnSimpleObject(guard.getNpcId(), item.getLoc()));

					item.deleteMe();
				}

				CastleHiredGuardDAO.getInstance().delete(getResidence());

				if(containsObjects(HIRED_GUARDS))
					spawnAction(HIRED_GUARDS, true);
			}
		}

		SiegeClanDAO.getInstance().delete(getResidence());

		updateParticles(true, ATTACKERS, DEFENDERS);

		broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT, ATTACKERS);
		broadcastTo(new SystemMessagePacket(SystemMsg.YOU_ARE_PARTICIPATING_IN_THE_SIEGE_OF_S1_THIS_SIEGE_IS_SCHEDULED_FOR_2_HOURS).addResidenceName(getResidence()), ATTACKERS, DEFENDERS);

		super.startEvent();

		if(_oldOwner == null)
			initControlTowers();
		else
			damageZoneAction(true);
	}

	@Override
	public void stopEvent(boolean force)
	{
		List<DoorObject> doorObjects = getObjects(DOORS);
		for(DoorObject doorObject : doorObjects)
			doorObject.setWeak(false);

        for(int objectId : _visitedParticipants.toArray())
        {
            Player player = GameObjectsStorage.getPlayer(objectId);
            if(player != null)
                player.getListeners().onParticipateInCastleSiege(this);
        }

		damageZoneAction(false);

        _blockedFameOnKill.clear();

		updateParticles(false, ATTACKERS, DEFENDERS);

		List<SiegeClanObject> attackers = removeObjects(ATTACKERS);
		for(SiegeClanObject siegeClan : attackers)
			siegeClan.deleteFlag();

		broadcastToWorld(new SystemMessagePacket(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()));

		removeObjects(DEFENDERS);
		removeObjects(DEFENDERS_WAITING);
		removeObjects(DEFENDERS_REFUSED);

		Clan ownerClan = getResidence().getOwner();
		if(ownerClan != null)
		{
			if(_oldOwner == ownerClan)
			{
				getResidence().getOwner().setCastleDefendCount(getResidence().getOwner().getCastleDefendCount() + 1);
				getResidence().getOwner().updateClanInDB();

				ownerClan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE).addInteger(ownerClan.incReputation(1500, false, toString())));
			}
			else
			{
				broadcastToWorld(new SystemMessagePacket(SystemMsg.CLAN_S1_IS_VICTORIOUS_OVER_S2S_CASTLE_SIEGE).addString(ownerClan.getName()).addResidenceName(getResidence()));

				ownerClan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE).addInteger(ownerClan.incReputation(3000, false, toString())));

				if(_oldOwner != null)
					_oldOwner.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOU_CLAN_REPUTATION_SCORE_AND_ADDED_TO_YOUR_OPPONENTS).addInteger(-_oldOwner.incReputation(-3000, false, toString())));

				for(UnitMember member : ownerClan)
				{
					Player player = member.getPlayer();
					if(player != null)
					{
						player.sendPacket(PlaySoundPacket.SIEGE_VICTORY);
						if(player.isOnline() && player.isHero())
							Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_CASTLE_TAKEN, getResidence().getId());
					}
				}
			}

            for(Castle castle : ResidenceHolder.getInstance().getResidenceList(Castle.class))
            {
                if(castle == getResidence())
                    continue;

                SiegeEvent<?, ?> siegeEvent = castle.getSiegeEvent();

                SiegeClanObject siegeClan = siegeEvent.getSiegeClan(ATTACKERS, ownerClan);
                if(siegeClan == null)
                    siegeClan = siegeEvent.getSiegeClan(DEFENDERS, ownerClan);
                if(siegeClan == null)
                    siegeClan = siegeEvent.getSiegeClan(DEFENDERS_WAITING, ownerClan);

                if (siegeClan != null)
                {
                    siegeEvent.getObjects(siegeClan.getType()).remove(siegeClan);

                    SiegeClanDAO.getInstance().delete(castle, siegeClan);
                }
            }

			getResidence().getOwnDate().setTimeInMillis(System.currentTimeMillis());
			getResidence().getLastSiegeDate().setTimeInMillis(getResidence().getSiegeDate().getTimeInMillis());
		}
		else
		{
			broadcastToWorld(new SystemMessagePacket(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));

			getResidence().getOwnDate().setTimeInMillis(0);
            getResidence().getLastSiegeDate().setTimeInMillis(getResidence().getSiegeDate().getTimeInMillis());
            getResidence().setResidenceSide(ResidenceSide.NEUTRAL, false);
            getResidence().broadcastResidenceState();
		}

		despawnSiegeSummons();

		if(_oldOwner != null)
		{
			if(containsObjects(HIRED_GUARDS))
				spawnAction(HIRED_GUARDS, false);
			removeObjects(HIRED_GUARDS);
		}

		super.stopEvent(force);
	}

	//========================================================================================================================================================================

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		clearActions();

		final long currentTimeMillis = System.currentTimeMillis();
		final Calendar startSiegeDate = getResidence().getSiegeDate();
		final Calendar ownSiegeDate = getResidence().getOwnDate();
		if(onInit)
		{
			if(startSiegeDate.getTimeInMillis() > currentTimeMillis)
            {
                addState(REGISTRATION_STATE);
                registerActions();
            }
			else if(startSiegeDate.getTimeInMillis() == 0 || startSiegeDate.getTimeInMillis() <= currentTimeMillis)
				setNextSiegeTime();
		}
		else
		{
			if(getResidence().getOwner() != null)
			{
				getResidence().getSiegeDate().setTimeInMillis(0);
				getResidence().setJdbcState(JdbcEntityState.UPDATED);
				getResidence().update();
            }
            setNextSiegeTime();
		}
	}

	@Override
	public void loadSiegeClans()
	{
		super.loadSiegeClans();

		addObjects(DEFENDERS_WAITING, SiegeClanDAO.getInstance().load(getResidence(), DEFENDERS_WAITING));
		addObjects(DEFENDERS_REFUSED, SiegeClanDAO.getInstance().load(getResidence(), DEFENDERS_REFUSED));
	}

	@Override
    public void removeState(int val)
    {
        super.removeState(val);

        if(val == REGISTRATION_STATE)
            broadcastToWorld(new SystemMessagePacket(SystemMsg.THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED).addResidenceName(getResidence()));
    }

	@Override
	public void announce(SystemMsg msgId, int val, int time)
	{
		SystemMessagePacket msg;
		int min = val / 60;
		int hour = min / 60;

		if(hour > 0)
			msg = new SystemMessagePacket(SystemMsg.S1_HOURS_UNTIL_CASTLE_SIEGE_CONCLUSION).addInteger(hour);
		else if(min > 0)
			msg = new SystemMessagePacket(SystemMsg.S1_MINUTES_UNTIL_CASTLE_SIEGE_CONCLUSION).addInteger(min);
		else
			msg = new SystemMessagePacket(SystemMsg.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECONDS).addInteger(val);

		broadcastTo(msg, ATTACKERS, DEFENDERS);
	}

	//========================================================================================================================================================================
	//                                                   Control Tower Support
	//========================================================================================================================================================================
	private void initControlTowers()
	{
		List<SpawnExObject> objects = getObjects(GUARDS);
		List<Spawner> spawns = new ArrayList<Spawner>();
		for(SpawnExObject o : objects)
			spawns.addAll(o.getSpawns());

		List<SiegeToggleNpcObject> ct = getObjects(CONTROL_TOWERS);

		SiegeToggleNpcInstance closestCt;
		double distance, distanceClosest;

		for(Spawner spawn : spawns)
		{
			Location spawnLoc = spawn.getRandomSpawnRange().getRandomLoc(ReflectionManager.MAIN.getGeoIndex());

			closestCt = null;
			distanceClosest = 0;

			for(SiegeToggleNpcObject c : ct)
			{
				SiegeToggleNpcInstance npcTower = c.getToggleNpc();
				distance = npcTower.getDistance(spawnLoc);

				if(closestCt == null || distance < distanceClosest)
				{
					closestCt = npcTower;
					distanceClosest = distance;
				}

				closestCt.register(spawn);
			}
		}
	}

	//========================================================================================================================================================================
	//                                                    Damage Zone Actions
	//========================================================================================================================================================================
	private void damageZoneAction(boolean active)
	{
		if(containsObjects(BOUGHT_ZONES))
			zoneAction(BOUGHT_ZONES, active);
	}

	//========================================================================================================================================================================
	//                                                    Суппорт Методы для установки времени осады
	//========================================================================================================================================================================

	/**
	 * Автоматически генерит и устанавливает дату осады
	 */
	private void setNextSiegeTime()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(_validationDate.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_WEEK, _dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, _hourOfDay);

        if(calendar.before(_validationDate))
            calendar.add(Calendar.DAY_OF_MONTH, 7);

        validateSiegeDate(calendar, _siegeIntervalInWeeks * 7);

		broadcastToWorld(new SystemMessagePacket(SystemMsg.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME).addResidenceName(getResidence()));

		clearActions();

		getResidence().getSiegeDate().setTimeInMillis(calendar.getTimeInMillis());
		getResidence().setJdbcState(JdbcEntityState.UPDATED);
		getResidence().update();

		registerActions();

        addState(REGISTRATION_STATE);
	}

	@Override
	public boolean isAttackersInAlly()
	{
		return !_firstStep;
	}

	@Override
    public boolean canResurrect(Creature active, Creature target, boolean force, boolean quiet)
	{
        boolean playerInZone = checkIfInZone(active);
        boolean targetInZone = checkIfInZone(target);
		// если оба вне зоны - рес разрешен
		// если таргет вне осадный зоны - рес разрешен
        if(!playerInZone && !targetInZone || !targetInZone)
			return true;

        Player resurectPlayer = active.getPlayer();
		Player targetPlayer = target.getPlayer();

		// если оба незареганы - невозможно ресать
		// если таргет незареган - невозможно ресать
        if(!resurectPlayer.containsEvent(this) || !targetPlayer.containsEvent(this))
        {
            if (!quiet)
            {
                if(force)
                    targetPlayer.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
                active.sendPacket(force ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
            }
			return false;
		}

		SiegeClanObject targetSiegeClan = getSiegeClan(ATTACKERS, targetPlayer.getClan());
		if(targetSiegeClan == null)
			targetSiegeClan = getSiegeClan(DEFENDERS, targetPlayer.getClan());

		if(targetSiegeClan == null || targetSiegeClan.getType() == ATTACKERS)
		{
			// если нету флага - рес запрещен
			if(targetSiegeClan == null || targetSiegeClan.getFlag() == null)
			{
                if(!quiet)
                {
                    if(force)
                        targetPlayer.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
                    active.sendPacket(force ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
                }
				return false;
			}
		}
		else
		{
			List<SiegeToggleNpcObject> towers = getObjects(CastleSiegeEvent.CONTROL_TOWERS);

			boolean canRes = true;
			for(SiegeToggleNpcObject t : towers)
				if(!t.isAlive())
					canRes = false;

			if(!canRes)
			{
                if(!quiet)
                {
                    if(force)
                        targetPlayer.sendPacket(SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
                    active.sendPacket(force ? SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
                }
				return false;
			}
		}

        if(force)
		    return true; //test is all conds are good return true

        if(!quiet)
            active.sendPacket(SystemMsg.INVALID_TARGET);
        return false;
	}

	@Override
    public boolean ifVar(String name)
    {
        if(name.equals(LIGHT_SIDE))
            return getResidence().getResidenceSide() == ResidenceSide.LIGHT;
        if(name.equals(DARK_SIDE))
            return getResidence().getResidenceSide() == ResidenceSide.DARK;

        return super.ifVar(name);
    }

    public void addVisitedParticipant(Player player)
    {
        _visitedParticipants.add(player.getObjectId());
    }

	public boolean canRegisterOnSiege(Player player, Clan clan, boolean attacker)
	{
		if(attacker)
		{
			if(getResidence().getOwnerId() == clan.getClanId())
			{
				player.sendPacket(SystemMsg.CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);
				return false;
			}

			Alliance alliance = clan.getAlliance();
			if(alliance != null)
			{
				for(Clan c : alliance.getMembers())
				{
					if(c.getCastle() == getResidence().getId())
					{
						player.sendPacket(SystemMsg.YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN);
						return false;
					}
				}
			}
			if(clan.getCastle() != 0)
			{
				player.sendPacket(SystemMsg.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);
				return false;
			}

			if(getSiegeClan(ATTACKERS, clan) != null)
			{
				player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
				return false;
			}

			if(getSiegeClan(DEFENDERS, clan) != null || getSiegeClan(DEFENDERS_WAITING, clan) != null || getSiegeClan(DEFENDERS_REFUSED, clan) != null)
			{
				player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REGISTERED_TO_THE_DEFENDER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST);
				return false;
			}
		}
		else
		{
			if(getResidence().getOwnerId() == 0)
				return false;

			if(getResidence().getOwnerId() == clan.getClanId())
			{
				player.sendPacket(SystemMsg.CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);
				return false;
			}

			if(clan.getCastle() != 0)
			{
				player.sendPacket(SystemMsg.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);
				return false;
			}

			if(getSiegeClan(DEFENDERS, clan) != null || getSiegeClan(DEFENDERS_WAITING, clan) != null || getSiegeClan(DEFENDERS_REFUSED, clan) != null)
			{
				player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
				return false;
			}

			if(getSiegeClan(ATTACKERS, clan) != null)
			{
				player.sendPacket(SystemMsg.YOU_ARE_ALREADY_REGISTERED_TO_THE_ATTACKER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST);
				return false;
			}
		}

		return true;
	}

	public IBroadcastPacket checkSiegeClanLevel(Clan clan)
	{
		if(clan.getLevel() < SiegeUtils.MIN_CLAN_SIEGE_LEVEL)
			return SystemMsg.ONLY_CLANS_OF_LEVEL_5_OR_HIGHER_MAY_REGISTER_FOR_A_CASTLE_SIEGE;
		return null;
	}

	public boolean canCastSeal(Player player)
	{
		return true;
	}

	public void onLordDie(NpcInstance npc)
	{}
}