package go.sinzchr.migalib.event;

import org.jetbrains.annotations.NotNull;


public interface Listener
{
        
        <C> void emit (@NotNull Context<C> context);
        
}
