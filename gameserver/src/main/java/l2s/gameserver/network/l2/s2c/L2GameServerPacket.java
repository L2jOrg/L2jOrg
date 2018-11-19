package l2s.gameserver.network.l2.s2c;

import l2s.commons.net.nio.impl.SendablePacket;
import l2s.gameserver.GameServer;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.base.MultiSellIngredient;
import l2s.gameserver.model.items.CommissionItem;
import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.ServerPacketOpcodes;
import l2s.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.support.Ensoul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class L2GameServerPacket extends SendablePacket<GameClient> implements IBroadcastPacket
{
	private static final int IS_AUGMENTED = 1 << 0;
	private static final int IS_ELEMENTED = 1 << 1;
	private static final int HAVE_ENCHANT_OPTIONS = 1 << 2;
	private static final int HAVE_ENSOUL = 1 << 4;

	private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class);

	@Override
	public final boolean write()
	{
		try
		{
			if(writeOpcodes())
			{
				writeImpl();
				return true;
			}
		}
		catch(Exception e)
		{
			_log.error("Client: " + getClient() + " - Failed writing: " + getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), e);
		}
		return false;
	}

	protected ServerPacketOpcodes getOpcodes()
	{
		try
		{
			return ServerPacketOpcodes.valueOf(getClass().getSimpleName());
		}
		catch(Exception e)
		{
			_log.error("Cannot find serverpacket opcode: " + getClass().getSimpleName() + "!");
		}
		return null;
	}

	protected boolean writeOpcodes()
	{
		ServerPacketOpcodes opcodes = getOpcodes();
		if(opcodes == null)
		{
			return false;
		}

		int opcode = opcodes.getId();
		writeC(opcode);
		if(opcode == 0xFE)
		{
			writeH(opcodes.getExId());
		}

		return true;
	}

	protected abstract void writeImpl();

	protected void writeD(boolean b)
	{
		writeD(b ? 1 : 0);
	}

	protected void writeH(boolean b)
	{
		writeH(b ? 1 : 0);
	}

	protected void writeC(boolean b)
	{
		writeC(b ? 1 : 0);
	}

	/**
	 * Отсылает число позиций + массив
	 */
	protected void writeDD(int[] values, boolean sendCount)
	{
		if(sendCount)
		{
			getByteBuffer().putInt(values.length);
		}
		for(int value : values)
		{
			getByteBuffer().putInt(value);
		}
	}

	protected void writeDD(int[] values)
	{
		writeDD(values, false);
	}

	protected void writeItemInfo(ItemInstance item)
	{
		writeItemInfo(null, item, item.getCount());
	}

	protected void writeItemInfo(Player player, ItemInstance item)
	{
		writeItemInfo(player, item, item.getCount());
	}

	protected void writeItemInfo(ItemInstance item, long count)
	{
		writeItemInfo(null, item, count);
	}

	protected void writeItemInfo(Player player, ItemInstance item, long count)
	{
		int flags = 0;

		if(item.isAugmented())
			flags |= IS_AUGMENTED;

		int attackElementValue = item.getAttackElementValue();
		int defenceFire = item.getDefenceFire();
		int defenceWater = item.getDefenceWater();
		int defenceWind = item.getDefenceWind();
		int defenceEarth = item.getDefenceEarth();
		int defenceHoly = item.getDefenceHoly();
		int defenceUnholy = item.getDefenceUnholy();
		if(attackElementValue > 0 || defenceFire > 0 || defenceWater > 0 || defenceWind > 0 || defenceEarth > 0 || defenceHoly > 0 || defenceUnholy > 0)
			flags |= IS_ELEMENTED;

		for(int enchantOption : item.getEnchantOptions())
		{
			if(enchantOption > 0)
			{
				flags |= HAVE_ENCHANT_OPTIONS;
				break;
			}
		}

		Ensoul[] normalEnsouls = item.getNormalEnsouls();
		Ensoul[] specialEnsouls = item.getSpecialEnsouls();
		if(normalEnsouls.length > 0 || specialEnsouls.length > 0)
			flags |= HAVE_ENSOUL;

		writeC(flags);
		writeD(item.getObjectId());
		writeD(item.getItemId());
		writeC(item.isEquipped() ? -1 : item.getEquipSlot());
		writeQ(count);
		writeC(item.getTemplate().getType2());
		writeC(item.getCustomType1());
		writeH(item.isEquipped() ? 1 : 0);
		writeQ(item.getBodyPart());
		writeC(item.getFixedEnchantLevel(player));
		writeC(item.getCustomType2());
		writeD(item.getShadowLifeTime());
		writeD(item.getTemporalLifeTime());

		if(player != null)
			writeC(!item.getTemplate().isBlocked(player, item));
		else
			writeC(0x01);

		if((flags & IS_AUGMENTED) == IS_AUGMENTED)
		{
			writeD(item.getVariation1Id());
			writeD(item.getVariation2Id());
		}

		if((flags & IS_ELEMENTED) == IS_ELEMENTED)
		{
			writeH(item.getAttackElement().getId());
			writeH(attackElementValue);
			writeH(defenceFire);
			writeH(defenceWater);
			writeH(defenceWind);
			writeH(defenceEarth);
			writeH(defenceHoly);
			writeH(defenceUnholy);
		}

		if((flags & HAVE_ENCHANT_OPTIONS) == HAVE_ENCHANT_OPTIONS)
		{
			writeD(item.getEnchantOptions()[0]);
			writeD(item.getEnchantOptions()[1]);
			writeD(item.getEnchantOptions()[2]);
		}

		if((flags & HAVE_ENSOUL) == HAVE_ENSOUL)
		{
			writeC(normalEnsouls.length);
			for(Ensoul ensoul : normalEnsouls)
				writeD(ensoul.getId());

			writeC(specialEnsouls.length);
			for(Ensoul ensoul : specialEnsouls)
				writeD(ensoul.getId());
		}
	}

	protected void writeItemInfo(ItemInfo item)
	{
		writeItemInfo(item, item.getCount());
	}

	protected void writeItemInfo(ItemInfo item, long count)
	{
		int flags = 0;

		if(item.getVariation1Id() > 0 || item.getVariation2Id() > 0)
			flags |= IS_AUGMENTED;

		int attackElementValue = item.getAttackElementValue();
		int defenceFire = item.getDefenceFire();
		int defenceWater = item.getDefenceWater();
		int defenceWind = item.getDefenceWind();
		int defenceEarth = item.getDefenceEarth();
		int defenceHoly = item.getDefenceHoly();
		int defenceUnholy = item.getDefenceUnholy();
		if(attackElementValue > 0 || defenceFire > 0 || defenceWater > 0 || defenceWind > 0 || defenceEarth > 0 || defenceHoly > 0 || defenceUnholy > 0)
			flags |= IS_ELEMENTED;

		for(int enchantOption : item.getEnchantOptions())
		{
			if(enchantOption > 0)
			{
				flags |= HAVE_ENCHANT_OPTIONS;
				break;
			}
		}

		Ensoul[] normalEnsouls = item.getNormalEnsouls();
		Ensoul[] specialEnsouls = item.getSpecialEnsouls();
		if(normalEnsouls.length > 0 || specialEnsouls.length > 0)
			flags |= HAVE_ENSOUL;

		writeC(flags);
		writeD(item.getObjectId());
		writeD(item.getItemId());
		writeC(item.isEquipped() ? -1 : item.getEquipSlot());
		writeQ(count);
		writeC(item.getItem().getType2());
		writeC(item.getCustomType1());
		writeH(item.isEquipped() ? 1 : 0);
		writeQ(item.getItem().getBodyPart());
		writeC(item.getEnchantLevel());
		writeC(item.getCustomType2());
		writeD(item.getShadowLifeTime());
		writeD(item.getTemporalLifeTime());
		writeC(!item.isBlocked());

		if((flags & IS_AUGMENTED) == IS_AUGMENTED)
		{
			writeD(item.getVariation1Id());
			writeD(item.getVariation2Id());
		}

		if((flags & IS_ELEMENTED) == IS_ELEMENTED)
		{
			writeH(item.getAttackElement());
			writeH(attackElementValue);
			writeH(defenceFire);
			writeH(defenceWater);
			writeH(defenceWind);
			writeH(defenceEarth);
			writeH(defenceHoly);
			writeH(defenceUnholy);
		}

		if((flags & HAVE_ENCHANT_OPTIONS) == HAVE_ENCHANT_OPTIONS)
		{
			writeD(item.getEnchantOptions()[0]);
			writeD(item.getEnchantOptions()[1]);
			writeD(item.getEnchantOptions()[2]);
		}

		if((flags & HAVE_ENSOUL) == HAVE_ENSOUL)
		{
			writeC(normalEnsouls.length);
			for(Ensoul ensoul : normalEnsouls)
				writeD(ensoul.getId());

			writeC(specialEnsouls.length);
			for(Ensoul ensoul : specialEnsouls)
				writeD(ensoul.getId());
		}
	}

	protected void writeCommissionItem(CommissionItem item)
	{
		writeD(item.getItemId());
		writeC(item.getEquipSlot());
		writeQ(item.getCount());
		writeH(item.getItem().getType2()); //??item.getCustomType1()??
		writeQ(item.getItem().getBodyPart());
		writeH(item.getEnchantLevel());
		writeH(item.getCustomType2());
		writeH(item.getAttackElement());
		writeH(item.getAttackElementValue());
		writeH(item.getDefenceFire());
		writeH(item.getDefenceWater());
		writeH(item.getDefenceWind());
		writeH(item.getDefenceEarth());
		writeH(item.getDefenceHoly());
		writeH(item.getDefenceUnholy());
		writeD(item.getEnchantOptions()[0]);
		writeD(item.getEnchantOptions()[1]);
		writeD(item.getEnchantOptions()[2]);
	}

	protected void writeItemElements(MultiSellIngredient item)
	{
		if(item.getItemId() <= 0)
		{
			writeItemElements();
			return;
		}

		ItemTemplate i = ItemHolder.getInstance().getTemplate(item.getItemId());
		if(item.getItemAttributes().getValue() > 0)
		{
			if(i.isWeapon())
			{
				Element e = item.getItemAttributes().getElement();
				writeH(e.getId()); // attack element (-1 - none)
				writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e)); // attack element value
				writeH(0); // водная стихия (fire pdef)
				writeH(0); // огненная стихия (water pdef)
				writeH(0); // земляная стихия (wind pdef)
				writeH(0); // воздушная стихия (earth pdef)
				writeH(0); // темная стихия (holy pdef)
				writeH(0); // светлая стихия (dark pdef)
			}
			else if(i.isArmor())
			{
				writeH(-1); // attack element (-1 - none)
				writeH(0); // attack element value
				for(Element e : Element.VALUES)
					writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e));
			}
			else
				writeItemElements();
		}
		else
			writeItemElements();
	}

	protected void writeItemElements()
	{
		writeH(-1); // attack element (-1 - none)
		writeH(0x00); // attack element value
		writeH(0x00); // водная стихия (fire pdef)
		writeH(0x00); // огненная стихия (water pdef)
		writeH(0x00); // земляная стихия (wind pdef)
		writeH(0x00); // воздушная стихия (earth pdef)
		writeH(0x00); // темная стихия (holy pdef)
		writeH(0x00); // светлая стихия (dark pdef)
	}

	public String getType()
	{
		return "[S] " + getClass().getSimpleName();
	}

	public L2GameServerPacket packet(Player player)
	{
		return this;
	}

	/**
	 * @param masks
	 * @param type
	 * @return {@code true} if the mask contains the current update component type
	 */
	protected static boolean containsMask(int masks, IUpdateTypeComponent type)
	{
		return (masks & type.getMask()) == type.getMask();
	}
}