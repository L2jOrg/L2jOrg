package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

@StaticPacket
public class ExIsCharNameCreatable extends L2GameServerPacket
{
	public static final L2GameServerPacket SUCCESS = new ExIsCharNameCreatable(-1); // Успешное создание чара.
	public static final L2GameServerPacket UNABLE_TO_CREATE_A_CHARACTER = new ExIsCharNameCreatable(0x00); // Не удалось создать персонажа.
	public static final L2GameServerPacket TOO_MANY_CHARACTERS = new ExIsCharNameCreatable(0x01); // Нельза создать персонажа. Удалите существующего и попробуйте еще раз.
	public static final L2GameServerPacket NAME_ALREADY_EXISTS = new ExIsCharNameCreatable(0x02); // Такое имя уже используется.
	public static final L2GameServerPacket ENTER_CHAR_NAME__MAX_16_CHARS = new ExIsCharNameCreatable(0x03); // Введите имя персонажа (максимум 16 символов).
	public static final L2GameServerPacket WRONG_NAME = new ExIsCharNameCreatable(0x04); // Не правильное имя, попробуйте еще раз.
	public static final L2GameServerPacket WRONG_SERVER = new ExIsCharNameCreatable(0x05); // Персонажи не могут быть созданы с этого сервера.
	public static final L2GameServerPacket DONT_CREATE_CHARS_ON_THIS_SERVER = new ExIsCharNameCreatable(0x06); // Нельзя создать персонажа на даном сервере. Действуют ограничения не позволяющие создавать песронажа.
	public static final L2GameServerPacket DONT_USE_ENG_CHARS = new ExIsCharNameCreatable(0x07); // Нельзя использовать англ символы в имени персонажа.

	private int _errorCode;

	private ExIsCharNameCreatable(int errorCode)
	{
		_errorCode = errorCode;
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_errorCode);
	}

	@Override
	protected int packetSize() {
		return 9;
	}
}
