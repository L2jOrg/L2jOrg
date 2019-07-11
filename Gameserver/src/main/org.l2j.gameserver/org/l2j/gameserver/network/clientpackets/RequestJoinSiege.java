package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * @author KenM
 */
public final class RequestJoinSiege extends ClientPacket {
    private int _castleId;
    private int _isAttacker;
    private int _isJoining;

    @Override
    public void readImpl() {
        _castleId = readInt();
        _isAttacker = readInt();
        _isJoining = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!activeChar.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE)) {
            client.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final Clan clan = activeChar.getClan();
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
