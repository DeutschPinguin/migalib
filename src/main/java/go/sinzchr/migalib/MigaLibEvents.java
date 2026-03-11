package go.sinzchr.migalib;

import go.sinzchr.migalib.event.Event;
import go.sinzchr.migalib.event.callback.DamageCallback;


public final class MigaLibEvents
{
        
        private MigaLibEvents () {}
        
        
        public static final Event<Void> SESSION_START = new Event<>(MigaLib.id("session_start"));
        
        
        public static final Event<Void> SESSION_STOP = new Event<>(MigaLib.id("session_stop"));
        
        
        public static final Event<Void> LISTENER_ADDED = new Event<>(MigaLib.id("listener_added"));
        
        
        public static final Event<Void> LISTENER_REMOVED = new Event<>(MigaLib.id("listener_removed"));
        
        
        public static final Event<DamageCallback> ENTITY_DAMAGE = new Event<>(MigaLib.id("entity_damage"));
        
        
        static void init () {}
        
}
