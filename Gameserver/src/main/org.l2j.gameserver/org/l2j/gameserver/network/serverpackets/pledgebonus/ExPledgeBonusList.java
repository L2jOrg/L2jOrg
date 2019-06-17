package org.l2j.gameserver.network.serverpackets.pledgebonus;

import org.l2j.gameserver.data.xml.impl.ClanRewardData;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Comparator;

/**
 * @author Mobius
 */
public class ExPledgeBonusList extends ServerPacket {
    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_BONUS_LIST);
        writeByte((byte) 0x00); // 140
        ClanRewardData.getInstance().getClanRewardBonuses(ClanRewardType.MEMBERS_ONLINE).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus ->
                writeInt(bonus.getSkillReward().getSkillId()));
        writeByte((byte) 0x01); // 140
        ClanRewardData.getInstance().getClanRewardBonuses(ClanRewardType.HUNTING_MONSTERS).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus ->
                writeInt(bonus.getItemReward().getId()));
    }

}
