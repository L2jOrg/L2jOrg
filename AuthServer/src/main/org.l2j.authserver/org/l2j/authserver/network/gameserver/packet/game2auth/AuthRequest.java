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
package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.ClusterServerInfo;
import org.l2j.authserver.network.Endpoint;
import org.l2j.authserver.network.ServerInfo;
import org.l2j.authserver.network.SingleServerInfo;
import org.l2j.authserver.network.gameserver.ServerClientState;
import org.l2j.authserver.network.gameserver.packet.auth2game.AuthResponse;
import org.l2j.authserver.settings.AuthServerSettings;
import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

import static org.l2j.authserver.network.gameserver.packet.auth2game.GameServerAuthFail.FailReason;
import static org.l2j.authserver.settings.AuthServerSettings.acceptNewGameServerEnabled;

/**
 * @author JoeAlisson
 */
public class AuthRequest extends GameserverReadablePacket {

    private static final Logger LOGGER = LoggerFactory.getLogger("ServerJoinManager");

    private static final Object registerLock = new Object();

	private int desiredId;
	private int maxPlayers;
	private String[] hosts;
	private String[] subnetAddresses;
	private int type;
    private byte ageLimit;
    private boolean showBrackets;
    private boolean isPvp;
    private short port;
    private String authKey;
    private String key;

    @Override
	protected void readImpl() {
		desiredId = readByte();
        key = readSizedString();
        authKey = readSizedString();
        type = readInt();
        maxPlayers = readInt();
        ageLimit = readByte();
        showBrackets = readBoolean();
        isPvp = readBoolean();

        var subnets = readByte();
        hosts = new String[subnets];
        subnetAddresses = new String[subnets];

        for (int i = 0; i < hosts.length; i++) {
            hosts[i] =  readString();
            subnetAddresses[i] = readString();
        }
        port = readShort();
    }

	@Override
	protected void runImpl()  {
        if(!AuthServerSettings.acceptKey(authKey)) {
            client.close(FailReason.NOT_AUTHED);
            return;
        }

        if(hosts.length < 1) {
            client.close(FailReason.BAD_DATA);
            return;
        }

        if(Util.isNullOrEmpty(key)) {
            client.close(FailReason.MISSING_KEY);
            return;
        }

        var gsi = GameServerManager.getInstance().getRegisteredGameServerById(desiredId);

        if(gsi == null) {
            gsi = processNewGameServer();
        } else {
            authenticGameServer(gsi);
        }

        if(gsi != null && gsi.isAuthed()) {
            client.sendPacket(new AuthResponse(gsi.id()));
        }
	}

    private void authenticGameServer(ServerInfo gsi) {
        if(gsi.key().equals(key)){
            synchronized (registerLock) {
                if (gsi.isAuthed()) {
                    if (gsi.type() != type) {
                        client.close(FailReason.ID_RESERVED);
                        return;
                    }
                    registerInCluster(gsi);
                } else {
                    updateGameServerInfo(gsi);
                }
            }
        } else {
            client.close(FailReason.ID_RESERVED);
        }
    }

    private void registerInCluster(ServerInfo gsi) {
        var newServer = GameServerManager.getInstance().registerInCluster(gsi, client);
        updateGameServerInfo(newServer);
    }

    private ServerInfo processNewGameServer() {
        if (acceptNewGameServerEnabled()) {
            synchronized (registerLock) {
                var gsi = GameServerManager.getInstance().getRegisteredGameServerById(desiredId);
                if(gsi == null) {
                    gsi = registerNewGameServer();
                } else {
                    authenticGameServer(gsi);
                }
                return gsi;
            }
        } else {
            client.close(FailReason.NOT_AUTHED);
        }
        return null;
    }

    private ServerInfo registerNewGameServer() {
        var gsi = GameServerManager.getInstance().register(key, desiredId, client, type);
        updateGameServerInfo(gsi);
        return  gsi;
    }

    private void updateGameServerInfo(ServerInfo gsi) {
        switch (gsi) {
            case SingleServerInfo s -> updateServerInfo(s);
            case ClusterServerInfo c -> registerInCluster(c);
        }
    }

    private void updateServerInfo(SingleServerInfo gsi) {

        client.setGameServerInfo(gsi);
        client.setState(ServerClientState.AUTHED);
        gsi.setClient(client);
        gsi.setMaxAccounts(maxPlayers);
        gsi.setAuthed(true);
        gsi.setType(type);
        gsi.setAgeLimit(ageLimit);
        gsi.setShowingBrackets(showBrackets);
        gsi.setIsPvp(isPvp);
        gsi.setStatus(ServerStatus.STATUS_AUTO);

        Endpoint[] endpoints = new Endpoint[hosts.length];

        var name = GameServerManager.getInstance().getServerNameById(gsi.id());
        for (int i = 0; i < hosts.length; i++) {
            try {
                endpoints[i] = Endpoint.of(hosts[i], port, subnetAddresses[i]);
                LOGGER.info("Add new endpoint to {}[{}] [ subnet:{} host:{} port:{}]", name, gsi.id(), subnetAddresses[i], hosts[i], port);
            } catch (UnknownHostException e) {
                LOGGER.warn("Could not resolve hostname", e);
            }
        }

        gsi.setEndpoints(endpoints);
    }
}
