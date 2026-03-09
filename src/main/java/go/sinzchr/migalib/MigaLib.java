package go.sinzchr.migalib;

import go.sinzchr.migalib.behavior.GameSession;
import go.sinzchr.migalib.behavior.System;
import go.sinzchr.migalib.misc.Priority;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class MigaLib
        implements ModInitializer
{
        
        public static final String ID = "migalib", NAME = "MIGA Library";
        public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
        
        
        @Override
        public void onInitialize ()
        {
                MigaLibEvents.init();
                MigaLibResources.init();
                MigaLibSessions.init();
                MigaLibUtils.init();
                
                var session = new GameSession();
                var modifierSystem = new System()
                        .add(MigaLibEvents.ENTITY_DAMAGE, ctx -> {
                                if (ctx.callback.damageAmount < 4f) ctx.callback.cancel();
                        });
                var loggerSystem = new System()
                        .add(MigaLibEvents.ENTITY_DAMAGE, ctx -> {
                                var attackerString = ctx.callback.attacker == null
                                        ? ""
                                        : " by [" + ctx.callback.attacker.getName().getString() + "]";
                                LOGGER.info("Entity [{}] got damage [{}]{}",
                                        ctx.callback.victim.getName().getString(),
                                        ctx.callback.damageAmount,
                                        attackerString
                                );
                        });
                session.eventBus.register(Priority.NORMAL, modifierSystem).register(loggerSystem);
                MigaLibSessions.subscribe(session);
        }
        
        
        public static @NotNull String id (@NotNull String path)
        {
                return ID + ":" + path;
        }
        
        
        public static @NotNull Identifier identifier (@NotNull String path)
        {
                return Objects.requireNonNull(Identifier.of(ID, path));
        }
        
}
