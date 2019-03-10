package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ManagePledgePower;

import java.nio.ByteBuffer;

public final class RequestPledgePower extends IClientIncomingPacket {
    private int _rank;
    private int _action;
    private int _privs;

    @Override
    public void readImpl(ByteBuffer packet) {
        _rank = packet.getInt();
        _action = packet.getInt();
        if (_action == 2) {
            _privs = packet.getInt();
        } else {
            _privs = 0;
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        player.sendPacket(new ManagePledgePower(client.getActiveChar().getClan(), _action, _rank));
        if (_action == 2) {
            if (player.isClanLeader()) {
                if (_rank == 9) {
                    // The rights below cannot be bestowed upon Academy members:
                    // Join a clan or be dismissed
                    // Title management, crest management, master management, level management,
                    // bulletin board administration
                    // Clan war, right to dismiss, set functions
                    // Auction, manage taxes, attack/defend registration, mercenary management
                    // => Leaves only CP_CL_VIEW_WAREHOUSE, CP_CH_OPEN_DOOR, CP_CS_OPEN_DOOR?
                    _privs &= ClanPrivilege.CL_VIEW_WAREHOUSE.getBitmask() | ClanPrivilege.CH_OPEN_DOOR.getBitmask() | ClanPrivilege.CS_OPEN_DOOR.getBitmask();
                }
                player.getClan().setRankPrivs(_rank, _privs);
            }
        }
    }
}