package org.l2j.gameserver.network.serverpackets.pledge;

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
    public void writeImpl(GameClient client) throws InvalidDataPacketException {
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
        } else if (highestMembersOnlineBonus.getSkillReward() == null) {
            LOGGER.warn("Couldn't find skill reward for highest available members online bonus!!");
            throw new InvalidDataPacketException();
        } else if (highestHuntingBonus.getItemReward() == null) {
            LOGGER.warn("Couldn't find item reward for highest available hunting bonus!!");
            throw new InvalidDataPacketException();
        }

        // General OP Code
        writeId(ServerExPacketId.EX_PLEDGE_BONUS_UI_OPEN);

        // Members online bonus
        writeInt(highestMembersOnlineBonus.getRequiredAmount());
        writeInt(clan.getMaxOnlineMembers());
        writeByte( 0x00); // progress ?
        writeInt(membersOnlineBonus != null ? highestMembersOnlineBonus.getSkillReward().getSkillId() : 0x00);
        writeByte((membersOnlineBonus != null ? membersOnlineBonus.getLevel() : 0x00));
        writeByte((membersOnlineBonus != null ? 0x01 : 0x00));

        // Hunting bonus
        writeInt(highestHuntingBonus.getRequiredAmount());
        writeInt(clan.getHuntingPoints());
        writeByte(0x00); // progress
        writeInt(huntingBonus != null ? highestHuntingBonus.getItemReward().getId() : 0x00);
        writeByte((huntingBonus != null ? huntingBonus.getLevel() : 0x00));
        writeByte((huntingBonus != null ? 0x01 : 0x00));
    }

}
