package go.sinzchr.migalib.event.callback;


import go.sinzchr.migalib.misc.Cancellable;


public class CancellableCallback
        implements Cancellable
{
        
        protected boolean canceled = false;
        
        
        @Override
        public boolean isCancelled ()
        {
                return canceled;
        }
        
        
        @Override
        public void setCancelled (boolean value)
        {
                canceled = value;
        }
        
}
