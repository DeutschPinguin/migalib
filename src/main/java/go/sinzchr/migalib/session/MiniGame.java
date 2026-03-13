package go.sinzchr.migalib.session;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public class MiniGame
{
        
        protected final @NotNull String ID;
        protected final int hash;
        protected final @NotNull Function<@NotNull MinecraftServer, @NotNull GameSession> CREATOR;
        
        
        public MiniGame (@NotNull String id, @NotNull Function<@NotNull MinecraftServer, @NotNull GameSession> creator)
        {
                ID = id;
                hash = ID.hashCode();
                CREATOR = creator;
        }
        
        
        public @NotNull String id ()
        {
                return ID;
        }
        
        
        public @NotNull GameSession create (@NotNull MinecraftServer server)
        {
                return CREATOR.apply(server);
        }
        
        
        @Override
        public int hashCode ()
        {
                return hash;
        }
        
        
        @Override
        public boolean equals (Object obj)
        {
                return obj instanceof MiniGame other && other.hash == hash;
        }
        
        
        @Override
        public String toString ()
        {
                return "MiniGame[" + ID + "]";
        }
        
}
