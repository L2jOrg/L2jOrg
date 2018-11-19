package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;

/**
 * @author VISTALL
 * @date 14:45/08.03.2011
 */
public class SystemMessagePacket extends SysMsgContainer<SystemMessagePacket>
{
	public SystemMessagePacket(SystemMsg message)
	{
		super(message);
	}

	public static SystemMessagePacket obtainItems(int itemId, long count, int enchantLevel)
	{
		if(itemId == 57)
			return new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1_ADENA).addLong(count);
		if(count > 1)
			return new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S2_S1S).addItemName(itemId).addLong(count);
		if(enchantLevel > 0)
			return new SystemMessagePacket(SystemMsg.YOU_HAVE_OBTAINED_A_S1_S2).addInteger(enchantLevel).addItemName(itemId);
		return new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1).addItemName(itemId);
	}

	public static SystemMessagePacket obtainItems(ItemInstance item)
	{
		return obtainItems(item.getItemId(), item.getCount(), item.isEquipable() ? item.getEnchantLevel() : 0);
	}

	public static SystemMessagePacket obtainItemsBy(int itemId, long count, int enchantLevel, Creature target)
	{
		if(count > 1)
			return new SystemMessagePacket(SystemMsg.C1_HAS_OBTAINED_S3_S2).addName(target).addItemName(itemId).addLong(count);
		if(enchantLevel > 0)
			return new SystemMessagePacket(SystemMsg.C1_HAS_OBTAINED_S2S3).addName(target).addInteger(enchantLevel).addItemName(itemId);
		return new SystemMessagePacket(SystemMsg.C1_HAS_OBTAINED_S2).addName(target).addItemName(itemId);
	}

	public static SystemMessagePacket obtainItemsBy(ItemInstance item, Creature target)
	{
		return obtainItemsBy(item.getItemId(), item.getCount(), item.isEquipable() ? item.getEnchantLevel() : 0, target);
	}

	public static SystemMessagePacket removeItems(int itemId, long count)
	{
		if(itemId == 57)
			return new SystemMessagePacket(SystemMsg.S1_ADENA_DISAPPEARED).addLong(count);
		if(count > 1)
			return new SystemMessagePacket(SystemMsg.S2_S1_HAS_DISAPPEARED).addItemName(itemId).addLong(count);
		return new SystemMessagePacket(SystemMsg.S1_HAS_DISAPPEARED).addItemName(itemId);
	}

	public static SystemMessagePacket removeItems(ItemInstance item)
	{
		return removeItems(item.getItemId(), item.getCount());
	}

	@Override
	protected void writeImpl()
	{
		writeElements();
	}
}
