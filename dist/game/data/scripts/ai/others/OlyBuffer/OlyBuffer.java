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
package ai.others.OlyBuffer;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * Olympiad Buffer AI.
 * @author St3eT
 */
public final class OlyBuffer extends AbstractNpcAI
{
	// NPC
	private static final int OLYMPIAD_BUFFER = 36402;
	// Skills
	private static final SkillHolder[] ALLOWED_BUFFS =
	{
		new SkillHolder(4357, 2), // Haste Lv2
		new SkillHolder(4355, 3), // Acumen Lv3
		new SkillHolder(4342, 2), // Wind Walk Lv2
		new SkillHolder(4345, 3), // Might Lv3
		new SkillHolder(4344, 3), // Shield Lv3
		new SkillHolder(4349, 2), // Magic Barrier lv.2		
		new SkillHolder(4347, 4), // Blessed Body lv.4
		new SkillHolder(4348, 4), // Blessed Soul lv.4
		new SkillHolder(4352, 2), // Berserker Spirit Lv2
	};
	
	private OlyBuffer()
	{
		addStartNpc(OLYMPIAD_BUFFER);
		addFirstTalkId(OLYMPIAD_BUFFER);
		addTalkId(OLYMPIAD_BUFFER);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (npc.getScriptValue() < 5)
		{
			htmltext = "OlyBuffer-index.html";
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (event.startsWith("giveBuff;") && (npc.getScriptValue() < 5))
		{
			final int buffId = Integer.parseInt(event.replace("giveBuff;", ""));
			if (ALLOWED_BUFFS[buffId] != null)
			{
				npc.setScriptValue(npc.getScriptValue() + 1);
				addSkillCastDesire(npc, player, ALLOWED_BUFFS[buffId], 23);
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
	
	public static void main(String[] args)
	{
		new OlyBuffer();
	}
}