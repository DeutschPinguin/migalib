package go.sinzchr.migalib.behavior;

import go.sinzchr.migalib.MigaLibEvents;
import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.event.Event;
import go.sinzchr.migalib.event.Listener;
import go.sinzchr.migalib.misc.Status;
import go.sinzchr.migalib.resource.DataContainer;
import org.jetbrains.annotations.NotNull;


public class GameSession
        implements Listener
{
        
        public final @NotNull DataContainer dataContainer = new DataContainer();
        public final @NotNull EventBus eventBus = new EventBus(dataContainer);
        
        /**
         * <p>
         *         Lifetime status for external handling.
         * </p>
         */
        public @NotNull Status status = Status.DEAD;
        
        
        public void start ()
        {
                emit(MigaLibEvents.SESSION_START, null);
        }
        
        
        public void stop ()
        {
                emit(MigaLibEvents.SESSION_STOP, null);
        }
        
        
        @Override
        public <C> void emit (@NotNull Context<C> context)
        {
                eventBus.emit(context);
                if (eventBus.stopped) status = Status.SHUTDOWN;
        }
        
        
        public <C> void emit (@NotNull Event<C> event, C callback)
        {
                emit(new Context<C>(event, dataContainer, callback));
        }
        
}
