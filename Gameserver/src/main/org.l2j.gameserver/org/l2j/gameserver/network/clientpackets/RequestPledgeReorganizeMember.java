package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Format: (ch) dSdS
 *
 * @author -Wooden-
 */
public final class RequestPledgeReorganizeMember extends ClientPacket {
    private int _isMemberSelected;
    private String _memberName;
    private int _newPledgeType;
    private String _selectedMember;

    @Override
    public void readImpl() {
        _isMemberSelected = readInt();
        _memberName = readString();
        _newPledgeType = readInt();
        _selectedMember = readString();
    }

    @Override
    public void runImpl() {
        if (_isMemberSelected == 0) {
            return;
        }

        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_MANAGE_RANKS)) {
            return;
        }

        final ClanMember member1 = clan.getClanMember(_memberName);
        if ((member1 == null) || (member1.getObjectId() == clan.getLeaderId())) {
            return;
        }

        final ClanMember member2 = clan.getClanMember(_selectedMember);
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
