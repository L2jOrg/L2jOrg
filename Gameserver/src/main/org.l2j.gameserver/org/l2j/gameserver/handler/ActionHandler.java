package org.l2j.gameserver.handler;

import org.l2j.gameserver.enums.InstanceType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class ActionHandler implements IHandler<IActionHandler, InstanceType> {
    private final Map<InstanceType, IActionHandler> _actions;

    private ActionHandler() {
        _actions = new HashMap<>();
    }

    @Override
    public void registerHandler(IActionHandler handler) {
        _actions.put(handler.getInstanceType(), handler);
    }

    @Override
    public synchronized void removeHandler(IActionHandler handler) {
        _actions.remove(handler.getInstanceType());
    }

    @Override
    public IActionHandler getHandler(InstanceType iType) {
        IActionHandler result = null;
        for (InstanceType t = iType; t != null; t = t.getParent()) {
            result = _actions.get(t);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public int size() {
        return _actions.size();
    }

    public static ActionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ActionHandler INSTANCE = new ActionHandler();
    }
}