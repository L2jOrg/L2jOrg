package instances;

import java.util.ServiceLoader;

public class Loader {

    public static void main(String[] args) {
        ServiceLoader.load(AbstractInstance.class, Loader.class.getClassLoader()).forEach(i -> {});
    }
}
