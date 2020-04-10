package handlers.itemhandlers;

import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.costume.ExChooseCostumeItem;

/**
 * @author JoeAlisson
 */
public class TransformationBook implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {
		playable.sendPacket(new ExChooseCostumeItem(item.getId()));
		return true;
	}
}
