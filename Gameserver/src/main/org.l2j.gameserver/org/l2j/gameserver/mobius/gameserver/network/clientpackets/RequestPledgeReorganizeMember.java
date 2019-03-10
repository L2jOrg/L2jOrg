package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * Format: (ch) dSdS
 *
 * @author -Wooden-
 */
public final class RequestPledgeReorganizeMember extends IClientIncomingPacket {
    private int _isMemberSelected;
    private String _memberName;
    private int _newPledgeType;
    private String _selectedMember;

    @Override
    public void readImpl(ByteBuffer packet) {
        _isMemberSelected = packet.getInt();
        _memberName = readString(packet);
        _newPledgeType = packet.getInt();
        _selectedMember = readString(packet);
    }

    @Override
    public void runImpl() {
        if (_isMemberSelected == 0) {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_MANAGE_RANKS)) {
            return;
        }

        final L2ClanMember member1 = clan.getClanMember(_memberName);
        if ((member1 == null) || (member1.getObjectId() == clan.getLeaderId())) {
            return;
        }

        final L2ClanMember member2 = clan.getClanMember(_selectedMember);
        if ((member2 == null) || (member2.getObjectId() == clan.getLeaderId())) {
            return;
        }

        final int oldPledgeType = member1.getPledgeType();
        if (oldPledgeType == _newPledgeType) {
            return;
        }

        member1.setPledgeType(_newPledgeType);
        member2.setPledgeType(oldPledgeType);
        clan.broadcastClanStatus();
    }

}
