package org.l2j.authserver.network;

/**
 * @author JoeAlisson
 */
public sealed interface ServerInfo permits SingleServerInfo, ClusterServerInfo{

    void requestAccountInfo(String login);

    boolean isAccountInUse(String login);

    void disconnectAccount(String login);

    String key();

    int id();

    boolean isAuthed();

    int type();

    int onlineAccounts();

    int maxAccounts();

    int status();

    Endpoint endpointFrom(String hostAddress);

    byte ageLimit();

    boolean isPvp();

    boolean isShowingBrackets();
}
