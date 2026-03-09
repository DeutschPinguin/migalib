package go.sinzchr.migalib.resource;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public interface DataProvider
{
        
        <T> boolean has (@NotNull Resource<T> resource);
        
        
        <T> T get (@NotNull Resource<T> resource);
        
        
        <T> void set (@NotNull Resource<T> resource, T data);
        
        
        <T> void remove (@NotNull Resource<T> resource);
        
        
        default <T> void consume (
                @NotNull Resource<T> resource,
                @NotNull Consumer<T> valueConsumer,
                @NotNull Supplier<T> fallbackGetter
        )
        {
                if (!has(resource)) set(resource, fallbackGetter.get());
                valueConsumer.accept(get(resource));
        }
        
        
        default <T> void convert (
                @NotNull Resource<T> resource,
                @NotNull Function<T, T> valueFunction,
                @NotNull Supplier<T> fallbackGetter
        )
        {
                if (!has(resource)) set(resource, fallbackGetter.get());
                var result = valueFunction.apply(get(resource));
                set(resource, result);
        }
        
        
        default <T> void consumeOrDefault (
                @NotNull Resource<T> resource,
                @NotNull Consumer<T> valueConsumer
        )
        {
                consume(resource, valueConsumer, resource::getDefaultValue);
        }
        
        
        default <T> void convertOrDefault (
                @NotNull Resource<T> resource,
                @NotNull Function<T, T> valueFunction
        )
        {
                convert(resource, valueFunction, resource::getDefaultValue);
        }
        
        
        default <T> void consumeOrThrow (
                @NotNull Resource<T> resource,
                @NotNull Consumer<T> valueConsumer
        ) throws IllegalStateException
        {
                if (!has(resource)) throw new IllegalStateException("Cannot find: " + resource);
                consume(resource, valueConsumer, () -> null);
        }
        
        
        default <T> void convertOrThrow (
                @NotNull Resource<T> resource,
                @NotNull Function<T, T> valueFunction
        ) throws IllegalStateException
        {
                if (!has(resource)) throw new IllegalStateException("Cannot find: " + resource);
                convert(resource, valueFunction, () -> null);
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
                return getOr(resource, (Supplier<T>) resource::getDefaultValue);
        }
        
        
        default <T> T getOrThrow (@NotNull Resource<T> resource)
                throws IllegalStateException
        {
                if (has(resource)) return get(resource);
                throw new IllegalStateException("Cannot find: " + resource);
        }
        
        
        default <T> void setToDefault (@NotNull Resource<T> resource)
        {
                set(resource, resource.getDefaultValue());
        }
        
        
        default <T> void setToFallbackIfHas (@NotNull Resource<T> resource)
        {
                if (has(resource)) set(resource, resource.getDefaultValue());
        }
        
        
        default <T> void setToFallbackUnlessHas (@NotNull Resource<T> resource)
        {
                if (!has(resource)) set(resource, resource.getDefaultValue());
        }
        
}
