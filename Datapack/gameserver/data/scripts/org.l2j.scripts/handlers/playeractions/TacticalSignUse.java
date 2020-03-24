package handlers.playeractions;

import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Tactical Signs setting player action handler.
 * @author Nik
 */
public final class TacticalSignUse implements IPlayerActionHandler {

	@Override
	public void useAction(Player player, ActionData action, boolean ctrlPressed, boolean shiftPressed) {
		if (!player.isInParty() || !isCreature(player.getTarget())) {
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.getParty().addTacticalSign(player, action.getOptionId(), (Creature) player.getTarget());
	}
}
