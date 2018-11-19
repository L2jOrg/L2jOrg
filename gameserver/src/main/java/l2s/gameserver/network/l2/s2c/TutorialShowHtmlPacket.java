package l2s.gameserver.network.l2.s2c;

public class TutorialShowHtmlPacket extends L2GameServerPacket
{
	public static int NORMAL_WINDOW = 0x01;
	public static int LARGE_WINDOW = 0x02;

	private int _windowType;
	private String _html;

	public TutorialShowHtmlPacket(int windowType, String html)
	{
		_windowType = windowType;
		_html = html;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_windowType);
		writeS(_html);
	}
}