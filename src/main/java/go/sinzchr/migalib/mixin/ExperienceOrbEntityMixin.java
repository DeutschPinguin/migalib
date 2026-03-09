package go.sinzchr.migalib.mixin;

import go.sinzchr.migalib.mixinshelper.DamageEventMixinHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin
        extends Entity
{
        
        public ExperienceOrbEntityMixin (EntityType<?> type, World world)
        {
                super(type, world);
        }
        
        
        private final DamageEventMixinHelper damageEventMixinHelper = new DamageEventMixinHelper(this);
        
        
        @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
        private void damageEventInjection (DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
        {
                damageEventMixinHelper.damage(source, amount, cir);
        }
        
}
