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
package org.l2j.scripts.handlers.punishmenthandlers;

import org.l2j.gameserver.handler.IPunishmentHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.world.World;

/**
 * @author JoeAlisson
 */
public abstract class PunishmentHandler implements IPunishmentHandler {

    @Override
    public void onStart(PunishmentTask task) {
        switch (task.getAffect()) {
            case CHARACTER -> punishPlayer(task);
            case ACCOUNT -> punishAccount(task);
            case IP -> punishIP(task);
        }
    }

    private void punishIP(PunishmentTask task) {
        var ip = String.valueOf(task.getKey());
        for (var player : World.getInstance().getPlayers()) {
            if (player.getIPAddress().equals(ip)) {
                applyToPlayer(task, player);
            }
        }
    }

    private void punishAccount(PunishmentTask task) {
        var account = String.valueOf(task.getKey());
        var client = AuthServerCommunication.getInstance().getAuthedClient(account);
        if (client != null) {
            var player = client.getPlayer();
            if (player != null) {
                applyToPlayer(task, player);
            }
        }
    }

    private void punishPlayer(PunishmentTask task) {
        var objectId = Integer.parseInt(String.valueOf(task.getKey()));
        var player = World.getInstance().findPlayer(objectId);
        if (player != null) {
            applyToPlayer(task, player);
        }
    }

    @Override
    public void onEnd(PunishmentTask task) {
        switch (task.getAffect()) {
            case CHARACTER -> stopPlayerPunishment(task);
            case ACCOUNT -> stopAccountPunishment(task);
            case IP -> stopIPPunishment(task);
        }
    }

    private void stopIPPunishment(PunishmentTask task) {
        var ip = String.valueOf(task.getKey());
        for (var player : World.getInstance().getPlayers()) {
            if (player.getIPAddress().equals(ip)) {
                removeFromPlayer(player);
            }
        }
    }

    private void stopAccountPunishment(PunishmentTask task) {
        var account = String.valueOf(task.getKey());
        var client = AuthServerCommunication.getInstance().getAuthedClient(account);
        if (client != null) {
            var player = client.getPlayer();
            if (player != null) {
                removeFromPlayer(player);
            }
        }
    }

    private void stopPlayerPunishment(PunishmentTask task) {
        var objectId = Integer.parseInt(String.valueOf(task.getKey()));
        var player = World.getInstance().findPlayer(objectId);
        if (player != null) {
            removeFromPlayer(player);
        }
    }

    protected abstract void applyToPlayer(PunishmentTask task, Player player);

    protected abstract void removeFromPlayer(Player player);
}
