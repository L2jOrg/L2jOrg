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
package org.l2j.scripts.ai.others.MonumentOfHeroes;

import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Monument of Heroes AI.
 * @author St3eT
 * @author JoeAlisson
 */
public final class MonumentOfHeroes extends AbstractNpcAI {

	private static final int MONUMENT = 31690;
	
	private MonumentOfHeroes() {
		addStartNpc(MONUMENT);
		addFirstTalkId(MONUMENT);
		addTalkId(MONUMENT);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		String htmltext = null;
		
		switch (event) {
			case "index": {
				htmltext = onFirstTalk(npc, player);
				break;
			}
			case "heroCertification": {
				if(player.isHero()) {
					htmltext = "already-hero.html";
				} else if(Olympiad.getInstance().isUnclaimedHero(player)) {
					htmltext = "certification.html";
				} else {
					htmltext = "no-hero-certification.html";
				}
				break;
			}
			case "certificationConfirm": {
				if(player.isHero()) {
					htmltext = "already-hero.html";
				} else if(Olympiad.getInstance().claimHero(player)) {
					htmltext = "hero.html";
				} else {
					htmltext = "no-hero-certification.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player) {
		if (!Olympiad.getInstance().checkLevelAndClassRestriction(player)) {
			return "no-requirements.html";
		}
		return "monument.html";
	}
	
	public static AbstractNpcAI provider()
	{
		return new MonumentOfHeroes();
	}
}