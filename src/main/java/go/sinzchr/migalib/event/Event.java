package go.sinzchr.migalib.event;

import org.jetbrains.annotations.NotNull;


public class Event<C>
{
        
        protected final @NotNull String ID;
        protected final int hash;
        
        
        public Event (@NotNull String id)
        {
                ID = id;
                hash = ID.hashCode();
        }
        
        
        public @NotNull String getId ()
        {
                return ID;
        }
        
        
        @Override
        public int hashCode ()
        {
                return hash;
        }
        
        
        @Override
        public boolean equals (Object obj)
        {
                return obj instanceof Event<?> other && other.hash == hash;
        }
        
        
        @Override
        public String toString ()
        {
                return "Event[" + ID + "]";
        }
        
}
