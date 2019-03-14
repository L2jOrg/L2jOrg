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
package events.ThePowerOfLove;

import events.ScriptEvent;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.quest.LongTimeEvent;
import org.l2j.gameserver.model.skills.SkillCaster;

/**
 * The Power Of Love
 * @URL http://www.lineage2.com/en/news/events/02102016-the-power-of-love-part-iii.php
 * @author hlwrave
 */
public final class ThePowerOfLove extends LongTimeEvent implements ScriptEvent
{
	// NPC
	private static final int COCO = 33893;
	// Items
	private static final int COCOGIFBOX = 36081;
	private static final int AMULETLOVE = 70232;
	// Skill
	private static final SkillHolder COCO_M = new SkillHolder(55327, 1); // Sweet Chocolate Energy
	
	private ThePowerOfLove()
	{
		addStartNpc(COCO);
		addFirstTalkId(COCO);
		addTalkId(COCO);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33893-1.htm":
			case "33893-2.htm":
			case "33893-3.htm":
			{
				htmltext = event;
				break;
			}
			case "coco_giveItem":
			{
				if (!hasQuestItems(player, COCOGIFBOX))
				{
					giveItems(player, COCOGIFBOX, 1);
					htmltext = "33893-5.htm";
				}
				else
				{
					htmltext = "33893-9.htm";
				}
				break;
			}
			case "coco_takeAmulet":
			{
				if (hasQuestItems(player, AMULETLOVE))
				{
					SkillCaster.triggerCast(npc, player, COCO_M.getSkill());
					htmltext = "33893-4.htm";
					takeItems(player, AMULETLOVE, 1);
				}
				else
				{
					htmltext = "33893-9.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + "-1.htm";
	}

	public static ScriptEvent provider()
	{
		return new ThePowerOfLove();
	}
}
