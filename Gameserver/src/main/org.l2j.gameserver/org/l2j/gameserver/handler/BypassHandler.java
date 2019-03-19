package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nBd, UnAfraid
 */
public class BypassHandler implements IHandler<IBypassHandler, String> {
    private final Map<String, IBypassHandler> _datatable;

    private BypassHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IBypassHandler handler) {
        for (String element : handler.getBypassList()) {
            _datatable.put(element.toLowerCase(), handler);
        }
    }

    @Override
    public synchronized void removeHandler(IBypassHandler handler) {
        for (String element : handler.getBypassList()) {
            _datatable.remove(element.toLowerCase());
        }
    }

    @Override
    public IBypassHandler getHandler(String command) {
        if (command.contains(" ")) {
            command = command.substring(0, command.indexOf(" "));
        }
        return _datatable.get(command.toLowerCase());
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static BypassHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BypassHandler INSTANCE = new BypassHandler();
    }
}