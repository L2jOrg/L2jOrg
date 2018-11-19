package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.RecipeShopSellListPacket;

/**
 * Возврат к списку из информации о рецепте
 */
public class RequestRecipeShopSellList extends L2GameClientPacket
{
	int _manufacturerId;

	@Override
	protected void readImpl()
	{
		_manufacturerId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		Player manufacturer = (Player) activeChar.getVisibleObject(_manufacturerId);
		if(manufacturer == null || manufacturer.getPrivateStoreType() != Player.STORE_PRIVATE_MANUFACTURE || !manufacturer.checkInteractionDistance(activeChar))
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new RecipeShopSellListPacket(activeChar, manufacturer));
	}
}