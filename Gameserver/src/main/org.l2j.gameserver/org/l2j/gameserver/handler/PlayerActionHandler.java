package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class PlayerActionHandler implements IHandler<IPlayerActionHandler, String> {
    private final Map<String, IPlayerActionHandler> _actions = new HashMap<>();

    private PlayerActionHandler() {
    }

    @Override
    public void registerHandler(IPlayerActionHandler handler) {
        _actions.put(handler.getClass().getSimpleName(), handler);
    }

    @Override
    public synchronized void removeHandler(IPlayerActionHandler handler) {
        _actions.remove(handler.getClass().getSimpleName());
    }

    @Override
    public IPlayerActionHandler getHandler(String name) {
        return _actions.get(name);
    }

    @Override
    public int size() {
        return _actions.size();
    }

    public static PlayerActionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PlayerActionHandler INSTANCE = new PlayerActionHandler();
    }
}