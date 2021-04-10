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
package org.l2j.gameserver.network.serverpackets.pledge;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.data.SubPledgeData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.database.data.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.ServerSettings;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author JoeAlisson
 */
public class PledgeShowMemberListAll extends PledgeAbstractPacket {
    private final SubPledgeData pledge;
    private final boolean isSubPledge;

    private PledgeShowMemberListAll(Clan clan, SubPledgeData pledge, boolean isSubPledge) {
        super(clan);
        this.pledge = pledge;
        this.isSubPledge = isSubPledge;
    }

    public static void sendAllTo(Player player) {
        final Clan clan = player.getClan();
        if (clan != null) {
            for (var subPledge : clan.getAllSubPledges()) {
                player.sendPacket(new PledgeShowMemberListAll(clan, subPledge, false));
            }
            player.sendPacket(new PledgeShowMemberListAll(clan, null, true));
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_ALL, buffer );

        buffer.writeInt(!isSubPledge);
        buffer.writeInt(clan.getId());
        buffer.writeInt(getSettings(ServerSettings.class).serverId());

        var pledgeId = isNull(pledge) ? 0x00 : pledge.getId();
        var leaderName = isNull(pledge) ? clan.getLeaderName() : PlayerNameTable.getInstance().getNameById(pledge.getLeaderId());

        buffer.writeInt(pledgeId);
        buffer.writeString(isNull(pledge) ? clan.getName() : pledge.getName());
        buffer.writeString(leaderName);

        writeClanInfo(pledgeId, buffer);

        clan.forEachMember(m1 -> writeMemberInfo(m1, buffer), m -> m.getPledgeType() == pledgeId);
    }

    protected void writeMemberInfo(ClanMember m, WritableBuffer buffer) {
        buffer.writeString(m.getName());
        buffer.writeInt(m.getLevel());
        buffer.writeInt(m.getClassId());

        var player = m.getPlayerInstance();
        if (nonNull(player)) {
            buffer.writeInt(player.getAppearance().isFemale()); // no visible effect
            buffer.writeInt(player.getRace().ordinal()); // packet.putInt(1);
        } else {
            buffer.writeInt(0x01); // no visible effect
            buffer.writeInt(0x01); // packet.putInt(1);
        }
        buffer.writeInt(m.isOnline() ? m.getObjectId() : 0); // objectId = online 0 = offline
        buffer.writeInt(m.getSponsor() != 0);
        buffer.writeByte(m.getOnlineStatus());
    }

}
