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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author JoeAlisson
 */
public class ExPledgeClassicRaidInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_PLEDGE_CLASSIC_RAID_INFO);
        writeInt( zeroIfNullOrElse(client.getPlayer().getClan(), Clan::getArenaProgress));
        writeInt(0x05);

        ClanRewardManager.getInstance().forEachReward(ClanRewardType.ARENA, reward -> {
            final var skill = reward.getSkillReward();
            writeInt(skill.getSkillId());
            writeInt(skill.getLevel());
        });
    }
}
