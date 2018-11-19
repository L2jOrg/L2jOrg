package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.templates.item.support.Ensoul;

/**
 * @author Bonux
 **/
public class ExEnsoulResult extends L2GameServerPacket
{
	public static final L2GameServerPacket FAIL = new ExEnsoulResult();

	private final boolean _success;
	private final Ensoul[] _normalEnsouls;
	private final Ensoul[] _specialEnsouls;

	private ExEnsoulResult()
	{
		_success = false;
		_normalEnsouls = null;
		_specialEnsouls = null;
	}

	public ExEnsoulResult(Ensoul[] normalEnsouls, Ensoul[] specialEnsouls)
	{
		_success = true;
		_normalEnsouls = normalEnsouls;
		_specialEnsouls = specialEnsouls;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(_success);
		if(_success)
		{
			writeC(_normalEnsouls.length);
			for(Ensoul ensoul : _normalEnsouls)
				writeD(ensoul.getId());

			writeC(_specialEnsouls.length);
			for(Ensoul ensoul : _specialEnsouls)
				writeD(ensoul.getId());
		}
	}
}