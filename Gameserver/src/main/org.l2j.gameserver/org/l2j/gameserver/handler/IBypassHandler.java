package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nBd
 */
public interface IBypassHandler {
    Logger LOGGER = LoggerFactory.getLogger(IBypassHandler.class.getName());

    /**
     * This is the worker method that is called when someone uses an bypass command.
     *
     * @param command
     * @param activeChar
     * @param bypassOrigin
     * @return success
     */
    boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin);

    /**
     * This method is called at initialization to register all bypasses automatically.
     *
     * @return all known bypasses
     */
    String[] getBypassList();
}