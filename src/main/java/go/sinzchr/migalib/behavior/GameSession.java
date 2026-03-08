package go.sinzchr.migalib.behavior;

import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.event.Event;
import go.sinzchr.migalib.event.Listener;
import go.sinzchr.migalib.resource.DataContainer;
import org.jetbrains.annotations.NotNull;


public class GameSession
        implements Listener
{
        
        public final @NotNull EventBus eventBus = new EventBus();
        public final @NotNull DataContainer dataContainer = new DataContainer();
        
        
        @Override
        public <C> void emit (@NotNull Context<C> context)
        {
                eventBus.emit(context);
        }
        
        
        public <C> void emit (@NotNull Event<C> event, C callback)
        {
                emit(new Context<C>(event, dataContainer, callback));
        }
        
}
