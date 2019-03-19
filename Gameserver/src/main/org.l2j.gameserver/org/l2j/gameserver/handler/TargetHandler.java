package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.skills.targets.TargetType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class TargetHandler implements IHandler<ITargetTypeHandler, Enum<TargetType>> {
    private final Map<Enum<TargetType>, ITargetTypeHandler> _datatable;

    private TargetHandler() {
        _datatable = new HashMap<>();
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

    public static TargetHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {

        protected static final TargetHandler INSTANCE = new TargetHandler();
    }
}
