package handlers;

import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IParseBoardHandler;

import java.util.ServiceLoader;

public class Loader {

    public static void main(String[] args) {
        ServiceLoader.load(IParseBoardHandler.class, Loader.class.getClassLoader()).forEach(CommunityBoardHandler.getInstance()::registerHandler);
    }
}
