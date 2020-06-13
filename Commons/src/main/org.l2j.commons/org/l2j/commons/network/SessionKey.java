/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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