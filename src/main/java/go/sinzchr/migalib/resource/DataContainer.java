package go.sinzchr.migalib.resource;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class DataContainer
        implements DataProvider
{
        
        protected @NotNull Map<@NotNull Resource<?>, @NotNull Object> RESOURCES = new HashMap<>();
        
        
        public DataContainer ()
        {}
        
        
        @Override
        public <T> boolean has (@NotNull Resource<T> resource)
        {
                return RESOURCES.containsKey(resource);
        }
        
        
        @Override
        public <T> @NotNull T get (@NotNull Resource<T> resource)
        {
                if (!RESOURCES.containsKey(resource)) RESOURCES.put(resource, resource.getFallback());
                return (T) RESOURCES.get(resource);
        }
        
        
        @Override
        public <T> void set (@NotNull Resource<T> resource, @NotNull T value)
        {
                RESOURCES.put(resource, value);
        }
        
        
        @Override
        public <T> void remove (@NotNull Resource<T> resource)
        {
                RESOURCES.remove(resource);
        }
        
}
