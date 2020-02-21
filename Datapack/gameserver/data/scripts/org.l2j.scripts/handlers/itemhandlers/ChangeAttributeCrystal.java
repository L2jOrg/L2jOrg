package handlers.itemhandlers;

import org.l2j.gameserver.enums.ItemGrade;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.attributechange.ExChangeAttributeItemList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Mobius
 */
public class ChangeAttributeCrystal implements IItemHandler {

	private static final Map<Integer, ItemGrade> ITEM_GRADES = new HashMap<>();{
		ITEM_GRADES.put(33502, ItemGrade.S);
	}
	
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {

		if (!isPlayer(playable)) {
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player player = playable.getActingPlayer();
		if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_CHANGE_AN_ATTRIBUTE_WHILE_USING_A_PRIVATE_STORE_OR_WORKSHOP));
			return false;
		}
		
		if (ITEM_GRADES.get(item.getId()) == null)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CHANGING_ATTRIBUTES_HAS_BEEN_FAILED));
			return false;
		}
		
		final List<ItemInfo> itemList = new ArrayList<>();
		for (Item i : player.getInventory().getItems())
		{
			if (i.isWeapon() && i.hasAttributes() && (i.getTemplate().getItemGrade() == ITEM_GRADES.get(item.getId())))
			{
				itemList.add(new ItemInfo(i));
			}
		}
		
		if (itemList.isEmpty())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_ITEM_FOR_CHANGING_AN_ATTRIBUTE_DOES_NOT_EXIST));
			return false;
		}
		
		player.sendPacket(new ExChangeAttributeItemList(item.getId(), itemList.toArray(new ItemInfo[itemList.size()])));
		return true;
	}
}