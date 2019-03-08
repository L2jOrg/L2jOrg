package org.l2j.gameserver.mobius.gameserver.network.clientpackets.ensoul;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.EnsoulData;
import org.l2j.gameserver.mobius.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.mobius.gameserver.model.ensoul.EnsoulStone;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ensoul.ExEnsoulResult;
import org.l2j.gameserver.mobius.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestItemEnsoul extends IClientIncomingPacket
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestItemEnsoul.class);
	private int _itemObjectId;
	private EnsoulItemOption[] _options;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_itemObjectId = packet.getInt();
		final int options = packet.get();
		if ((options > 0) && (options <= 3))
		{
			_options = new EnsoulItemOption[options];
			for (int i = 0; i < options; i++)
			{
				final int type = packet.get(); // 1 = normal ; 2 = mystic
				final int position = packet.get();
				final int soulCrystalObjectId = packet.getInt();
				final int soulCrystalOption = packet.getInt();
				if ((position > 0) && (position < 3) && ((type == 1) || (type == 2)))
				{
					_options[i] = new EnsoulItemOption(type, position, soulCrystalObjectId, soulCrystalOption);
				}
			}
		}
	}
	
	@Override
	public void runImpl() {
		if(_options == null) {
			return;
		}

		final L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHEN_PRIVATE_STORE_AND_WORKSHOP_ARE_OPENED);
			return;
		}
		else if (player.hasAbnormalType(AbnormalType.FREEZING))
		{
			player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_IN_FROZEN_STATE);
		}
		else if (player.isDead())
		{
			player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_IF_THE_CHARACTER_IS_DEAD);
			return;
		}
		else if ((player.getActiveTradeList() != null) || player.hasItemRequest())
		{
			player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_DURING_EXCHANGE);
			return;
		}
		else if (player.hasAbnormalType(AbnormalType.PARALYZE))
		{
			player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_PETRIFIED);
			return;
		}
		else if (player.isFishing())
		{
			player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_DURING_FISHING);
			return;
		}
		else if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_SITTING);
			return;
		}
		else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player))
		{
			player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_IN_COMBAT);
			return;
		}
		
		final L2ItemInstance item = player.getInventory().getItemByObjectId(_itemObjectId);
		if (item == null)
		{
			LOGGER.warn("Player: {} attempting to ensoul item without having it!", player);
			return;
		}
		else if (!item.isEquipable())
		{
			LOGGER.warn("Player: {} attempting to ensoul non equippable item: {}!", player, item);
			return;
		}
		else if (!item.isWeapon())
		{
			LOGGER.warn("Player: " + player + " attempting to ensoul item that's not a weapon: " + item + "!");
			return;
		}
		else if (item.isCommonItem())
		{
			LOGGER.warn("Player: " + player + " attempting to ensoul common item: " + item + "!");
			return;
		}
		else if (item.isShadowItem())
		{
			LOGGER.warn("Player: " + player + " attempting to ensoul shadow item: " + item + "!");
			return;
		}
		else if (item.isHeroItem())
		{
			LOGGER.warn("Player: " + player + " attempting to ensoul hero item: " + item + "!");
			return;
		}
		
		if ((_options == null) || (_options.length == 0))
		{
			LOGGER.warn("Player: " + player + " attempting to ensoul item without any special ability declared!");
			return;
		}
		
		int success = 0;
		final InventoryUpdate iu = new InventoryUpdate();
		for (EnsoulItemOption itemOption : _options)
		{
			final int position = itemOption.getPosition() - 1;
			final L2ItemInstance soulCrystal = player.getInventory().getItemByObjectId(itemOption.getSoulCrystalObjectId());
			if (soulCrystal == null)
			{
				player.sendPacket(SystemMessageId.THE_RUNE_DOES_NOT_FIT);
				continue;
			}
			
			final EnsoulStone stone = EnsoulData.getInstance().getStone(soulCrystal.getId());
			if (stone == null)
			{
				continue;
			}
			
			if (!stone.getOptions().contains(itemOption.getSoulCrystalOption()))
			{
				LOGGER.warn("Player: " + player + " attempting to ensoul item option that stone doesn't contains!");
				continue;
			}
			
			final EnsoulOption option = EnsoulData.getInstance().getOption(itemOption.getSoulCrystalOption());
			if (option == null)
			{
				LOGGER.warn("Player: " + player + " attempting to ensoul item option that doesn't exists!");
				continue;
			}
			
			ItemHolder fee;
			if (itemOption.getType() == 1)
			{
				// Normal Soul Crystal
				fee = EnsoulData.getInstance().getEnsoulFee(item.getItem().getCrystalType(), position);
				if ((itemOption.getPosition() == 1) || (itemOption.getPosition() == 2))
				{
					if (item.getSpecialAbility(position) != null)
					{
						fee = EnsoulData.getInstance().getResoulFee(item.getItem().getCrystalType(), position);
					}
				}
			}
			else if (itemOption.getType() == 2)
			{
				// Mystic Soul Crystal
				fee = EnsoulData.getInstance().getEnsoulFee(item.getItem().getCrystalType(), position);
				if (itemOption.getPosition() == 1)
				{
					if (item.getAdditionalSpecialAbility(position) != null)
					{
						fee = EnsoulData.getInstance().getResoulFee(item.getItem().getCrystalType(), position);
					}
				}
			}
			else
			{
				LOGGER.warn("Player: " + player + " attempting to ensoul item option with unhandled type: " + itemOption.getType() + "!");
				continue;
			}
			
			if (fee == null)
			{
				LOGGER.warn("Player: " + player + " attempting to ensoul item option that doesn't exists! (unknown fee)");
				continue;
			}
			
			final L2ItemInstance gemStones = player.getInventory().getItemByItemId(fee.getId());
			if ((gemStones == null) || (gemStones.getCount() < fee.getCount()))
			{
				continue;
			}
			
			if (player.destroyItem("EnsoulOption", soulCrystal, 1, player, true) && player.destroyItem("EnsoulOption", gemStones, fee.getCount(), player, true))
			{
				item.addSpecialAbility(option, position, stone.getSlotType(), true);
				success = 1;
			}
			
			iu.addModifiedItem(soulCrystal);
			iu.addModifiedItem(gemStones);
			iu.addModifiedItem(item);
		}
		player.sendInventoryUpdate(iu);
		if (item.isEquipped())
		{
			item.applySpecialAbilities();
		}
		player.sendPacket(new ExEnsoulResult(success, item));
	}
	
	static class EnsoulItemOption
	{
		private final int _type;
		private final int _position;
		private final int _soulCrystalObjectId;
		private final int _soulCrystalOption;
		
		EnsoulItemOption(int type, int position, int soulCrystalObjectId, int soulCrystalOption)
		{
			_type = type;
			_position = position;
			_soulCrystalObjectId = soulCrystalObjectId;
			_soulCrystalOption = soulCrystalOption;
		}
		
		public int getType()
		{
			return _type;
		}
		
		public int getPosition()
		{
			return _position;
		}
		
		public int getSoulCrystalObjectId()
		{
			return _soulCrystalObjectId;
		}
		
		public int getSoulCrystalOption()
		{
			return _soulCrystalOption;
		}
	}
}
