/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.targets.AffectScope;

/**
 * Dead Party and Clan affect scope implementation.
 * @author Nik
 * @author JoeAlisson
 */
public class DeadPartyPledge extends Pledge {

	@Override
	protected boolean isAffected(Player initialTarget, Player player) {
		return !player.isDead() && isInSameParty(initialTarget, player);
	}

	private boolean isInSameParty(Player initialTarget, Player player) {
		var party = initialTarget.getParty();
		return party != null && party.isMember(player);
	}

	@Override
	public Enum<AffectScope> getAffectScopeType() {
		return AffectScope.DEAD_PARTY_PLEDGE;
	}
}
