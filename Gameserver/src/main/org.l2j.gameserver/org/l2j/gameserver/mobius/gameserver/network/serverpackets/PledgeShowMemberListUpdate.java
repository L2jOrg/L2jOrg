package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author -Wooden-
 */
public final class PledgeShowMemberListUpdate implements IClientOutgoingPacket
{
    private final int _pledgeType;
    private int _hasSponsor;
    private final String _name;
    private final int _level;
    private final int _classId;
    private final int _objectId;
    private final int _onlineStatus;
    private final int _race;
    private final int _sex;

    public PledgeShowMemberListUpdate(L2PcInstance player)
    {
        this(player.getClan().getClanMember(player.getObjectId()));
    }

    public PledgeShowMemberListUpdate(L2ClanMember member)
    {
        _name = member.getName();
        _level = member.getLevel();
        _classId = member.getClassId();
        _objectId = member.getObjectId();
        _pledgeType = member.getPledgeType();
        _race = member.getRaceOrdinal();
        _sex = member.getSex() ? 1 : 0;
        _onlineStatus = member.getOnlineStatus();
        if (_pledgeType == L2Clan.SUBUNIT_ACADEMY)
        {
            _hasSponsor = member.getSponsor() != 0 ? 1 : 0;
        }
        else
        {
            _hasSponsor = 0;
        }
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.PLEDGE_SHOW_MEMBER_LIST_UPDATE.writeId(packet);

        packet.writeS(_name);
        packet.writeD(_level);
        packet.writeD(_classId);
        packet.writeD(_sex);
        packet.writeD(_race);
        if (_onlineStatus > 0)
        {
            packet.writeD(_objectId);
            packet.writeD(_pledgeType);
        }
        else
        {
            // when going offline send as 0
            packet.writeD(0);
            packet.writeD(0);
        }
        packet.writeD(_hasSponsor);
        packet.writeC(_onlineStatus);
        return true;
    }
}
