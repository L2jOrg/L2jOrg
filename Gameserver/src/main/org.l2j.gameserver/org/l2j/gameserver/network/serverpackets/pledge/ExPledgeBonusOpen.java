package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class ExPledgeBonusOpen extends ServerPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExPledgeBonusOpen.class);

    private final Player _player;

    public ExPledgeBonusOpen(Player player) {
        _player = player;
    }

    @Override
    public void writeImpl(GameClient client) throws InvalidDataPacketException {
        final Clan clan = _player.getClan();
        if (clan == null) {
            LOGGER.warn("Player: {} attempting to write to a null clan!", _player);
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
        } else if (highestMembersOnlineBonus.getSkillReward() == null) {
            LOGGER.warn("Couldn't find skill reward for highest available members online bonus!!");
            throw new InvalidDataPacketException();
        } else if (highestHuntingBonus.getItemReward() == null) {
            LOGGER.warn("Couldn't find item reward for highest available hunting bonus!!");
            throw new InvalidDataPacketException();
        }

        // General OP Code
        writeId(ServerPacketId.EX_PLEDGE_BONUS_OPEN);

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
