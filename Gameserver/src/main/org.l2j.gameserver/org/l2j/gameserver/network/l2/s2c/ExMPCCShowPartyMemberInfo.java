package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

/**
 * Format: ch d[Sdd]
 * @author SYS
 */
public class ExMPCCShowPartyMemberInfo extends L2GameServerPacket
{
	private List<PartyMemberInfo> members;

	public ExMPCCShowPartyMemberInfo(Party party)
	{
		members = new ArrayList<PartyMemberInfo>();
		for(Player _member : party.getPartyMembers())
			members.add(new PartyMemberInfo(_member.getName(), _member.getObjectId(), _member.getClassId().getId()));
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(members.size()); // Количество членов в пати

		for(PartyMemberInfo member : members)
		{
			writeString(member.name, buffer); // Имя члена пати
			buffer.putInt(member.object_id); // object Id члена пати
			buffer.putInt(member.class_id); // id класса члена пати
		}

		members.clear();
	}

	static class PartyMemberInfo
	{
		public String name;
		public int object_id, class_id;

		public PartyMemberInfo(String _name, int _object_id, int _class_id)
		{
			name = _name;
			object_id = _object_id;
			class_id = _class_id;
		}
	}
}