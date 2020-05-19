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
