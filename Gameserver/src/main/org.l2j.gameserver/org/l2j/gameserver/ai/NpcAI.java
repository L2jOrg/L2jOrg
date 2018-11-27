package org.l2j.gameserver.ai;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.WorldRegion;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.s2c.ExRotation;
import org.l2j.gameserver.network.l2.s2c.SocialActionPacket;
import org.l2j.gameserver.taskmanager.AiTaskManager;
import org.l2j.gameserver.templates.npc.RandomActions;
import org.l2j.gameserver.templates.npc.WalkerRoute;
import org.l2j.gameserver.templates.npc.WalkerRoutePoint;
import org.l2j.gameserver.utils.Functions;
import org.l2j.gameserver.utils.Location;

import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.CArrayIntSet;

/**
 * @author Bonux
 */
public class NpcAI extends CharacterAI
{
	public static final String WALKER_ROUTE_PARAM = "walker_route_id";

	private static final int WALKER_ROUTE_TIMER_ID = -1000;
	private static final int RANDOM_ACTION_TIMER_ID = -2000;

	protected ScheduledFuture<?> _aiTask;
	protected long _attackAITaskDelay = Config.AI_TASK_ATTACK_DELAY;
	protected long _activeAITaskDelay = Config.AI_TASK_ACTIVE_DELAY;
	protected long _currentAITaskDelay = _activeAITaskDelay;

	protected Lock _thinking = new ReentrantLock();

	//random actions params
	private final RandomActions _randomActions;
	private final boolean _haveRandomActions;
	private int _currentActionId;

	//Walker Routes params
	private WalkerRoute _walkerRoute;
	private boolean _haveWalkerRoute;
	private boolean _toBackWay;
	private int _currentWalkerPoint;
	private boolean _delete;

	private IntSet _neighbors = null;
	private long _lastNeighborsClean = 0;

	protected boolean _isGlobal;

	protected long _lastActiveCheck;
	private int _walkTryCount = 0;

	public NpcAI(NpcInstance actor)
	{
		super(actor);

		//initialize random actions params
		_randomActions = actor.getTemplate().getRandomActions();
		_haveRandomActions = _randomActions != null && _randomActions.getActionsCount() > 0;
		_currentActionId = 0; //При спавне начинаем действия с 1го действия.

		//initialize Walker Routes params
		setWalkerRoute(actor.getParameter(WALKER_ROUTE_PARAM, -1));

		_isGlobal = actor.getParameter("GlobalAI", false);
	}

	public void setWalkerRoute(WalkerRoute walkerRoute)
	{
		_walkerRoute = walkerRoute;
		_haveWalkerRoute = _walkerRoute != null && _walkerRoute.isValid();
		_toBackWay = false;
		_currentWalkerPoint = -1;
		_delete = false;

		if(isActive())
			setIntention(CtrlIntention.AI_INTENTION_WALKER_ROUTE);
	}

	public void setWalkerRoute(int id)
	{
		setWalkerRoute(getActor().getTemplate().getWalkerRoute(id));
	}

	@Override
	protected void onEvtArrived()
	{
		NpcInstance actor = getActor();
		actor.broadcastPacket(new ExRotation(actor.getObjectId(), actor.getHeading()));
		continueWalkerRoute();
	}

	@Override
	protected void onEvtTeleported()
	{
		continueWalkerRoute();
	}

	@Override
	protected void onEvtSeeCreatue(Creature creature)
	{
		getActor().onSeeCreatue(creature);
	}

	@Override
	protected void onEvtDisappearCreatue(Creature creature)
	{
		getActor().onDisappearCreatue(creature);
	}

	@Override
	protected void onIntentionWalkerRoute()
	{
		if(_haveWalkerRoute)
		{
			clientStopMoving();
			moveToNextPoint(0);
			changeIntention(CtrlIntention.AI_INTENTION_WALKER_ROUTE, null, null);
		}
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		NpcInstance actor = getActor();
		if(timerId == WALKER_ROUTE_TIMER_ID)
		{
			if(_haveWalkerRoute)
			{
				if(!(arg1 instanceof Location))
					return;

				if(((Boolean) arg2).booleanValue())
				{
					actor.teleToLocation((Location) arg1);
					continueWalkerRoute();
				}
				else
					moveToLocation((Location) arg1);
			}
		}
		else if(timerId == RANDOM_ACTION_TIMER_ID)
		{
			if(_haveRandomActions)
				makeRandomAction();
		}

		actor.onTimerFired(timerId);
	}

	@Override
	protected void onEvtThink()
	{
		NpcInstance actor = getActor();
		if(actor == null || actor.isActionsDisabled())
			return;

		if(!_thinking.tryLock())
			return;

		try
		{
			if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || getIntention() == CtrlIntention.AI_INTENTION_WALKER_ROUTE)
				thinkActive();
		}
		finally
		{
			_thinking.unlock();
		}
	}

	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return false;

		lookNeighbor(actor.getAggroRange(), false);

		if(_haveWalkerRoute)
		{
			if(getIntention() == CtrlIntention.AI_INTENTION_WALKER_ROUTE)
			{
				if(!actor.isMoving && !haveTask(WALKER_ROUTE_TIMER_ID))
				{
					_walkTryCount++;
					if(_walkTryCount >= 10)
					{
						moveToNextPoint(0);
						return true;
					}
				}
			}
			else
			{
				changeIntention(CtrlIntention.AI_INTENTION_WALKER_ROUTE, null, null);
				moveToLocation(actor.getSpawnedLoc());
				return true;
			}
		}
		return false;
	}

	@Override
	public final synchronized void startAITask()
	{
		if(_aiTask == null)
		{
			_currentAITaskDelay = _activeAITaskDelay;
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0, _currentAITaskDelay);
		}

		if(_haveWalkerRoute)
			setIntention(CtrlIntention.AI_INTENTION_WALKER_ROUTE);

		if(_haveRandomActions)
		{
			RandomActions.Action action = _randomActions.getAction(1);
			if(action != null) //При спауне начинаем делать действия через случайное время, иначе все нпс будут одновременно начинать, что будет не очень красиво.
				addTask(RANDOM_ACTION_TIMER_ID, Rnd.get(0, action.getDelay()) * 1000L);
		}
	}

	protected final synchronized void switchAITask(long delay)
	{
		if(_aiTask != null)
		{
			if(_currentAITaskDelay == delay)
				return;
			_aiTask.cancel(false);
		}

		_currentAITaskDelay = delay;
		_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0, _currentAITaskDelay);
	}

	@Override
	public final synchronized void stopAITask()
	{
		if(_aiTask != null)
		{
			_aiTask.cancel(false);
			_aiTask = null;
		}
	}

	@Override
	public boolean isGlobalAI()
	{
		return _isGlobal;
	}

	@Override
	public boolean isActive()
	{
		return _aiTask != null;
	}

	@Override
	public void runImpl()
	{
		if(!isActive())
			return;

		if(!isGlobalAI() && System.currentTimeMillis() - _lastActiveCheck > 60000)
		{
			_lastActiveCheck = System.currentTimeMillis();
			NpcInstance actor = getActor();
			WorldRegion region = actor == null ? null : actor.getCurrentRegion();
			if(region == null || !region.isActive())
			{
				stopAITask();
				return;
			}
		}
		onEvtThink();
	}

	private void continueWalkerRoute()
	{
		if(!isActive() || getIntention() != CtrlIntention.AI_INTENTION_WALKER_ROUTE)
			return;

		//Когда дошли, говорим фразу, делаем социальное действие, и через указаный промежуток времени начием идти дальше
		if(_haveWalkerRoute)
		{
			if(_currentWalkerPoint >= 0)
			{
				WalkerRoutePoint route = _walkerRoute.getPoint(_currentWalkerPoint);
				if(route == null)
				{
					moveToNextPoint(0);
					return;
				}

				NpcInstance actor = getActor();
				int socialActionId = route.getSocialActionId();
				if(socialActionId >= 0)
					actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), socialActionId));

				NpcString phrase = Rnd.get(route.getPhrases());
				if(phrase != null)
					Functions.npcSay(actor, phrase);

				moveToNextPoint(route.getDelay());
			}
			else
				moveToNextPoint(0);
		}
	}

	private void moveToNextPoint(int delay)
	{
		if(!isActive() || getIntention() != CtrlIntention.AI_INTENTION_WALKER_ROUTE)
			return;

		if(!_haveWalkerRoute)
			return;

		_walkTryCount = 0;

		NpcInstance actor = getActor();
		if(actor == null)
			return;

		switch(_walkerRoute.getType())
		{
			case LENGTH:
			{
				if(_toBackWay)
					_currentWalkerPoint--;
				else
					_currentWalkerPoint++;

				if(_currentWalkerPoint >= _walkerRoute.size() - 1)
					_toBackWay = true;

				if(_currentWalkerPoint == 0)
					_toBackWay = false;
				break;
			}
			case ROUND:
			{
				_currentWalkerPoint++;

				if(_currentWalkerPoint >= _walkerRoute.size())
					_currentWalkerPoint = 0;
				break;
			}
			case RANDOM:
			{
				if(_walkerRoute.size() > 1)
				{
					int oldPoint = _currentWalkerPoint;
					while(oldPoint == _currentWalkerPoint)
						_currentWalkerPoint = Rnd.get(_walkerRoute.size() - 1);
				}
				break;
			}
			case DELETE:
			{
				if(_delete)
				{
					actor.deleteMe(); // TODO: [Bonux] Мб сделать, чтобы он респаунился? Если респаун указан в спавне.
					return;
				}
				_currentWalkerPoint++;
				if(_currentWalkerPoint >= _walkerRoute.size())
					_delete = true;
				break;
			}
			case FINISH:
			{
				_currentWalkerPoint++;
				if(_currentWalkerPoint >= _walkerRoute.size())
				{
					actor.stopMove();
					setWalkerRoute(null);
					notifyEvent(CtrlEvent.EVT_FINISH_WALKER_ROUTE);
					return;
				}
				break;
			}
		}

		WalkerRoutePoint route = _walkerRoute.getPoint(_currentWalkerPoint);
		if(route == null)
			return;

		if(route.isRunning())
			actor.setRunning();
		else
			actor.setWalking();

		if(delay > 0)
			addTask(WALKER_ROUTE_TIMER_ID, route.getLocation(), route.isTeleport(), delay * 1000L);
		else if(route.isTeleport())
		{
			actor.teleToLocation(route.getLocation());

			continueWalkerRoute();
		}
		else
			moveToLocation(route.getLocation());
	}

	private void makeRandomAction()
	{
		if(!isActive())
			return;

		if(!_haveRandomActions)
			return;

		NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || getIntention() == CtrlIntention.AI_INTENTION_WALKER_ROUTE)
		{
			_currentActionId++;
			if(_currentActionId > _randomActions.getActionsCount())
				_currentActionId = 1;

			RandomActions.Action action = _randomActions.getAction(_currentActionId);
			if(action == null)
				return;

			int socialActionId = action.getSocialActionId();
			if(socialActionId >= 0)
				actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), socialActionId));

			NpcString phrase = action.getPhrase();
			if(phrase != null)
				Functions.npcSay(actor, phrase);

			addTask(RANDOM_ACTION_TIMER_ID, action.getDelay() * 1000L);
		}
		else
			addTask(RANDOM_ACTION_TIMER_ID, 1000L);
	}

	private void moveToLocation(Location loc)
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(getIntention() == CtrlIntention.AI_INTENTION_WALKER_ROUTE)
		{
			loc = Location.findPointToStay(loc, 50, actor.getGeoIndex());
			actor.setSpawnedLoc(loc);
			if (!actor.moveToLocation(loc, 0, true))
			{
				clientStopMoving();
				actor.teleToLocation(loc);
			}
		}
	}

	@Override
	public NpcInstance getActor()
	{
		return (NpcInstance) super.getActor();
	}

	protected boolean isHaveRandomActions()
	{
		return _haveRandomActions;
	}

	protected boolean isHaveWalkerRoute()
	{
		return _haveWalkerRoute;
	}

	protected boolean lookNeighbor(int range, boolean force)
	{
		if(!isActive())
			return false;

		if(range <= 0)
			return false;

		NpcInstance actor = getActor();
		if(actor == null)
			return false;

		if(_neighbors == null)
			_neighbors = new CArrayIntSet();

		for(Creature creature : actor.getAroundCharacters(range, range))
		{
			if(!creature.isInvisible(actor) && !_neighbors.contains(creature.getObjectId()))
			{
				notifyEvent(CtrlEvent.EVT_SEE_CREATURE, creature);
				_neighbors.add(creature.getObjectId());
			}
		}

		for (int objectId : _neighbors.toArray())
		{
			GameObject object = GameObjectsStorage.findObject(objectId);
			if(object == null || !actor.isInRange(object, range))
			{
				_neighbors.remove(objectId);
				if(object != null && object instanceof Creature)
					notifyEvent(CtrlEvent.EVT_DISAPPEAR_CREATURE, (Creature) object);
			}
		}
		return true;
	}

	private void removeNeighbor(Creature creature)
	{
		if(_neighbors != null)
			_neighbors.remove(creature.getObjectId());
	}

	protected boolean hasRandomWalk()
	{
		return !_haveWalkerRoute && getActor().hasRandomWalk();
	}

	public boolean returnHomeAndRestore(boolean running)
	{
		return false;
	}
}