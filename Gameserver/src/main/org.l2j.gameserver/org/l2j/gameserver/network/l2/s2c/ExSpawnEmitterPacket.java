package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;

/**
 * Этот пакет отвечает за анимацию высасывания душ из трупов
 * @author SYS
 */
public class ExSpawnEmitterPacket extends L2GameServerPacket
{
	private int _monsterObjId;
	private int _playerObjId;

	public ExSpawnEmitterPacket(NpcInstance monster, Player player)
	{
		_playerObjId = player.getObjectId();
		_monsterObjId = monster.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		//ddd
		writeInt(_monsterObjId);
		writeInt(_playerObjId);
		writeInt(0x00); //unk
	}
}