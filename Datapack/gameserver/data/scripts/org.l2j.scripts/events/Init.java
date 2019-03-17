package events;

import java.util.ServiceLoader;

public class Init {

    public static void main(String[] args) {
        ServiceLoader.load(ScriptEvent.class, Init.class.getClassLoader()).forEach(e -> {});
    }
}
