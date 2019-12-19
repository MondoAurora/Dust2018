package dust.utils;

import java.util.HashSet;
import java.util.Set;

public class DustUtilsMuteManager {
    public enum MutableModule {
        GUI
    }
    
    public interface Mutable {
        MutableModule getModule();
        void muteReleased();
    }
    
    public static class MuteInfo {
        boolean mute;
        Set<Mutable> muted = new HashSet<Mutable>();
        
        public MuteInfo() {
        }
        
        public void mute(boolean mute_) {
            if ( mute != mute_) {
                mute = mute_;
                
                if ( !mute ) {
                    for ( Mutable l : muted) {
                        l.muteReleased();
                    }
                    muted.clear();
                }
            }
        }

        public boolean isMuted(Mutable m) {
            if ( mute ) {
                muted.add(m);
                return true;
            } else {
                return false;
            }
        }
    }
    
    private static DustUtilsFactory<MutableModule, MuteInfo> muted = new DustUtilsFactory.Simple<MutableModule, MuteInfo>(true, MuteInfo.class);
    
    public static void mute(MutableModule module, boolean mute_) {
        muted.get(module).mute(mute_);
    }

    public static boolean isMuted(Mutable m) {
        return muted.get(m.getModule()).isMuted(m);
    }
}
