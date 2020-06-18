/*
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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

import java.util.StringTokenizer;

/**
 * @reworked by Thoss
 */
public class Tower extends FriendlyNpc {
	private static int[] ITEM_REWARD = {49764, 49765};
	private Player _talkingPlayer;

	public Tower(NpcTemplate template) {
		super(template);
	}

	@Override
	public void onBypassFeedback(Player player, String command) {
		_talkingPlayer = player;
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if (cmd.equals("getreward")) {
			if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_reward", 0) == 1) {
				GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 0);
				if (player.isGM()) {
					for (int r : ITEM_REWARD)
						player.addItem("TowerInstance", r, 1, this, true);
				} else {
					for (Player partyMember : player.getParty().getMembers()) {
						for (int r : ITEM_REWARD)
							partyMember.addItem("TowerInstance", r, 1, this, true);
					}
				}
				showChatWindow(player, "data/html/default/" + getId() + "-3.htm");
			}
		} else
			super.onBypassFeedback(player, command);
	}

	@Override
	public int getHateBaseAmount() {
		return 1000;
	}

	@Override
	public void showChatWindow(Player player, int val)  {
		_talkingPlayer = player;
		super.showChatWindow(player, val);
	}

	@Override
	public String getHtmlPath(int npcId, int val) {
		String filename = "data/html/default/";

		if (!_talkingPlayer.isGM() && (!_talkingPlayer.isInParty() || !_talkingPlayer.getParty().isLeader(_talkingPlayer)))
			filename += npcId + "-4.htm";
		else if (!isDead() && GlobalVariablesManager.getInstance().getInt("heavenly_rift_complete", 0) == 2) {
			if (GlobalVariablesManager.getInstance().getInt("heavenly_rift_reward", 0) == 1)
				filename += npcId + ".htm";
			else
				filename += npcId + "-2.htm";
		} else
			filename += npcId + "-1.htm";

		return filename;
	}
}
