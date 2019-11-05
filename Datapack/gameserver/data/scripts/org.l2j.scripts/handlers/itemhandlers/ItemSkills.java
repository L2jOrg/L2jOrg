package handlers.itemhandlers;

import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.nonNull;

/**
 * Item skills not allowed on Olympiad.
 */
public class ItemSkills extends ItemSkillsTemplate {

	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {
		var player = playable.getActingPlayer();

		if (nonNull(player) && player.isInOlympiadMode()) {
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_ITEM_IN_A_OLYMPIAD_MATCH);
			return false;
		}
		return super.useItem(playable, item, forceUse);
	}
}
