package l2s.gameserver.skills.effects;

import l2s.gameserver.data.xml.holder.TransformTemplateHolder;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.base.TransformType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.player.transform.TransformTemplate;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectTransformation extends Effect
{
	public EffectTransformation(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected() != getEffector())
			return false;

		if(!getEffected().isPlayer())
			return false;

		int transformId = (int) getValue();
		if(transformId > 0)
		{
			TransformTemplate template = TransformTemplateHolder.getInstance().getTemplate(getEffected().getSex(), transformId);
			if(template == null)
				return false;

			if(template.getType() == TransformType.FLYING && getEffected().getX() > -166168)
				return false;
		}

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().setTransform((int) getValue());
	}

	@Override
	public void onExit()
	{
		if(getValue() > 0)
			getEffected().setTransform(null);
	}
}