package org.l2j.gameserver.mobius.gameserver.model;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExCloseMPCC;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExMPCCPartyInfoUpdate;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExOpenMPCC;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * This class serves as a container for command channels.
 *
 * @author chris_00
 */
public class L2CommandChannel extends AbstractPlayerGroup {
    private final List<L2Party> _parties = new CopyOnWriteArrayList<>();
    private L2PcInstance _commandLeader;
    private int _channelLvl;

    /**
     * Create a new command channel and add the leader's party to it.
     *
     * @param leader the leader of this command channel
     */
    public L2CommandChannel(L2PcInstance leader) {
        _commandLeader = leader;
        final L2Party party = leader.getParty();
        _parties.add(party);
        _channelLvl = party.getLevel();
        party.setCommandChannel(this);
        party.broadcastMessage(SystemMessageId.THE_COMMAND_CHANNEL_HAS_BEEN_FORMED);
        party.broadcastPacket(ExOpenMPCC.STATIC_PACKET);
    }

    /**
     * Add a party to this command channel.
     *
     * @param party the party to add
     */
    public void addParty(L2Party party) {
        if (party == null) {
            return;
        }
        // Update the CCinfo for existing players
        broadcastPacket(new ExMPCCPartyInfoUpdate(party, 1));

        _parties.add(party);
        if (party.getLevel() > _channelLvl) {
            _channelLvl = party.getLevel();
        }
        party.setCommandChannel(this);
        party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_JOINED_THE_COMMAND_CHANNEL));
        party.broadcastPacket(ExOpenMPCC.STATIC_PACKET);
    }

    /**
     * Remove a party from this command channel.
     *
     * @param party the party to remove
     */
    public void removeParty(L2Party party) {
        if (party == null) {
            return;
        }

        _parties.remove(party);
        _channelLvl = 0;
        for (L2Party pty : _parties) {
            if (pty.getLevel() > _channelLvl) {
                _channelLvl = pty.getLevel();
            }
        }
        party.setCommandChannel(null);
        party.broadcastPacket(ExCloseMPCC.STATIC_PACKET);
        if (_parties.size() < 2) {
            broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED));
            disbandChannel();
        } else {
            // Update the CCinfo for existing players
            broadcastPacket(new ExMPCCPartyInfoUpdate(party, 0));
        }
    }

    /**
     * Disband this command channel.
     */
    public void disbandChannel() {
        if (_parties != null) {
            for (L2Party party : _parties) {
                if (party != null) {
                    removeParty(party);
                }
            }
            _parties.clear();
        }
    }

    /**
     * @return the total count of all members of this command channel
     */
    @Override
    public int getMemberCount() {
        int count = 0;
        for (L2Party party : _parties) {
            if (party != null) {
                count += party.getMemberCount();
            }
        }
        return count;
    }

    /**
     * @return a list of all parties in this command channel
     */
    public List<L2Party> getPartys() {
        return _parties;
    }

    /**
     * @return a list of all members in this command channel
     */
    @Override
    public List<L2PcInstance> getMembers() {
        final List<L2PcInstance> members = new LinkedList<>();
        for (L2Party party : _parties) {
            members.addAll(party.getMembers());
        }
        return members;
    }

    /**
     * @return the level of this command channel (equals the level of the highest-leveled character in this command channel)
     */
    @Override
    public int getLevel() {
        return _channelLvl;
    }

    /**
     * @param obj
     * @return true if proper condition for RaidWar
     */
    public boolean meetRaidWarCondition(L2Object obj) {
        if (!(obj.isCharacter() && ((L2Character) obj).isRaid())) {
            return false;
        }
        return (getMemberCount() >= Config.LOOT_RAIDS_PRIVILEGE_CC_SIZE);
    }

    /**
     * @return the leader of this command channel
     */
    @Override
    public L2PcInstance getLeader() {
        return _commandLeader;
    }

    @Override
    public void setLeader(L2PcInstance leader) {
        _commandLeader = leader;
        if (leader.getLevel() > _channelLvl) {
            _channelLvl = leader.getLevel();
        }
    }

    /**
     * Check if a given player is in this command channel.
     *
     * @param player the player to check
     * @return {@code true} if he does, {@code false} otherwise
     */
    @Override
    public boolean containsPlayer(L2PcInstance player) {
        if ((_parties != null) && !_parties.isEmpty()) {
            for (L2Party party : _parties) {
                if (party.containsPlayer(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Iterates over all command channel members without the need to allocate a new list
     *
     * @see AbstractPlayerGroup#forEachMember(Function)
     */
    @Override
    public boolean forEachMember(Function<L2PcInstance, Boolean> function) {
        if ((_parties != null) && !_parties.isEmpty()) {
            for (L2Party party : _parties) {
                if (!party.forEachMember(function)) {
                    return false;
                }
            }
        }
        return true;
    }
}
