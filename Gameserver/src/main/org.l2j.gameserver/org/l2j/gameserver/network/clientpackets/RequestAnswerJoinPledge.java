package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;

/**
 * This class ...
 *
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestAnswerJoinPledge extends ClientPacket {
    private int _answer;

    @Override
    public void readImpl() {
        _answer = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Player requestor = activeChar.getRequest().getPartner();
        if (requestor == null) {
            return;
        }

        if (_answer == 0) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DIDN_T_RESPOND_TO_S1_S_INVITATION_JOINING_HAS_BEEN_CANCELLED);
            sm.addString(requestor.getName());
            activeChar.sendPacket(sm);
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED);
            sm.addString(activeChar.getName());
            requestor.sendPacket(sm);
        } else {
            if (!((requestor.getRequest().getRequestPacket() instanceof RequestJoinPledge) || (requestor.getRequest().getRequestPacket() instanceof RequestClanAskJoinByName))) {
                return; // hax
            }

            final int pledgeType;
            if (requestor.getRequest().getRequestPacket() instanceof RequestJoinPledge)
            {
                pledgeType = ((RequestJoinPledge) requestor.getRequest().getRequestPacket()).getPledgeType();
            }
            else
            {
                pledgeType = ((RequestClanAskJoinByName) requestor.getRequest().getRequestPacket()).getPledgeType();
            }
            final Clan clan = requestor.getClan();
            // we must double check this cause during response time conditions can be changed, i.e. another player could join clan
            if (clan.checkClanJoinCondition(requestor, activeChar, pledgeType)) {
                if (activeChar.getClan() != null) {
                    return;
                }

                activeChar.sendPacket(new JoinPledge(requestor.getClanId()));

                activeChar.setPledgeType(pledgeType);
                if (pledgeType == Clan.SUBUNIT_ACADEMY) {
                    activeChar.setPowerGrade(9); // academy
                    activeChar.setLvlJoinedAcademy(activeChar.getLevel());
                } else {
                    activeChar.setPowerGrade(5); // new member starts at 5, not confirmed
                }

                clan.addClanMember(activeChar);
                activeChar.setClanPrivileges(activeChar.getClan().getRankPrivs(activeChar.getPowerGrade()));
                activeChar.sendPacket(SystemMessageId.ENTERED_THE_CLAN);

                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_JOINED_THE_CLAN);
                sm.addString(activeChar.getName());
                clan.broadcastToOnlineMembers(sm);

                if (clan.getCastleId() > 0) {
                    CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(activeChar);
                }
                if (clan.getFortId() > 0) {
                    FortDataManager.getInstance().getFortByOwner(clan).giveResidentialSkills(activeChar);
                }
                activeChar.sendSkillList();

                clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(activeChar), activeChar);
                clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                clan.broadcastToOnlineMembers(new ExPledgeCount(clan));

                // this activates the clan tab on the new member
                PledgeShowMemberListAll.sendAllTo(activeChar);
                activeChar.setClanJoinExpiryTime(0);
                activeChar.broadcastUserInfo();
            }
        }

        activeChar.getRequest().onRequestResponse();
    }
}
