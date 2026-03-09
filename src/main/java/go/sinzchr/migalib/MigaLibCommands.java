package go.sinzchr.migalib;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;


public final class MigaLibCommands
{
        
        private MigaLibCommands () {}
        
        static void init ()
        {
                CommandRegistrationCallback.EVENT.register((dispatcher,
                         access, env) ->
                {
                        dispatcher.register(CommandManager.literal(""));
                });
        }
        
}
