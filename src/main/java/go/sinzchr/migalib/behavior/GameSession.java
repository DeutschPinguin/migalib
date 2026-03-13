package go.sinzchr.migalib.behavior;

import go.sinzchr.migalib.MigaLibEvents;
import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.event.Event;
import go.sinzchr.migalib.event.Listener;
import go.sinzchr.migalib.resource.DataContainer;
import org.jetbrains.annotations.NotNull;


public class GameSession
        implements Listener
{
        
        public final @NotNull DataContainer dataContainer = new DataContainer();
        public final @NotNull EventBus eventBus = new EventBus(dataContainer);
        
        public boolean stopped = false;
        
        
        public void start ()
        {
                stopped = false;
                emit(MigaLibEvents.SESSION_START, null);
        }
        
        
        public void stop ()
        {
                stopped = true;
                emit(MigaLibEvents.SESSION_STOP, null);
        }
        
        
        @Override
        public <C> void emit (@NotNull Context<C> context)
        {
                eventBus.emit(context);
                if (eventBus.stopped) stopped = true;
        }
        
        
        public <C> void emit (@NotNull Event<C> event, C callback)
        {
                emit(new Context<C>(event, dataContainer, callback));
        }
        
}
