package  org.l2j.gameserver.scripts;

import org.l2j.commons.compiler.Compiler;
import org.l2j.commons.listener.ListenerList;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.bypass.Bypass;
import org.l2j.gameserver.handler.bypass.BypassHolder;
import org.l2j.gameserver.listener.script.OnInitScriptListener;
import org.l2j.gameserver.listener.script.OnLoadScriptListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Scripts {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scripts.class);
    private static final String INNER_CLASS_SEPARATOR = "$";

    private static final Scripts INSTANCE = new Scripts();

    private final Map<String, Class<?>> _classes = new TreeMap<>();
    private final ScriptListenerImpl _listeners = new ScriptListenerImpl();

    private Scripts() {
        load();
    }

    public static Scripts getInstance() {
        return INSTANCE;
    }

    /**
     * Loads all scripts in data/scripts and lib/scripts.jar. Does not trigger objects and handlers.
     *
     */
    private void load() {
        File f = new File("./lib/scripts.jar");

        if(f.exists()) {
            LOGGER.info("Loading Script library...");
            loadScriptsFromJar(f);
        }

        LOGGER.info("Loading Scripts...");
        List<Class<?>> classes = loadScriptsFromFile(new File(Config.DATAPACK_ROOT, "data/scripts"));

        if(classes.isEmpty() && _classes.isEmpty()) {
            LOGGER.warn("No Scripts loaded");
            return;
        }

        for(Class<?> clazz : classes) {
            _classes.put(clazz.getName(), clazz);
        }

        LOGGER.info("Scripts: Loaded {} classes.", _classes.size());

        _listeners.load();
    }

    private void loadScriptsFromJar(File f) {
        try(JarInputStream stream = new JarInputStream(new FileInputStream(f))) {
            JarEntry entry ;
            while(nonNull((entry = stream.getNextJarEntry()))) {
                if(entry.getName().contains(INNER_CLASS_SEPARATOR) || !entry.getName().endsWith(".class")) {
                    continue;
                }

                String name = entry.getName().replace(".class", "").replace("/", ".");

                Class<?> clazz = getClass().getClassLoader().loadClass(name);
                if(Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                _classes.put(clazz.getName(), clazz);
                try {
                    processOnLoadScriptClass(clazz);
                } catch (Exception e) {
                    LOGGER.error("Can't load script class: " + name, e);
                }

            }
        } catch(Exception e) {
            LOGGER.error("Failed loading scripts library!", e);
        }
    }

    private void processOnLoadScriptClass(Class<?> clazz) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        if(OnLoadScriptListener.class.isAssignableFrom(clazz)) {
            if(OnInitScriptListener.class.isAssignableFrom(clazz)) {
                LOGGER.warn("Scripts: Error in class: {}. Can not use OnLoad and OnInit listeners together!", clazz.getName());
                return;
            }
            _listeners.add((OnLoadScriptListener) clazz.getDeclaredConstructor().newInstance());
        }
    }

    public List<Class<?>> loadScriptsFromFile(File target) {
        List<Class<?>> classes = new ArrayList<>();
        Compiler compiler = new Compiler();

        try {
            var classDir = Path.of("compiledScript");
            if(compiler.compile(target.toPath(), classDir,
                    "--module-path", System.getProperty("jdk.module.path"), "--module-source-path", target.getAbsolutePath())) {

                Configuration configuration = ModuleLayer.boot().configuration().resolve(ModuleFinder.of(classDir), ModuleFinder.of(), Set.of("org.l2j.scripts"));
                ModuleLayer layer = ModuleLayer.boot().defineModulesWithOneLoader(configuration, ClassLoader.getSystemClassLoader());
                ClassLoader loader = layer.findLoader("org.l2j.scripts");

                if(isNull(loader)) {
                    return classes;
                }

                for(String name : compiler.getLoadedClasses()) {

                    if(name.contains(INNER_CLASS_SEPARATOR) || name.equalsIgnoreCase("module-info")) {
                        continue;
                    }

                    try {
                        Class<?> clazz = loader.loadClass(name);
                        if(Modifier.isAbstract(clazz.getModifiers())) {
                            continue;
                        }

                        classes.add(clazz);
                        processOnLoadScriptClass(clazz);
                    }
                    catch(Exception e) {
                        LOGGER.error("Can't load script class: " + name, e);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return classes;
    }

    public void init() {
        for (Class<?> clazz : _classes.values()) {
            try {
                processBypassClass(clazz);

                if(OnLoadScriptListener.class.isAssignableFrom(clazz)) {
                    continue;
                }

                if(OnInitScriptListener.class.isAssignableFrom(clazz)) {
                    _listeners.add((OnInitScriptListener) clazz.getDeclaredConstructor().newInstance());
                }

            } catch (Exception e) {
                LOGGER.error("Can't init script class: " + clazz.getName(), e);
            }
        }
        _listeners.init();
    }

    private void processBypassClass(Class<?> clazz) throws Exception {
        for(Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Bypass.class)) {
                Bypass bypass = method.getAnnotation(Bypass.class);
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length == 0 || parameters[0] != Player.class || parameters[1] != NpcInstance.class || parameters[2] != String[].class) {
                    LOGGER.error("Wrong parameters for bypass method: {}, class: {}", method.getName(), clazz.getSimpleName());
                    continue;
                }
                BypassHolder.getInstance().registerBypass(bypass.value(), clazz.getDeclaredConstructor().newInstance(), method);
            }
        }
    }

    public Map<String, Class<?>> getClasses() {
        return _classes;
    }

    public class ScriptListenerImpl extends ListenerList<Scripts> {
        public void load() {
            listeners.stream().filter(l -> l instanceof OnLoadScriptListener).forEach(l -> ((OnLoadScriptListener)l).onLoad());
        }

        public void init() {
            listeners.stream().filter(l -> l instanceof OnInitScriptListener).forEach(l -> ((OnInitScriptListener)l).onInit());
        }
    }
}