package go.sinzchr.migalib.misc;

public interface Cancellable
{
        
        boolean isCancelled ();
        
        
        void setCancelled (boolean value);
        
        
        default void cancel ()
        {
                setCancelled(true);
        }
        
}
