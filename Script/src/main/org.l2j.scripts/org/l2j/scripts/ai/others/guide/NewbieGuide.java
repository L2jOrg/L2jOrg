/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.ai.others.guide;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class NewbieGuide extends AbstractNpcAI {

	private static final int[] NEWBIE_GUIDES = {
		30598,
		30599,
		30600,
		30601,
		30602,
		34110,
	};

	private static final ItemHolder SOULSHOT_REWARD = new ItemHolder(91927, 200);
	private static final ItemHolder SPIRITSHOT_REWARD = new ItemHolder(91927, 100);

	private static final String TUTORIAL_QUEST = "Q00255_Tutorial";
	private static final String SUPPORT_MAGIC_STRING = "<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h Link default/SupportMagic.htm\">Receive help from beneficial magic.</Button>";
	
	private NewbieGuide() {
		addStartNpc(NEWBIE_GUIDES);
		addTalkId(NEWBIE_GUIDES);
		addFirstTalkId(NEWBIE_GUIDES);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		String htmlText;
		if (event.equals("0")) {
			if (CharacterSettings.maxNewbieBuffLevel() > 0) {
				htmlText = npc.getId() + ".htm";
			} else {
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId(), getHtml(player, npc.getId() + ".htm"));
				html.replace(SUPPORT_MAGIC_STRING, "");
				player.sendPacket(html);
				return null;
			}
		} else {
			htmlText = npc.getId() + "-" + event + (player.isMageClass() ? "m" : "f") + ".htm";
		}
		return htmlText;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player) {
		if (npc.getRace() != player.getTemplate().getRace()) {
			return npc.getId() + "-no.htm";
		}

		final QuestState qs = player.getQuestState(TUTORIAL_QUEST);
		if ((qs != null) && !CharacterSettings.disableTutorial() && qs.isMemoState(5))
		{
			qs.setMemoState(6);
			if (player.isMageClass() && (player.getRace() != Race.ORC)) {
				giveItems(player, SPIRITSHOT_REWARD);
				playTutorialVoice(player, "tutorial_voice_027");
			} else {
				giveItems(player, SOULSHOT_REWARD);
				playTutorialVoice(player, "tutorial_voice_026");
			}
		}

		if (CharacterSettings.maxNewbieBuffLevel() > 0)
		{
			return npc.getId() + ".htm";
		}

		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId(), getHtml(player, npc.getId() + ".htm"));
		html.replace(SUPPORT_MAGIC_STRING, "");
		player.sendPacket(html);
		return null;
	}
	
	public void playTutorialVoice(Player player, String voice)
	{
		player.sendPacket(PlaySound.voice(voice));
	}
	
	public static AbstractNpcAI provider()
	{
		return new NewbieGuide();
	}
}
