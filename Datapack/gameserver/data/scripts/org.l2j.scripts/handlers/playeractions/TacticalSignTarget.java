package handlers.playeractions;

import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

/**
 * Tactical Signs targeting player action handler.
 * @author Nik
 */
public final class TacticalSignTarget implements IPlayerActionHandler
{
	@Override
	public void useAction(Player player, ActionData action, boolean ctrlPressed, boolean shiftPressed)
	{
		if (!player.isInParty())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.getParty().setTargetBasedOnTacticalSignId(player, action.getOptionId());
	}
}
