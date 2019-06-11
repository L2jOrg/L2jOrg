package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.serverpackets.ServerClose;

public class KickPlayer extends ReceivablePacket
{
    private String account;

    @Override
    public void readImpl()
    {
        account = readString();
    }

    @Override
    protected void runImpl()
    {
        L2GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
        if(client == null)
            client = AuthServerCommunication.getInstance().removeAuthedClient(account);
        if(client == null)
            return;

        L2PcInstance activeChar = client.getActiveChar();
        if(activeChar != null) {
            //FIXME [G1ta0] сообщение чаще всего не показывается, т.к. при закрытии соединения очередь на отправку очищается
            activeChar.sendPacket(SystemMessageId.YOU_ARE_LOGGED_IN_TO_TWO_PLACES_IF_YOU_SUSPECT_ACCOUNT_THEFT_WE_RECOMMEND_CHANGING_YOUR_PASSWORD_SCANNING_YOUR_COMPUTER_FOR_VIRUSES_AND_USING_AN_ANTI_VIRUS_SOFTWARE);
            Disconnection.of(activeChar).defaultSequence(false);
        } else {
            client.close(ServerClose.STATIC_PACKET);
        }
    }
}