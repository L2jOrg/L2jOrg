package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author UnAfraid
 */
public interface IPunishmentHandler {
    Logger LOGGER = LoggerFactory.getLogger(IPunishmentHandler.class.getName());

    void onStart(PunishmentTask task);

    void onEnd(PunishmentTask task);

    PunishmentType getType();
}
