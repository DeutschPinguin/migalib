package go.sinzchr.migalib;

import go.sinzchr.migalib.command.PointsEditorCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;


public final class MigaLibCommands
{
        
        private MigaLibCommands () {}
        
        
        
        static void init ()
        {
                CommandRegistrationCallback.EVENT.register((dispatcher,
                         access, env) ->
                {
                        dispatcher.register(PointsEditorCommand.create("migalib:points")
                                .requires(source -> source.hasPermissionLevel(4))
                        );
                });
        }
        
}
