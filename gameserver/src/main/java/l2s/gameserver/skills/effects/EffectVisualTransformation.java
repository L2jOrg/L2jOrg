package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.skills.skillclasses.Transformation;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectVisualTransformation extends Effect
{
	public EffectVisualTransformation(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isTransformImmune())
			return false;

		if(getEffected().isInFlyingTransform())
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().getFlags().getMuted().start(this);
		getEffected().getFlags().getAMuted().start(this);
		getEffected().getFlags().getPMuted().start(this);
		getEffected().abortCast(true, true);
		getEffected().abortAttack(true, true);
		getEffected().setVisualTransform((int) getValue());
	}

	@Override
	public void onExit()
	{
		getEffected().setVisualTransform(null);
		getEffected().getFlags().getMuted().stop(this);
		getEffected().getFlags().getAMuted().stop(this);
		getEffected().getFlags().getPMuted().stop(this);
	}
}