package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.item.support.Ensoul;

import java.nio.ByteBuffer;

public class ExEnSoulExtractionResult extends L2GameServerPacket
{
	public static final L2GameServerPacket FAIL = new ExEnSoulExtractionResult();

	private final boolean _success;
	private final Ensoul[] _normalEnsouls;
	private final Ensoul[] _specialEnsouls;

	private ExEnSoulExtractionResult()
	{
		_success = false;
		_normalEnsouls = null;
		_specialEnsouls = null;
	}

	public ExEnSoulExtractionResult(Ensoul[] normalEnsouls, Ensoul[] specialEnsouls)
	{
		_success = true;
		_normalEnsouls = normalEnsouls;
		_specialEnsouls = specialEnsouls;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte) (_success ? 0x01 : 0x00));
		if(_success)
		{
			buffer.put((byte)_normalEnsouls.length);
			for(Ensoul ensoul : _normalEnsouls)
				buffer.putInt(ensoul.getId());

			buffer.put((byte)_specialEnsouls.length);
			for(Ensoul ensoul : _specialEnsouls)
				buffer.putInt(ensoul.getId());
		}
	}
}