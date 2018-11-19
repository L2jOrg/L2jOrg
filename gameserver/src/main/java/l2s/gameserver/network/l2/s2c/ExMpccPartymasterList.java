package l2s.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.Set;

/**
 * @author VISTALL
 * @date 6:22/12.06.2011
 */
public class ExMpccPartymasterList extends L2GameServerPacket
{
	private Set<String> _members = Collections.emptySet();

	public ExMpccPartymasterList(Set<String> s)
	{
		_members = s;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_members.size());
		for(String t : _members)
			writeS(t);
	}
}
