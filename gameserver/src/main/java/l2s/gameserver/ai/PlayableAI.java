package l2s.gameserver.ai;

import static l2s.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static l2s.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static l2s.gameserver.ai.CtrlIntention.AI_INTENTION_CAST;
import static l2s.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;
import static l2s.gameserver.ai.CtrlIntention.AI_INTENTION_INTERACT;
import static l2s.gameserver.ai.CtrlIntention.AI_INTENTION_PICK_UP;

import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.NextAction;
import l2s.gameserver.model.Skill.SkillTargetType;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket.FlyType;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;
import l2s.gameserver.model.FakePlayer;

public class PlayableAI extends CharacterAI
{
	private volatile int thinking = 0; // to prevent recursive thinking

	protected Object _intention_arg0 = null, _intention_arg1 = null;
	protected Skill _skill;

	private AINextAction _nextAction;
	private Object _nextAction_arg0;
	private Object _nextAction_arg1;
	private boolean _nextAction_arg2;
	private boolean _nextAction_arg3;

	protected boolean _forceUse;
	private boolean _dontMove;

	private ScheduledFuture<?> _followTask;

	public PlayableAI(Playable actor)
	{
		super(actor);
	}

	public enum AINextAction
	{
		ATTACK,
		CAST,
		MOVE,
		REST,
		PICKUP,
		EQUIP,
		INTERACT,
		COUPLE_ACTION
	}

	@Override
	public void changeIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		super.changeIntention(intention, arg0, arg1);
		_intention_arg0 = arg0;
		_intention_arg1 = arg1;
	}

	@Override
	public void setIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		_intention_arg0 = null;
		_intention_arg1 = null;
		super.setIntention(intention, arg0, arg1);
	}

	@Override
	protected void onIntentionCast(Skill skill, Creature target)
	{
		_skill = skill;
		super.onIntentionCast(skill, target);
	}

	@Override
	public void setNextAction(AINextAction action, Object arg0, Object arg1, boolean arg2, boolean arg3)
	{
		_nextAction = action;
		_nextAction_arg0 = arg0;
		_nextAction_arg1 = arg1;
		_nextAction_arg2 = arg2;
		_nextAction_arg3 = arg3;
	}

	public boolean setNextIntention()
	{
		AINextAction nextAction = _nextAction;
		Object nextAction_arg0 = _nextAction_arg0;
		Object nextAction_arg1 = _nextAction_arg1;
		boolean nextAction_arg2 = _nextAction_arg2;
		boolean nextAction_arg3 = _nextAction_arg3;

		Playable actor = getActor();
		if(nextAction == null)
			return false;

		if(nextAction == AINextAction.CAST)
		{
			if(actor.isActionsDisabled(false) || actor.isCastingNow())
				return false;
		}
		else if(actor.isActionsDisabled())
			return false;

		Skill skill;
		Creature target;
		GameObject object;

		switch(nextAction)
		{
			case ATTACK:
				if(nextAction_arg0 == null)
					return false;
				target = (Creature) nextAction_arg0;
				_forceUse = nextAction_arg2;
				_dontMove = nextAction_arg3;
				clearNextAction();
				setIntention(AI_INTENTION_ATTACK, target);
				break;
			case CAST:
				if(nextAction_arg0 == null || nextAction_arg1 == null)
					return false;
				skill = (Skill) nextAction_arg0;
				target = (Creature) nextAction_arg1;
				_forceUse = nextAction_arg2;
				_dontMove = nextAction_arg3;
				clearNextAction();
				if(!skill.checkCondition(actor, target, _forceUse, _dontMove, true))
				{
					setIntention(AI_INTENTION_ACTIVE);
					actor.sendActionFailed();
					return false;
				}
				setIntention(AI_INTENTION_CAST, skill, target);
				break;
			case MOVE:
				if(nextAction_arg0 == null || nextAction_arg1 == null)
					return false;
				Location loc = (Location) nextAction_arg0;
				Integer offset = (Integer) nextAction_arg1;
				clearNextAction();
				actor.moveToLocation(loc, offset, nextAction_arg2);
				break;
			case REST:
				actor.sitDown(null);
				break;
			case INTERACT:
				if(nextAction_arg0 == null)
					return false;
				object = (GameObject) nextAction_arg0;
				clearNextAction();
				onIntentionInteract(object);
				break;
			case PICKUP:
				if(nextAction_arg0 == null)
					return false;
				object = (GameObject) nextAction_arg0;
				clearNextAction();
				onIntentionPickUp(object);
				break;
			case EQUIP:
				if(!(nextAction_arg0 instanceof ItemInstance))
					return false;
				ItemInstance item = (ItemInstance) nextAction_arg0;
				if(item.isEquipable())
					actor.useItem(item, nextAction_arg2, nextAction_arg3);
				clearNextAction();
				if(getIntention() == AI_INTENTION_ATTACK) // autoattack not aborted
					return false;
				break;
			case COUPLE_ACTION:
				if(nextAction_arg0 == null || nextAction_arg1 == null)
					return false;
				target = (Creature) nextAction_arg0;
				int socialId = (Integer) nextAction_arg1;
				_forceUse = nextAction_arg2;
				_nextAction = null;
				clearNextAction();
				onIntentionCoupleAction((Player) target, socialId);
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public void clearNextAction()
	{
		_nextAction = null;
		_nextAction_arg0 = null;
		_nextAction_arg1 = null;
		_nextAction_arg2 = false;
		_nextAction_arg3 = false;
	}

	@Override
	public AINextAction getNextAction()
	{
		return _nextAction;
	}

	@Override
	public Object[] getNextActionArgs()
	{
		return new Object[] { _nextAction_arg0, _nextAction_arg1 };
	}

	@Override
	protected void onEvtFinishCasting(Skill skill, Creature target, boolean success)
	{
		if(!setNextIntention())
			setIntention(AI_INTENTION_ACTIVE);
	}

	@Override
	protected void onEvtReadyToAct()
	{
		if(!setNextIntention())
			onEvtThink();
	}

	@Override
	protected void onEvtArrived()
	{
		if(!setNextIntention())
		{
			if(getIntention() == AI_INTENTION_ATTACK)
				thinkAttack(true);
			else if(getIntention() == AI_INTENTION_CAST)
				thinkCast(true);
			else if(getIntention() == AI_INTENTION_INTERACT || getIntention() == AI_INTENTION_PICK_UP)
				onEvtThink();
			else
				changeIntention(AI_INTENTION_ACTIVE, null, null);
		}
	}

	@Override
	protected void onEvtArrivedTarget()
	{
		switch(getIntention())
		{
			case AI_INTENTION_ATTACK:
				thinkAttack(true);
				break;
			case AI_INTENTION_CAST:
				thinkCast(true);
				break;
			case AI_INTENTION_FOLLOW:
				thinkFollow();
				break;
			default:
				onEvtThink();
				break;
		}
	}

	@Override
	protected final void onEvtThink()
	{
		Playable actor = getActor();
		CtrlIntention intention = getIntention();

		if(intention == AI_INTENTION_CAST)
		{
			if(actor.isActionsDisabled(false) || actor.isCastingNow())
				return;
		}
		else if(actor.isActionsDisabled())
			return;

		try
		{
			if(thinking++ > 1)
				return;

			switch(intention)
			{
				case AI_INTENTION_ACTIVE:
					thinkActive();
					break;
				case AI_INTENTION_ATTACK:
					thinkAttack(false);
					break;
				case AI_INTENTION_CAST:
					thinkCast(false);
					break;
				case AI_INTENTION_PICK_UP:
					thinkPickUp();
					break;
				case AI_INTENTION_INTERACT:
					thinkInteract();
					break;
				case AI_INTENTION_FOLLOW:
					thinkFollow();
					break;
				case AI_INTENTION_COUPLE_ACTION:
					thinkCoupleAction((Player) _intention_arg0, (Integer) _intention_arg1, false);
					break;
			}
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			thinking--;
		}
	}

	protected void thinkActive()
	{

	}

	protected void thinkFollow()
	{
		Playable actor = getActor();

		Creature target = (Creature) _intention_arg0;
		Integer offset = (Integer) _intention_arg1;

		//Находимся слишком далеко цели, либо цель не пригодна для следования
		if(target == null || actor.getDistance(target) > 4000 || offset == null || actor.getReflection() != target.getReflection())
		{
			clientActionFailed();
			return;
		}

		//Уже следуем за этой целью
		if(actor.isFollow && actor.getFollowTarget() == target)
		{
			clientActionFailed();
			return;
		}

		//Находимся достаточно близко или не можем двигаться - побежим потом ?
		if(actor.isInRange(target, offset + 20) || actor.isMovementDisabled())
			clientActionFailed();

		if(_followTask != null)
		{
			_followTask.cancel(false);
			_followTask = null;
		}

		_followTask = ThreadPoolManager.getInstance().schedule(new ThinkFollow(), 250L);
	}

	protected class ThinkFollow extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			Playable actor = getActor();

			if(getIntention() != AI_INTENTION_FOLLOW)
			{
				// Если пет прекратил преследование, меняем статус, чтобы не пришлось щелкать на кнопку следования 2 раза.
				if(actor.isServitor() && getIntention() == AI_INTENTION_ACTIVE)
					((Servitor) actor).setFollowMode(false);
				return;
			}

			if(!(_intention_arg0 instanceof Creature))
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}

			Creature target = (Creature) _intention_arg0;
			if(actor.getDistance(target) > 4000 || actor.getReflection() != target.getReflection())
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}

			Player player = actor.getPlayer();
			if(player == null || player.isLogoutStarted() || actor.isServitor() && !player.isMyServitor(actor.getObjectId()))
			{
				setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				return;
			}

			int offset = _intention_arg1 instanceof Integer ? (Integer) _intention_arg1 : 0;

			if(!actor.isAfraid() && !actor.isInRange(target, offset + 20) && (!actor.isFollow || actor.getFollowTarget() != target))
			{
				if(actor.isServitor()) // Заглушка чтобы саммоны не бегали кучей.
				{
					final int servitorsCount = actor.getPlayer().getServitorsCount();
					if(servitorsCount > 1)
					{
						final int frontMaxRadius = 6000;
						final int heading = target.getHeading();
						final int radius = frontMaxRadius / (servitorsCount - 1) * (((Servitor) actor).getIndex() - 1) - (frontMaxRadius / 2);
						final int x = (int) (target.getX() - offset * Math.sin(PositionUtils.convertHeadingToRadian(radius + heading)));
						final int y = (int) (target.getY() + offset * Math.cos(PositionUtils.convertHeadingToRadian(radius + heading)));
						actor.followToCharacter(new Location(x, y, target.getZ()), target, offset, false);
					}
					else
						actor.followToCharacter(target, offset, false);
				}
				else if(actor instanceof FakePlayer)
				{
					Location loc = new Location(target.getX() + 30, target.getY() + 30, target.getZ());
					actor.followToCharacter(loc, target, offset, false);
				}
				else
					actor.followToCharacter(target, offset, false);
			}
			_followTask = ThreadPoolManager.getInstance().schedule(this, 250L);
		}
	}

	protected class ExecuteFollow extends RunnableImpl
	{
		private Creature _target;
		private Location _loc;
		private int _range;

		public ExecuteFollow(Creature target, int range)
		{
			this(target, null, range);
		}

		public ExecuteFollow(Creature target, Location loc, int range)
		{
			_target = target;
			_loc = loc;
			_range = range;
		}

		@Override
		public void runImpl()
		{
			if(_loc != null)
				_actor.moveToLocation(_loc, _range, true, false, false);
			else if(_target.isDoor())
				_actor.moveToLocation(_target.getLoc(), 40, true, false, false);
			else
				_actor.followToCharacter(_target, _range, false);
		}
	}

	@Override
	protected void onIntentionInteract(GameObject object)
	{
		Playable actor = getActor();

		if(actor.isActionsDisabled())
		{
			setNextAction(AINextAction.INTERACT, object, null, false, false);
			clientActionFailed();
			return;
		}

		clearNextAction();
		changeIntention(AI_INTENTION_INTERACT, object, null);
		onEvtThink();
	}

	@Override
	protected void onIntentionCoupleAction(Player player, Integer socialId)
	{
		_nextAction = null;
		clearNextAction();
		changeIntention(CtrlIntention.AI_INTENTION_COUPLE_ACTION, player, socialId);
		onEvtThink();
	}

	protected void thinkInteract()
	{
		Playable actor = getActor();

		GameObject target = (GameObject) _intention_arg0;

		if(target == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}

		int range = actor.getInteractionDistance(target);

		if(actor.isInRangeZ(target, range))
		{
			if(actor.isPlayer())
				((Player) actor).doInteract(target);
			setIntention(AI_INTENTION_ACTIVE);
		}
		else
		{
			actor.moveToLocation(target.getLoc(), Math.max(range - 20, 0), true);
			setNextAction(AINextAction.INTERACT, target, null, false, false);
		}
	}

	@Override
	protected void onIntentionPickUp(GameObject object)
	{
		Playable actor = getActor();

		if(actor.isActionsDisabled())
		{
			setNextAction(AINextAction.PICKUP, object, null, false, false);
			clientActionFailed();
			return;
		}

		clearNextAction();
		changeIntention(AI_INTENTION_PICK_UP, object, null);
		onEvtThink();
	}

	protected void thinkPickUp()
	{
		final Playable actor = getActor();

		final GameObject target = (GameObject) _intention_arg0;

		if(target == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}

		if(actor.isInRange(target, 30) && Math.abs(actor.getZ() - target.getZ()) < 50)
		{
			if(actor.isPlayer() || actor.isPet())
				actor.doPickupItem(target);
			setIntention(AI_INTENTION_ACTIVE);
		}
		else
			ThreadPoolManager.getInstance().execute(() -> {
				actor.moveToLocation(target.getLoc(), 10, true);
				setNextAction(AINextAction.PICKUP, target, null, false, false);
			});
	}

	protected void thinkAttack(boolean arrived)
	{
		Playable actor = getActor();

		Player player = actor.getPlayer();
		if(player == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			return;
		}

		if(actor.isActionsDisabled() || actor.isAttackingDisabled())
		{
			actor.sendActionFailed();
			return;
		}

		Creature attack_target = getAttackTarget();
		if(attack_target == null || attack_target.isDead())
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return;
		}

		int range = Math.max(10, actor.getPhysicalAttackRange()) + (int) actor.getMinDistance(attack_target);
		if(!actor.isInRangeZ(attack_target, range + (arrived ? 40 : 20)))
		{
			if(_dontMove)
			{
				actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
				setIntention(AI_INTENTION_ACTIVE);
				actor.sendActionFailed();
			}
			else if(!actor.followToCharacter(attack_target, range, false))
				actor.moveToLocation(attack_target.getLoc(), range, true, false, false);
			return;
		}

		if(!GeoEngine.canSeeTarget(actor, attack_target, false))
		{
			if(actor.isPlayer())
			{
				actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
				setIntention(AI_INTENTION_ACTIVE);
				actor.sendActionFailed();
			}
			else
			{
				if(!actor.followToCharacter(attack_target, range, false))
				{
					if(!actor.moveToLocation(attack_target.getLoc(), range, true, false, false))
					{
						setIntention(AI_INTENTION_ACTIVE);
						actor.sendActionFailed();
					}
				}
			}
			return;
		}

		if(actor.isServitor() && !((Servitor) actor).isDepressed())
		{
			if(!_forceUse && attack_target.isServitor() && actor.getPlayer().isMyServitor(attack_target.getObjectId()))
				return;

			if(!_forceUse && attack_target.isCreature() && !attack_target.isAutoAttackable(actor))
				return;

			if(_forceUse && !attack_target.isAttackable(actor))
				return;

			if(!attack_target.isMonster() && (actor.isInPeaceZone() || attack_target.isCreature() && attack_target.isInPeaceZone()))
				return;
		}

		clientStopMoving(false);
		actor.doAttack(attack_target);
	}

	protected boolean thinkCast(boolean arrived)
	{
		Playable actor = getActor();

		Creature target = getCastTarget();

		if(_skill.getSkillType() == SkillType.CRAFT || (_skill.isToggle() && _skill.getHitTime() <= 0))
		{
			if(_skill.checkCondition(actor, target, _forceUse, _dontMove, true))
				actor.doCast(_skill.getEntry(), target, _forceUse);
			return true;
		}

		if(target == null)
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return false;
		}

		boolean isCorpseSkill = _skill.isCorpse() || _skill.getTargetType() == SkillTargetType.TARGET_AREA_AIM_CORPSE;
		if(target.isDead() != isCorpseSkill && !_skill.isNotTargetAoE())
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
			return false;
		}

		final boolean isGroundSkill = _skill.getTargetType() == SkillTargetType.TARGET_GROUND;

		Location targetLoc = target.getLoc();
		if(isGroundSkill)
		{
			if(actor.isPlayer())
			{
				Location groundLoc = actor.getPlayer().getGroundSkillLoc();
				if(groundLoc == null)
				{
					setIntention(AI_INTENTION_ACTIVE);
					actor.sendActionFailed();
					return false;
				}

				targetLoc = groundLoc;
			}
			else
			{
				setIntention(AI_INTENTION_ACTIVE);
				actor.sendActionFailed();
				return false;
			}
		}

		boolean noRangeSkill = _skill.getCastRange() == -1;
		if(!noRangeSkill)
		{
			int range = Math.max(10, actor.getMagicalAttackRange(_skill));
			if(!isGroundSkill)
				range += actor.getMinDistance(target);

			if(!actor.isInRangeZ(targetLoc, range + (arrived ? 40 : 20)))
			{
				if(_dontMove)
				{
					if(!isGroundSkill)
						actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);

					setIntention(AI_INTENTION_ACTIVE);
					actor.sendActionFailed();
				}
				else if(isGroundSkill || !actor.followToCharacter(target, range, false))
					actor.moveToLocation(targetLoc, range, true, false, false);
				return false;
			}

			boolean canSee = isGroundSkill || _skill.getSkillType() == SkillType.TAKECASTLE || GeoEngine.canSeeTarget(actor, target, actor.isFlying());
			if(!canSee)
			{
				if(actor.isPlayer())
				{
					if(!isGroundSkill)
						actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);

					setIntention(AI_INTENTION_ACTIVE);
					actor.sendActionFailed();
				}
				else
				{
					if(!actor.followToCharacter(target, range, false))
					{
						if(!actor.moveToLocation(targetLoc, range, true, false, false))
						{
							setIntention(AI_INTENTION_ACTIVE);
							actor.sendActionFailed();
						}
					}
				}
				return false;
			}
		}

		if(actor.isFakeDeath())
			actor.breakFakeDeath();

		if(_skill.checkCondition(actor, target, _forceUse, _dontMove, true))
		{
			if(target.isAutoAttackable(actor))
			{
				// Если скилл имеет следующее действие, назначим это действие после окончания действия скилла
				if(_skill.getNextAction() == NextAction.ATTACK && !actor.equals(target))
					setNextAction(AINextAction.ATTACK, target, null, _forceUse, false);
				else if(_skill.getNextAction() == NextAction.CAST && !actor.equals(target))
					setNextAction(AINextAction.CAST, _skill, target, false, _dontMove);
				else
					clearNextAction();
			}
			else
				clearNextAction();

			clientStopMoving(false);
			actor.doCast(_skill.getEntry(), target, _forceUse);
			return true;
		}
		else
		{
			setIntention(AI_INTENTION_ACTIVE);
			actor.sendActionFailed();
		}
		return false;
	}

	protected void thinkCoupleAction(Player target, Integer socialId, boolean cancel)
	{
		//
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		clearNextAction();
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtFakeDeath()
	{
		clearNextAction();
		super.onEvtFakeDeath();
	}

	@Override
	public void Attack(GameObject target, boolean forceUse, boolean dontMove)
	{
		Playable actor = getActor();

		if(target.isCreature() && (actor.isActionsDisabled() || actor.isAttackingDisabled()))
		{
			// Если не можем атаковать, то атаковать позже
			setNextAction(AINextAction.ATTACK, target, null, forceUse, false);
			actor.sendActionFailed();
			return;
		}

		_dontMove = dontMove;
		_forceUse = forceUse;
		clearNextAction();
		setIntention(AI_INTENTION_ATTACK, target);
	}

	@Override
	public boolean Cast(Skill skill, Creature target, boolean forceUse, boolean dontMove)
	{
		Playable actor = getActor();
		
		if(skill.isCanUseWhileAbnormal() && (actor.isStunned() || actor.isSleeping() || actor.isDecontrolled() || actor.isFrozen())) //trying to use without making the skills alt or handler for 11093
		{
			actor.altUseSkill(skill, target);
			return true;
		}
		
		if(actor.getAbnormalList().contains(1570))
		{
			clientActionFailed();
			return false;
		}

		// Если скилл альтернативного типа (например, бутылка на хп),
		// то он может использоваться во время каста других скиллов, или во время атаки, или на бегу.
		// Поэтому пропускаем дополнительные проверки.
		if(skill.altUse() || (skill.isToggle() && skill.getHitTime() <= 0))
		{
			if(skill.isToggle() && !skill.checkCondition(actor, target, forceUse, dontMove, true))
			{
				clientActionFailed();
				return false;
			}

			if((skill.isToggle() || skill.isHandler()) && !skill.isCanUseWhileAbnormal() && (actor.isStunned() || actor.isSleeping() || actor.isDecontrolled() || actor.isFrozen()))
			{
				clientActionFailed();
				return false;
			}

			actor.altUseSkill(skill, target);
			return true;
		}

		// Если не можем кастовать, то использовать скилл позже
		if(actor.isActionsDisabled(false) || actor.isCastingNow())
		{
			//if(!actor.isSkillDisabled(skill.getId()))
			if(!skill.isHandler())
			{
				setNextAction(AINextAction.CAST, skill, target, forceUse, dontMove);
				clientActionFailed();
				return true;
			}
			clientActionFailed();
			return false;
		}

		//_actor.stopMove(null);
		_forceUse = forceUse;
		_dontMove = dontMove;
		clearNextAction();
		setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
		return true;
	}

	@Override
	public Playable getActor()
	{
		return (Playable) super.getActor();
	}
}