package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.clan.entry.PledgeWaitingInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestPledgeDraftListApply extends IClientIncomingPacket {
    private int _applyType;
    private int _karma;

    @Override
    public void readImpl() {
        _applyType = readInt();
        _karma = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();

        if ((activeChar == null) || (activeChar.getClan() != null)) {
            return;
        }

        if (activeChar.getClan() != null) {
            client.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
            return;
        }

        switch (_applyType) {
            case 0: // remove
            {
                if (ClanEntryManager.getInstance().removeFromWaitingList(activeChar.getObjectId())) {
                    client.sendPacket(SystemMessageId.ENTRY_APPLICATION_CANCELLED_YOU_MAY_APPLY_TO_A_NEW_CLAN_AFTER_5_MINUTES);
                }
                break;
            }
            case 1: // add
            {
                final PledgeWaitingInfo pledgeDraftList = new PledgeWaitingInfo(activeChar.getObjectId(), activeChar.getLevel(), _karma, activeChar.getClassId().getId(), activeChar.getName());

                if (ClanEntryManager.getInstance().addToWaitingList(activeChar.getObjectId(), pledgeDraftList)) {
                    client.sendPacket(SystemMessageId.YOU_HAVE_JOINED_THE_WAITING_LIST_CHARACTERS_ARE_AUTOMATICALLY_DELETED_FROM_THE_LIST_AFTER_30_DAYS_IF_EXIT_WAITING_LIST_IS_USED_YOU_CANNOT_JOIN_THE_WAITING_LIST_FOR_5_MINUTES);
                } else {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
                    sm.addLong(ClanEntryManager.getInstance().getPlayerLockTime(activeChar.getObjectId()));
                    client.sendPacket(sm);
                }
                break;
            }
        }
    }
}
