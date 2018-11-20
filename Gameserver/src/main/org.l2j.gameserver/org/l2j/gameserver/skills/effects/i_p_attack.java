package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.FinishRotatingPacket;
import org.l2j.gameserver.network.l2.s2c.StartRotatingPacket;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.stats.Formulas.AttackInfo;
import org.l2j.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class i_p_attack extends i_abstract_effect
{
	private final boolean _onCrit;
	private final boolean _directHp;
	private final boolean _turner;
	private final boolean _blow;
    private final boolean _static;

	public i_p_attack(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);

		_onCrit = template.getParam().getBool("onCrit", false);
		_directHp = template.getParam().getBool("directHp", false);
		_turner = template.getParam().getBool("turner", false);
		_blow = template.getParam().getBool("blow", false);
        _static = template.getParam().getBool("static", false);
	}

	@Override
    public boolean checkCondition()
    {
        if(getEffected().isDead())
            return false;
        return super.checkCondition();
    }

	@Override
    public void instantUse()
    {
        if(_turner && !getEffected().isInvulnerable())
        {
			getEffected().broadcastPacket(new StartRotatingPacket(getEffected(), getEffected().getHeading(), 1, 65535));
			getEffected().broadcastPacket(new FinishRotatingPacket(getEffected(), getEffector().getHeading(), 65535));
			getEffected().setHeading(getEffector().getHeading());
			getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(getSkill()));
		}

		final Creature realTarget = isReflected() ? getEffector() : getEffected();
        double power = getValue();
        if(getSkill().getId() == 10300)
        {
            if(realTarget.isMonster() && !realTarget.isRaid())
                power = realTarget.getCurrentHp() - 1;
        }
        if (_static)
        {
            realTarget.reduceCurrentHp(power, getEffector(), getSkill(), true, true, _directHp, true, false, false, power != 0, true, false, false, false);
            return;
        }

        final AttackInfo info = Formulas.calcPhysDam(getEffector(), realTarget, getSkill(), 1, power, false, _blow, getSkill().isSSPossible(), _onCrit, true);
        if(info == null)
            return;

        realTarget.reduceCurrentHp(info.damage, getEffector(), getSkill(), true, true, _directHp, true, false, false, getTemplate().isInstant() && power != 0, getTemplate().isInstant(), info.crit || info.blow, false, false);

        if(!info.miss || info.damage >= 1)
		{
			double lethalDmg = Formulas.calcLethalDamage(getEffector(), realTarget, getSkill());
			if(lethalDmg > 0)
				realTarget.reduceCurrentHp(lethalDmg, getEffector(), getSkill(), true, true, false, false, false, false, false);
			else if(!isReflected())
				realTarget.doCounterAttack(getSkill(), getEffector(), _blow);
		}
	}
}