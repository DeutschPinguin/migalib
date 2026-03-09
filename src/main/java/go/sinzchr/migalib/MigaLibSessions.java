package go.sinzchr.migalib;

import go.sinzchr.migalib.behavior.GameSession;
import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;


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
        
        
        public static <C> @NotNull GameSession subscribe (
                @NotNull BiPredicate<@NotNull Event<C>, C> shouldHandleEvent,
                @NotNull GameSession session
        )
        {
                SESSIONS.putIfAbsent(session, (BiPredicate) shouldHandleEvent);
                return session;
        }
        
        
        public static @NotNull GameSession subscribe (@NotNull GameSession session)
        {
                return subscribe((event, callback) -> true, session);
        }
        
        
        public static void unsubscribe (@NotNull GameSession session)
        {
                SESSIONS.remove(session);
        }
        
        
        public static <C> @NotNull GameSession[] subscribe (
                @NotNull BiPredicate<@NotNull Event<C>, C> shouldHandleEvent,
                @NotNull GameSession... sessions
        )
        {
                for (var session : sessions) subscribe(session);
                return sessions;
        }
        
        
        public static @NotNull GameSession[] subscribe (@NotNull GameSession... sessions)
        {
                return subscribe((event, callback) -> true, sessions);
        }
        
        
        public static void unsubscribe (@NotNull GameSession... sessions)
        {
                for (var session : sessions) unsubscribe(session);
        }
        
        
        public static <C> @NotNull Collection<@NotNull GameSession> subscribe (
                @NotNull BiPredicate<@NotNull Event<C>, C> shouldHandleEvent,
                @NotNull Collection<@NotNull GameSession> sessions
        )
        {
                for (var session : sessions) subscribe(session);
                return sessions;
        }
        
        
        public static @NotNull Collection<@NotNull GameSession> subscribe (
                @NotNull Collection<@NotNull GameSession> sessions
        )
        {
                return subscribe((event, callback) -> true, sessions);
        }
        
        
        public static void unsubscribe (@NotNull Collection<@NotNull GameSession> sessions)
        {
                for (var session : sessions) unsubscribe(session);
        }
        
        
        static void init () {}
        
}
