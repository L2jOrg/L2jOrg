package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public abstract class PledgeAbstractPacket extends ServerPacket {

    protected final Clan clan;

    public PledgeAbstractPacket(Clan clan) {
        this.clan = clan;
    }

    protected void writeClanInfo(int pledgeId) {
        writeInt(clan.getCrestId());
        writeInt(clan.getLevel());
        writeInt(clan.getCastleId());
        writeInt(0x00);
        writeInt(clan.getHideoutId());
        writeInt(clan.getFortId());
        writeInt(clan.getRank());
        writeInt(clan.getReputationScore());
        writeInt(0x00); // 0
        writeInt(0x00); // 0
        writeInt(clan.getAllyId());
        writeString(clan.getAllyName());
        writeInt(clan.getAllyCrestId());
        writeInt(clan.isAtWar()); // new c3
        writeInt(0x00); // Territory castle ID
        writeInt(clan.getSubPledgeMembersCount(pledgeId));
    }
}
