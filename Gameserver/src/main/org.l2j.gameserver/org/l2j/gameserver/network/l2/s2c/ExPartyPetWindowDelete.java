package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExPartyPetWindowDelete extends L2GameServerPacket
{
	private int _summonObjectId;
	private int _ownerObjectId;
	private int _type;
	private String _summonName;

	public ExPartyPetWindowDelete(Servitor summon)
	{
		_summonObjectId = summon.getObjectId();
		_summonName = summon.getName();
		_type = summon.getServitorType();
		_ownerObjectId = summon.getPlayer().getObjectId();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_summonObjectId);
		buffer.putInt(_type);
		buffer.putInt(_ownerObjectId);
		writeString(_summonName, buffer);
	}
}