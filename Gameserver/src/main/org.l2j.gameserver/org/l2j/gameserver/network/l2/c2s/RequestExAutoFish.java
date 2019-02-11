package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.FishDataHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Zone.ZoneType;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExAutoFishAvailable;
import org.l2j.gameserver.templates.fish.LureTemplate;
import org.l2j.gameserver.templates.fish.RodTemplate;
import org.l2j.gameserver.templates.item.WeaponTemplate;

import java.nio.ByteBuffer;

public class RequestExAutoFish extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isFishing())
			activeChar.getFishing().stop();
		else
		{
			activeChar.sendPacket(ExAutoFishAvailable.REMOVE);

			if(!activeChar.isInZone(ZoneType.FISHING))
				return;

			if(Config.FISHING_ONLY_PREMIUM_ACCOUNTS && !activeChar.hasPremiumAccount())
			{
				activeChar.sendPacket(SystemMsg.YOU_CAN_ONLY_FUSH_DURING_THE_PAID_PERIOD);
				return;
			}

			if(Config.FISHING_MINIMUM_LEVEL > activeChar.getLevel())
			{
				activeChar.sendPacket(SystemMsg.YOU_CAN_ONLY_FUSH_DURING_THE_PAID_PERIOD);
				return;
			}

			if(activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_MANUFACTURE_OR_PRIVATE_STORE);
				return;
			}

			WeaponTemplate weaponItem = activeChar.getActiveWeaponTemplate();
			if(weaponItem == null || weaponItem.getItemType() != WeaponTemplate.WeaponType.ROD)
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
				return;
			}

			RodTemplate rod = FishDataHolder.getInstance().getRod(weaponItem.getItemId());
			if(rod == null)
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
				return;
			}

			ItemInstance lureItem = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			if(lureItem == null || lureItem.getCount() < rod.getShotConsumeCount())
			{
				activeChar.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
				return;
			}

			LureTemplate lure = FishDataHolder.getInstance().getLure(lureItem.getItemId());
			if(lure == null)
			{
				activeChar.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
				return;
			}

			if(activeChar.isInWater())
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
				return;
			}

			if(activeChar.isInBoat() || activeChar.isTransformed())
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
				return;
			}

			if(activeChar.getFishing().findHookLocation() == null)
			{
				activeChar.sendPacket(SystemMsg.YOU_CANT_FISH_HERE);
				return;
			}

			activeChar.getFishing().start(rod, lure);
		}
	}
}