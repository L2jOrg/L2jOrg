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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.SecondaryAuthManager;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSelect;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.serverpackets.CharSelected;
import org.l2j.gameserver.network.serverpackets.ServerClose;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterSelect extends ClientPacket {
    protected static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");

    // cd
    private int _charSlot;

    @SuppressWarnings("unused")
    private int _unk1; // new in C4
    @SuppressWarnings("unused")
    private int _unk2; // new in C4
    @SuppressWarnings("unused")
    private int _unk3; // new in C4
    @SuppressWarnings("unused")
    private int _unk4; // new in C4

    @Override
    public void readImpl() {
        _charSlot = readInt();
        _unk1 = readShort();
        _unk2 = readInt();
        _unk3 = readInt();
        _unk4 = readInt();

    }

    @Override
    public void runImpl() {
        if (!client.getFloodProtectors().getCharacterSelect().tryPerformAction("CharacterSelect")) {
            return;
        }

        if (SecondaryAuthManager.getInstance().isEnabled() && !client.isSecondaryAuthed()) {
            client.openSecondaryAuthDialog();
            return;
        }

        // We should always be able to acquire the lock
        // But if we can't lock then nothing should be done (i.e. repeated packet)
        if (client.getActivePlayerLock().tryLock()) {
            try {
                // should always be null
                // but if not then this is repeated packet and nothing should be done here
                if (client.getPlayer() == null) {
                    final CharSelectInfoPackage info = client.getCharSelection(_charSlot);
                    if (info == null) {
                        return;
                    }

                    // Banned?
                    if (PunishmentManager.getInstance().hasPunishment(info.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.BAN) || PunishmentManager.getInstance().hasPunishment(client.getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.BAN) || PunishmentManager.getInstance().hasPunishment(client.getHostAddress(), PunishmentAffect.IP, PunishmentType.BAN)) {
                        client.close(ServerClose.STATIC_PACKET);
                        return;
                    }

                    // Selected character is banned (compatibility with previous versions).
                    if (info.getAccessLevel() < 0) {
                        client.close(ServerClose.STATIC_PACKET);
                        return;
                    }

                    if ((Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddClient(AntiFeedManager.GAME_ID, client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP)) {
                        final NpcHtmlMessage msg = new NpcHtmlMessage();
                        msg.setFile(null, "data/html/mods/IPRestriction.htm");
                        msg.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP)));
                        client.sendPacket(msg);
                        return;
                    }

                    // load up character from disk
                    final Player player = client.load(_charSlot);
                    if (player == null) {
                        return; // handled in GameClient
                    }

                    PlayerNameTable.getInstance().addName(player);

                    player.setClient(client);
                    client.setPlayer(player);
                    player.setOnlineStatus(true, true);
                    player.setVipTier(VipEngine.getInstance().getVipTier(player));

                    final TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerSelect(player, player.getObjectId(), player.getName(), client), Listeners.players(), TerminateReturn.class);
                    if ((terminate != null) && terminate.terminate()) {
                        Disconnection.of(player).defaultSequence(false);
                        return;
                    }

                    client.setConnectionState(ConnectionState.JOINING_GAME);
                    client.sendPacket(new CharSelected(player, client.getSessionId().getGameServerSessionId()));
                }
            } finally {
                client.getActivePlayerLock().unlock();
            }

            LOGGER_ACCOUNTING.info("{} Logged in", client);
        }
    }
}
