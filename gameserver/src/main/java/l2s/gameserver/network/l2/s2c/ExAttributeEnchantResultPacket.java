package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.base.Element;

/**
 * @author Bonux
**/
public class ExAttributeEnchantResultPacket extends L2GameServerPacket
{
	private final boolean _isWeapon;
	private final Element _element;
	private final int _oldValue;
	private final int _newValue;
	private final int _usedStones;
	private final int _failedStones;

	public ExAttributeEnchantResultPacket(boolean isWeapon, Element element, int oldValue, int newValue, int usedStones, int failedStones)
	{
		_isWeapon = isWeapon;
		_element = element;
		_oldValue = oldValue;
		_newValue = newValue;
		_usedStones = usedStones;
		_failedStones = failedStones;
	}

	@Override
	protected final void writeImpl()
	{
		writeH(0x00); // TODO
		writeH(0x00); // TODO
		writeC(_isWeapon ? 0x01 : 0x00); // Armor - 0x00 / Weapon - 0x01
		writeH(_element.getId()); // Element
		writeH(_oldValue);
		writeH(_newValue);
		writeH(_usedStones);
		writeH(_failedStones);
	}
}