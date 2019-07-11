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

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.AcquireSkillList;
import org.l2j.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Sdw
 */
public class ClassChange extends AbstractEffect
{
	private final int _index;
	private static final int IDENTITY_CRISIS_SKILL_ID = 1570;
	
	public ClassChange(StatsSet params)
	{
		_index = params.getInt("index", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effected.isPlayer())
		{
			final Player player = effected.getActingPlayer();
			// TODO: FIX ME - Executing 1 second later otherwise interupted exception during storeCharBase()
			ThreadPoolManager.schedule(() ->
			{
				final int activeClass = player.getClassId().getId();
				
				if (!player.setActiveClass(_index))
				{
					player.sendMessage("You cannot switch your class right now!");
					return;
				}
				
				final Skill identifyCrisis = SkillData.getInstance().getSkill(IDENTITY_CRISIS_SKILL_ID, 1);
				if (identifyCrisis != null)
				{
					identifyCrisis.applyEffects(player, player);
				}
				
				final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUCCESSFULLY_SWITCHED_S1_TO_S2);
				msg.addClassId(activeClass);
				msg.addClassId(player.getClassId().getId());
				player.sendPacket(msg);
				
				player.broadcastUserInfo();
				player.sendPacket(new AcquireSkillList(player));
				player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
			}, 1000);
		}
	}
}
