package org.l2j.gameserver.handler;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.logging.Logger;

public interface IActionShiftHandler {
    Logger LOGGER = Logger.getLogger(IActionShiftHandler.class.getName());

    boolean action(L2PcInstance activeChar, L2Object target, boolean interact);

    InstanceType getInstanceType();
}