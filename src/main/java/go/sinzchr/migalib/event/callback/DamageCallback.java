package go.sinzchr.migalib.event.callback;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DamageCallback
        extends CancellableCallback
{
        
        public final @NotNull Entity victim;
        public final @NotNull World world;
        
        public @Nullable RegistryEntry<DamageType> damageType;
        public @Nullable Entity attacker;
        public @Nullable Entity directAttacker;
        public @Nullable Vec3d position;
        public float damageAmount;
        
        
        public DamageCallback (
                @NotNull Entity victim,
                @Nullable RegistryEntry<DamageType> damageType,
                @Nullable Entity attacker,
                @Nullable Entity directAttacker,
                @Nullable Vec3d position,
                float damageAmount
        )
        {
                this.victim = victim;
                this.world = this.victim.getWorld();
                this.damageType = damageType;
                this.attacker = attacker;
                this.directAttacker = directAttacker;
                this.position = position;
                this.damageAmount = damageAmount;
        }
        
        
        public DamageCallback (@NotNull Entity victim, float damageAmount)
        {
                this(victim, null, null, null, null, damageAmount);
        }
        
        
        public DamageCallback (@NotNull Entity victim)
        {
                this(victim, 0f);
        }
        
        
        public DamageCallback (@NotNull Entity victim, @NotNull DamageSource source, float damageAmount)
        {
                this(victim, source.getTypeRegistryEntry(), source.getAttacker(), source.getSource(), source.getPosition(), damageAmount);
        }
        
        
        public DamageCallback (@NotNull Entity victim, @NotNull DamageSource source)
        {
                this(victim, source, 0f);
        }
        
        
        public @Nullable Vec3d getAttackPos ()
        {
                if (position != null) return position;
                if (directAttacker != null) return directAttacker.getPos();
                if (attacker != null) return attacker.getPos();
                return null;
        }
        
}
