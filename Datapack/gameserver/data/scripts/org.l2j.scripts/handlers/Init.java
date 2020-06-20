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
package handlers;

import org.l2j.gameserver.engine.mission.MissionEngine;
import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.handler.*;
import org.l2j.gameserver.model.conditions.ConditionFactory;

import java.util.ServiceLoader;

/**
 * @author JoeAlisson
 */
public class Init {

    public static void main(String[] args) {
        ClassLoader loader = Init.class.getClassLoader();
        ServiceLoader.load(IActionHandler.class, loader).forEach(ActionHandler.getInstance()::registerHandler);
        ServiceLoader.load(IActionShiftHandler.class, loader).forEach(ActionShiftHandler.getInstance()::registerHandler);
        ServiceLoader.load(IAdminCommandHandler.class, loader).forEach(AdminCommandHandler.getInstance()::registerHandler);
        ServiceLoader.load(IBypassHandler.class, loader).forEach(BypassHandler.getInstance()::registerHandler);
        ServiceLoader.load(IChatHandler.class, loader).forEach(ChatHandler.getInstance()::registerHandler);
        ServiceLoader.load(IItemHandler.class, loader).forEach(ItemHandler.getInstance()::registerHandler);
        ServiceLoader.load(IPunishmentHandler.class, loader).forEach(PunishmentHandler.getInstance()::registerHandler);
        ServiceLoader.load(IUserCommandHandler.class, loader).forEach(UserCommandHandler.getInstance()::registerHandler);
        ServiceLoader.load(IVoicedCommandHandler.class, loader).forEach(VoicedCommandHandler.getInstance()::registerHandler);
        ServiceLoader.load(ITargetTypeHandler.class, loader).forEach(TargetHandler.getInstance()::registerHandler);
        ServiceLoader.load(IAffectObjectHandler.class, loader).forEach(AffectObjectHandler.getInstance()::registerHandler);
        ServiceLoader.load(IAffectScopeHandler.class, loader).forEach(AffectScopeHandler.getInstance()::registerHandler);
        ServiceLoader.load(IPlayerActionHandler.class, loader).forEach(PlayerActionHandler.getInstance()::registerHandler);
        ServiceLoader.load(SkillConditionFactory.class, loader).forEach(SkillConditionHandler.getInstance()::registerFactory);
        ServiceLoader.load(SkillEffectFactory.class, loader).forEach(EffectHandler.getInstance()::registerFactory);
        ServiceLoader.load(ConditionFactory.class, loader).forEach(ConditionHandler.getInstance()::registerFactory);
        ServiceLoader.load(MissionHandlerFactory.class, loader).forEach(MissionEngine.getInstance()::registerHandler);
    }
}
