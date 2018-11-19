package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public final class i_spirit_shot extends i_abstract_effect
{
	private final double _power;

	public i_spirit_shot(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_power = template.getParam().getDouble("power", 100);
		//template.getParam().getInteger("unk_spiritshot_parameter_1", 40);
		//template.getParam().getDouble("unk_spiritshot_parameter_2", 1.0);
	}

	@Override
	public void instantUse()
	{
		getEffected().sendPacket(SystemMsg.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED);
		getEffected().setChargedSpiritshotPower(_power);
	}
}