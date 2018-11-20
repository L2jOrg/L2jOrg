package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * @author Bonux
**/
public class ExShowBeautyMenuPacket extends L2GameServerPacket
{
	public static final int CHANGE_STYLE = 0x00;
	public static final int RESTORE_STYLE = 0x01;

	private final int _type;
	private final int _hairStyle;
	private final int _hairColor;
	private final int _face;

	public ExShowBeautyMenuPacket(Player player, int type)
	{
		_type = type;
		_hairStyle = player.getBeautyHairStyle() > 0 ? player.getBeautyHairStyle() : player.getHairStyle();
		_hairColor = player.getBeautyHairColor() > 0 ? player.getBeautyHairColor() : player.getHairColor();
		_face = player.getBeautyFace() > 0 ? player.getBeautyFace() : player.getFace();
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_type);  // 0x00 - изменение стиля, 0x01 отмена стиля
		writeInt(_hairStyle);
		writeInt(_hairColor);
		writeInt(_face);
	}
}
