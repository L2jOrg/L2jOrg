package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;

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
		writeD(_monsterObjId);
		writeD(_playerObjId);
		writeD(0x00); //unk
	}
}