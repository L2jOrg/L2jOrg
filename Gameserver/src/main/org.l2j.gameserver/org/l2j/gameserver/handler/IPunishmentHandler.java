package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;

import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public interface IPunishmentHandler {
    Logger LOGGER = Logger.getLogger(IPunishmentHandler.class.getName());

    void onStart(PunishmentTask task);

    void onEnd(PunishmentTask task);

    PunishmentType getType();
}
