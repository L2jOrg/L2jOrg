package l2s.gameserver.skills.effects;

import l2s.gameserver.data.xml.holder.CubicHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.actor.instances.player.Cubic;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.CubicTemplate;
import l2s.gameserver.templates.skill.EffectTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class i_summon_cubic extends i_abstract_effect
{
	private static final Logger _log = LoggerFactory.getLogger(i_summon_cubic.class);

	private final CubicTemplate _template;

	public i_summon_cubic(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		int cubicId = getTemplate().getParam().getInteger("id", 0);
		int cubicLevel = getTemplate().getParam().getInteger("level", 0);
		if(cubicId <= 0 || cubicLevel <= 0)
		{
			_template = null;
			_log.warn(getClass().getSimpleName() + ": Cannot find cubic template for skill: ID[" + getSkill().getId() + "], LEVEL[" + getSkill().getLevel() + "]!");
			return;
		}
		_template = CubicHolder.getInstance().getTemplate(cubicId, cubicLevel);
	}

	@Override
	public boolean checkCondition()
	{
		if(!getEffected().isPlayer())
			return false;

		if(_template == null)
			return false;

		Player player = getEffected().getPlayer();
		if(player.getCubic(_template.getSlot()) != null)
			return true;

		int size = (int) player.calcStat(Stats.CUBICS_LIMIT, 1.);
		if(player.getCubics().size() >= size)
		{
			if(getEffector() == player)
				player.sendPacket(SystemMsg.CUBIC_SUMMONING_FAILED);
			return false;
		}

		return true;
	}

	@Override
	public void instantUse()
	{
		Player player = getEffected().getPlayer();
		if(player == null)
			return;

		Cubic cubic = new Cubic(player, _template, getSkill());
		cubic.init();
	}
}