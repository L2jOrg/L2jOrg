package org.l2j.gameserver.handler;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.util.TimeAmountInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
     * WARNING: Please use {@link #useAdminCommand(L2PcInstance, String, boolean)} instead.
     */
    @Override
    public IAdminCommandHandler getHandler(String adminCommand) {
        String command = adminCommand;
        if (adminCommand.contains(" ")) {
            command = adminCommand.substring(0, adminCommand.indexOf(" "));
        }
        return _datatable.get(command);
    }

    public void useAdminCommand(L2PcInstance player, String fullCommand, boolean useConfirm) {
        final String command = fullCommand.split(" ")[0];
        final String commandNoPrefix = command.substring(6);

        final IAdminCommandHandler handler = getHandler(command);
        if (handler == null) {
            if (player.isGM()) {
                player.sendMessage("The command '" + commandNoPrefix + "' does not exist!");
            }
            LOGGER.warn("No handler registered for admin command '" + command + "'");
            return;
        }

        if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel())) {
            player.sendMessage("You don't have the access rights to use this command!");
            LOGGER.warn("Player " + player.getName() + " tried to use admin command '" + command + "', without proper access level!");
            return;
        }

        if (useConfirm && AdminData.getInstance().requireConfirm(command)) {
            player.setAdminConfirmCmd(fullCommand);
            final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1_3);
            dlg.addString("Are you sure you want execute command '" + commandNoPrefix + "' ?");
            player.addAction(PlayerAction.ADMIN_COMMAND);
            player.sendPacket(dlg);
        } else {
            // Admin Commands must run through a long running task, otherwise a command that takes too much time will freeze the server, this way you'll feel only a minor spike.
            ThreadPoolManager.getInstance().execute(() ->
            {
                final long begin = System.currentTimeMillis();
                try {
                    if (Config.GMAUDIT) {
                        final L2Object target = player.getTarget();
                        GMAudit.auditGMAction(player.getName() + " [" + player.getObjectId() + "]", fullCommand, (target != null ? target.getName() : "no-target"));
                    }

                    handler.useAdminCommand(fullCommand, player);
                } catch (RuntimeException e) {
                    player.sendMessage("Exception during execution of  '" + fullCommand + "': " + e.toString());
                    LOGGER.warn("Exception during execution of " + fullCommand + " " + e);
                } finally {
                    final long runtime = System.currentTimeMillis() - begin;
                    player.sendMessage("The execution of '" + fullCommand + "' took " + TimeAmountInterpreter.consolidateMillis(runtime) + ".");
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
