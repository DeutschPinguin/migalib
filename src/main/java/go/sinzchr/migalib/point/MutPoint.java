package go.sinzchr.migalib.point;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


public class MutPoint
        extends Point
{
        
        
        public MutPoint (@NotNull Identifier id, @NotNull Identifier world, double x, double y, double z, float pitch, float yaw)
        {
                super(id, world, x, y, z, pitch, yaw);
        }
        
        
        public MutPoint (@NotNull Identifier id, @NotNull Identifier world)
        {
                super(id, world);
        }
        
        
        public void tag (@NotNull Identifier id)
        {
                TAGS.add(id);
        }
        
        
        public void tag (@NotNull Identifier... ids)
        {
                for (var id : ids) tag(id);
        }
        
        
        public void tag (@NotNull Collection<@NotNull Identifier> ids)
        {
                for (var id : ids) tag(id);
        }
        
        
        public void untag (@NotNull Identifier id)
        {
                TAGS.remove(id);
        }
        
        
        public void untag (@NotNull Identifier... ids)
        {
                for (var id : ids) untag(id);
        }
        
        
        public void untag (@NotNull Collection<@NotNull Identifier> ids)
        {
                for (var id : ids) untag(id);
        }
        
        
        public void clearAllTags ()
        {
                TAGS.clear();
        }
        
        
        @Override
        public @Nullable NbtCompound metadata (@NotNull Identifier modId)
        {
                return METADATA.get(modId);
        }
        
        
        public void world (@NotNull Identifier id)
        {
                WORLD = id;
        }
        
        
        public void world (@NotNull RegistryKey<World> key)
        {
                world(key.getValue());
        }
        
        
        public void world (@NotNull World world)
        {
                world(world.getRegistryKey());
        }
        
        
        public void position (double x, double y, double z)
        {
                this.x = x;
                this.y = y;
                this.z = z;
        }
        
        
        public void x (double v)
        {
                x = v;
        }
        
        
        public void y (double v)
        {
                y = v;
        }
        
        
        public void z (double v)
        {
                z = v;
        }
        
        
        public void rotation (float pitch, float yaw)
        {
                this.pitch = pitch;
                this.yaw = yaw;
        }
        
        
        public void pitch (float v)
        {
                pitch = v;
        }
        
        
        public void yaw (float v)
        {
                yaw = v;
        }
        
}
