package go.sinzchr.migalib;

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
        
        }
        
        
        public static @NotNull Identifier id (String path)
        {
                return Objects.requireNonNull(Identifier.of(ID, path));
        }
        
}
