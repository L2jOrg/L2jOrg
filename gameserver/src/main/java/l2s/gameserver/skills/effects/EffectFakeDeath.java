package l2s.gameserver.skills.effects;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.GameObjectTasks.EndBreakFakeDeathTask;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ChangeWaitTypePacket;
import l2s.gameserver.network.l2.s2c.RevivePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectFakeDeath extends Effect
{
	public EffectFakeDeath(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isInvisible(null)) //same as invis cannot be invis & fake death
			return false;

		if(getEffected().isPlayer())
		{
			Player player = getEffected().getPlayer();
			if(player.getActiveWeaponFlagAttachment() != null) //same as invis, if doing fake death during siege, cannot target? nonsense.
				return false;
		}
		return super.checkCondition();
	}
	
	@Override
	public void onStart()
	{
		Player player = (Player) getEffected();
		player.setFakeDeath(true);
		player.getAI().notifyEvent(CtrlEvent.EVT_FAKE_DEATH, null, null);
		player.broadcastPacket(new ChangeWaitTypePacket(player, ChangeWaitTypePacket.WT_START_FAKEDEATH));
		player.broadcastCharInfo();
	}

	@Override
	public void onExit()
	{
		// 5 секунд после FakeDeath на персонажа не агрятся мобы
		Player player = (Player) getEffected();
		player.setNonAggroTime(System.currentTimeMillis() + 5000L);
		//player.setFakeDeath(false); //after
		player.broadcastPacket(new ChangeWaitTypePacket(player, ChangeWaitTypePacket.WT_STOP_FAKEDEATH));
		if(getSkill().getId() == 10528) //stupid but fine for now
			player.setTargetable(true);		
		player.broadcastPacket(new RevivePacket(player));
		player.broadcastCharInfo();
		ThreadPoolManager.getInstance().schedule(new EndBreakFakeDeathTask(player), 2500);
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isDead())
			return false;

		double manaDam = getValue();

		if(manaDam > getEffected().getCurrentMp())
		{
			if(getSkill().isToggle())
			{
				getEffected().sendPacket(SystemMsg.NOT_ENOUGH_MP);
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
				return false;
			}
		}

		getEffected().reduceCurrentMp(manaDam, null);
		return true;
	}
}