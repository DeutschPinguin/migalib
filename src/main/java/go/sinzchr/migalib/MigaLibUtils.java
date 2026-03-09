package go.sinzchr.migalib;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;


public final class MigaLibUtils
{
        
        private MigaLibUtils () {}
        
        
        public static @NotNull RegistryEntry<DamageType> entryOf (@NotNull World world, @NotNull RegistryKey<DamageType> key)
        {
                return world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key);
        }
        
        
        static void init () {}
        
}
