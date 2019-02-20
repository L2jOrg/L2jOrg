package org.l2j.commons.network;

import java.util.Objects;

/**
 * <p>
 * This class is used to represent session keys used by the client to authenticate in the gameserver
 * </p>
 *
 * @author -Wooden-
 */
public class SessionKey {
    private int gameServerSessionId;
    private int gameServerAccountId;
    private int authAccountId;
    private int authKey;

    public SessionKey(int authAccountId, int authKey, int gameServerSession, int gameServerAccountId) {
        gameServerSessionId = gameServerSession;
        this.gameServerAccountId = gameServerAccountId;
        this.authAccountId = authAccountId;
        this.authKey = authKey;
    }

    public boolean checkLoginPair(int accountId, int authKey) {
        return this.authAccountId == accountId && this.authKey == authKey;
    }

    public int getGameServerAccountId() {
        return gameServerAccountId;
    }

    public int getGameServerSessionId() {
        return gameServerSessionId;
    }

    public int getAuthAccountId() {
        return authAccountId;
    }

    public int getAuthKey() {
        return authKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionKey that = (SessionKey) o;
        return gameServerSessionId == that.gameServerSessionId &&
                gameServerAccountId == that.gameServerAccountId &&
                authAccountId == that.authAccountId &&
                authKey == that.authKey;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameServerSessionId, gameServerAccountId, authAccountId, authKey);
    }

    @Override
    public String toString() {
        return "PlayOk: " + gameServerSessionId + " " + gameServerAccountId + " LoginOk:" + authAccountId + " " + authKey;
    }
}