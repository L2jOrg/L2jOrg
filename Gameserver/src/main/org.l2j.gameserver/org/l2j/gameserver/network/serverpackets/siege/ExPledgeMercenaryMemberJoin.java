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
package org.l2j.gameserver.network.serverpackets.siege;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExPledgeMercenaryMemberJoin extends ServerPacket {

    private final boolean result;
    private final int clanId;
    private final int type;

    private ExPledgeMercenaryMemberJoin(boolean result, int clanId, int type) {
        this.result = result;
        this.clanId = clanId;
        this.type = type;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) throws Exception {
        writeId(ServerExPacketId.EX_PLEDGE_MERCENARY_MEMBER_JOIN, buffer);
        buffer.writeInt(result);
        buffer.writeInt(type);
        buffer.writeInt(client.getPlayer().getId());
        buffer.writeInt(clanId);
    }

    public static ExPledgeMercenaryMemberJoin joined(int clanId) {
        return new ExPledgeMercenaryMemberJoin(true, clanId, 1);
    }

    public static ExPledgeMercenaryMemberJoin joinFailed(int clanId) {
        return new ExPledgeMercenaryMemberJoin(false, clanId, 1);
    }

    public static ExPledgeMercenaryMemberJoin left(int clanId) {
        return new ExPledgeMercenaryMemberJoin(true, clanId, 0);
    }

    public static ExPledgeMercenaryMemberJoin leaveFailed(int clanId) {
        return new ExPledgeMercenaryMemberJoin(false, clanId, 0);
    }
}
