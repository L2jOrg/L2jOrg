package org.l2j.gameserver.mobius.gameserver.network.clientpackets.appearance;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.AppearanceItemData;
import org.l2j.gameserver.mobius.gameserver.enums.ItemLocation;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.ShapeShiftingItemRequest;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.PcInventory;
import org.l2j.gameserver.mobius.gameserver.model.items.appearance.AppearanceStone;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.appearance.ExPutShapeShiftingTargetItemResult;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestExTryToPutShapeShiftingTargetItem extends IClientIncomingPacket
{
	private int _targetItemObjId;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_targetItemObjId = packet.getInt();
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
			client.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			return;
		}
		
		final PcInventory inventory = player.getInventory();
		final L2ItemInstance targetItem = inventory.getItemByObjectId(_targetItemObjId);
		L2ItemInstance stone = request.getAppearanceStone();
		
		if ((targetItem == null) || (stone == null))
		{
			client.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone.getOwnerId() != player.getObjectId()) || (targetItem.getOwnerId() != player.getObjectId()))
		{
			client.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!targetItem.getItem().isAppearanceable())
		{
			client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_MODIFIED_OR_RESTORED);
			client.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			return;
		}
		
		if ((targetItem.getItemLocation() != ItemLocation.INVENTORY) && (targetItem.getItemLocation() != ItemLocation.PAPERDOLL))
		{
			client.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			client.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		final AppearanceStone appearanceStone = AppearanceItemData.getInstance().getStone(stone.getId());
		if (appearanceStone == null)
		{
			client.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!appearanceStone.checkConditions(player, targetItem))
		{
			player.sendPacket(ExPutShapeShiftingTargetItemResult.FAILED);
			return;
		}
		
		client.sendPacket(new ExPutShapeShiftingTargetItemResult(ExPutShapeShiftingTargetItemResult.RESULT_SUCCESS, appearanceStone.getCost()));
	}
}
