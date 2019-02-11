package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 16:25/24.04.2011
 */
public class ExNpcQuestHtmlMessage extends L2GameServerPacket
{
	private int _npcObjId;
	private CharSequence _html;
	private int _questId;

	public ExNpcQuestHtmlMessage(int npcObjId, CharSequence html, int questId)
	{
		_npcObjId = npcObjId;
		_html = html;
		_questId = questId;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_npcObjId);
		writeString(_html, buffer);
		buffer.putInt(_questId);
	}
}
