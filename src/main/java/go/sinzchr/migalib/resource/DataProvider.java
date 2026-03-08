package go.sinzchr.migalib.resource;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public interface DataProvider
{
        
        <T> boolean has (@NotNull Resource<T> resource);
        
        
        <T> T get (@NotNull Resource<T> resource);
        
        
        <T> void set (@NotNull Resource<T> resource, T data);
        
        
        <T> void remove (@NotNull Resource<T> resource);
        
        
        default <T> void resetToFallback (@NotNull Resource<T> resource)
        {
                set(resource, resource.getFallback());
        }
        
        
        default <T> T getOr (@NotNull Resource<T> resource, T fallback)
        {
                return has(resource) ? get(resource) : fallback;
        }
        
        
        default <T> T getOr (@NotNull Resource<T> resource, @NotNull Supplier<T> fallback)
        {
                return has(resource) ? get(resource) : fallback.get();
        }
        
        
        default <T> T getOrDefault (@NotNull Resource<T> resource)
        {
                return getOr(resource, (Supplier<T>) resource::getFallback);
        }
        
        
        default <T> T getOrThrow (@NotNull Resource<T> resource)
                throws IllegalStateException
        {
                if (has(resource)) return get(resource);
                throw new IllegalStateException("Cannot find: " + resource);
        }
        
}
