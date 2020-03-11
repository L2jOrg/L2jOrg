package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid, Nos, Mobius
 */
public class Die extends ServerPacket {
    private final int objectId;
    private final boolean isSweepable;
    private int flags = 0;

    public Die(Creature activeChar) {
        objectId = activeChar.getObjectId();
        isSweepable = isAttackable(activeChar) && activeChar.isSweepActive();

        if (isPlayer(activeChar)) {
            final Clan clan = activeChar.getActingPlayer().getClan();
            boolean isInCastleDefense = false;
            boolean isInFortDefense = false;

            SiegeClanData siegeClan = null;
            final Castle castle = CastleManager.getInstance().getCastle(activeChar);
            final Fort fort = FortDataManager.getInstance().getFort(activeChar);
            if ((castle != null) && castle.getSiege().isInProgress()) {
                siegeClan = castle.getSiege().getAttackerClan(clan);
                isInCastleDefense = (siegeClan == null) && castle.getSiege().checkIsDefender(clan);
            } else if ((fort != null) && fort.getSiege().isInProgress()) {
                siegeClan = fort.getSiege().getAttackerClan(clan);
                isInFortDefense = (siegeClan == null) && fort.getSiege().checkIsDefender(clan);
            }

            flags += nonNull(clan) && clan.getHideoutId() > 0 ? 2 : 0; // clan hall
            flags += (nonNull(clan) && (clan.getCastleId() > 0)) || isInCastleDefense ? 4 : 0; // castle
            flags += (nonNull(clan) && (clan.getFortId() > 0)) || isInFortDefense ? 8 : 0; // fortress
            flags += nonNull(siegeClan) && !isInCastleDefense && !isInFortDefense && !siegeClan.getFlags().isEmpty() ? 16 : 0; // outpost
            flags += activeChar.getAccessLevel().allowFixedRes() || activeChar.getInventory().haveItemForSelfResurrection() ? 32 : 0; // feather
        }
    }


    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.DIE);

        writeInt(objectId);
        writeByte(flags);
        writeByte(0);
        writeByte(isSweepable);
        writeByte(0); // resurrection during siege.
    }

}
