/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author -Wooden-
 */
public class PledgeReceiveMemberInfo extends ServerPacket {
    private final ClanMember _member;

    public PledgeReceiveMemberInfo(ClanMember member) {
        _member = member;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_VIEW_PLEDGE_MEMBER_INFO, buffer );

        buffer.writeInt(_member.getPledgeType());
        buffer.writeString(_member.getName());
        buffer.writeString(_member.getTitle()); // title
        buffer.writeInt(_member.getPowerGrade()); // power

        // clan or subpledge name
        if (_member.getPledgeType() != 0) {
            buffer.writeString((_member.getClan().getSubPledge(_member.getPledgeType())).getName());
        } else {
            buffer.writeString(_member.getClan().getName());
        }

        buffer.writeString(_member.getApprenticeOrSponsorName()); // name of this member's apprentice/sponsor
    }

}
