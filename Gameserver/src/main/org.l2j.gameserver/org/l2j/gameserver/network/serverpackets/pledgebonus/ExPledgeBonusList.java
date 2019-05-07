package org.l2j.gameserver.network.serverpackets.pledgebonus;

import org.l2j.gameserver.data.xml.impl.ClanRewardData;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.Comparator;

/**
 * @author Mobius
 */
public class ExPledgeBonusList extends IClientOutgoingPacket {
    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_BONUS_LIST.writeId(packet);
        packet.put((byte) 0x00); // 140
        ClanRewardData.getInstance().getClanRewardBonuses(ClanRewardType.MEMBERS_ONLINE).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus ->
                packet.putInt(bonus.getSkillReward().getSkillId()));
        packet.put((byte) 0x01); // 140
        ClanRewardData.getInstance().getClanRewardBonuses(ClanRewardType.HUNTING_MONSTERS).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus ->
                packet.putInt(bonus.getItemReward().getId()));
    }

    @Override
    protected int size(L2GameClient client) {
        return 6 + ClanRewardData.getInstance().getClanRewardBonuses().size() * 4;
    }
}
