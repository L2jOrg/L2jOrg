package l2s.gameserver.network.l2.s2c;

public class StartAllianceWar extends L2GameServerPacket
{
	private String _allianceName;
	private String _char;

	public StartAllianceWar(String alliance, String charName)
	{
		_allianceName = alliance;
		_char = charName;
	}

	@Override
	protected final void writeImpl()
	{
		writeS(_char);
		writeS(_allianceName);
	}
}