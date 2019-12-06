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
public class FriendPc implements IAffectObjectHandler
{
	@Override
	public boolean checkAffectedObject(Creature activeChar, Creature target)
	{
		if (!isPlayer(target))
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
				return true;
			}
			
			// Party (command channel doesn't make you friends).
			final Party party = player.getParty();
			final Party targetParty = targetPlayer.getParty();
			if ((party != null) && (targetParty != null) && (party.getLeaderObjectId() == targetParty.getLeaderObjectId()))
			{
				return true;
			}
			
			// Arena.
			if (activeChar.isInsideZone(ZoneType.PVP) && target.isInsideZone(ZoneType.PVP))
			{
				return false;
			}
			
			// Duel.
			if (player.isInDuel() && targetPlayer.isInDuel() && (player.getDuelId() == targetPlayer.getDuelId()))
			{
				return false;
			}
			
			// Olympiad.
			if (player.isInOlympiadMode() && targetPlayer.isInOlympiadMode() && (player.getOlympiadGameId() == targetPlayer.getOlympiadGameId()))
			{
				return false;
			}
			
			// Clan.
			final Clan clan = player.getClan();
			final Clan targetClan = targetPlayer.getClan();
			if (clan != null)
			{
				if (clan == targetClan)
				{
					return true;
				}
				
				// War
				if ((targetClan != null) && clan.isAtWarWith(targetClan) && targetClan.isAtWarWith(clan))
				{
					return false;
				}
			}
			
			// Alliance.
			if ((player.getAllyId() != 0) && (player.getAllyId() == targetPlayer.getAllyId()))
			{
				return true;
			}
			
			// Siege.
			if (target.isInsideZone(ZoneType.SIEGE))
			{
				// Players in the same siege side at the same castle are considered friends.
				if (player.isSiegeFriend(targetPlayer)) {
					return true;
				}
				
				return false;
			}
			
			// By default any neutral non-flagged player is considered a friend.
			return (target.getActingPlayer().getPvpFlag() == 0) && (target.getActingPlayer().getReputation() >= 0);
		}
		
		return target.isAutoAttackable(activeChar);
	}
	
	@Override
	public Enum<AffectObject> getAffectObjectType()
	{
		return AffectObject.FRIEND_PC;
	}
}
