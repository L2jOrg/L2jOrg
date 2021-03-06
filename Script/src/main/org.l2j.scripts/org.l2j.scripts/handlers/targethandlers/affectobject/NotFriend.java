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
package org.l2j.scripts.handlers.targethandlers.affectobject;

import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Not Friend affect object implementation. Based on Gracia Final retail tests.<br>
 * Such are considered flagged/karma players (except party/clan/ally). Doesn't matter if in command channel.<br>
 * In arena such are considered clan/ally/command channel (except party). <br>
 * In peace zone such are considered monsters.<br>
 * Monsters consider such all players, npcs (Citizens, Guild Masters, Merchants, Guards, etc. except monsters). Doesn't matter if in peace zone or arena.
 * @author Nik
 */
public class NotFriend implements IAffectObjectHandler
{
	@Override
	public boolean checkAffectedObject(Creature activeChar, Creature target)
	{
		if (activeChar == target)
		{
			return false;
		}
		
		final Player player = activeChar.getActingPlayer();
		final Player targetPlayer = target.getActingPlayer();

		if ((player != null) && (targetPlayer != null))
		{
			// Same player.
			if (player == targetPlayer)
			{
				return false;
			}

			// Peace Zone.
			if (target.isInsidePeaceZone(player) && !player.getAccessLevel().allowPeaceAttack())
			{
				return false;
			}

			// Party (command channel doesn't make you friends).
			final Party party = player.getParty();
			final Party targetParty = targetPlayer.getParty();
			if ((party != null) && (targetParty != null) && (party.getLeaderObjectId() == targetParty.getLeaderObjectId()))
			{
				return false;
			}

			// Arena.
			if (activeChar.isInsideZone(ZoneType.PVP) && target.isInsideZone(ZoneType.PVP))
			{
				return true;
			}

			// Duel.
			if (player.isInDuel() && targetPlayer.isInDuel() && (player.getDuelId() == targetPlayer.getDuelId()))
			{
				return true;
			}

			// Olympiad.
			if (player.isInOlympiadMode() && targetPlayer.isInOlympiadMode() && (player.getOlympiadMatchId() == targetPlayer.getOlympiadMatchId()))
			{
				return true;
			}

			// Clan.
			final Clan clan = player.getClan();
			final Clan targetClan = targetPlayer.getClan();
			if (clan != null)
			{
				if (clan == targetClan)
				{
					return false;
				}

				// War
				if ((targetClan != null) && clan.isAtWarWith(targetClan) && targetClan.isAtWarWith(clan))
				{
					return true;
				}
			}

			// Alliance.
			if ((player.getAllyId() != 0) && (player.getAllyId() == targetPlayer.getAllyId()))
			{
				return false;
			}

			// Siege.
			if (target.isInsideZone(ZoneType.SIEGE))
			{
				// Players in the same siege side at the same castle are considered friends.
				if (player.isSiegeFriend(targetPlayer)) {
					return false;
				}

				return true;
			}

			// At this point summon should be prevented from attacking friendly targets.
			if (isSummon(activeChar) && (target == activeChar.getTarget()))
			{
				return true;
			}

			// By default any flagged/PK player is considered enemy.
			return (target.getActingPlayer().getPvpFlag() > 0) || (target.getActingPlayer().getReputation() < 0);
		}
		
		return target.isAutoAttackable(activeChar);
	}
	
	@Override
	public Enum<AffectObject> getAffectObjectType()
	{
		return AffectObject.NOT_FRIEND;
	}
}
