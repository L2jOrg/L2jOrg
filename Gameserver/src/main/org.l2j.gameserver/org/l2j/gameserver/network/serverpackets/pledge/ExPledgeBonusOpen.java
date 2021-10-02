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
import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class ExPledgeBonusOpen extends ServerPacket {

    private final ClanRewardBonus highestMembersOnlineBonus;
    private final ClanRewardBonus highestHuntingBonus;
    private final ClanRewardBonus membersOnlineBonus;
    private final ClanRewardBonus huntingBonus;
    private final Clan clan;
    private final boolean canClainHuntingBonus;
    private final boolean canClainMembersOnline;

    public ExPledgeBonusOpen(Player player) {
        clan = player.getClan();
        var rewardManager = ClanRewardManager.getInstance();
        highestMembersOnlineBonus = rewardManager.getHighestReward(ClanRewardType.MEMBERS_ONLINE);
        highestHuntingBonus = rewardManager.getHighestReward(ClanRewardType.HUNTING_MONSTERS);
        membersOnlineBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(clan);
        huntingBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(clan);

        canClainMembersOnline = clan.canClaimBonusReward(player, ClanRewardType.MEMBERS_ONLINE);
        canClainHuntingBonus = clan.canClaimBonusReward(player, ClanRewardType.HUNTING_MONSTERS);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) throws InvalidDataPacketException {
        writeId(ServerExPacketId.EX_PLEDGE_BONUS_UI_OPEN, buffer );

        buffer.writeInt(highestMembersOnlineBonus.requiredAmount());
        buffer.writeInt(clan.getMaxOnlineMembers());
        buffer.writeByte( 0x00);
        buffer.writeInt(membersOnlineBonus != null ? highestMembersOnlineBonus.skill().getId() : 0x00);
        buffer.writeByte(membersOnlineBonus != null ? membersOnlineBonus.level() : 0x00);
        buffer.writeByte(canClainMembersOnline);

        buffer.writeInt(highestHuntingBonus.requiredAmount());
        buffer.writeInt(clan.getHuntingPoints());
        buffer.writeByte(0x01);
        buffer.writeInt(huntingBonus != null ? highestHuntingBonus.item().getId() : 0x00);
        buffer.writeByte(huntingBonus != null ? huntingBonus.level() : 0x00);
        buffer.writeByte(canClainHuntingBonus);
    }

}
