package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class UserCommandHandler implements IHandler<IUserCommandHandler, Integer> {
    private final Map<Integer, IUserCommandHandler> _datatable;

    private UserCommandHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IUserCommandHandler handler) {
        for (int id : handler.getUserCommandList()) {
            _datatable.put(id, handler);
        }
    }

    @Override
    public synchronized void removeHandler(IUserCommandHandler handler) {
        for (int id : handler.getUserCommandList()) {
            _datatable.remove(id);
        }
    }

    @Override
    public IUserCommandHandler getHandler(Integer userCommand) {
        return _datatable.get(userCommand);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static UserCommandHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final UserCommandHandler INSTANCE = new UserCommandHandler();
    }
}
