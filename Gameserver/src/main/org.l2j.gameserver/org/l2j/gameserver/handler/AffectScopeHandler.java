package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.skills.targets.AffectScope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nik
 */
public class AffectScopeHandler implements IHandler<IAffectScopeHandler, Enum<AffectScope>> {
    private final Map<Enum<AffectScope>, IAffectScopeHandler> _datatable;

    private AffectScopeHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IAffectScopeHandler handler) {
        _datatable.put(handler.getAffectScopeType(), handler);
    }

    @Override
    public synchronized void removeHandler(IAffectScopeHandler handler) {
        _datatable.remove(handler.getAffectScopeType());
    }

    @Override
    public IAffectScopeHandler getHandler(Enum<AffectScope> affectScope) {
        return _datatable.get(affectScope);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static AffectScopeHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final AffectScopeHandler INSTANCE = new AffectScopeHandler();
    }
}
