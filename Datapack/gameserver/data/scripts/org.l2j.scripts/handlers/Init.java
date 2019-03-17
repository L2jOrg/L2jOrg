package handlers;

import org.l2j.gameserver.handler.*;

import java.util.ServiceLoader;

public class Init {

    public static void main(String[] args) {
        ClassLoader loader = Init.class.getClassLoader();
        ServiceLoader.load(IActionHandler.class, loader).forEach(handler -> ActionHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IActionShiftHandler.class, loader).forEach(handler -> ActionShiftHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IAdminCommandHandler.class, loader).forEach(handler -> AdminCommandHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IBypassHandler.class, loader).forEach(handler -> BypassHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IChatHandler.class, loader).forEach(handler -> ChatHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IParseBoardHandler.class, loader).forEach(handler -> CommunityBoardHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IItemHandler.class, loader).forEach(handler -> ItemHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IPunishmentHandler.class, loader).forEach(handler -> PunishmentHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IUserCommandHandler.class, loader).forEach(handler -> UserCommandHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IVoicedCommandHandler.class, loader).forEach(handler -> VoicedCommandHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(ITargetTypeHandler.class, loader).forEach(handler -> TargetHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IAffectObjectHandler.class, loader).forEach(handler -> AffectObjectHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IAffectScopeHandler.class, loader).forEach(handler -> AffectScopeHandler.getInstance().registerHandler(handler));
        ServiceLoader.load(IPlayerActionHandler.class, loader).forEach(handler -> PlayerActionHandler.getInstance().registerHandler(handler));

    }
}
