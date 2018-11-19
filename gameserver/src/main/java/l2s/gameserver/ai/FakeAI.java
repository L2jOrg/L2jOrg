package l2s.gameserver.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.data.xml.holder.FakePlayersHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerChatMessageReceive;
import l2s.gameserver.listener.actor.player.OnTeleportListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Territory;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.actor.instances.creature.AbnormalList;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.tables.FakePlayersTable;
import l2s.gameserver.templates.ZoneTemplate;
import l2s.gameserver.templates.fakeplayer.FakePlayerAITemplate;
import l2s.gameserver.templates.fakeplayer.FarmZoneTemplate;
import l2s.gameserver.templates.fakeplayer.TownZoneTemplate;
import l2s.gameserver.templates.fakeplayer.actions.AbstractAction;
import l2s.gameserver.templates.fakeplayer.actions.GoToTownActions;
import l2s.gameserver.templates.fakeplayer.actions.OrdinaryActions;
import l2s.gameserver.templates.fakeplayer.actions.ReviveAction;
import l2s.gameserver.templates.fakeplayer.actions.TeleportToClosestTownAction;
import l2s.gameserver.utils.FakePlayerUtils;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;
import l2s.gameserver.utils.TeleportUtils;

import org.napile.primitive.sets.IntSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeAI extends PlayerAI implements OnDeathListener, OnLevelChangeListener, OnTeleportListener, OnPlayerChatMessageReceive
{
	private class DistanceComparator implements Comparator<GameObject>
	{
		@Override
		public int compare(GameObject o1, GameObject o2)
		{
			Player player = getActor();
			if(player != null)
				return (int) (o1.getDistance(player) - o2.getDistance(player));
			return 0;
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(FakeAI.class);

	private static final int MAX_ACTION_TRY_COUNT = 500;

	private static final int GIVE_CONSUMABLE_ITEMS_DELAY = 600000;

	private static final int SEARCH_PVP_PK_DELAY = 60000;
	private static final int ATTACK_WAIT_DELAY = 180000;
	private static final int SHOUT_CHAT_MIN_DELAY = 60000;
	private static final int SHOUT_CHAT_MAX_DELAY = 600000;
	private final FakePlayerAITemplate _aiTemplate;
	private final DistanceComparator _distanceComparator = new DistanceComparator();

	private ScheduledFuture<?> _actionTask;

	private final List<AbstractAction> _plannedActions = new ArrayList<AbstractAction>();
	private AbstractAction _lastPerformedAction = null;
	private int _lastActionTryCount = 0;

	private FarmZoneTemplate _currentFarmZone = null;

	private long _waitEndTime = 0;
	private long _goToTownTime = -1;
	private long _lastGiveConsumableItemsTime = 0;
	private long _lastSearchPvPPKTime = 0;
	private long _lastAttackTime = 0;
	private long _nextShoutChatTime = 0;

	public FakeAI(Player player, FakePlayerAITemplate aiTemplate)
	{
		super(player);
		_aiTemplate = aiTemplate;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		Player actor = getActor();

		actor.addListener(this);

		if(actor.entering)
		{
			if(actor.getOnlineTime() == 0)
				planActions(_aiTemplate.getOnCreateAction());
		}
		actor.setActive();

		startActionTask();

		FakePlayerUtils.checkAutoShots(this);
	}

	@Override
	public void onEvtDeSpawn()
	{
		getActor().removeListener(this);
		stopActionTask();
		super.onEvtDeSpawn();
	}

	@Override
	public boolean isFake()
	{
		return true;
	}

	public void startWait(int minDelay, int maxDelay)
	{
		_waitEndTime = System.currentTimeMillis() + Rnd.get(minDelay, maxDelay);
	}

	public void stopWait()
	{
		_waitEndTime = 0;
	}

	private boolean isWait()
	{
		return _waitEndTime > System.currentTimeMillis() || getIntention() == CtrlIntention.AI_INTENTION_PICK_UP;
	}

	private boolean planActions(OrdinaryActions action)
	{
		if(action == null)
			return false;

		List<AbstractAction> actions = makeActionsList(action.makeActionsList());
		if(actions.isEmpty())
			return false;

		clearPlannedActions();

		_plannedActions.addAll(actions);
		return true;
	}

	private List<AbstractAction> makeActionsList(List<AbstractAction> actions)
	{
		if(actions.isEmpty())
			return Collections.emptyList();

		List<AbstractAction> actionsList = new ArrayList<AbstractAction>();
		for(AbstractAction action : actions)
		{
			double chance = action.getChance();
			if(chance <= 0 || chance >= 100 || Rnd.chance(chance))
			{
				List<AbstractAction> tempList = action.makeActionsList();
				if(tempList == null)
					actionsList.add(action);
				else
					actionsList.addAll(makeActionsList(tempList));
			}
		}
		return actionsList;
	}

	private void clearPlannedActions()
	{
		_plannedActions.clear();
		_lastPerformedAction = null;
		_lastActionTryCount = 0;
	}

	private void clearCurrentFarmZone()
	{
		getActor().setTarget(null);
		_currentFarmZone = null;
		_goToTownTime = -1;
	}

	private boolean performNextAction(boolean force)
	{
		synchronized(this)
		{
			if(_nextShoutChatTime < System.currentTimeMillis())
			{
				FakePlayerUtils.writeToRandomChat(this);
				_nextShoutChatTime = System.currentTimeMillis() + Rnd.get(SHOUT_CHAT_MIN_DELAY, SHOUT_CHAT_MAX_DELAY);
			}

			if(isWait())
				return false;

			Player player = getActor();
			if(!player.isAlikeDead())
			{
				int dropCount = 0;
				ItemInstance dropItem = null;
				for(GameObject object : World.getAroundObjects(player, 2000, 1000))
				{
					if(object instanceof ItemInstance)
					{
						ItemInstance item = (ItemInstance) object;
						if (item.getItemId() != 8190 && item.getItemId() != 8689)
						{
							if(player.getDistance(item) > 10000 || !GeoEngine.canMoveToCoord(player.getX(), player.getY(), player.getZ(), item.getX(), item.getY(), item.getZ(), player.getGeoIndex()))
								continue;

							if(ItemFunctions.checkIfCanPickup(player, item))
							{
								if(dropItem == null || player.getDistance(item) < player.getDistance(dropItem))
									dropItem = item;
								dropCount++;
							}
						}
					}
				}
				GameObject target = player.getTarget();
				if(target != null && target instanceof Creature && Rnd.chance(95))
				{
					Creature creatureTarget = (Creature) target;
					boolean attackable = creatureTarget.isAutoAttackable(player);

					Player targetPlayer = creatureTarget.getPlayer();
					if(targetPlayer != null)
						attackable = targetPlayer.isCtrlAttackable(player, targetPlayer.isPK() || targetPlayer.getPvpFlag() > 0, false);

					if(!attackable || creatureTarget.isAlikeDead() || !creatureTarget.isVisible() || creatureTarget.isInvisible(player) || player.getDistance(creatureTarget) > 10000 || !GeoEngine.canMoveToCoord(player.getX(), player.getY(), player.getZ(), creatureTarget.getX(), creatureTarget.getY(), creatureTarget.getZ(), player.getGeoIndex()))
					{
						player.setTarget(null);
						player.abortAttack(true, false);
						player.abortCast(true, false);
						startWait(100, dropCount == 0 ? 2000 : 700);
						return true;
					}
					if((player.getAI().getAttackTarget() != creatureTarget && player.getAI().getCastTarget() != creatureTarget) || getIntention() != CtrlIntention.AI_INTENTION_ATTACK || Rnd.chance(5))
					{
						attack(creatureTarget);
						return true;
					}
				}

				if((_plannedActions.isEmpty() && getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) || Rnd.chance(5))
				{
					if(dropItem != null && !player.isMoving && !player.isMovementDisabled())
					{
						dropItem.onAction(player, false);
						startWait(500, dropCount == 1 ? 3000 : 1000);
						return true;
					}

					if(_lastSearchPvPPKTime + SEARCH_PVP_PK_DELAY < System.currentTimeMillis())
					{
						for(Creature pk : player.getAroundCharacters(1000, 250))
						{
							if(pk.isPlayer())
							{
								Player targetPlayer = pk.getPlayer();
								if(!targetPlayer.isAlikeDead() && targetPlayer.isVisible() && !targetPlayer.isInvisible(player) && player.getDistance(targetPlayer) <= 10000 && GeoEngine.canMoveToCoord(player.getX(), player.getY(), player.getZ(), pk.getX(), pk.getY(), pk.getZ(), player.getGeoIndex()))
								{
									double attackChance = 0;

									boolean targeted = (targetPlayer.getAI().getAttackTarget() == player) && (targetPlayer.getAI().getCastTarget() == player);
									boolean attackable = targetPlayer.isCtrlAttackable(player, false, false);
									if(attackable)
									{
										attackChance = 20;
										if(!targeted)
											attackChance /= 5;
									}
									else
									{
										attackable = targetPlayer.isCtrlAttackable(player, true, false);
										if(attackable)
										{
											attackChance = 5;
											if(!targeted)
												attackChance /= 5;
										}
									}
									if(Rnd.chance(attackChance))
									{
										player.setTarget(targetPlayer);
										startWait(1000, 3000);
										return true;
									}
								}
							}
						}
						_lastSearchPvPPKTime = System.currentTimeMillis();
					}
				}

				if(_lastGiveConsumableItemsTime + GIVE_CONSUMABLE_ITEMS_DELAY < System.currentTimeMillis())
				{
					FakePlayerUtils.giveConsumableItems(this);
					_lastGiveConsumableItemsTime = System.currentTimeMillis();
				}
			}

			if(!_plannedActions.isEmpty())
			{
				AbstractAction action = _plannedActions.get(0);

				_lastActionTryCount++;
				if(_lastActionTryCount <= MAX_ACTION_TRY_COUNT)
				{
					if(!action.checkCondition(this, force))
						return false;

					if(!action.performAction(this))
						return false;
				}
				else
					_lastActionTryCount = 0;

				_plannedActions.remove(action);
				_lastPerformedAction = action;
			}
			else if(player.isDead())
			{
				clearPlannedActions();
				clearCurrentFarmZone();

				_plannedActions.add(new ReviveAction());
			}
			else if(_currentFarmZone != null)
				farm();
			else
			{
				Location closestTownLoc = TeleportUtils.getRestartPoint(player, RestartType.TO_VILLAGE).getLoc();

				TownZoneTemplate townZone = null;
				loop: for(TownZoneTemplate t : FakePlayersHolder.getInstance().getTownZones())
				{
					for(ZoneTemplate zoneTemplate : t.getZoneTemplates())
					{
						if(zoneTemplate.getTerritory().isInside(closestTownLoc.x, closestTownLoc.y, closestTownLoc.z))
						{
							townZone = t;
							break loop;
						}
					}
				}

				if(townZone != null)
				{
					for(ZoneTemplate zoneTemplate : townZone.getZoneTemplates())
					{
						Territory territory = zoneTemplate.getTerritory();
						if(territory.isInside(player.getX(), player.getY(), player.getZ()))
						{
							planActions(townZone.getActions());
							return true;
						}
					}
				}

				FarmZoneTemplate farmZone = null;
				loop: for(FarmZoneTemplate f : _aiTemplate.getFarmZones())
				{
					for(ZoneTemplate zoneTemplate : f.getZoneTemplates())
					{
						if(zoneTemplate.getTerritory().isInside(player.getX(), player.getY(), player.getZ()))
						{
							farmZone = f;
							break loop;
						}
					}
				}

				if(farmZone != null)
				{
					if(!farmZone.checkCondition(player))
					{
						if(player.getLevel() >= farmZone.getMaxLevel())
						{
							OrdinaryActions actions = farmZone.getOnObtainMaxLevelAction();
							if(actions != null)
								planActions(actions);
						}
						else
							_plannedActions.add(new TeleportToClosestTownAction(100));
					}
					else
					{
						_currentFarmZone = farmZone;
						return performFarm();
					}
				}
				else
				{
					if(townZone == null)
					{
						List<FarmZoneTemplate> availableFarmZones = new ArrayList<FarmZoneTemplate>();
						for(FarmZoneTemplate f : _aiTemplate.getFarmZones())
						{
							if(f.checkCondition(player))
								availableFarmZones.add(f);
						}
						farmZone = Rnd.get(availableFarmZones);
						if(farmZone == null)
							return false;

						_currentFarmZone = farmZone;
						return performFarm();
					}

					_plannedActions.add(new TeleportToClosestTownAction(100));
				}
			}

			return true;
		}
	}

	public boolean performFarm()
	{
		Player player = getActor();

		if(_currentFarmZone == null)
		{
			List<FarmZoneTemplate> availableFarmZones = new ArrayList<FarmZoneTemplate>();
			loop: for (FarmZoneTemplate f : _aiTemplate.getFarmZones())
			{
				if (!f.checkCondition(player))
					continue;

				for(ZoneTemplate zoneTemplate : f.getZoneTemplates())
				{
					if(!zoneTemplate.getTerritory().isInside(player.getX(), player.getY(), player.getZ()))
						continue;

					availableFarmZones.add(f);
					break loop;
				}
			}

			if(availableFarmZones.isEmpty())
			{
				for(FarmZoneTemplate f : _aiTemplate.getFarmZones())
				{
					if(f.checkCondition(player))
						availableFarmZones.add(f);
				}
			}
			FarmZoneTemplate farmZone = Rnd.get(availableFarmZones);
			if(farmZone == null)
			{
				deleteFake(player);

				return false;
			}

			_currentFarmZone = farmZone;
			_lastAttackTime = System.currentTimeMillis();
		}

		clearPlannedActions();
		return true;
	}

	private void farm()
	{
		if(isWait())
			return;

		Player player = getActor();

		if(player.isMoving || player.isMovementDisabled())
			return;

		if(!_currentFarmZone.checkCondition(player))
		{
			if(player.getLevel() >= _currentFarmZone.getMaxLevel())
			{
				OrdinaryActions actions = _currentFarmZone.getOnObtainMaxLevelAction();
				if(actions != null)
					planActions(actions);
			}
			clearCurrentFarmZone();
			return;
		}

		GoToTownActions goToTownActions = _currentFarmZone.getGoToTownActions();
		if(goToTownActions != null)
		{
			if(_goToTownTime == -1)
				_goToTownTime = System.currentTimeMillis() + Rnd.get(goToTownActions.getMinFarmTime(), goToTownActions.getMaxFarmTime()) * 1000L;
			else if(_goToTownTime < System.currentTimeMillis())
			{
				planActions(goToTownActions);
				clearCurrentFarmZone();
				return;
			}
		}

		List<NpcInstance> npcs = new ArrayList<NpcInstance>();

		for(ZoneTemplate zoneTemplate : _currentFarmZone.getZoneTemplates())
		{
			Reflection reflection = player.getReflection();
			Zone zone = reflection.getZone(zoneTemplate.getName());
			if(zone == null)
			{
				zone = new Zone(zoneTemplate);
				zone.setReflection(reflection);
				zone.setActive(true);
				reflection.addZone(zone);
			}
			npcs.addAll(getNpcsForAttack(zone.getInsideNpcs()));
		}

		Collections.sort(npcs, _distanceComparator);

		for (NpcInstance npc : npcs)
		{
			if(prepareAttack(npc))
				return;
		}
		List<NpcInstance> arroundNpcs = getNpcsForAttack(player.getAroundNpc(2000, 1000));
		Collections.sort(arroundNpcs, _distanceComparator);

		for(NpcInstance npc : arroundNpcs)
		{
			if(prepareAttack(npc))
				return;
		}
		NpcInstance npc = npcs.isEmpty() ? null : npcs.get(0);

		if(_lastAttackTime + ATTACK_WAIT_DELAY < System.currentTimeMillis())
		{
			_lastAttackTime = System.currentTimeMillis();

			Location loc = npc != null ? npc.getLoc() : getRandomLoc(_currentFarmZone.getZoneTemplates(), player.getGeoIndex());
			player.teleToLocation(loc, 0, 0);
			return;
		}

		if(npc != null || !isInside(_currentFarmZone.getZoneTemplates(), player.getX(), player.getY(), player.getZ()))
		{
			Location loc = npc != null ? npc.getLoc() : getRandomLoc(_currentFarmZone.getZoneTemplates(), player.getGeoIndex());
			if(player.getDistance(loc) > 10000 || !player.moveToLocation(Location.findAroundPosition(loc, 0, player.getGeoIndex()), 0, true, 50))
			{
				Location restartLoc = Rnd.get(_currentFarmZone.getSpawnPoints());
				if(!isInside(_currentFarmZone.getZoneTemplates(), restartLoc.x, restartLoc.y, restartLoc.z))
				{
					if(PositionUtils.calculateDistance(restartLoc.x, restartLoc.y, loc.x, loc.y) > 10000)
						restartLoc = null;
				}
				if(restartLoc == null)
					restartLoc = loc;
				if(player.isInRange(restartLoc, 50))
					restartLoc = loc;
				if(!player.isInRange(restartLoc, 50))
				{
					if(player.getDistance(restartLoc) > 10000 || !player.moveToLocation(Location.findAroundPosition(restartLoc, 50, 150, player.getGeoIndex()), 0, true, 50))
						player.teleToLocation(restartLoc, 0, 0);
					return;
				}
			}
			else
				return;
		}
		Location loc = Location.coordsRandomize(player.getLoc(), 100, 300);
		if(isInside(_currentFarmZone.getZoneTemplates(), loc.x, loc.y, loc.z) && player.moveToLocation(Location.findAroundPosition(loc, 0, player.getGeoIndex()), 0, true, 50))
			startWait(1000, 10000);
	}

	private List<NpcInstance> getNpcsForAttack(List<NpcInstance> avaialbleNpcs)
	{
		Player player = getActor();

		List<NpcInstance> npcs = new ArrayList<NpcInstance>();
		for(NpcInstance n : avaialbleNpcs)
		{
			if (n.isAlikeDead())
				continue;

			if (n.isInvulnerable())
				continue;

			if (!n.isVisible())
				continue;

			if (n.isInvisible(player))
				continue;

			if (_currentFarmZone.isIgnoredMonster(n.getNpcId()))
				continue;

			IntSet farmMonsters = _currentFarmZone.getFarmMonsters();
			if (farmMonsters.isEmpty())
			{
				if (Math.abs(player.getLevel() - n.getLevel()) > 10)
					continue;
			}
			else if (!farmMonsters.contains(n.getNpcId()))
				continue;

			if (!n.isMonster())
				continue;

			if (n.isRaid())
				continue;

			if (((n.getAI().getAttackTarget() != null && n.getAI().getAttackTarget() != player) || (n.getAI().getCastTarget() != null && n.getAI().getCastTarget() != player)) && Rnd.chance(95))
				continue;

			npcs.add(n);
		}
		return npcs;
	}

	private boolean prepareAttack(Creature target)
	{
		Player player = getActor();

		if(player.getDistance(target) <= 10000 && GeoEngine.canMoveToCoord(player.getX(), player.getY(), player.getZ(), target.getX(), target.getY(), target.getZ(), player.getGeoIndex()))
		{
			if(Rnd.chance(80))
			{
				int distanceToTarget = (int) player.getDistance(target);
				for(Creature neighbor : target.getAroundCharacters(distanceToTarget + 5000, 250))
				{
					if (!neighbor.isFakePlayer())
						continue;

					if (neighbor.getTarget() != target && neighbor.getAI().getAttackTarget() != target && neighbor.getAI().getCastTarget() != target)
						continue;

					return false;
				}
			}
			if(Rnd.chance(20))
			{
				player.setCurrentHp(player.getMaxHp(), true, true);
				player.setCurrentMp(player.getMaxMp());
			}

			if(Rnd.chance(10))
			{
				if(player.getClassId().isOfRace(Race.ORC) || !player.isMageClass())
					player.moveToLocation(Location.findAroundPosition(target, 80, 180), 0, true);
				else
					player.moveToLocation(Location.findAroundPosition(target, 160, 360), 0, true);
				player.setTarget(target);
				startWait(500, 3000);
				return true;
			}

			_lastAttackTime = System.currentTimeMillis();

			player.setTarget(target);
			startWait(1000, 3000);
			return true;
		}
		return false;
	}

	private void attack(Creature target)
	{
		Player player = getActor();

		if(Rnd.chance(80))
		{
			if(tryRunOff(target))
				return;
		}
		if(Rnd.chance(5))
		{
			Skill skill = getRandomSkillSelf();
			if(skill != null)
			{
				Cast(skill, player);
				return;
			}
		}

		if(!GeoEngine.canSeeTarget(player, target, false))
		{
			if(!player.isMoving)
			{
				if(!player.moveToLocation(Location.findAroundPosition(target, 50, 150), 0, true, 50))
					player.setTarget(null);
				return;
			}
		}

		Skill skill = getRandomSkill(player, target);
		if(skill == null && player.isMageClass() && !player.getClassId().isOfRace(Race.ORC) && Rnd.chance(90))
			return;

		if(skill != null)
		{
			if(Rnd.chance(30))
			{
				Cast(skill, target, false, false);
				return;
			}
		}

		if(player.getClassId().isOfRace(Race.ORC) || !player.isMageClass())
			Attack(target, true, false);
	}

	private boolean tryRunOff(Creature target)
	{
		if(target.isAlikeDead())
			return false;

		Player player = getActor();
		if(player.getPhysicalAttackRange() > 100 || (!player.getClassId().isOfRace(Race.ORC) && player.isMageClass()))
		{
			if(player.getDistance(target) <= 200 && !player.isMoving)
			{
				int posX = player.getX();
				int posY = player.getY();
				int posZ = player.getZ();

				int old_posX = posX;
				int old_posY = posY;
				int old_posZ = posZ;

				int signx = posX < target.getX() ? -1 : 1;
				int signy = posY < target.getY() ? -1 : 1;

				int range = Math.max((int) (player.getPhysicalAttackRange() * 0.9), 200);

				posX += signx * range;
				posY += signy * range;
				posZ = GeoEngine.getHeight(posX, posY, posZ, player.getGeoIndex());

				if(GeoEngine.canMoveToCoord(old_posX, old_posY, old_posZ, posX, posY, posZ, player.getGeoIndex()))
				{
					player.abortAttack(true, false);
					if(player.moveToLocation(Location.findAroundPosition(posX, posY, posZ, 0, 0, player.getGeoIndex()), 0, true))
						return true;
				}
			}
		}
		return false;
	}

	private Skill getRandomSkillSelf()
	{
		List<Skill> skills = new ArrayList<Skill>();
		loop: for(SkillEntry skillEntry : getActor().getAllSkills())
		{
			Skill skill = skillEntry.getTemplate();
			if (!skill.isActive() && !skill.isToggle())
				continue;

			if (skill.hasEffect(EffectUseType.NORMAL, EffectType.Transformation))
				continue;

			if(getActor().isSkillDisabled(skill))
				continue;

			if(skill.getSkillType() != Skill.SkillType.BUFF)
				continue;

			for(Abnormal e : getActor().getAbnormalList())
			{
				if(checkAbnormal(e, skill))
					continue loop;
			}

			switch(skill.getTargetType())
			{
				case TARGET_ONE:
				case TARGET_SELF:
					skills.add(skill);
					break;
			}
		}

		return skills.isEmpty() ? null : skills.get(Rnd.get(skills.size()));
	}

	private boolean checkAbnormal(Abnormal abnormal, Skill skill)
	{
		if(skill.getAbnormalTime() <= 0)
			return true;

		if(abnormal == null)
			return false;

		if(!skill.hasEffects(EffectUseType.NORMAL))
			return false;

		if(abnormal.checkBlockedAbnormalType(skill.getAbnormalType()))
			return true;

		if(!AbnormalList.checkAbnormalType(abnormal.getSkill(), skill))
			return false;
		if(abnormal.getAbnormalLvl() < skill.getAbnormalLvl())
			return false;
		if(abnormal.getTimeLeft() > 10)
			return true;
		return false;
	}

	private Skill getRandomSkill(Player player, Creature target)
	{
		List<Skill> weakSkills = new ArrayList<Skill>();
		List<Skill> skills = new ArrayList<Skill>();
		for(SkillEntry skillEntry : player.getAllSkills())
		{
			Skill skill = skillEntry.getTemplate();
			if (!skill.isActive())
				continue;

			switch(skill.getId())
			{
				case 11030:
				case 30546:
				case 30547:
					continue;
				default:
					if(player.isSkillDisabled(skill))
						continue;

					if(!skill.checkCondition(player, target, false, false, true))
						continue;

					double chance = 0;

					switch(skill.getSkillType())
					{
						case DEBUFF:
						case PARALYZE:
						case ROOT:
						case STEAL_BUFF:
						case DOT:
						case AIEFFECTS:
						case CPDAM:
						case DELETE_HATE:
						case MDOT:
						case DECOY:
						case CHARGE:
						case POISON:
						case SLEEP:
						case CHARGE_SOUL:
						case DESTROY_SUMMON:
						case SHIFT_AGGRESSION:
						case DISCORD:
						case MANADAM:
						case MUTE:
							chance = 5;
							break;
						case DRAIN:
							chance = (5 + (100 - getActor().getCurrentCpPercents()) / 5) / (player.isMageClass() ? 1.0 : 3.0);
							break;
						case MDAM:
							chance = (!player.getClassId().isOfRace(Race.ORC)) && (player.isMageClass()) ? 100 : 5;
							break;
						case PDAM:
						case STUN:
						case LETHAL_SHOT:
							chance = 15;
							break;
					}

					switch (skill.getTargetType())
					{
						case TARGET_AURA:
						case TARGET_AREA:
						case TARGET_MULTIFACE:
						case TARGET_MULTIFACE_AURA:
							chance /= 10;
							break;
					}

					if(!Rnd.chance(chance))
						continue;

					if(skill.getMagicLevel() < player.getLevel() - 10)
						weakSkills.add(skill);
					else
						skills.add(skill);
			}
			continue;
		}
		if(skills.isEmpty())
			skills = weakSkills;

		return Rnd.get(skills);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		synchronized(this)
		{
			if(Rnd.chance(60))
			{
				if(tryRunOff(attacker))
					return;
			}
			if(damage > 0)
			{
				Player player = getActor();
				if(attacker.isNpc())
				{
					if(Rnd.chance(25))
					{
						player.setCurrentHp(player.getCurrentHp() + player.getMaxHp() / 5, true, true);
						player.setCurrentMp(player.getCurrentMp() + player.getMaxMp() / 5);
					}
				}

				double chance = 25;

				GameObject target = player.getTarget();
				if(target != null)
				{
					if(target == attacker)
						return;

					if(target instanceof Creature)
					{
						Creature creatureTarget = (Creature) target;
						if(creatureTarget.getAI().getAttackTarget() != player && creatureTarget.getAI().getCastTarget() != player)
							chance = 80;
						else
							chance = 5;
					}
					else
						return;
				}
				if(attacker.isPlayable())
					chance = 30;

				if(attacker.getLevel() - player.getLevel() >= 10)
					chance /= 5;

				if(Rnd.chance(chance))
				{
					player.setTarget(attacker);
					stopWait();
				}
			}
		}
	}

	@Override
	public void onDeath(Creature actor, Creature killer)
	{
		synchronized(this)
		{
			clearPlannedActions();
			clearCurrentFarmZone();

			startWait(2000, 15000);
			_plannedActions.add(new ReviveAction());
		}
	}

	@Override
	public void onLevelChange(Player player, int oldLvl, int newLvl)
	{
		synchronized(this)
		{
			if(player.isFakePlayer())
			{
				FakePlayerUtils.setProf(player);

				if(FakePlayerUtils.checkInventory(this))
					startWait(200, 5000);
			}
			if(player.getLevel() == Config.ALT_MAX_LEVEL)
				deleteFake(player);
		}
	}

	private static void deleteFake(Player player)
	{
		if(!player.isFakePlayer())
			return;

		int objectId = player.getObjectId();
		player.kick();
		CharacterDAO.getInstance().deleteCharByObjId(objectId);
		FakePlayersTable.spawnNewFakePlayer();
	}

	@Override
	public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
	{
		synchronized(this)
		{
			startWait(2000, 5000);
		}
	}

	@Override
	public void onChatMessageReceive(Player player, ChatType type, String charName, String text)
	{
		if(type == ChatType.TELL)
			FakePlayerUtils.writeInPrivateChat(this, charName);
	}

	public void runImpl() throws Exception
	{
		Player actor = getActor();
		if(actor == null)
		{
			stopActionTask();
			return;
		}
		performNextAction(false);
	}

	private synchronized void startActionTask()
	{
		if(_actionTask == null)
			_actionTask = ThreadPoolManager.getInstance().scheduleAtFixedDelay(this, 500, 500);
	}

	private synchronized void stopActionTask()
	{
		if(_actionTask != null)
		{
			_actionTask.cancel(true);
			_actionTask = null;
		}
	}

	private static Location getRandomLoc(List<ZoneTemplate> zoneTemplates, int geoIndex)
	{
		ZoneTemplate zoneTemplate = Rnd.get(zoneTemplates);
		if(zoneTemplate != null)
			return zoneTemplate.getTerritory().getRandomLoc(geoIndex);
		return new Location();
	}

	private static boolean isInside(List<ZoneTemplate> zoneTemplates, int x, int y, int z)
	{
		for(ZoneTemplate zoneTemplate : zoneTemplates)
		{
			if(zoneTemplate.getTerritory().isInside(x, y, z))
				return true;
		}
		return false;
	}
}