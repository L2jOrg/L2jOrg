package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.punishment.PunishmentType;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages handlers of punishments.
 *
 * @author UnAfraid
 */
public class PunishmentHandler implements IHandler<IPunishmentHandler, PunishmentType> {
    private final Map<PunishmentType, IPunishmentHandler> _handlers = new HashMap<>();

    private PunishmentHandler() {
    }

    @Override
    public void registerHandler(IPunishmentHandler handler) {
        _handlers.put(handler.getType(), handler);
    }

    @Override
    public synchronized void removeHandler(IPunishmentHandler handler) {
        _handlers.remove(handler.getType());
    }

    @Override
    public IPunishmentHandler getHandler(PunishmentType val) {
        return _handlers.get(val);
    }

    @Override
    public int size() {
        return _handlers.size();
    }

    public static PunishmentHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PunishmentHandler INSTANCE = new PunishmentHandler();
    }
}
