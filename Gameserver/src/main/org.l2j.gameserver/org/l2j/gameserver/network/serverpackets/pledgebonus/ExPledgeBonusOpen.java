package org.l2j.gameserver.network.serverpackets.pledgebonus;

import org.l2j.gameserver.data.xml.impl.ClanRewardData;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExPledgeBonusOpen extends IClientOutgoingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExPledgeBonusOpen.class);

    private final L2PcInstance _player;

    public ExPledgeBonusOpen(L2PcInstance player) {
        _player = player;
    }

    @Override
    public void writeImpl(L2GameClient client) throws InvalidDataPacketException {
        final L2Clan clan = _player.getClan();
        if (clan == null) {
            LOGGER.warn("Player: " + _player + " attempting to write to a null clan!");
            throw new InvalidDataPacketException();
        }

        final ClanRewardBonus highestMembersOnlineBonus = ClanRewardData.getInstance().getHighestReward(ClanRewardType.MEMBERS_ONLINE);
        final ClanRewardBonus highestHuntingBonus = ClanRewardData.getInstance().getHighestReward(ClanRewardType.HUNTING_MONSTERS);
        final ClanRewardBonus membersOnlineBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(clan);
        final ClanRewardBonus huntingBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(clan);
        if (highestMembersOnlineBonus == null) {
            LOGGER.warn("Couldn't find highest available clan members online bonus!!");
            throw new InvalidDataPacketException();
        } else if (highestHuntingBonus == null) {
            LOGGER.warn("Couldn't find highest available clan hunting bonus!!");
            throw new InvalidDataPacketException();
        } else if (highestMembersOnlineBonus.getSkillReward() == null) {
            LOGGER.warn("Couldn't find skill reward for highest available members online bonus!!");
            throw new InvalidDataPacketException();
        } else if (highestHuntingBonus.getItemReward() == null) {
            LOGGER.warn("Couldn't find item reward for highest available hunting bonus!!");
            throw new InvalidDataPacketException();
        }

        // General OP Code
        writeId(OutgoingPackets.EX_PLEDGE_BONUS_OPEN);

        // Members online bonus
        writeInt(highestMembersOnlineBonus.getRequiredAmount());
        writeInt(clan.getMaxOnlineMembers());
        writeByte((byte) 0x00); // 140
        writeInt(membersOnlineBonus != null ? highestMembersOnlineBonus.getSkillReward().getSkillId() : 0x00);
        writeByte((byte) (membersOnlineBonus != null ? membersOnlineBonus.getLevel() : 0x00));
        writeByte((byte) (membersOnlineBonus != null ? 0x01 : 0x00));

        // Hunting bonus
        writeInt(highestHuntingBonus.getRequiredAmount());
        writeInt(clan.getHuntingPoints());
        writeByte((byte) 0x00); // 140
        writeInt(huntingBonus != null ? highestHuntingBonus.getItemReward().getId() : 0x00);
        writeByte((byte) (huntingBonus != null ? huntingBonus.getLevel() : 0x00));
        writeByte((byte) (huntingBonus != null ? 0x01 : 0x00));
    }

}
