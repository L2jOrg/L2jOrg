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
package org.l2j.gameserver;

import org.l2j.gameserver.handler.*;
import org.l2j.gameserver.model.conditions.ConditionFactory;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.quest.Quest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

/**
 * @author JoeAlisson
 * @since 1.7.0
 */
public class ExtensionBoot {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionBoot.class);

    static void initializers() {
        ServiceLoader.load(IActionHandler.class).forEach(ActionHandler.getInstance()::registerHandler);
        ServiceLoader.load(IActionShiftHandler.class).forEach(ActionShiftHandler.getInstance()::registerHandler);
        ServiceLoader.load(IAdminCommandHandler.class).forEach(AdminCommandHandler.getInstance()::registerHandler);
        ServiceLoader.load(IBypassHandler.class).forEach(BypassHandler.getInstance()::registerHandler);
        ServiceLoader.load(IChatHandler.class).forEach(ChatHandler.getInstance()::registerHandler);
        ServiceLoader.load(IPunishmentHandler.class).forEach(PunishmentHandler.getInstance()::registerHandler);
        ServiceLoader.load(IUserCommandHandler.class).forEach(UserCommandHandler.getInstance()::registerHandler);
        ServiceLoader.load(IVoicedCommandHandler.class).forEach(VoicedCommandHandler.getInstance()::registerHandler);
        ServiceLoader.load(IPlayerActionHandler.class).forEach(PlayerActionHandler.getInstance()::registerHandler);
        ServiceLoader.load(ConditionFactory.class).forEach(ConditionHandler.getInstance()::registerFactory);
    }

    static void loaders() {
        ServiceLoader.load(Quest.class).forEach(q -> LOGGER.debug("Quest {} Loaded", q));
        // TODO split in more specific types
        ServiceLoader.load(AbstractScript.class).forEach(s -> LOGGER.debug("Script {} Loaded", s));
    }

    static void posLoaders(){
        ServiceLoader.load(IParseBoardHandler.class).forEach(CommunityBoardHandler.getInstance()::registerHandler);
    }
}
