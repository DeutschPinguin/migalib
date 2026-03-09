package go.sinzchr.migalib.mixinshelper;

import go.sinzchr.migalib.MigaLibEvents;
import go.sinzchr.migalib.MigaLibSessions;
import go.sinzchr.migalib.MigaLibUtils;
import go.sinzchr.migalib.event.Context;
import go.sinzchr.migalib.event.callback.DamageCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


public class DamageEventMixinHelper
{
        
        protected final @NotNull Entity ENTITY;
        protected boolean damageEventOccured = false;
        
        
        public DamageEventMixinHelper (@NotNull Entity entity)
        {
                ENTITY = entity;
        }
        
        
        public void damage (DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
        {
                if (ENTITY.getWorld().isClient)
                {
                        cir.setReturnValue(false);
                        return;
                }
                
                if (damageEventOccured)
                {
                        damageEventOccured = false;
                        return;
                }
                
                var cb = new DamageCallback(ENTITY, source, amount);
                MigaLibSessions.emit(MigaLibEvents.ENTITY_DAMAGE, cb);
                
                if (cb.isCancelled() || cb.damageAmount <= 0f)
                {
                        damageEventOccured = false;
                        cir.setReturnValue(false);
                        return;
                }
                
                DamageSource newSource;
                if (cb.attacker != null) newSource = new DamageSource(cb.damageType, cb.directAttacker, cb.attacker);
                else newSource = new DamageSource(cb.damageType, cb.position);
                
                damageEventOccured = true;
                cir.setReturnValue(ENTITY.damage(newSource, cb.damageAmount));
        }
        
}
