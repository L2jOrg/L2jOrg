package handlers.targethandlers.affectobject;

import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Nik
 */
public class NotFriendPc implements IAffectObjectHandler
{
	@Override
	public boolean checkAffectedObject(Creature activeChar, Creature target)
	{
		if (!isPlayer(target))
		{
			return false;
		}
		
		if (activeChar == target)
		{
			return false;
		}
		
		final Player player = activeChar.getActingPlayer();
		final Player targetPlayer = target.getActingPlayer();
		
		if (player != null)
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
			if (player.isInOlympiadMode() && targetPlayer.isInOlympiadMode() && (player.getOlympiadGameId() == targetPlayer.getOlympiadGameId()))
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
			
			// By default any flagged/PK player is considered enemy.
			return (target.getActingPlayer().getPvpFlag() > 0) || (target.getActingPlayer().getReputation() < 0);
		}
		
		return target.isAutoAttackable(activeChar);
	}
	
	@Override
	public Enum<AffectObject> getAffectObjectType()
	{
		return AffectObject.NOT_FRIEND_PC;
	}
}
