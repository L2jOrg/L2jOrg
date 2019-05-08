package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2ClanMember;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public final class PledgeShowMemberListUpdate extends IClientOutgoingPacket {
    private final int _pledgeType;
    private final String _name;
    private final int _level;
    private final int _classId;
    private final int _objectId;
    private final int _onlineStatus;
    private final int _race;
    private final int _sex;
    private int _hasSponsor;

    public PledgeShowMemberListUpdate(L2PcInstance player) {
        this(player.getClan().getClanMember(player.getObjectId()));
    }

    public PledgeShowMemberListUpdate(L2ClanMember member) {
        _name = member.getName();
        _level = member.getLevel();
        _classId = member.getClassId();
        _objectId = member.getObjectId();
        _pledgeType = member.getPledgeType();
        _race = member.getRaceOrdinal();
        _sex = member.getSex() ? 1 : 0;
        _onlineStatus = member.getOnlineStatus();
        if (_pledgeType == L2Clan.SUBUNIT_ACADEMY) {
            _hasSponsor = member.getSponsor() != 0 ? 1 : 0;
        } else {
            _hasSponsor = 0;
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_SHOW_MEMBER_LIST_UPDATE.writeId(packet);

        writeString(_name, packet);
        packet.putInt(_level);
        packet.putInt(_classId);
        packet.putInt(_sex);
        packet.putInt(_race);
        if (_onlineStatus > 0) {
            packet.putInt(_objectId);
            packet.putInt(_pledgeType);
        } else {
            // when going offline send as 0
            packet.putInt(0);
            packet.putInt(0);
        }
        packet.putInt(_hasSponsor);
        packet.put((byte) _onlineStatus);
    }

    @Override
    protected int size(L2GameClient client) {
        return 36 + _name.length() * 2;
    }
}
