package org.l2j.gameserver.engine.mission;

import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import org.l2j.gameserver.handler.AbstractMissionHandler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public final class MissionEngine {
    private static final Path ONE_DAY_REWARD_MASTER_HANDLER = ScriptEngineManager.SCRIPT_FOLDER.resolve(Path.of("org.l2j.scripts", "handlers", "MissionMasterHandler.java"));

    private final Map<String, Function<MissionDataHolder, AbstractMissionHandler>> handlerFactories = new HashMap<>();

    private MissionEngine() {
    }

    public void registerHandler(String name, Function<MissionDataHolder, AbstractMissionHandler> handlerFactory) {
        handlerFactories.put(name, handlerFactory);
    }

    public Function<MissionDataHolder, AbstractMissionHandler> getHandler(String name) {
        return handlerFactories.get(name);
    }

    public int size() {
        return handlerFactories.size();
    }

    private void executeScript() {
        try {
            ScriptEngineManager.getInstance().executeScript(ONE_DAY_REWARD_MASTER_HANDLER);
        } catch (Exception e) {
            throw new Error("Problems while running MissionMasterHandler", e);
        }
    }

    public static void init() {
        getInstance().executeScript();
        MissionData.init();
    }

    public static MissionEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final MissionEngine INSTANCE = new MissionEngine();
    }
}
