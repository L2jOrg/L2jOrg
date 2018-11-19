package l2s.gameserver.network.l2.c2s;

import java.util.List;

import l2s.gameserver.data.xml.holder.EnsoulHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExEnSoulExtractionResult;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.templates.item.support.Ensoul;
import l2s.gameserver.templates.item.support.EnsoulFee;
import l2s.gameserver.templates.item.support.EnsoulFee.EnsoulFeeInfo;
import l2s.gameserver.templates.item.support.EnsoulFee.EnsoulFeeItem;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.NpcUtils;

public class RequestTryEnSoulExtraction extends L2GameClientPacket
{
	private int _itemObjectId, _ensoulType, _ensoulId;

	@Override
	protected void readImpl()
	{
		_itemObjectId = readD();
		_ensoulType = readC();
		_ensoulId = readC();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(NpcUtils.canPassPacket(activeChar, this) == null)
		{
			activeChar.sendPacket(ExEnSoulExtractionResult.FAIL);
			return;
		}

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendPacket(ExEnSoulExtractionResult.FAIL);
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(ExEnSoulExtractionResult.FAIL);
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendPacket(ExEnSoulExtractionResult.FAIL);
			return;
		}

		activeChar.getInventory().writeLock();
		try
		{
			ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_itemObjectId);
			if(targetItem == null)
			{
				activeChar.sendPacket(ExEnSoulExtractionResult.FAIL);
				return;
			}

			Ensoul enosul = targetItem.getEnsoul(_ensoulType, _ensoulId);
			if(enosul == null)
			{
				activeChar.sendPacket(ExEnSoulExtractionResult.FAIL);
				return;
			}

			EnsoulFee ensoulFee = EnsoulHolder.getInstance().getEnsoulFee(targetItem.getGrade());
			if(ensoulFee == null)
			{
				activeChar.sendPacket(ExEnSoulExtractionResult.FAIL);
				return;
			}

			EnsoulFeeInfo feeInfo = ensoulFee.getFeeInfo(_ensoulType, _ensoulId);
			if(feeInfo == null)
			{
				activeChar.sendPacket(ExEnSoulExtractionResult.FAIL);
				return;
			}

			List<EnsoulFeeItem> feeItems = feeInfo.getRemoveFee();
			for(EnsoulFeeItem feeItem : feeItems)
			{
				if(!ItemFunctions.haveItem(activeChar, feeItem.getId(), feeItem.getCount()))
				{
					activeChar.sendPacket(ExEnSoulExtractionResult.FAIL);
					return;
				}
			}

			for(EnsoulFeeItem feeItem : feeItems)
				ItemFunctions.deleteItem(activeChar, feeItem.getId(), feeItem.getCount());

			boolean equipped = false;
			if((equipped = targetItem.isEquipped()))
			{
				activeChar.getInventory().isRefresh = true;
				activeChar.getInventory().unEquipItem(targetItem);
			}

			targetItem.removeEnsoul(_ensoulType, _ensoulId, true);

			if(equipped)
			{
				activeChar.getInventory().equipItem(targetItem);
				activeChar.getInventory().isRefresh = false;
			}

			ItemFunctions.addItem(activeChar, enosul.getItemId(), 1);

			activeChar.sendPacket(new InventoryUpdatePacket().addModifiedItem(activeChar, targetItem));
			activeChar.sendPacket(new ExEnSoulExtractionResult(targetItem.getNormalEnsouls(), targetItem.getSpecialEnsouls()));
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}
	}
}