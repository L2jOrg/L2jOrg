package l2s.gameserver.network.l2.s2c;

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
	protected void writeImpl()
	{
		writeD(_npcObjId);
		writeS(_html);
		writeD(_questId);
	}
}
