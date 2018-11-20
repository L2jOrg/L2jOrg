package org.l2j.gameserver.network.l2.c2s;

@SuppressWarnings("unused")
public class RequestSendMsnChatLog extends L2GameClientPacket
{
	private int unk3;
	private String unk, unk2;

	@Override
	protected void runImpl()
	{
		//_log.info.println(getType() + " :: " + unk + " :: " + unk2 + " :: " + unk3);
	}

	/**
	 * format: SSd
	 */
	@Override
	protected void readImpl()
	{
		unk = readString();
		unk2 = readString();
		unk3 = readD();
	}
}