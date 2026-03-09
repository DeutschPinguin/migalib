package go.sinzchr.migalib;

import go.sinzchr.migalib.event.Event;
import go.sinzchr.migalib.event.callback.DamageCallback;


public final class MigaLibEvents
{
        
        private MigaLibEvents () {}
        
        
        public static final Event<DamageCallback> ENTITY_DAMAGE = new Event<>(MigaLib.id("entity_damage"));
        
        
        static void init () {}
        
}
