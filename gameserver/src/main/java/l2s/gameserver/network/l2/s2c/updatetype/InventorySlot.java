package l2s.gameserver.network.l2.s2c.updatetype;

import l2s.gameserver.model.items.Inventory;

/**
 * @author UnAfraid
 */
public enum InventorySlot implements IUpdateTypeComponent
{
	PENDANT(Inventory.PAPERDOLL_PENDANT),
	REAR(Inventory.PAPERDOLL_REAR),
	LEAR(Inventory.PAPERDOLL_LEAR),
	NECK(Inventory.PAPERDOLL_NECK),
	RFINGER(Inventory.PAPERDOLL_RFINGER),
	LFINGER(Inventory.PAPERDOLL_LFINGER),
	HEAD(Inventory.PAPERDOLL_HEAD),
	RHAND(Inventory.PAPERDOLL_RHAND),
	LHAND(Inventory.PAPERDOLL_LHAND),
	GLOVES(Inventory.PAPERDOLL_GLOVES),
	CHEST(Inventory.PAPERDOLL_CHEST),
	LEGS(Inventory.PAPERDOLL_LEGS),
	FEET(Inventory.PAPERDOLL_FEET),
	CLOAK(Inventory.PAPERDOLL_BACK),
	LRHAND(Inventory.PAPERDOLL_LRHAND),
	HAIR(Inventory.PAPERDOLL_HAIR),
	HAIR2(Inventory.PAPERDOLL_DHAIR),
	RBRACELET(Inventory.PAPERDOLL_RBRACELET),
	LBRACELET(Inventory.PAPERDOLL_LBRACELET),
	DECO1(Inventory.PAPERDOLL_DECO1),
	DECO2(Inventory.PAPERDOLL_DECO2),
	DECO3(Inventory.PAPERDOLL_DECO3),
	DECO4(Inventory.PAPERDOLL_DECO4),
	DECO5(Inventory.PAPERDOLL_DECO5),
	DECO6(Inventory.PAPERDOLL_DECO6),
	BELT(Inventory.PAPERDOLL_BELT),
	BROOCH(Inventory.PAPERDOLL_BROOCH),
	BROOCH_JEWEL(Inventory.PAPERDOLL_JEWEL1),
	BROOCH_JEWEL2(Inventory.PAPERDOLL_JEWEL2),
	BROOCH_JEWEL3(Inventory.PAPERDOLL_JEWEL3),
	BROOCH_JEWEL4(Inventory.PAPERDOLL_JEWEL4),
	BROOCH_JEWEL5(Inventory.PAPERDOLL_JEWEL5),
	BROOCH_JEWEL6(Inventory.PAPERDOLL_JEWEL6);

	public static final InventorySlot[] VALUES = values();
	private final int _paperdollSlot;

	public static InventorySlot valueOf(int slot)
	{
		for(InventorySlot s : VALUES)
		{
			if(s.getSlot() == slot)
				return s;
		}
		return null;
	}

	private InventorySlot(int paperdollSlot)
	{
		_paperdollSlot = paperdollSlot;
	}

	public int getSlot()
	{
		return _paperdollSlot;
	}

	@Override
	public int getMask()
	{
		return ordinal();
	}
}