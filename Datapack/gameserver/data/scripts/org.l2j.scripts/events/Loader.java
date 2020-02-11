package events;

import java.util.ServiceLoader;

public class Loader {

    public static void main(String[] args) {
        ServiceLoader.load(ScriptEvent.class, Loader.class.getClassLoader()).forEach(e -> {});
    }
}
