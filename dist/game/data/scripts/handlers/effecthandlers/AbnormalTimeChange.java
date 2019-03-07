/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.l2jmobius.gameserver.model.L2Object;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.skills.AbnormalType;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.network.serverpackets.AbnormalStatusUpdate;
import com.l2jmobius.gameserver.network.serverpackets.ExAbnormalStatusUpdateFromTarget;

/**
 * @author Sdw
 */
public class AbnormalTimeChange extends AbstractEffect
{
	private final Set<AbnormalType> _abnormals;
	private final int _time;
	private final int _mode;
	
	public AbnormalTimeChange(StatsSet params)
	{
		final String abnormals = params.getString("slot", null);
		if ((abnormals != null) && !abnormals.isEmpty())
		{
			_abnormals = new HashSet<>();
			for (String slot : abnormals.split(";"))
			{
				_abnormals.add(AbnormalType.getAbnormalType(slot));
			}
		}
		else
		{
			_abnormals = Collections.<AbnormalType> emptySet();
		}
		
		_time = params.getInt("time", -1);
		
		switch (params.getString("mode", "DEBUFF"))
		{
			case "DIFF":
			{
				_mode = 0;
				break;
			}
			case "DEBUFF":
			{
				_mode = 1;
				break;
			}
			default:
			{
				throw new IllegalArgumentException("Mode should be DIFF or DEBUFF for skill id:" + params.getInt("id"));
			}
		}
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		final AbnormalStatusUpdate asu = new AbnormalStatusUpdate();
		
		switch (_mode)
		{
			case 0: // DIFF
			{
				if (_abnormals.isEmpty())
				{
					effected.getEffectList().getEffects().stream().filter(b -> b.getSkill().canBeDispelled()).forEach(b ->
					{
						b.resetAbnormalTime(b.getTime() + _time);
						asu.addSkill(b);
					});
				}
				else
				{
					effected.getEffectList().getEffects().stream().filter(b -> b.getSkill().canBeDispelled() && _abnormals.contains(b.getSkill().getAbnormalType())).forEach(b ->
					{
						b.resetAbnormalTime(b.getTime() + _time);
						asu.addSkill(b);
					});
				}
				break;
			}
			case 1: // DEBUFF
			{
				if (_abnormals.isEmpty())
				{
					effected.getEffectList().getDebuffs().stream().filter(b -> b.getSkill().canBeDispelled()).forEach(b ->
					{
						b.resetAbnormalTime(b.getAbnormalTime());
						asu.addSkill(b);
					});
				}
				else
				{
					effected.getEffectList().getDebuffs().stream().filter(b -> b.getSkill().canBeDispelled() && _abnormals.contains(b.getSkill().getAbnormalType())).forEach(b ->
					{
						b.resetAbnormalTime(b.getAbnormalTime());
						asu.addSkill(b);
					});
				}
				break;
			}
		}
		
		effected.sendPacket(asu);
		
		final ExAbnormalStatusUpdateFromTarget upd = new ExAbnormalStatusUpdateFromTarget(effected);
		
		// @formatter:off
		effected.getStatus().getStatusListener().stream()
			.filter(Objects::nonNull)
			.filter(L2Object::isPlayer)
			.map(L2Character::getActingPlayer)
			.forEach(upd::sendTo);
		// @formatter:on
		
		if (effected.isPlayer() && (effected.getTarget() == effected))
		{
			effected.sendPacket(upd);
		}
	}
}
