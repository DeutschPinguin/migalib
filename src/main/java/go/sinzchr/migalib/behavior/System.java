package go.sinzchr.migalib.behavior;

import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.event.Event;
import go.sinzchr.migalib.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;


public class System
        implements Listener
{
        
        protected final Map<Event<?>, Set<Consumer<Context<Object>>>> MAP = new HashMap<>();
        
        
        @Override
        public <C> void emit (@NotNull Context<C> context)
        {
                var set = MAP.get(context.event);
                if (set == null || set.isEmpty()) return;
                set.forEach(consumer -> consumer.accept((Context) context));
        }
        
        
        public <C> System add (
                @NotNull Event<C> event,
                @NotNull Consumer<@NotNull Context<C>> consumer
        )
        {
                if (!MAP.containsKey(event)) MAP.put(event, new HashSet<>());
                MAP.get(event).add((Consumer) consumer);
                return this;
        }
        
        
        public <C> System remove (
                @NotNull Event<C> event,
                @NotNull Consumer<@NotNull Context<C>> consumer
        )
        {
                if (MAP.containsKey(event)) MAP.get(event).remove(consumer);
                return this;
        }
        
        
        @SafeVarargs
        public final <C> System add (
                @NotNull Event<C> event,
                @NotNull Consumer<@NotNull Context<C>>... consumers
        )
        {
                for (var consumer : consumers) add(event, consumer);
                return this;
        }
        
        
        @SafeVarargs
        public final <C> System remove (
                @NotNull Event<C> event,
                @NotNull Consumer<@NotNull Context<C>>... consumers
        )
        {
                for (var consumer : consumers) remove(event, consumer);
                return this;
        }
        
        
        public final <C> System add (
                @NotNull Event<C> event,
                @NotNull Collection<@NotNull Consumer<@NotNull Context<C>>> consumers
        )
        {
                for (var consumer : consumers) add(event, consumer);
                return this;
        }
        
        
        public final <C> System remove (
                @NotNull Event<C> event,
                @NotNull Collection<@NotNull Consumer<@NotNull Context<C>>> consumers
        )
        {
                for (var consumer : consumers) remove(event, consumer);
                return this;
        }
        
        
        public <C> void clear (@NotNull Event<C> event)
        {
                if (MAP.containsKey(event)) MAP.get(event).clear();
        }
        
        
        public void clearAll ()
        {
                MAP.clear();
        }
        
}
