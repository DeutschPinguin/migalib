package go.sinzchr.migalib;

import go.sinzchr.migalib.command.PointsEditorCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;


public final class MigaLibCommands
{
        
        private MigaLibCommands () {}
        
        
        
        static void init ()
        {
                CommandRegistrationCallback.EVENT.register((dispatcher,
                         access, env) ->
                {
                        PointsEditorCommand.registerBoth(dispatcher);
                });
                
                ServerLifecycleEvents.SERVER_STOPPING.register(server -> PointsEditorCommand.unselectContainersForAllPlayers());
                ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> PointsEditorCommand.unselectContainer(handler.getPlayer().getUuid()));
        }
        
}
