package go.sinzchr.migalib.behavior;

import go.sinzchr.migalib.MigaLib;
import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.misc.Cancellable;
import go.sinzchr.migalib.event.Listener;
import go.sinzchr.migalib.misc.Priority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class EventBus
        implements Listener
{
        
        protected final Map<Listener, Priority> PRIORITIES = new HashMap<>();
        protected final Map<Priority, List<Listener>> LISTENERS = new HashMap<>();
        
        
        public EventBus ()
        {
                for (var priority : Priority.ALL_SORTED) LISTENERS.put(priority, new ArrayList<>());
        }
        
        
        @Override
        public <C> void emit (@NotNull Context<C> ctx)
        {
                for (var priority : Priority.ALL_SORTED)
                {
                        for (var listener : LISTENERS.get(priority))
                        {
                                try {
                                        listener.emit(ctx);
                                } catch (Exception e)
                                {
                                        MigaLib.LOGGER.error("Listener caught unexpected error when {} was emitted", ctx.event);
                                        MigaLib.LOGGER.error(e.getMessage(), e);
                                }
                        }
                        
                        if (ctx.callback instanceof Cancellable cb && cb.isCancelled()) break;
                }
        }
        
        
        public boolean has (@NotNull Listener listener)
        {
                return PRIORITIES.containsKey(listener);
        }
        
        
        public @Nullable Priority getPriority (@NotNull Listener listener)
        {
                return PRIORITIES.get(listener);
        }
        
        
        public @NotNull EventBus register (@NotNull Priority priority, @NotNull Listener listener)
        {
                if (has(listener)) return this;
                PRIORITIES.put(listener, priority);
                LISTENERS.get(priority).add(listener);
                return this;
        }
        
        
        public @NotNull EventBus register (@NotNull Listener listener)
        {
                return register(Priority.MONITOR, listener);
        }
        
        
        
        public @NotNull EventBus remove (@NotNull Listener listener)
        {
                var priority = getPriority(listener);
                if (priority == null) return this;
                PRIORITIES.remove(listener);
                LISTENERS.get(priority).remove(listener);
                return this;
        }
        
        
        public @NotNull EventBus register (@NotNull Priority priority, @NotNull Listener... listeners)
        {
                for (var listener : listeners) register(priority, listener);
                return this;
        }
        
        
        public @NotNull EventBus register (@NotNull Listener... listeners)
        {
                return register(Priority.MONITOR, listeners);
        }
        
        
        public @NotNull EventBus remove (@NotNull Listener... listeners)
        {
                for (var listener : listeners) remove(listener);
                return this;
        }
        
        
        public @NotNull EventBus register (@NotNull Priority priority, @NotNull Collection<@NotNull Listener> listeners)
        {
                for (var listener : listeners) register(priority, listener);
                return this;
        }
        
        
        public @NotNull EventBus register (@NotNull Collection<@NotNull Listener> listeners)
        {
                return register(Priority.MONITOR, listeners);
        }
        
        
        public @NotNull EventBus remove (@NotNull Collection<@NotNull Listener> listeners)
        {
                for (var listener : listeners) remove(listener);
                return this;
        }
        
        
        public void clear ()
        {
                PRIORITIES.clear();
                LISTENERS.values().forEach(List::clear);
        }
        
}
