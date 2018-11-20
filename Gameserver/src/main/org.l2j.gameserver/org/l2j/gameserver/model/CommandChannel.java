package org.l2j.gameserver.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.l2j.commons.collections.JoinedIterator;
import org.l2j.gameserver.model.base.PledgeRank;
import org.l2j.gameserver.model.instances.NpcFriendInstance;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.IBroadcastPacket;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExCloseMPCCPacket;
import org.l2j.gameserver.network.l2.s2c.ExOpenMPCCPacket;
import org.l2j.gameserver.network.l2.s2c.ExMPCCPartyInfoUpdate;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.network.l2.s2c.SystemMessage;

public class CommandChannel implements PlayerGroup
{
	public static final int STRATEGY_GUIDE_ID = 8871;
	public static final int CLAN_IMPERIUM_ID = 391;

	private final List<Party> _commandChannelParties = new CopyOnWriteArrayList<Party>();
	private Player _commandChannelLeader;
	private int _commandChannelLvl;

	private MatchingRoom _matchingRoom;

	/**
	 * Creates a New Command Channel and Add the Leaders party to the CC
	 * @param leader
	 */
	public CommandChannel(Player leader)
	{
		_commandChannelLeader = leader;
		_commandChannelParties.add(leader.getParty());
		_commandChannelLvl = leader.getParty().getLevel();
		leader.getParty().setCommandChannel(this);
		broadCast(ExOpenMPCCPacket.STATIC);
	}

	/**
	 * Adds a Party to the Command Channel
	 * @param party
	 */
	public void addParty(Party party)
	{
		broadCast(new ExMPCCPartyInfoUpdate(party, 1));
		_commandChannelParties.add(party);
		refreshLevel();
		party.setCommandChannel(this);

		for(Player $member : party)
		{
			$member.sendPacket(ExOpenMPCCPacket.STATIC);
			if(_matchingRoom != null)
				_matchingRoom.broadcastPlayerUpdate($member);
		}
	}

	/**
	 * Removes a Party from the Command Channel
	 * @param party
	 */
	public void removeParty(Party party)
	{
		_commandChannelParties.remove(party);
		refreshLevel();
		party.setCommandChannel(null);
		party.broadCast(ExCloseMPCCPacket.STATIC);

		if(_commandChannelParties.size() < 2)
			disbandChannel();
		else
		{
			for(Player $member : party)
			{
				$member.sendPacket(new ExMPCCPartyInfoUpdate(party, 0));
				if(_matchingRoom != null)
					_matchingRoom.broadcastPlayerUpdate($member);
			}
		}
	}

	/**
	 * Распускает Command Channel
	 */
	public void disbandChannel()
	{
		broadCast(SystemMsg.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED);
		for(Party party : _commandChannelParties)
		{
			party.setCommandChannel(null);
			party.broadCast(ExCloseMPCCPacket.STATIC);
		}

		if(_matchingRoom != null)
			_matchingRoom.disband();
		_commandChannelParties.clear();
		_commandChannelLeader = null;
	}

	/**
	 * @return overall count members of the Command Channel
	 */
	public int getMemberCount()
	{
		int count = 0;
		for(Party party : _commandChannelParties)
			count += party.getMemberCount();
		return count;
	}

	/**
	 * Broadcast packet to every channel member
	 * @param gsp
	 */
	@Override
	public void broadCast(IBroadcastPacket... gsp)
	{
		for(Party party : _commandChannelParties)
			party.broadCast(gsp);
	}

	/**
	 * Broadcast packet to every party leader of command channel
	 */
	public void broadcastToChannelPartyLeaders(L2GameServerPacket gsp)
	{
		for(Party party : _commandChannelParties)
		{
			Player leader = party.getPartyLeader();
			if(leader != null)
				leader.sendPacket(gsp);
		}
	}

	/**
	 * @return list of Parties in Command Channel
	 */
	public List<Party> getParties()
	{
		return _commandChannelParties;
	}

	//@Deprecated
	public List<Player> getMembers()
	{
		List<Player> members = new ArrayList<Player>(_commandChannelParties.size());
		for(Party party : getParties())
			members.addAll(party.getPartyMembers());
		return members;
	}

	@Override
	public Player getGroupLeader()
	{
		return getChannelLeader();
	}

	@Override
	public Iterator<Player> iterator()
	{
		List<Iterator<Player>> iterators = new ArrayList<Iterator<Player>>(_commandChannelParties.size());
		for(Party p : getParties())
			iterators.add(p.getPartyMembers().iterator());
		return new JoinedIterator<Player>(iterators);
	}

	/**
	 * @return Level of CC
	 */
	public int getLevel()
	{
		return _commandChannelLvl;
	}

	/**
	 * @param newLeader the leader of the Command Channel
	 */
	public void setChannelLeader(Player newLeader)
	{
		_commandChannelLeader = newLeader;
		broadCast(new SystemMessage(SystemMessage.COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_S1).addString(newLeader.getName()));
	}

	/**
	 * @return the leader of the Command Channel
	 */
	public Player getChannelLeader()
	{
		return _commandChannelLeader;
	}

	public boolean isLeaderCommandChannel(Player player)
	{
		return _commandChannelLeader == player;
	}

	/**
	 * Queen Ant, Core, Orfen, Zaken: MemberCount > 36<br>
	 * Baium: MemberCount > 56<br>
	 * Antharas: MemberCount > 225<br>
	 * Valakas: MemberCount > 99<br>
	 * normal RaidBoss: MemberCount > 18
	 *
	 * @param npc
	 * @return true if proper condition for RaidWar
	 */
	public boolean meetRaidWarCondition(NpcFriendInstance npc)
	{
		if(!npc.isRaid())
			return false;
		int npcId = npc.getNpcId();
		switch(npcId)
		{
			case 29001: // Queen Ant
			case 29006: // Core
			case 29014: // Orfen
			case 29022: // Zaken
				return getMemberCount() > 36;
			case 29020: // Baium
				return getMemberCount() > 56;
			case 29019: // Antharas
				return getMemberCount() > 225;
			case 29028: // Valakas
				return getMemberCount() > 99;
			default: // normal Raidboss
				return getMemberCount() > 18;
		}
	}

	private void refreshLevel()
	{
		_commandChannelLvl = 0;
		for(Party pty : _commandChannelParties)
			if(pty.getLevel() > _commandChannelLvl)
				_commandChannelLvl = pty.getLevel();
	}

	/**
	 * Проверяет возможность создания командного канала
	 */
	public static boolean checkAuthority(Player creator)
	{
		Party party = creator.getParty();
		Clan clan = creator.getClan();
		// CC могут создавать только лидеры партий, состоящие в клане ранком не ниже барона
		if(party == null || !party.isLeader(creator) || clan == null || creator.getPledgeRank().ordinal() < PledgeRank.WISEMAN.ordinal())
		{
			creator.sendPacket(SystemMsg.COMMAND_CHANNELS_CAN_ONLY_BE_FORMED_BY_A_PARTY_LEADER_WHO_IS_ALSO_THE_LEADER_OF_A_LEVEL_5_CLAN);
			return false;
		}

		// CC можно создать, если есть клановый скилл Clan Imperium
		boolean haveSkill = creator.getSkillLevel(CLAN_IMPERIUM_ID) > 0;

		// Ищем Strategy Guide в инвентаре
		boolean haveItem = creator.getInventory().getItemByItemId(STRATEGY_GUIDE_ID) != null;

		if(!haveSkill && !haveItem)
		{
			creator.sendPacket(SystemMsg.YOU_CAN_NO_LONGER_SET_UP_A_COMMAND_CHANNEL);
			return false;
		}

		return true;
	}

	public MatchingRoom getMatchingRoom()
	{
		return _matchingRoom;
	}

	public void setMatchingRoom(MatchingRoom matchingRoom)
	{
		_matchingRoom = matchingRoom;
	}
}