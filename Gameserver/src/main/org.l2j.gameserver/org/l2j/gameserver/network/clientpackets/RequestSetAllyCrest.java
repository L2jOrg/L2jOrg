package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2Crest;
import org.l2j.gameserver.model.L2Crest.CrestType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Client packet for setting ally crest.
 */
public final class RequestSetAllyCrest extends IClientIncomingPacket {
    private int _length;
    private byte[] _data = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _length = readInt();
        if (_length > 192) {
            throw new InvalidDataPacketException();
        }

        _data = new byte[_length];
        readBytes(_data);
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_length < 0) {
            activeChar.sendMessage("File transfer error.");
            return;
        }

        if (_length > 192) {
            activeChar.sendPacket(SystemMessageId.PLEASE_ADJUST_THE_IMAGE_SIZE_TO_8X12);
            return;
        }

        if (activeChar.getAllyId() == 0) {
            activeChar.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }

        final L2Clan leaderClan = ClanTable.getInstance().getClan(activeChar.getAllyId());

        if ((activeChar.getClanId() != leaderClan.getId()) || !activeChar.isClanLeader()) {
            activeChar.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }

        if (_length == 0) {
            if (leaderClan.getAllyCrestId() != 0) {
                leaderClan.changeAllyCrest(0, false);
            }
        } else {
            final L2Crest crest = CrestTable.getInstance().createCrest(_data, CrestType.ALLY);
            if (crest != null) {
                leaderClan.changeAllyCrest(crest.getId(), false);
                activeChar.sendPacket(SystemMessageId.THE_CREST_WAS_SUCCESSFULLY_REGISTERED);
            }
        }
    }
}
