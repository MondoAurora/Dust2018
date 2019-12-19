package dust.utils;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class DustUtilsShutdownManager {
    public static interface ShutdownAware {
        void shutdown() throws Exception;
    }

    public static class ShutdownWrapper {
        public final String name;
        public final WeakReference<ShutdownAware> ref;

        public ShutdownWrapper(String name, ShutdownAware target) {
            this.name = name;
            ref = new WeakReference<ShutdownAware>(target);
        }

        @Override
        public String toString() {
            return name;
        }

        public void shutdown() {
            ShutdownAware c = ref.get();
            if (null != c) {
                try {
                    DustUtilsDev.dump("Shutting down ", c, "...");
                    c.shutdown();
                    DustUtilsDev.dump(c, "was shut down");
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static Set<ShutdownWrapper> TO_SHUT_DOWN = new HashSet<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                for (ShutdownWrapper sw : TO_SHUT_DOWN) {
                    sw.shutdown();
                }
            }
        });
    }

    public static void add(String name, ShutdownAware target) {
        TO_SHUT_DOWN.add(new ShutdownWrapper(name, target));
    }
}
