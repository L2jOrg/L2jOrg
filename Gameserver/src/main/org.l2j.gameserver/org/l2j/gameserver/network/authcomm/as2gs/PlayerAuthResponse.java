package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.commons.network.SessionKey;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.html.TutorialShowHtml;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class PlayerAuthResponse extends ReceivablePacket {
    private String account;
    private boolean authed;
    private int gameserverSession;
    private int gameserverAccountId;
    private int authAccountId;
    private int authKey;
    private int points;
    private String hwid;
    private long phoneNumber;

    @Override
    public void readImpl() {
        account = readString();
        authed = readByte() == 1;
        if(authed) {
            gameserverSession = readInt();
            gameserverAccountId = readInt();
            authAccountId = readInt();
            authKey = readInt();
        }
    }

    @Override
    protected void runImpl() {
        GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
        if(isNull(client)) {
            return;
        }

        SessionKey skey = new SessionKey(authAccountId, authKey, gameserverSession, gameserverAccountId);
        if(authed && client.getSessionId().equals(skey)) {
            client.setConnectionState(ConnectionState.AUTHENTICATED);
            client.sendPacket(LoginFail.LOGIN_SUCCESS);

            GameClient oldClient = AuthServerCommunication.getInstance().addAuthedClient(client);
            if(nonNull(oldClient))  {
                oldClient.setConnectionState(ConnectionState.DISCONNECTED);
                Player activeChar = oldClient.getPlayer();

                if(nonNull(activeChar )) {
                    activeChar.sendPacket(SystemMessageId.YOU_ARE_LOGGED_IN_TO_TWO_PLACES_IF_YOU_SUSPECT_ACCOUNT_THEFT_WE_RECOMMEND_CHANGING_YOUR_PASSWORD_SCANNING_YOUR_COMPUTER_FOR_VIRUSES_AND_USING_AN_ANTI_VIRUS_SOFTWARE);
                    Disconnection.of(activeChar).defaultSequence(false);
                } else  {
                    oldClient.close(ServerClose.STATIC_PACKET);
                }
            }

            sendPacket(new PlayerInGame(client.getAccountName()));
            var charSelectionInfo = new CharSelectionInfo(client.getAccountName(), client.getSessionId().getGameServerSessionId());
            client.sendPacket(charSelectionInfo);
            client.setCharSelection(charSelectionInfo.getCharInfo());
        } else {
            client.close(new LoginFail(LoginFail.ACCESS_FAILED_TRY_LATER));
        }
    }

    private boolean hasMoreClientThanLimit(GameClient client, int limit, List<GameClient> clients) {
        int activeWindows = clients.size();

        if(activeWindows >= limit) {
            String html = HtmCache.getInstance().getHtm(null,"windows_limit_ip.htm");
            if(nonNull(html)) {
                html = html.replace("<?active_windows?>", String.valueOf(activeWindows));
                html = html.replace("<?windows_limit?>", String.valueOf(limit));
                client.close(new TutorialShowHtml(html));
            }
            else {
                client.close(new LoginFail(LoginFail.ACCESS_FAILED_TRY_LATER));
            }
            return true;
        }
        return false;
    }
}