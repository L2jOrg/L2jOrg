package l2s.gameserver.network.l2.s2c;

/**
 * Пример пакета Kamael 828 протокол:
 * 0000: fe 45 00 d9 9f 5e 8d 8b da 38 41 7a a0 3b 73 d1    .E...^...8Az.;s.
 * 0010: 27 78 23 7f af 11 4b cc c4 2c de 81 86 73 9e 92    'x#..K..,...s..
 * 0020: d2 e3 f4 3b e6 a2 fb 69 65 df 44 c2 4f 6c 75 07    ...;...ie.D.Olu.
 * 0030: b5 6b e5 29 00 d8 7f f7 78 60 f1 b7 a1 17 f8 f9    .k.)...x`......
 * 0040: 12 eb 07                                           ...
 */
public class ExSearchOrc extends L2GameServerPacket
{
	//bchb
	private final static byte[] _test = {
			(byte) 0xE4,
			(byte) 0xAB,
			(byte) 0x8E,
			(byte) 0xC5,
			(byte) 0xE9,
			(byte) 0xF9,
			(byte) 0x86,
			(byte) 0x7B,
			(byte) 0x9E,
			(byte) 0x5D,
			(byte) 0x83,
			(byte) 0x14,
			(byte) 0x05,
			(byte) 0xD4,
			(byte) 0x48,
			(byte) 0x01,
			(byte) 0xCD,
			(byte) 0xA2,
			(byte) 0x8D,
			(byte) 0x90,
			(byte) 0x62,
			(byte) 0x8C,
			(byte) 0xDA,
			(byte) 0x32,
			(byte) 0x7B,
			(byte) 0x1B,
			(byte) 0x87,
			(byte) 0x6D,
			(byte) 0x08,
			(byte) 0xC4,
			(byte) 0xE1,
			(byte) 0x56,
			(byte) 0x9B,
			(byte) 0x3B,
			(byte) 0xC3,
			(byte) 0x40,
			(byte) 0xDF,
			(byte) 0xE8,
			(byte) 0xD7,
			(byte) 0xE1,
			(byte) 0x98,
			(byte) 0x38,
			(byte) 0x1C,
			(byte) 0xA5,
			(byte) 0x8E,
			(byte) 0x45,
			(byte) 0x3F,
			(byte) 0xF2,
			(byte) 0x5E,
			(byte) 0x1C,
			(byte) 0x59,
			(byte) 0x8E,
			(byte) 0x74,
			(byte) 0x01,
			(byte) 0x9E,
			(byte) 0xC2,
			(byte) 0x00,
			(byte) 0x95,
			(byte) 0xB0,
			(byte) 0x1D,
			(byte) 0x87,
			(byte) 0xED,
			(byte) 0x9C,
			(byte) 0x8A };

	//bchb
	@Override
	protected final void writeImpl()
	{
		writeB(_test);
	}
}