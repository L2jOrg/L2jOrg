package instances;

import java.util.ServiceLoader;

public class Init {

    public static void main(String[] args) {
        ServiceLoader.load(AbstractInstance.class, Init.class.getClassLoader()).forEach( i -> {});
    }
}
