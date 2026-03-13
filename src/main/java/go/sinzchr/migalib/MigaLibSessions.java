package go.sinzchr.migalib;

import go.sinzchr.migalib.behavior.GameSession;
import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.event.Event;
import go.sinzchr.migalib.misc.Status;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;


public final class MigaLibSessions
{
        
        private static final Map<GameSession, BiPredicate<Event<?>, Object>> SESSIONS = new HashMap<>();
        
        
        private MigaLibSessions () {}
        
        
        public static void checkForStopped ()
        {
                var iter = SESSIONS.keySet().iterator();
                
                while (iter.hasNext())
                {
                        var session = iter.next();
                        
                        if (session.status == Status.SHUTDOWN)
                        {
                                MigaLib.LOGGER.info("session is about to stop");
                                session.stop();
                                session.status = Status.DEAD;
                        }
                        
                        if (session.status == Status.DEAD)
                        {
                                MigaLib.LOGGER.info("session is removed");
                                iter.remove();
                        }
                        
                        else
                        {
                                MigaLib.LOGGER.info("session is alive");
                        }
                }
        }
        
        
        public static <C> void start (
                @NotNull BiPredicate<@NotNull Event<C>, C> shouldHandleEvent,
                @NotNull GameSession session
        )
        {
                if (has(session)) return;
                session.status = Status.STARTUP;
                add(session);
                session.start();
                session.status = Status.ALIVE;
        }
        
        
        public static void stop (@NotNull GameSession session)
        {
                if (!has(session)) return;
                session.status = Status.SHUTDOWN;
                remove(session);
                session.stop();
                session.status = Status.DEAD;
        }
        
        
        public static <C> void emit (@NotNull Event<C> event, C callback)
        {
                SESSIONS.forEach((session, predicate) -> {
                        if (!predicate.test(event, callback)) return;
                        session.emit(event, callback);
                });
        }
        
        
        public static <C> void emit (@NotNull Context<C> ctx)
        {
                emit(ctx.event, ctx.callback);
        }
        
        
        public static boolean has (@Nullable GameSession session)
        {
                return session != null && SESSIONS.containsKey(session);
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
                ServerTickEvents.END_SERVER_TICK.register(server -> checkForStopped());
                ServerLifecycleEvents.SERVER_STOPPING.register(server -> clearAll());
        }
        
}
