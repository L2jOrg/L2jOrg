package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2Crest;
import org.l2j.gameserver.model.L2Crest.CrestType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;

/**
 * Client packet for setting/deleting clan crest.
 */
public final class RequestSetPledgeCrest extends IClientIncomingPacket {
    private int _length;
    private byte[] _data = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _length = readInt();
        if (_length > 256) {
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

        if ((_length < 0)) {
            activeChar.sendPacket(SystemMessageId.THE_SIZE_OF_THE_UPLOADED_SYMBOL_DOES_NOT_MEET_THE_STANDARD_REQUIREMENTS);
            return;
        }

        if (_length > 256) {
            activeChar.sendPacket(SystemMessageId.THE_SIZE_OF_THE_IMAGE_FILE_IS_INAPPROPRIATE_PLEASE_ADJUST_TO_16X12_PIXELS);
            return;
        }

        final L2Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        if (clan.getDissolvingExpiryTime() > System.currentTimeMillis()) {
            activeChar.sendPacket(SystemMessageId.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOU_CANNOT_REGISTER_OR_DELETE_A_CLAN_CREST);
            return;
        }

        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_REGISTER_CREST)) {
            activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        if (_length == 0) {
            if (clan.getCrestId() != 0) {
                clan.changeClanCrest(0);
                activeChar.sendPacket(SystemMessageId.THE_CLAN_MARK_HAS_BEEN_DELETED);
            }
        } else {
            if (clan.getLevel() < 3) {
                activeChar.sendPacket(SystemMessageId.A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLAN_S_SKILL_LEVEL_IS_3_OR_ABOVE);
                return;
            }

            final L2Crest crest = CrestTable.getInstance().createCrest(_data, CrestType.PLEDGE);
            if (crest != null) {
                clan.changeClanCrest(crest.getId());
                activeChar.sendPacket(SystemMessageId.THE_CREST_WAS_SUCCESSFULLY_REGISTERED);
            }
        }
    }

}
