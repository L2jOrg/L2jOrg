package handlers.itemhandlers;

import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class Elixir extends ItemSkills {

	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {

		if (!isPlayer(playable)) {
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		return super.useItem(playable, item, forceUse);
	}
}
