package org.l2j.gameserver.handler;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface IActionHandler {
    Logger LOGGER = LoggerFactory.getLogger(IActionHandler.class.getName());

    boolean action(L2PcInstance activeChar, L2Object target, boolean interact);

    InstanceType getInstanceType();
}