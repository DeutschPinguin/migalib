package go.sinzchr.migalib.resource;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public class Resource<T>
{
        
        protected final @NotNull String ID;
        protected final int hash;
        protected final @NotNull Supplier<T> DEFAULT;
        
        
        public Resource (@NotNull String id, @NotNull Supplier<T> defaultValue)
        {
                ID = id;
                hash = ID.hashCode();
                DEFAULT = defaultValue;
        }
        
        
        public @NotNull String getIdentifier ()
        {
                return ID;
        }
        
        
        public T getDefaultValue ()
        {
                return DEFAULT.get();
        }
        
        
        @Override
        public int hashCode ()
        {
                return hash;
        }
        
        
        @Override
        public boolean equals (Object obj)
        {
                return obj instanceof Resource<?> other && other.hash == hash;
        }
        
        
        @Override
        public String toString ()
        {
                return "Resource[" + ID + "]";
        }
        
}
