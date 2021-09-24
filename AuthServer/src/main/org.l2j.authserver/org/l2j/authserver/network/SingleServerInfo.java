/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.authserver.network;

import org.l2j.authserver.data.database.ServerData;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.l2j.authserver.network.gameserver.packet.auth2game.KickPlayer;
import org.l2j.authserver.network.gameserver.packet.auth2game.RequestAccountInfo;
import org.l2j.authserver.network.gameserver.packet.game2auth.ServerStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author JoeAlisson
 */
public final class SingleServerInfo implements ServerInfo {

    private final Set<String> accounts = new HashSet<>();
    private final String key;
    private int id;
    private volatile boolean isAuthed;
    private int status = ServerStatus.STATUS_DOWN;

    private boolean isPvp;
    private boolean isShowingBrackets;
    private int maxAccounts;
    private int type;
    private byte ageLimit;
    private ServerClient client;
    private Endpoint[] endpoints;

    public SingleServerInfo(ServerData serverData) {
        this.key = serverData.key();
        this.id = serverData.getId();
        this.type = serverData.getServerType();
    }

    public SingleServerInfo(String key, int id, ServerClient client, int type) {
        this.key = key;
        this.id = id;
        this.client = client;
        this.type = type;
    }

    public void setClient(ServerClient client) {
        this.client = client;
    }

    public int onlineAccounts() {
        return accounts.size();
    }

    public void setDown() {
        setAuthed(false);
        setEndpoints(null);
        setStatus(ServerStatus.STATUS_DOWN);
        accounts.clear();
        client = null;
    }

    public void disconnectAccount(String account) {
        removeAccount(account);
        client.sendPacket(new KickPlayer(account));
    }

    public void requestAccountInfo(String account) {
        client.sendPacket(new RequestAccountInfo(account));
    }

    public void removeAccount(String account) {
        accounts.remove(account);
    }

    public boolean isAccountInUse(String account) {
        return accounts.contains(account);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public String key() {
        return key;
    }

    public void setAuthed(boolean isAuthed) {
        this.isAuthed = isAuthed;
    }

    public boolean isAuthed() {
        return isAuthed;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int status() {
        return status;
    }


    public void setMaxAccounts(int maxPlayers) {
        maxAccounts = maxPlayers;
    }

    public int maxAccounts() {
        return maxAccounts;
    }

    public boolean isPvp() {
        return isPvp;
    }

    public void setShowingBrackets(boolean val) {
        isShowingBrackets = val;
    }

    public boolean isShowingBrackets() {
        return isShowingBrackets;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int type() {
        return type;
    }

    public void addAccounts(List<String> accounts) {
        this.accounts.addAll(accounts);
    }

    public void setAgeLimit(byte ageLimit) {
        this.ageLimit = ageLimit;
    }

    public void setIsPvp(boolean isPvp) {
        this.isPvp = isPvp;
    }

    public byte ageLimit() {
        return ageLimit;
    }

    public Endpoint endpointFrom(String hostAddress)  {
        if(endpoints != null ) {
            for (Endpoint endpoint : endpoints) {
                if (endpoint != null && endpoint.isInSameSubnet(hostAddress)) {
                    return endpoint;
                }
            }
        }
        return Endpoint.LOCALHOST;
    }

    String serverAddress() {
      return client.getHostAddress();
    }

    float currentLoad() {
        return  onlineAccounts() / (float) maxAccounts();
    }

    public void setEndpoints(Endpoint[] endpoints) {
        this.endpoints = endpoints;
    }

}