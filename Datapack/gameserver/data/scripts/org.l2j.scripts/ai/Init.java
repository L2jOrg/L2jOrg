package ai;

import java.util.ServiceLoader;

public class Init {

    public static void main(String[] args) {
        ServiceLoader.load(AbstractNpcAI.class, Init.class.getClassLoader()).forEach(ai -> {});
    }
}
