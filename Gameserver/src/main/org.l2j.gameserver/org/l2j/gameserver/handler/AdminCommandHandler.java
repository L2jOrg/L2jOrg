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
package org.l2j.gameserver.handler;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.TimeInterpreter;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GMAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 */
public class AdminCommandHandler implements IHandler<IAdminCommandHandler, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminCommandHandler.class);

    private final Map<String, IAdminCommandHandler> _datatable;

    private AdminCommandHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IAdminCommandHandler handler) {
        for (String id : handler.getAdminCommandList()) {
            _datatable.put(id, handler);
        }
    }

    @Override
    public synchronized void removeHandler(IAdminCommandHandler handler) {
        for (String id : handler.getAdminCommandList()) {
            _datatable.remove(id);
        }
    }

    /**
     * WARNING: Please use {@link #useAdminCommand(Player, String, boolean)} instead.
     */
    @Override
    public IAdminCommandHandler getHandler(String adminCommand) {
        String command = adminCommand;
        if (adminCommand.contains(" ")) {
            command = adminCommand.substring(0, adminCommand.indexOf(" "));
        }
        return _datatable.get(command);
    }

    public void useAdminCommand(Player player, String fullCommand, boolean useConfirm) {
        final String command = fullCommand.split(" ")[0];
        final String commandNoPrefix = command.substring(6);

        if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel())) {
            LOGGER.warn("Player {} tried to use admin command '{}', without proper access level!", player.getName(), command);
            return;
        }

        final IAdminCommandHandler handler = getHandler(command);
        if (isNull(handler)) {
            player.sendMessage("The command '" + commandNoPrefix + "' does not exist!");
            LOGGER.warn("No handler registered for admin command '{}'", command);
            return;
        }

        if (useConfirm && AdminData.getInstance().requireConfirm(command)) {
            player.setAdminConfirmCmd(fullCommand);
            final ConfirmDlg dlg = new ConfirmDlg("Are you sure you want execute command '" + commandNoPrefix + "' ?");
            player.addAction(PlayerAction.ADMIN_COMMAND);
            player.sendPacket(dlg);
        } else {
            // Admin Commands must run through a long running task, otherwise a command that takes too much time will freeze the server, this way you'll feel only a minor spike.
            ThreadPool.execute(() ->
            {
                final long begin = System.currentTimeMillis();
                try {
                    if (getSettings(GeneralSettings.class).auditGM()) {
                        final WorldObject target = player.getTarget();
                        GMAudit.auditGMAction(player.getName() + " [" + player.getObjectId() + "]", fullCommand, (target != null ? target.getName() : "no-target"));
                    }

                    handler.useAdminCommand(fullCommand, player);
                } catch (RuntimeException e) {
                    player.sendMessage("Exception during execution of  '" + fullCommand + "': " + e.toString());
                    LOGGER.warn("Exception during execution of {}", fullCommand, e);
                } finally {
                    final long runtime = System.currentTimeMillis() - begin;
                    player.sendMessage("The execution of '" + fullCommand + "' took " + TimeInterpreter.consolidateMillis(runtime) + ".");
                }
            });
        }
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static AdminCommandHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AdminCommandHandler INSTANCE = new AdminCommandHandler();
    }
}
