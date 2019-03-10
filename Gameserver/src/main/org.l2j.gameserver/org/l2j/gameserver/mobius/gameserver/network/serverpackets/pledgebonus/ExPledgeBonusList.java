package org.l2j.gameserver.mobius.gameserver.network.serverpackets.pledgebonus;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.ClanRewardData;
import org.l2j.gameserver.mobius.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.mobius.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

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
        {
            packet.putInt(bonus.getSkillReward().getSkillId());
        });
        packet.put((byte) 0x01); // 140
        ClanRewardData.getInstance().getClanRewardBonuses(ClanRewardType.HUNTING_MONSTERS).stream().sorted(Comparator.comparingInt(ClanRewardBonus::getLevel)).forEach(bonus ->
        {
            packet.putInt(bonus.getItemReward().getId());
        });
    }
}
