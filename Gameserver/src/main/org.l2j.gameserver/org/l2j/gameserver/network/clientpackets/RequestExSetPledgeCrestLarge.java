/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.CrestData;
import org.l2j.gameserver.data.database.data.CrestData.CrestType;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Format : chdb c (id) 0xD0 h (subid) 0x11 d data size b raw data (picture i think ;) )
 *
 * @author -Wooden-
 */
public final class RequestExSetPledgeCrestLarge extends ClientPacket {
    private int _length;
    private byte[] _data = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _length = readInt();
        if (_length > 2176) {
            throw new InvalidDataPacketException();
        }

        _data = new byte[_length];
        readBytes(_data);
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        if ((_length < 0) || (_length > 2176)) {
            client.sendPacket(SystemMessageId.THE_SIZE_OF_THE_UPLOADED_SYMBOL_DOES_NOT_MEET_THE_STANDARD_REQUIREMENTS);
            return;
        }

        if (clan.getDissolvingExpiryTime() > System.currentTimeMillis()) {
            client.sendPacket(SystemMessageId.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOU_CANNOT_REGISTER_OR_DELETE_A_CLAN_CREST);
            return;
        }

        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_REGISTER_CREST)) {
            client.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        if (_length == 0) {
            if (clan.getCrestLargeId() != 0) {
                clan.changeLargeCrest(0);
                client.sendPacket(SystemMessageId.THE_CLAN_MARK_HAS_BEEN_DELETED);
            }
        } else {
            if (clan.getLevel() < 3) {
                client.sendPacket(SystemMessageId.A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLAN_S_SKILL_LEVEL_IS_3_OR_ABOVE);
                return;
            }

            final CrestData crest = CrestTable.getInstance().createCrest(_data, CrestType.PLEDGE_LARGE);
            if (crest != null) {
                clan.changeLargeCrest(crest.getId());
                client.sendPacket(SystemMessageId.THE_CLAN_MARK_WAS_SUCCESSFULLY_REGISTERED_THE_SYMBOL_WILL_APPEAR_ON_THE_CLAN_FLAG_AND_THE_INSIGNIA_IS_ONLY_DISPLAYED_ON_ITEMS_PERTAINING_TO_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_CASTLE);
            }
        }
    }
}
