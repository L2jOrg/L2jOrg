package org.l2j.gameserver.skills.effects;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.templates.skill.EffectTemplate;

public class EffectLaksis extends Effect
{
	public EffectLaksis(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
	}

	@Override
	public void onStart()
	{
		Player caster = (Player)getEffector();

		for (Creature cha : caster.getAroundCharacters(getSkill().getAffectRange(), 200))
		{
			if(cha != null && cha.isPlayer())
			{
				boolean heal = false;

				if(cha.isPlayer())
				{
					Player player = (Player)getEffector();
					Player target = (Player)cha;

					if(player.getParty() != null)
						if ((player.isInSameParty(target)) || (player.isInSameChannel(target)))
							heal = true;
					if ((player.getClan() != null) && (!player.isInPeaceZone()))
						if ((player.isInSameClan(target)) || (player.isInSameAlly(target)))
							heal = true;

					if (heal)
					{
						if ((target == null) || (target.isDead()))
						{
							continue;
						}

						//StatusUpdatePacket su = new StatusUpdatePacket(target);

						double powerCP = getValue();
						double powerHP = getValue();

						powerCP = Math.min(powerCP, target.getMaxCp() - target.getCurrentCp());
						powerHP = Math.min(powerHP, target.getMaxHp() - target.getCurrentHp());

						if (powerCP < 0.0D)
							powerCP = 0.0D;
						if (powerHP < 0.0D)
							powerHP = 0.0D;

						if (target.getCurrentCp() < target.getMaxCp())
						{
							target.setCurrentCp(powerCP + target.getCurrentCp());
							target.sendPacket(new SystemMessagePacket(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addInteger((long) powerCP));
							//su.addAttribute(33, (int)target.getCurrentCp());
							//target.sendPacket(su);
						}
						else
						{
							target.setCurrentHp(powerHP + target.getCurrentHp(), false);
							//SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED);
							target.sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger((long) powerHP));
							//su.addAttribute(9, (int)target.getCurrentHp());
							//target.sendPacket(su);
						}
					}
					else
					{
						if ((target == null) || (target.isDead()))
							continue;

						if ((target.getPvpFlag() > 0 || target.isAutoAttackable(player)) && !target.isInPeaceZone())
						{
							if(player.getPvpFlag() == 0)
								player.startPvPFlag(null);
							double damage = getValue();
							target.reduceCurrentHp(damage, player, getSkill(), true, true, false, true, false, false, true);
						}
					}
				}
			}
			else if(cha != null && cha.isMonster() && !cha.isInPeaceZone())
			{
				double damage = getValue();
				cha.reduceCurrentHp(damage, caster, getSkill(), true, true, false, true, false, false, true);
				cha.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, caster, getSkill(), damage);
			}
		}
	}
}