package org.l2j.gameserver.mobius.gameserver.handler;

import org.l2j.gameserver.mobius.gameserver.model.skills.targets.TargetType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class TargetHandler implements IHandler<ITargetTypeHandler, Enum<TargetType>> {
    private final Map<Enum<TargetType>, ITargetTypeHandler> _datatable;

    protected TargetHandler() {
        _datatable = new HashMap<>();
    }

    public static TargetHandler getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void registerHandler(ITargetTypeHandler handler) {
        _datatable.put(handler.getTargetType(), handler);
    }

    @Override
    public synchronized void removeHandler(ITargetTypeHandler handler) {
        _datatable.remove(handler.getTargetType());
    }

    @Override
    public ITargetTypeHandler getHandler(Enum<TargetType> targetType) {
        return _datatable.get(targetType);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    private static class SingletonHolder {
        protected static final TargetHandler _instance = new TargetHandler();
    }
}
