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
package handlers.itemhandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * @author JoeAlisson
 */
public class BeastSpiritShot extends AbstractBeastShot {

	@Override
	protected ShotType getShotType() {
		return ShotType.SPIRITSHOTS;
	}

	@Override
	protected boolean isBlessed() {
		return false;
	}

	@Override
	protected double getBonus(Summon summon) {
		return summon.getStats().getValue(Stat.SPIRIT_SHOTS_BONUS, 1) * 2;
	}

	@Override
	protected void sendUsesMessage(Player player) {
		player.sendPacket(SystemMessageId.YOUR_PET_USES_SPIRITSHOT);
	}

}
