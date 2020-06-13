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
package ai.others.CastleSiegeManager;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Castle Siege Manager AI.
 * @author St3eT
 */
public final class CastleSiegeManager extends AbstractNpcAI
{
	// NPCs
	private static final int[] SIEGE_MANAGER =
	{
		35104, // Gludio Castle
		35146, // Dion Castle
		35188, // Giran Castle
		35232, // Oren Castle
		35278, // Aden Castle
		35320, // Innadril Castle
		35367, // Goddard Castle
		35513, // Rune Castle
		35559, // Schuttgart Castle
	};
	
	private CastleSiegeManager()
	{
		addFirstTalkId(SIEGE_MANAGER);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = null;
		if (player.isClanLeader() && (player.getClanId() == npc.getCastle().getOwnerId()))
		{
			if (isInSiege(npc))
			{
				htmltext = "CastleSiegeManager.html";
			}
			else
			{
				htmltext = "CastleSiegeManager-01.html";
			}
		}
		else if (isInSiege(npc))
		{
			htmltext = "CastleSiegeManager-02.html";
		}
		else
		{
			npc.getCastle().getSiege().listRegisterClan(player);
		}
		return htmltext;
	}
	
	private boolean isInSiege(Npc npc)
	{
		return npc.getCastle().getSiege().isInProgress();
	}
	
	public static AbstractNpcAI provider()
	{
		return new CastleSiegeManager();
	}
}