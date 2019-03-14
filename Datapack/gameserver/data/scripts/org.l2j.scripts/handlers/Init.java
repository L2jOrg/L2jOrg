package handlers;

import org.l2j.gameserver.handler.*;

import java.util.ServiceLoader;

public class Init {

    public static void main(String[] args) {

        ServiceLoader.load(IActionHandler.class).forEach(handler -> ActionHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IActionShiftHandler.class).forEach(handler -> ActionShiftHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IAdminCommandHandler.class).forEach(handler -> AdminCommandHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IBypassHandler.class).forEach(handler -> BypassHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IChatHandler.class).forEach(handler -> ChatHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IParseBoardHandler.class).forEach(handler -> CommunityBoardHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IItemHandler.class).forEach(handler -> ItemHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IPunishmentHandler.class).forEach(handler -> PunishmentHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IUserCommandHandler.class).forEach(handler -> UserCommandHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IVoicedCommandHandler.class).forEach(handler -> VoicedCommandHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(ITargetTypeHandler.class).forEach(handler -> TargetHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IAffectObjectHandler.class).forEach(handler -> AffectObjectHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IAffectScopeHandler.class).forEach(handler -> AffectScopeHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IPlayerActionHandler.class).forEach(handler -> PlayerActionHandler.getInstance().registerHandler(handler));

        ConditionMasterHandler.init();
        DailyMissionMasterHandler.init();
        EffectMasterHandler.init();
        SkillConditionMasterHandler.init();

    }
}
