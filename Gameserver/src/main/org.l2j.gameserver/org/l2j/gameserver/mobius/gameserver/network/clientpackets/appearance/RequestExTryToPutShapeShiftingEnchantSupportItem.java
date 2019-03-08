package org.l2j.gameserver.mobius.gameserver.network.clientpackets.appearance;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.AppearanceItemData;
import org.l2j.gameserver.mobius.gameserver.enums.ItemLocation;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.ShapeShiftingItemRequest;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.PcInventory;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.items.appearance.AppearanceStone;
import org.l2j.gameserver.mobius.gameserver.model.items.appearance.AppearanceType;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.appearance.ExPutShapeShiftingExtractionItemResult;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.appearance.ExPutShapeShiftingTargetItemResult;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestExTryToPutShapeShiftingEnchantSupportItem extends IClientIncomingPacket
{
	private int _targetItemObjId;
	private int _extracItemObjId;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_targetItemObjId = packet.getInt();
		_extracItemObjId = packet.getInt();
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final ShapeShiftingItemRequest request = player.getRequest(ShapeShiftingItemRequest.class);
		
		if (player.isInStoreMode() || player.isCrafting() || player.isProcessingRequest() || player.isProcessingTransaction() || (request == null))
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			return;
		}
		
		final PcInventory inventory = player.getInventory();
		final L2ItemInstance targetItem = inventory.getItemByObjectId(_targetItemObjId);
		final L2ItemInstance extractItem = inventory.getItemByObjectId(_extracItemObjId);
		L2ItemInstance stone = request.getAppearanceStone();
		if ((targetItem == null) || (extractItem == null) || (stone == null))
		{
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone.getOwnerId() != player.getObjectId()) || (targetItem.getOwnerId() != player.getObjectId()) || (extractItem.getOwnerId() != player.getObjectId()))
		{
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!extractItem.getItem().isAppearanceable())
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
			client.sendPacket(ExPutShapeShiftingExtractionItemResult.FAILED);
			return;
		}
		
		if ((extractItem.getItemLocation() != ItemLocation.INVENTORY) && (extractItem.getItemLocation() != ItemLocation.PAPERDOLL))
		{
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		final AppearanceStone appearanceStone = AppearanceItemData.getInstance().getStone(stone.getId());
		if (appearanceStone == null)
		{
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((appearanceStone.getType() == AppearanceType.RESTORE) || (appearanceStone.getType() == AppearanceType.FIXED))
		{
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (extractItem.getVisualId() > 0)
		{
			client.sendPacket(ExPutShapeShiftingExtractionItemResult.FAILED);
			client.sendPacket(SystemMessageId.YOU_CANNOT_EXTRACT_FROM_A_MODIFIED_ITEM);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((extractItem.getItemLocation() != ItemLocation.INVENTORY) && (extractItem.getItemLocation() != ItemLocation.PAPERDOLL))
		{
			client.sendPacket(ExPutShapeShiftingExtractionItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((extractItem.getItemType() != targetItem.getItemType()) || (extractItem.getId() == targetItem.getId()) || (extractItem.getObjectId() == targetItem.getObjectId()))
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
			client.sendPacket(ExPutShapeShiftingExtractionItemResult.FAILED);
			return;
		}
		
		if ((extractItem.getItem().getBodyPart() != targetItem.getItem().getBodyPart()) && ((extractItem.getItem().getBodyPart() != L2Item.SLOT_FULL_ARMOR) || (targetItem.getItem().getBodyPart() != L2Item.SLOT_CHEST)))
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
			client.sendPacket(ExPutShapeShiftingExtractionItemResult.FAILED);
			return;
		}
		
		if (extractItem.getItem().getCrystalType().isGreater(targetItem.getItem().getCrystalType()))
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_EXTRACT_FROM_ITEMS_THAT_ARE_HIGHER_GRADE_THAN_ITEMS_TO_BE_MODIFIED);
			player.sendPacket(ExPutShapeShiftingExtractionItemResult.FAILED);
			return;
		}
		
		if (!appearanceStone.checkConditions(player, targetItem))
		{
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			return;
		}
		
		request.setAppearanceExtractItem(extractItem);
		client.sendPacket(ExPutShapeShiftingExtractionItemResult.SUCCESS);
	}
}
