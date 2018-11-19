package l2s.gameserver.model.instances;

import java.util.concurrent.Future;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.dao.SummonsDAO;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SetSummonRemainTimePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.npc.NpcTemplate;

public class SummonInstance extends Servitor
{
	public static class RestoredSummon
	{
		public final int skillId;
		public final int skillLvl;
		public final int curHp;
		public final int curMp;
		public final int time;

		public RestoredSummon(int skillId, int skillLvl, int curHp, int curMp, int time)
		{
			this.skillId = skillId;
			this.skillLvl = skillLvl;
			this.curHp = curHp;
			this.curMp = curMp;
			this.time = time;
		}
	}

	private static final long serialVersionUID = 1L;

	public final static int CYCLE = 5000; // in millis

	private final int _summonSkillId;
	private final int _itemConsumeIdInTime;
	private final int _itemConsumeCountInTime;
	private final int _itemConsumeDelay;
	private final int _maxLifetime;
	private final int _skillId;
	private final int _skillLvl;
	private final boolean _saveable;
	private final int _soulshots;
	private final int _spiritshots;

	private int _consumeCountdown;
	private int _lifetimeCountdown;

	private Future<?> _disappearTask;

	private double _expPenalty = 0;

	private boolean _isSiegeSummon;

	private AttackMode _attackMode = AttackMode.PASSIVE;

	public SummonInstance(int objectId, NpcTemplate template, Player owner, int lifetime, int consumeid, int consumecount, int consumedelay, Skill skill, boolean saveable)
	{
		super(objectId, template, owner);
		setName(template.name);
		_lifetimeCountdown = _maxLifetime = lifetime;
		_itemConsumeIdInTime = consumeid;
		_itemConsumeCountInTime = consumecount;
		_consumeCountdown = _itemConsumeDelay = consumedelay;
		_summonSkillId = skill.getDisplayId();
		_disappearTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Lifetime(), CYCLE, CYCLE);
		_skillId = skill.getId();
		_skillLvl = skill.getLevel();
		_saveable = saveable;
		_soulshots = template.getAIParams().getInteger("soulshot_count", 1);
		_spiritshots = template.getAIParams().getInteger("spiritshot_count", 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<SummonInstance> getRef()
	{
		return (HardReference<SummonInstance>) super.getRef();
	}

	@Override
	public final int getLevel()
	{
		return getTemplate() != null ? getTemplate().level : 0;
	}

	@Override
	public int getServitorType()
	{
		return 1;
	}

	@Override
	public int getCurrentFed()
	{
		return _lifetimeCountdown;
	}

	@Override
	public int getMaxFed()
	{
		return _maxLifetime;
	}

	public void setExpPenalty(double expPenalty)
	{
		_expPenalty = expPenalty;
	}

	@Override
	public double getExpPenalty()
	{
		return _expPenalty;
	}

	class Lifetime extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			Player owner = getPlayer();
			if(owner == null)
			{
				unSummon(false);
				return;
			}

			int usedtime = CYCLE; // CYCLE / 4? for what? in god there're no summon life time only for special traps or npcs no need to compare battle status.
			_lifetimeCountdown -= usedtime;

			if(_lifetimeCountdown <= 0)
			{
				owner.sendPacket(SystemMsg.YOUR_SERVITOR_HAS_VANISHED_YOULL_NEED_TO_SUMMON_A_NEW_ONE);
				unSummon(false);
				return;
			}

			_consumeCountdown -= usedtime;
			if(getItemConsumeIdInTime() > 0 && getItemConsumeCountInTime() > 0 && _consumeCountdown <= 0)
			{
				if(owner.getInventory().destroyItemByItemId(getItemConsumeIdInTime(), getItemConsumeCountInTime()))
				{
					_consumeCountdown = _itemConsumeDelay;
					owner.sendPacket(new SystemMessage(SystemMessage.A_SUMMONED_MONSTER_USES_S1).addItemName(getItemConsumeIdInTime()));
				}
				else
				{
					owner.sendPacket(SystemMsg.SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITORS_STAY_THE_SERVITOR_HAS_DISAPPEARED);
					unSummon(false);
					return;
				}
			}

			owner.sendPacket(new SetSummonRemainTimePacket(SummonInstance.this));
		}
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		stopDisappear();
	}

	public int getItemConsumeIdInTime()
	{
		return _itemConsumeIdInTime;
	}

	public int getItemConsumeCountInTime()
	{
		return _itemConsumeCountInTime;
	}

	public int getItemConsumeDelay()
	{
		return _itemConsumeDelay;
	}

	protected synchronized void stopDisappear()
	{
		if(_disappearTask != null)
		{
			_disappearTask.cancel(false);
			_disappearTask = null;
		}
	}

	@Override
	public void unSummon(boolean logout)
	{
		if(logout)
			SummonsDAO.getInstance().insert(this);
		else if(isSiegeSummon())
		{
			for(SiegeEvent<?, ?> siegeEvent : getEvents(SiegeEvent.class))
				siegeEvent.removeSiegeSummon(getPlayer(), this);
		}

		stopDisappear();
		super.unSummon(logout);
	}

	@Override
	public int getEffectIdentifier()
	{
		return _summonSkillId;
	}

	@Override
	public boolean isSummon()
	{
		return true;
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		super.onAction(player, shift);
		if(shift)
		{
			if(!player.getPlayerAccess().CanViewChar)
				return;

			String dialog;
			dialog = HtmCache.getInstance().getHtml("scripts/actions/admin.L2SummonInstance.onActionShift.htm", player);
			dialog = dialog.replaceFirst("%name%", String.valueOf(getName()));
			dialog = dialog.replaceFirst("%level%", String.valueOf(getLevel()));
			dialog = dialog.replaceFirst("%class%", String.valueOf(getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
			dialog = dialog.replaceFirst("%xyz%", getLoc().x + " " + getLoc().y + " " + getLoc().z);
			dialog = dialog.replaceFirst("%heading%", String.valueOf(getLoc().h));

			dialog = dialog.replaceFirst("%owner%", String.valueOf(getPlayer().getName()));
			dialog = dialog.replaceFirst("%ownerId%", String.valueOf(getPlayer().getObjectId()));

			dialog = dialog.replaceFirst("%npcId%", String.valueOf(getNpcId()));
			dialog = dialog.replaceFirst("%expPenalty%", String.valueOf(getExpPenalty()));

			dialog = dialog.replaceFirst("%maxHp%", String.valueOf(getMaxHp()));
			dialog = dialog.replaceFirst("%maxMp%", String.valueOf(getMaxMp()));
			dialog = dialog.replaceFirst("%currHp%", String.valueOf((int) getCurrentHp()));
			dialog = dialog.replaceFirst("%currMp%", String.valueOf((int) getCurrentMp()));

			dialog = dialog.replaceFirst("%pDef%", String.valueOf(getPDef(null)));
			dialog = dialog.replaceFirst("%mDef%", String.valueOf(getMDef(null, null)));
			dialog = dialog.replaceFirst("%pAtk%", String.valueOf(getPAtk(null)));
			dialog = dialog.replaceFirst("%mAtk%", String.valueOf(getMAtk(null, null)));
			dialog = dialog.replaceFirst("%accuracy%", String.valueOf(getPAccuracy()));
			dialog = dialog.replaceFirst("%evasionRate%", String.valueOf(getPEvasionRate(null)));
			dialog = dialog.replaceFirst("%crt%", String.valueOf(getPCriticalHit(null)));
			dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(getRunSpeed()));
			dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(getWalkSpeed()));
			dialog = dialog.replaceFirst("%pAtkSpd%", String.valueOf(getPAtkSpd()));
			dialog = dialog.replaceFirst("%mAtkSpd%", String.valueOf(getMAtkSpd()));
			dialog = dialog.replaceFirst("%dist%", String.valueOf((int) getRealDistance(player)));

			dialog = dialog.replaceFirst("%STR%", String.valueOf(getSTR()));
			dialog = dialog.replaceFirst("%DEX%", String.valueOf(getDEX()));
			dialog = dialog.replaceFirst("%CON%", String.valueOf(getCON()));
			dialog = dialog.replaceFirst("%INT%", String.valueOf(getINT()));
			dialog = dialog.replaceFirst("%WIT%", String.valueOf(getWIT()));
			dialog = dialog.replaceFirst("%MEN%", String.valueOf(getMEN()));

			dialog = dialog.replace("<?object_id?>", String.valueOf(getObjectId()));

			HtmlMessage msg = new HtmlMessage(5);
			msg.setHtml(dialog);
			player.sendPacket(msg);
		}
	}

	@Override
	public long getWearedMask()
	{
		return WeaponType.SWORD.mask(); // TODO: читать пассивки и смотреть тип оружия и брони там
	}

	public int getSkillId()
	{
		return _skillId;
	}

	public int getSkillLvl()
	{
		return _skillLvl;
	}

	public int getConsumeCountdown()
	{
		return _consumeCountdown;
	}

	public void setConsumeCountdown(int val)
	{
		_consumeCountdown = val;
	}

	public boolean isSaveable()
	{
		return _saveable && !isDead();
	}

	@Override
	public int getSoulshotConsumeCount()
	{
		return _soulshots;
	}

	@Override
	public int getSpiritshotConsumeCount()
	{
		return _spiritshots;
	}

	public boolean isSiegeSummon()
	{
		return _isSiegeSummon;
	}

	public void setSiegeSummon(boolean siegeSummon)
	{
		_isSiegeSummon = siegeSummon;
	}

	@Override
	public void onAttacked(Creature attacker)
	{
		if(isAttackingNow())
			return;

		if(attacker == null || getPlayer() == null)
			return;

		if(getAttackMode() == AttackMode.DEFENCE)
		{
			setTarget(attacker);
			getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
	}

	@Override
	public void onOwnerOfAttacks(Creature target)
	{
		if(isAttackingNow())
			return;

		if(target == null || getPlayer() == null)
			return;

		if(getAttackMode() == AttackMode.DEFENCE)
		{
			setTarget(target);
			getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
	}

	@Override
	public void setAttackMode(AttackMode mode)
	{
		_attackMode = mode;
	}

	@Override
	public AttackMode getAttackMode()
	{
		return _attackMode;
	}
}