package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Comparator;

/**
 * @author Mobius
 */
public class ExPledgeBonusList extends ServerPacket {
    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_BONUS_LIST);
        writeByte((byte) 0x00); // 140
        ClanRewardManager.getInstance().getClanRewardBonuses(ClanRewardType.MEMBERS_ONLINE).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus ->
                writeInt(bonus.getSkillReward().getSkillId()));
        writeByte((byte) 0x01); // 140
        ClanRewardManager.getInstance().getClanRewardBonuses(ClanRewardType.HUNTING_MONSTERS).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus ->
                writeInt(bonus.getItemReward().getId()));
    }

}
