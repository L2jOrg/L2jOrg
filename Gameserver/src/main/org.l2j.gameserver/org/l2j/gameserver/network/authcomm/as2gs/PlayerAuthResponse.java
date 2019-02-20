package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.commons.network.SessionKey;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.dao.HardwareLimitsDAO;
import org.l2j.gameserver.dao.PremiumAccountDAO;
import org.l2j.gameserver.data.htm.HtmCache;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.*;

import java.nio.ByteBuffer;
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
    private int bonus;
    private int bonusExpire;
    private int points;
    private String hwid;
    private long phoneNumber;

    @Override
    public void readImpl(ByteBuffer buffer) {
        account = readString(buffer);
        authed = buffer.get() == 1;
        if(authed) {
            gameserverSession = buffer.getInt();
            gameserverAccountId = buffer.getInt();
            authAccountId = buffer.getInt();
            authKey = buffer.getInt();
        }
    }

    @Override
    protected void runImpl() {
        GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
        if(isNull(client)) {
            return;
        }

        SessionKey skey = new SessionKey(authAccountId, authKey, gameserverSession, gameserverAccountId);
        if(authed && client.getSessionKey().equals(skey)) {
            if(Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_IP > 0 && isNull(AuthServerCommunication.getInstance().getAuthedClient(account))) {
                boolean ignored = false;
                for(String ignoredIP : Config.MAX_ACTIVE_ACCOUNTS_IGNORED_IP) {
                    if(ignoredIP.equalsIgnoreCase(client.getIpAddr())) {
                        ignored = true;
                        break;
                    }
                }

                if(!ignored) {
                    int limit = Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_IP;

                    int[] limits = HardwareLimitsDAO.getInstance().select(client.getIpAddr());
                    if(limits[1] == -1 || limits[1] > System.currentTimeMillis() / 1000L) {
                        limit += limits[0];
                    }

                    List<GameClient> clients = AuthServerCommunication.getInstance().getAuthedClientsByHWID(client.getIpAddr());
                    clients.add(client);
                    if (hasMoreClientThanLimit(client, limit, clients)) {
                        return;
                    }
                }
            }

            if(Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID > 0 && isNull(AuthServerCommunication.getInstance().getAuthedClient(account))) {
                int limit = Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID;

                int[] limits = HardwareLimitsDAO.getInstance().select(client.getHWID());
                if(limits[1] == -1 || limits[1] > System.currentTimeMillis() / 1000L) {
                    limit += limits[0];
                }

                List<GameClient> clients = AuthServerCommunication.getInstance().getAuthedClientsByHWID(client.getHWID());
                clients.add(client);
                if(hasMoreClientThanLimit(client, limit, clients)) {
                    return;
                }
            }

            if(Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_IP > 0 || Config.MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID > 0) {
                client.sendPacket(TutorialCloseHtmlPacket.STATIC);
            }

            client.setState(GameClient.GameClientState.AUTHED);
            client.sendPacket(LoginResultPacket.SUCCESS);

            if(Config.PREMIUM_ACCOUNT_BASED_ON_GAMESERVER) {
                int[] bonuses = PremiumAccountDAO.getInstance().select(account);
                bonus = bonuses[0];
                bonusExpire = bonuses[1];
            }

            client.setPremiumAccountType(bonus);
            client.setPremiumAccountExpire(bonusExpire);
            client.setPoints(points);

            GameClient oldClient = AuthServerCommunication.getInstance().addAuthedClient(client);
            if(nonNull(oldClient))  {
                oldClient.setState(GameClient.GameClientState.DISCONNECTED);
                Player activeChar = oldClient.getActiveChar();

                if(nonNull(activeChar )) {
                    activeChar.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
                    activeChar.logout();
                } else  {
                    oldClient.close(ServerCloseSocketPacket.STATIC);
                }
            }

            sendPacket(new PlayerInGame(client.getLogin()));

            CharacterSelectionInfoPacket csi = new CharacterSelectionInfoPacket(client);
            client.sendPacket(csi);
            client.setCharSelection(csi.getCharInfo());
            client.checkHwid(hwid);
            client.setPhoneNumber(phoneNumber);
        } else {
            client.close(LoginResultPacket.ACCESS_FAILED_TRY_LATER);
        }
    }

    private boolean hasMoreClientThanLimit(GameClient client, int limit, List<GameClient> clients) {
        for(GameClient c : clients) {
            int[] limitsByAccount = HardwareLimitsDAO.getInstance().select(c.getLogin());
            if(limitsByAccount[1] == -1 || limitsByAccount[1] > System.currentTimeMillis() / 1000L) {
                limit += limitsByAccount[0];
            }
        }
        int activeWindows = AuthServerCommunication.getInstance().getAuthedClientsByIP(client.getIpAddr()).size();

        if(activeWindows >= limit) {
            String html = HtmCache.getInstance().getCache("windows_limit_ip.htm", client.getLanguage());
            if(nonNull(html)) {
                html = html.replace("<?active_windows?>", String.valueOf(activeWindows));
                html = html.replace("<?windows_limit?>", String.valueOf(limit));
                client.close(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.NORMAL_WINDOW, html));
            }
            else {
                client.close(LoginResultPacket.ACCESS_FAILED_TRY_LATER);
            }
            return true;
        }
        return false;
    }
}