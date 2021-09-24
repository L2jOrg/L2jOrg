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

import java.util.ArrayList;
import java.util.List;

/**
 * @author JoeAlisson
 */
public final class ClusterServerInfo implements ServerInfo {

    private final List<SingleServerInfo> servers = new ArrayList<>();
    private SingleServerInfo referenceServer;

    private ClusterServerInfo join(SingleServerInfo incoming) {
        servers.add(incoming);
        if(!referenceServer.isAuthed()) {
            referenceServer = incoming;
        }
        return this;
    }

    public void out(SingleServerInfo info) {
        servers.remove(info);
        if(!servers.isEmpty()) {
            referenceServer = servers.get(0);
        }
    }

    @Override
    public void requestAccountInfo(String account) {
        var server = minLoadServer();
        if(server != null) {
            server.requestAccountInfo(account);
        } else {
            referenceServer.requestAccountInfo(account);
        }
    }

    private SingleServerInfo minLoadServer() {
        float minLoad = Float.MAX_VALUE;
        SingleServerInfo server = null;
        for (var info : servers) {
            if(info.currentLoad() < minLoad) {
                minLoad = info.currentLoad();
                server = info;
            }
        }
        return server;
    }

    private SingleServerInfo serverFromAccount(String account) {
        for (var server : servers) {
            if(server.isAccountInUse(account)) {
                return server;
            }
        }
        return null;
    }

    @Override
    public int onlineAccounts() {
        int accounts = 0;
        for (var server : servers) {
            accounts += server.onlineAccounts();
        }
        return accounts;
    }

    @Override
    public int maxAccounts() {
        int accounts = 0;
        for (var server : servers) {
            accounts += server.maxAccounts();
        }
        return accounts;
    }

    @Override
    public boolean isAccountInUse(String account) {
        return serverFromAccount(account) != null;
    }

    @Override
    public void disconnectAccount(String account) {
        var server = serverFromAccount(account);
        if(server != null) {
            server.disconnectAccount(account);
        }
    }

    @Override
    public Endpoint endpointFrom(String hostAddress) {
        Endpoint endpoint = Endpoint.LOCALHOST;

        var minLoad = Float.MAX_VALUE;
        for (var server : servers) {
            var selectedEndpoint = server.endpointFrom(hostAddress);

            if(server.currentLoad() < minLoad && selectedEndpoint != Endpoint.LOCALHOST) {
                endpoint = selectedEndpoint;
                minLoad = server.currentLoad();
            }
        }
        return endpoint;
    }

    @Override
    public String key() {
        return referenceServer.key();
    }

    @Override
    public int id() {
        return referenceServer.id();
    }

    @Override
    public boolean isAuthed() {
        return !servers.isEmpty();
    }

    @Override
    public int type() {
        return referenceServer.type();
    }

    @Override
    public int status() {
        return referenceServer.status();
    }

    @Override
    public byte ageLimit() {
        return referenceServer.ageLimit();
    }

    @Override
    public boolean isPvp() {
        return referenceServer.isPvp();
    }

    @Override
    public boolean isShowingBrackets() {
        return referenceServer.isShowingBrackets();
    }

    public static ClusterServerInfo of(ServerInfo authedServer, SingleServerInfo incoming) {
        return switch (authedServer) {
            case ClusterServerInfo c -> c.join(incoming);
            case SingleServerInfo s -> clusterFrom(s, incoming);
        };
    }

    private static ClusterServerInfo clusterFrom(SingleServerInfo authedServer, SingleServerInfo incoming) {
        var cluster = new ClusterServerInfo();
        cluster.referenceServer = authedServer;
        cluster.servers.add(authedServer);
        cluster.servers.add(incoming);
        return cluster;
    }
}
