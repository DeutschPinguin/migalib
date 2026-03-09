package go.sinzchr.migalib.mixin;

import go.sinzchr.migalib.mixinshelper.DamageEventMixinHelper;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public abstract class EntityMixin
        implements Nameable, EntityLike, CommandOutput, AttachmentTarget
{
        
        private final DamageEventMixinHelper damageEventMixinHelper = new DamageEventMixinHelper((Entity) (Object) this);
        
        
        @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
        private void damageEventInjection (DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
        {
                damageEventMixinHelper.damage(source, amount, cir);
        }
        
}
