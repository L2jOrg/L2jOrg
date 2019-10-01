package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Sdw
 */
public class DailyMissionHandler {
    private final Map<String, Function<DailyMissionDataHolder, AbstractDailyMissionHandler>> _handlerFactories = new HashMap<>();

    private DailyMissionHandler() {
    }

    public void registerHandler(String name, Function<DailyMissionDataHolder, AbstractDailyMissionHandler> handlerFactory) {
        _handlerFactories.put(name, handlerFactory);
    }

    public Function<DailyMissionDataHolder, AbstractDailyMissionHandler> getHandler(String name) {
        return _handlerFactories.get(name);
    }

    public int size() {
        return _handlerFactories.size();
    }

    public void executeScript() {
        try {
            ScriptEngineManager.getInstance().executeDailyMissionMasterHandler();
        } catch (Exception e) {
            throw new Error("Problems while running DailyMissionMasterHandler", e);
        }
    }

    public static DailyMissionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final DailyMissionHandler INSTANCE = new DailyMissionHandler();
    }
}
