package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.skills.targets.AffectObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nik
 */
public class AffectObjectHandler implements IHandler<IAffectObjectHandler, Enum<AffectObject>> {
    private final Map<Enum<AffectObject>, IAffectObjectHandler> _datatable;

    private AffectObjectHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IAffectObjectHandler handler) {
        _datatable.put(handler.getAffectObjectType(), handler);
    }

    @Override
    public synchronized void removeHandler(IAffectObjectHandler handler) {
        _datatable.remove(handler.getAffectObjectType());
    }

    @Override
    public IAffectObjectHandler getHandler(Enum<AffectObject> targetType) {
        return _datatable.get(targetType);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static AffectObjectHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AffectObjectHandler INSTANCE = new AffectObjectHandler();
    }
}
