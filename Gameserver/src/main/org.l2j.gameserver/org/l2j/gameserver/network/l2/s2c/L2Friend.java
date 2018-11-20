package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

public class L2Friend extends L2GameServerPacket
{
	private boolean _add, _online;
	private String _name;
	private int _object_id;

	public L2Friend(Player player, boolean add)
	{
		_add = add;
		_name = player.getName();
		_object_id = player.getObjectId();
		_online = true;
	}

	public L2Friend(String name, boolean add, boolean online, int object_id)
	{
		_name = name;
		_add = add;
		_object_id = object_id;
		_online = online;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_add ? 1 : 3); // 1 - добавить друга в спикок, 3 удалить друга со списка
		writeInt(0); //и снова тут идет ID персонажа в списке оффа, не object id
		writeString(_name);
		writeInt(_online ? 1 : 0); // онлайн или оффлайн
		writeInt(_object_id); //object_id if online
	}
}