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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class ExPledgeBonusOpen extends ServerPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExPledgeBonusOpen.class);

    private final Player player;

    public ExPledgeBonusOpen(Player player) {
        this.player = player;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) throws InvalidDataPacketException {
        final Clan clan = player.getClan();
        if (clan == null) {
            LOGGER.warn("Player: {} attempting to write to a null clan!", player);
            throw new InvalidDataPacketException();
        }

        final ClanRewardBonus highestMembersOnlineBonus = ClanRewardManager.getInstance().getHighestReward(ClanRewardType.MEMBERS_ONLINE);
        final ClanRewardBonus highestHuntingBonus = ClanRewardManager.getInstance().getHighestReward(ClanRewardType.HUNTING_MONSTERS);
        final ClanRewardBonus membersOnlineBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(clan);
        final ClanRewardBonus huntingBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(clan);

        if (highestMembersOnlineBonus == null) {
            LOGGER.warn("Couldn't find highest available clan members online bonus!!");
            throw new InvalidDataPacketException();
        } else if (highestHuntingBonus == null) {
            LOGGER.warn("Couldn't find highest available clan hunting bonus!!");
            throw new InvalidDataPacketException();
        } else if (highestMembersOnlineBonus.skill() == null) {
            LOGGER.warn("Couldn't find skill reward for highest available members online bonus!!");
            throw new InvalidDataPacketException();
        } else if (highestHuntingBonus.item() == null) {
            LOGGER.warn("Couldn't find item reward for highest available hunting bonus!!");
            throw new InvalidDataPacketException();
        }

        // General OP Code
        writeId(ServerExPacketId.EX_PLEDGE_BONUS_UI_OPEN, buffer );

        // Members online bonus
        buffer.writeInt(highestMembersOnlineBonus.requiredAmount());
        buffer.writeInt(clan.getMaxOnlineMembers());
        buffer.writeByte( 0x00); // progress ?
        buffer.writeInt(membersOnlineBonus != null ? highestMembersOnlineBonus.skill().getId() : 0x00);
        buffer.writeByte((membersOnlineBonus != null ? membersOnlineBonus.level() : 0x00));
        buffer.writeByte((membersOnlineBonus != null ? 0x01 : 0x00));

        // Hunting bonus
        buffer.writeInt(highestHuntingBonus.requiredAmount());
        buffer.writeInt(clan.getHuntingPoints());
        buffer.writeByte(0x00); // progress
        buffer.writeInt(huntingBonus != null ? highestHuntingBonus.item().getId() : 0x00);
        buffer.writeByte((huntingBonus != null ? huntingBonus.level() : 0x00));
        buffer.writeByte((huntingBonus != null ? 0x01 : 0x00));
    }

}
