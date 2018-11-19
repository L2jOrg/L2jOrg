package l2s.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillTargetType;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.NpcInfoPacket;
import l2s.gameserver.taskmanager.EffectTaskManager;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

public final class TrapInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static class CastTask extends RunnableImpl
	{
		private HardReference<NpcInstance> _trapRef;

		public CastTask(TrapInstance trap)
		{
			_trapRef = trap.getRef();
		}

		@Override
		public void runImpl() throws Exception
		{
			TrapInstance trap = (TrapInstance) _trapRef.get();

			if(trap == null)
				return;

			Creature owner = trap.getOwner();
			if(owner == null)
				return;
				
			if(trap._skill == null)
			{
				System.out.println("Trap Skill For Trap: "+trap.getNpcId()+"");
				return;
			}	
			for(Creature target : trap.getAroundCharacters(50, 50))
				if(target != owner)
					if(trap._skill != null && trap._skill.checkTarget(owner, target, null, false, false) == null)
					{
						List<Creature> targets = new ArrayList<Creature>();
						if(trap._skill.getTargetType() != SkillTargetType.TARGET_AREA)
							targets.add(target);
						else
							for(Creature t : trap.getAroundCharacters(trap._skill.getAffectRange(), 128))
								if(trap._skill.checkTarget(owner, t, null, false, false) == null)
									targets.add(target);

						trap._skill.onEndCast(trap, targets);
						if(target.isPlayer())
							target.sendMessage(new CustomMessage("common.Trap"));
						trap.deleteMe();
						break;
					}
		}
	}

	private final HardReference<? extends Creature> _ownerRef;
	private final Skill _skill;
	private ScheduledFuture<?> _targetTask;
	private boolean _detected;

	public TrapInstance(int objectId, NpcTemplate template, Creature owner, Skill skill)
	{
		this(objectId, template, owner, skill, owner.getLoc());
	}

	public TrapInstance(int objectId, NpcTemplate template, Creature owner, Skill skill, Location loc)
	{
		super(objectId, template, StatsSet.EMPTY);
		_ownerRef = owner.getRef();
		_skill = skill;

		setReflection(owner.getReflection());
		setLevel(owner.getLevel());
		setTitle(owner.getName());
		setLoc(loc);
	}

	@Override
	public boolean isTrap()
	{
		return true;
	}

	public Creature getOwner()
	{
		return _ownerRef.get();
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		startDeleteTask(120000L);

		_targetTask = EffectTaskManager.getInstance().scheduleAtFixedRate(new CastTask(this), 250L, 250L);
	}

	@Override
	public void broadcastCharInfo()
	{
		if(!isDetected())
			return;
		super.broadcastCharInfo();
	}

	@Override
	protected void onDelete()
	{
		Creature owner = getOwner();
		if(owner != null && owner.isPlayer())
			((Player) owner).removeTrap(this);
		if(_targetTask != null)
			_targetTask.cancel(false);
		_targetTask = null;
		super.onDelete();
	}

	public boolean isDetected()
	{
		return _detected;
	}

	public void setDetected(boolean detected)
	{
		_detected = detected;
	}

	@Override
	public int getPAtk(Creature target)
	{
		Creature owner = getOwner();
		return owner == null ? 0 : owner.getPAtk(target);
	}

	@Override
	public int getMAtk(Creature target, Skill skill)
	{
		Creature owner = getOwner();
		return owner == null ? 0 : owner.getMAtk(target, skill);
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{}

	@Override
	public void showChatWindow(Player player, String filename, boolean firstTalk, Object... replace)
	{}

	@Override
	public void onBypassFeedback(Player player, String command)
	{}

	@Override
	public void onAction(Player player, boolean shift)
	{
		if(player.getTarget() != this)
			player.setTarget(this);

		player.sendActionFailed();
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		// если не обезврежена и не овнер, ниче не показываем
		if(!isDetected() && getOwner() != forPlayer)
			return Collections.emptyList();

		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>();
		list.add(new NpcInfoPacket(this, forPlayer).init());

		return list;
	}
	
	public void selfDestroy()
	{
		Creature owner = getOwner();
		if(owner == null)
			return;
				
		if(_skill == null)
		{
			System.out.println("Trap Skill For Trap: "+getNpcId()+"");
			return;
		}		
		
		for(Creature target : getAroundCharacters(_skill.getAffectRange(), 250))
			if(target != owner)
				if(_skill != null && _skill.checkTarget(owner, target, null, false, false) == null)
				{
					List<Creature> targets = new ArrayList<Creature>();
						if(_skill.getTargetType() != SkillTargetType.TARGET_AREA)
						{
							targets.add(target);
						}	
						else
						{
							for(Creature t : getAroundCharacters(this._skill.getAffectRange(), 128))
								if(_skill.checkTarget(owner, t, null, false, false) == null)
								{
									targets.add(target);
								}	
						}
						_skill.onEndCast(this, targets);
						
						if(target.isPlayer())
							target.sendMessage(new CustomMessage("common.Trap"));
						deleteMe();
						break;
				}
	}	
}