package go.sinzchr.migalib.event.listener;

import go.sinzchr.migalib.MigaLib;
import go.sinzchr.migalib.MigaLibEvents;
import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.misc.Cancellable;
import go.sinzchr.migalib.misc.Priority;
import go.sinzchr.migalib.resource.DataProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class EventBus
        implements Listener
{
        
        protected final Map<Listener, Priority> PRIORITIES = new HashMap<>();
        protected final Map<Priority, List<Listener>> LISTENERS = new HashMap<>();
        
        public final @NotNull DataProvider data;
        public boolean stopped = false;
        
        
        public EventBus (@NotNull DataProvider data)
        {
                this.data = data;
                for (var priority : Priority.ALL_SORTED) LISTENERS.put(priority, new ArrayList<>());
        }
        
        
        @Override
        public <C> void emit (@NotNull Context<C> ctx)
        {
                for (var priority : Priority.ALL_SORTED)
                {
                        var iter = LISTENERS.get(priority).iterator();
                        
                        while (iter.hasNext())
                        {
                                var listener = iter.next();
                                
                                ctx.removeListener = false;
                                ctx.stopSession = false;
                                
                                try
                                {
                                        listener.emit(ctx);
                                }
                                catch (Exception e)
                                {
                                        MigaLib.LOGGER.error("Removed listener from session due to unexpected error when {} occured", ctx.event);
                                        MigaLib.LOGGER.error(e.getMessage(), e);
                                        iter.remove();
                                        return;
                                }
                                
                                if (ctx.stopSession) stopped = true;
                                if (ctx.removeListener) iter.remove();
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
        
        
        public void setPriority (@NotNull Priority priority, @NotNull Listener listener)
        {
                var prev = getPriority(listener);
                if (prev == null || prev == priority) return;
                
                LISTENERS.get(prev).remove(listener);
                
                PRIORITIES.put(listener, priority);
                LISTENERS.get(priority).add(listener);
        }
        
        
        public void setPriority (@NotNull Priority priority, @NotNull Listener... listeners)
        {
                for (var listener : listeners) setPriority(priority, listener);
        }
        
        
        public void setPriority (@NotNull Priority priority, @NotNull Collection<@NotNull Listener> listeners)
        {
                for (var listener : listeners) setPriority(priority, listener);
        }
        
        
        public @NotNull EventBus add (@NotNull Priority priority, @NotNull Listener listener)
        {
                if (has(listener)) return this;
                PRIORITIES.put(listener, priority);
                LISTENERS.get(priority).add(listener);
                listener.emit(new Context<>(MigaLibEvents.LISTENER_ADDED, data, null));
                return this;
        }
        
        
        public @NotNull EventBus add (@NotNull Listener listener)
        {
                return add(Priority.MONITOR, listener);
        }
        
        
        
        public @NotNull EventBus remove (@NotNull Listener listener)
        {
                var priority = getPriority(listener);
                if (priority == null) return this;
                PRIORITIES.remove(listener);
                LISTENERS.get(priority).remove(listener);
                listener.emit(new Context<>(MigaLibEvents.LISTENER_REMOVED, data, null));
                return this;
        }
        
        
        public @NotNull EventBus add (@NotNull Priority priority, @NotNull Listener... listeners)
        {
                for (var listener : listeners) add(priority, listener);
                return this;
        }
        
        
        public @NotNull EventBus add (@NotNull Listener... listeners)
        {
                return add(Priority.MONITOR, listeners);
        }
        
        
        public @NotNull EventBus remove (@NotNull Listener... listeners)
        {
                for (var listener : listeners) remove(listener);
                return this;
        }
        
        
        public @NotNull EventBus add (@NotNull Priority priority, @NotNull Collection<@NotNull Listener> listeners)
        {
                for (var listener : listeners) add(priority, listener);
                return this;
        }
        
        
        public @NotNull EventBus add (@NotNull Collection<@NotNull Listener> listeners)
        {
                return add(Priority.MONITOR, listeners);
        }
        
        
        public @NotNull EventBus remove (@NotNull Collection<@NotNull Listener> listeners)
        {
                for (var listener : listeners) remove(listener);
                return this;
        }
        
        
        public void clearAll ()
        {
                PRIORITIES.clear();
                LISTENERS.values().forEach(List::clear);
        }
        
}
