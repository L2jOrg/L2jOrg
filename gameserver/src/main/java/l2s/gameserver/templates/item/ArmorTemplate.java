package l2s.gameserver.templates.item;

import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.handler.items.ItemHandler;
import l2s.gameserver.templates.StatsSet;

public final class ArmorTemplate extends ItemTemplate
{
	public enum ArmorType implements ItemType
	{
		NONE("None"),
		LIGHT("Light"),
		HEAVY("Heavy"),
		MAGIC("Magic"),
		SIGIL("Sigil");

		public final static ArmorType[] VALUES = values();

		private final long _mask;
		private final String _name;

		ArmorType(String name)
		{
			_mask = 1L << ordinal();
			_name = name;
		}

		public long mask()
		{
			return _mask;
		}

		public IItemHandler getHandler()
		{
			return null;
		}

		public ExItemType getExType()
		{
			return null;
		}

		@Override
		public String toString()
		{
			return _name;
		}
	}

	public ArmorTemplate(StatsSet set)
	{
		super(set);

		_type = set.getEnum("type", ArmorType.class);

		if((SLOTS_JEWELRY & _bodyPart) == _bodyPart)
			_type1 = TYPE1_WEAPON_RING_EARRING_NECKLACE;
		else if(_bodyPart == SLOT_HAIR || _bodyPart == SLOT_DHAIR || _bodyPart == SLOT_HAIRALL)
			_type1 = TYPE1_OTHER;
		else
			_type1 = TYPE1_SHIELD_ARMOR;

		if(_type == ArmorType.SIGIL)
			_exType = ExItemType.SIGIL;
		else if(_bodyPart == ItemTemplate.SLOT_HEAD)
			_exType = ExItemType.HELMET;
		else if(_bodyPart == ItemTemplate.SLOT_CHEST)
			_exType = ExItemType.UPPER_PIECE;
		else if(_bodyPart == ItemTemplate.SLOT_LEGS)
			_exType = ExItemType.LOWER_PIECE;
		else if(_bodyPart == ItemTemplate.SLOT_FULL_ARMOR || _bodyPart == ItemTemplate.SLOT_FORMAL_WEAR)
			_exType = ExItemType.FULL_BODY;
		else if(_bodyPart == ItemTemplate.SLOT_GLOVES)
			_exType = ExItemType.GLOVES;
		else if(_bodyPart == ItemTemplate.SLOT_FEET)
			_exType = ExItemType.FEET;
		else if(_bodyPart == ItemTemplate.SLOT_PENDANT)
			_exType = ExItemType.PENDANT;
		else if(_bodyPart == ItemTemplate.SLOT_BACK)
			_exType = ExItemType.CLOAK;
		else if((_bodyPart & ItemTemplate.SLOT_R_FINGER) == ItemTemplate.SLOT_R_FINGER || (_bodyPart & ItemTemplate.SLOT_L_FINGER) == ItemTemplate.SLOT_L_FINGER)
			_exType = ExItemType.RING;
		else if((_bodyPart & ItemTemplate.SLOT_R_EAR) == ItemTemplate.SLOT_R_EAR || (_bodyPart & ItemTemplate.SLOT_L_EAR) == ItemTemplate.SLOT_L_EAR)
			_exType = ExItemType.EARRING;
		else if(_bodyPart == ItemTemplate.SLOT_NECK)
			_exType = ExItemType.NECKLACE;
		else if(_bodyPart == ItemTemplate.SLOT_BELT)
			_exType = ExItemType.BELT;
		else if(_bodyPart == ItemTemplate.SLOT_R_BRACELET || _bodyPart == ItemTemplate.SLOT_L_BRACELET)
			_exType = ExItemType.BRACELET;
		else if(_bodyPart == ItemTemplate.SLOT_HAIR || _bodyPart == ItemTemplate.SLOT_DHAIR || _bodyPart == ItemTemplate.SLOT_HAIRALL)
			_exType = ExItemType.HAIR_ACCESSORY;

		_type2 = _exType.mask();

		initEnchantFuncs();
	}

	@Override
	public IItemHandler getHandler()
	{
		return ItemHandler.EQUIPABLE_HANDLER;
	}

	/**
	 * Returns the type of the armor.
	 * @return L2ArmorType
	 */
	@Override
	public ArmorType getItemType()
	{
		return (ArmorType) super.getItemType();
	}

	/**
	 * Returns the ID of the item after applying the mask.
	 * @return int : ID of the item
	 */
	@Override
	public final long getItemMask()
	{
		return getItemType().mask();
	}
}