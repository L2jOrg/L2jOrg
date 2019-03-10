package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.entity.Castle;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public final class RequestJoinSiege extends IClientIncomingPacket {
    private int _castleId;
    private int _isAttacker;
    private int _isJoining;

    @Override
    public void readImpl(ByteBuffer packet) {
        _castleId = packet.getInt();
        _isAttacker = packet.getInt();
        _isJoining = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!activeChar.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE)) {
            client.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final L2Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        final Castle castle = CastleManager.getInstance().getCastleById(_castleId);
        if (castle != null) {
            if (_isJoining == 1) {
                if (System.currentTimeMillis() < clan.getDissolvingExpiryTime()) {
                    client.sendPacket(SystemMessageId.YOUR_CLAN_MAY_NOT_REGISTER_TO_PARTICIPATE_IN_A_SIEGE_WHILE_UNDER_A_GRACE_PERIOD_OF_THE_CLAN_S_DISSOLUTION);
                    return;
                }
                if (_isAttacker == 1) {
                    castle.getSiege().registerAttacker(activeChar);
                } else {
                    castle.getSiege().registerDefender(activeChar);
                }
            } else {
                castle.getSiege().removeSiegeClan(activeChar);
            }
            castle.getSiege().listRegisterClan(activeChar);
        }
    }
}
