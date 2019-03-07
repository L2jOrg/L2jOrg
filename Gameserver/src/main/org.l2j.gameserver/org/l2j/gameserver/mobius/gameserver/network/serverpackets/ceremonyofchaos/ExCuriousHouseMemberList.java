package org.l2j.gameserver.mobius.gameserver.network.serverpackets.ceremonyofchaos;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.util.Collection;

/**
 * @author UnAfraid
 */
public class ExCuriousHouseMemberList implements IClientOutgoingPacket
{
	private final int _id;
	private final int _maxPlayers;
	private final Collection<CeremonyOfChaosMember> _players;
	
	public ExCuriousHouseMemberList(int id, int maxPlayers, Collection<CeremonyOfChaosMember> players)
	{
		_id = id;
		_maxPlayers = maxPlayers;
		_players = players;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_CURIOUS_HOUSE_MEMBER_LIST.writeId(packet);
		
		packet.writeD(_id);
		packet.writeD(_maxPlayers);
		packet.writeD(_players.size());
		for (CeremonyOfChaosMember cocPlayer : _players)
		{
			final L2PcInstance player = cocPlayer.getPlayer();
			packet.writeD(cocPlayer.getObjectId());
			packet.writeD(cocPlayer.getPosition());
			if (player != null)
			{
				packet.writeD(player.getMaxHp());
				packet.writeD(player.getMaxCp());
				packet.writeD((int) player.getCurrentHp());
				packet.writeD((int) player.getCurrentCp());
			}
			else
			{
				packet.writeD(0x00);
				packet.writeD(0x00);
				packet.writeD(0x00);
				packet.writeD(0x00);
			}
		}
		return true;
	}
	
}
