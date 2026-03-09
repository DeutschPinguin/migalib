package go.sinzchr.migalib;

import go.sinzchr.migalib.behavior.GameSession;
import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;


public final class MigaLibSessions
{
        
        private static final Map<GameSession, BiPredicate<Event<?>, Object>> SESSIONS = new HashMap<>();
        
        
        private MigaLibSessions () {}
        
        
        public static <C> void emit (@NotNull Context<C> ctx)
        {
                SESSIONS.forEach((session, predicate) -> {
                        if (predicate.test(ctx.event, ctx.callback)) session.emit(ctx);
                });
        }
        
        
        public static <C> void emit (@NotNull Event<C> event, C callback)
        {
                SESSIONS.forEach((session, predicate) -> {
                        if (predicate.test(event, callback)) session.emit(event, callback);
                });
        }
        
        
        public static boolean has (@NotNull GameSession session)
        {
                return SESSIONS.containsKey(session);
        }
        
        
        public static <C> @NotNull GameSession add (
                @NotNull BiPredicate<@NotNull Event<C>, C> shouldHandleEvent,
                @NotNull GameSession session
        )
        {
                SESSIONS.putIfAbsent(session, (BiPredicate) shouldHandleEvent);
                return session;
        }
        
        
        public static @NotNull GameSession add (@NotNull GameSession session)
        {
                return add((event, callback) -> true, session);
        }
        
        
        public static void remove (@NotNull GameSession session)
        {
                SESSIONS.remove(session);
        }
        
        
        public static <C> @NotNull GameSession[] add (
                @NotNull BiPredicate<@NotNull Event<C>, C> shouldHandleEvent,
                @NotNull GameSession... sessions
        )
        {
                for (var session : sessions) add(session);
                return sessions;
        }
        
        
        public static @NotNull GameSession[] add (@NotNull GameSession... sessions)
        {
                return add((event, callback) -> true, sessions);
        }
        
        
        public static void remove (@NotNull GameSession... sessions)
        {
                for (var session : sessions) remove(session);
        }
        
        
        public static <C> @NotNull Collection<@NotNull GameSession> add (
                @NotNull BiPredicate<@NotNull Event<C>, C> shouldHandleEvent,
                @NotNull Collection<@NotNull GameSession> sessions
        )
        {
                for (var session : sessions) add(session);
                return sessions;
        }
        
        
        public static @NotNull Collection<@NotNull GameSession> add (
                @NotNull Collection<@NotNull GameSession> sessions
        )
        {
                return add((event, callback) -> true, sessions);
        }
        
        
        public static void remove (@NotNull Collection<@NotNull GameSession> sessions)
        {
                for (var session : sessions) remove(session);
        }
        
        
        public static void clearAll ()
        {
                SESSIONS.clear();
        }
        
        
        static void init ()
        {
                ServerLifecycleEvents.SERVER_STOPPING.register(server -> clearAll());
        }
        
}
