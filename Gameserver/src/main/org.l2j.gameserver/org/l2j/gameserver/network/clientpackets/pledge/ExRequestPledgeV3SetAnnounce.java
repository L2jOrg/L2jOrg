package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeV3Info;

public class ExRequestPledgeV3SetAnnounce extends ClientPacket {
    private String _notice;
    private boolean _showOnLogin;
    @Override
    protected void readImpl() throws Exception {
        _notice = readString();
        _showOnLogin = readByte() != 0x00;
    }

    @Override
    protected void runImpl() {
        Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }
        Clan clan = player.getClan();
        if ((clan == null) || (clan.getLeaderId() != player.getObjectId()))
        {
            return;
        }
        clan.setNotice(_notice);
        clan.setNoticeEnabled(_showOnLogin);
        player.sendPacket(new ExPledgeV3Info(player));
    }
}
