package handlers;

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

    }
}
