package org.l2j.gameserver.handler;

import org.l2j.gameserver.enums.InstanceType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class ActionShiftHandler implements IHandler<IActionShiftHandler, InstanceType> {
    private final Map<InstanceType, IActionShiftHandler> _actionsShift;

    private ActionShiftHandler() {
        _actionsShift = new HashMap<>();
    }

    @Override
    public void registerHandler(IActionShiftHandler handler) {
        _actionsShift.put(handler.getInstanceType(), handler);
    }

    @Override
    public synchronized void removeHandler(IActionShiftHandler handler) {
        _actionsShift.remove(handler.getInstanceType());
    }

    @Override
    public IActionShiftHandler getHandler(InstanceType iType) {
        IActionShiftHandler result = null;
        for (InstanceType t = iType; t != null; t = t.getParent()) {
            result = _actionsShift.get(t);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public int size() {
        return _actionsShift.size();
    }

    public static ActionShiftHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ActionShiftHandler INSTANCE = new ActionShiftHandler();
    }
}