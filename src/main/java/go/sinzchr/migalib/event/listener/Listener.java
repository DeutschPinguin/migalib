package go.sinzchr.migalib.event.listener;

import go.sinzchr.migalib.event.Context;
import org.jetbrains.annotations.NotNull;


public interface Listener
{
        
        <C> void emit (@NotNull Context<C> context);
        
}
