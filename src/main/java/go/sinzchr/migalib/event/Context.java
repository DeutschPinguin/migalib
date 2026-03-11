package go.sinzchr.migalib.event;

import go.sinzchr.migalib.resource.DataProvider;
import org.jetbrains.annotations.NotNull;


public class Context<C>
{
        
        public final @NotNull Event<C> event;
        public final @NotNull DataProvider data;
        public final C callback;
        
        public boolean removeListener = false, stopSession = false;
        
        
        public Context (@NotNull Event<C> event, @NotNull DataProvider data, C callback)
        {
                this.event = event;
                this.data = data;
                this.callback = callback;
        }
        
}
