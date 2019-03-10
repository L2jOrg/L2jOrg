package org.l2j.gameserver.mobius.gameserver.handler;

import org.l2j.gameserver.mobius.gameserver.enums.InstanceType;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.util.logging.Logger;

public interface IActionHandler {
    Logger LOGGER = Logger.getLogger(IActionHandler.class.getName());

    boolean action(L2PcInstance activeChar, L2Object target, boolean interact);

    InstanceType getInstanceType();
}