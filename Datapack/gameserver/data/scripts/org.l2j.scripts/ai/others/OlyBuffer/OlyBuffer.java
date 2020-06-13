/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.others.OlyBuffer;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.skills.SkillCaster;

/**
 * Olympiad Buffer AI.
 * @author St3eT
 */
public final class OlyBuffer extends AbstractNpcAI
{
	// NPC
	private static final int OLYMPIAD_BUFFER = 36402;
	// Skills
	private static final SkillHolder[] ALLOWED_BUFFS = {
		new SkillHolder(1086, 1), // Haste - Atk. Spd. +15%.
		new SkillHolder(1085, 1), // Acumen - Casting Spd. +15%
		new SkillHolder(1204, 1), // Wind Walk - Speed +20.
		new SkillHolder(1068, 1), // Might - P. Atk. +8%.
		new SkillHolder(1040, 1), // Shield - P. Def. +8%.
		new SkillHolder(1036, 1), // Magic Barrier - M. Def. +23%.
		new SkillHolder(1045, 1), // Blessed Body - Max HP +10%.
		new SkillHolder(1048, 1), // Blessed Soul - Max MP +10%.
		new SkillHolder(1062, 1), // Berserker Spirit - P. Def. -5%, M. Def. -10%, P. Evasion -2, P. Atk. +5%, M. Atk. +10%, Atk. Spd. +5%, Casting Spd. +5% and Speed +5.
	};
	
	private OlyBuffer()
	{
		addStartNpc(OLYMPIAD_BUFFER);
		addFirstTalkId(OLYMPIAD_BUFFER);
		addTalkId(OLYMPIAD_BUFFER);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (npc.getScriptValue() < 5)
		{
			htmltext = "OlyBuffer-index.html";
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		if (event.startsWith("giveBuff;") && (npc.getScriptValue() < 5))
		{
			final int buffId = Integer.parseInt(event.replace("giveBuff;", ""));
			if (ALLOWED_BUFFS[buffId] != null)
			{
				npc.setScriptValue(npc.getScriptValue() + 1);
				SkillCaster.triggerCast(npc, player, ALLOWED_BUFFS[buffId].getSkill());
				htmltext = "OlyBuffer-afterBuff.html";
			}
			if (npc.getScriptValue() >= 5)
			{
				htmltext = "OlyBuffer-noMore.html";
				getTimers().addTimer("DELETE_ME", 5000, evnt -> npc.deleteMe());
			}
		}
		return htmltext;
	}
	
	public static AbstractNpcAI provider()
	{
		return new OlyBuffer();
	}
}