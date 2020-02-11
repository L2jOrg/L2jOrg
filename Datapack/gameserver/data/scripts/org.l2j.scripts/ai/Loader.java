package ai;

import java.util.ServiceLoader;

public class Loader {

    public static void main(String[] args) {
        ServiceLoader.load(AbstractNpcAI.class, Loader.class.getClassLoader()).forEach(ai -> {});
    }
}
