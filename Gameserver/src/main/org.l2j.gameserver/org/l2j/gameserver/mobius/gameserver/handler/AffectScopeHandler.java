package org.l2j.gameserver.mobius.gameserver.handler;

import org.l2j.gameserver.mobius.gameserver.model.skills.targets.AffectScope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nik
 */
public class AffectScopeHandler implements IHandler<IAffectScopeHandler, Enum<AffectScope>> {
    private final Map<Enum<AffectScope>, IAffectScopeHandler> _datatable;

    protected AffectScopeHandler() {
        _datatable = new HashMap<>();
    }

    public static AffectScopeHandler getInstance() {
        return SingletonHolder._instance;
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

    private static class SingletonHolder {
        protected static final AffectScopeHandler _instance = new AffectScopeHandler();
    }
}
