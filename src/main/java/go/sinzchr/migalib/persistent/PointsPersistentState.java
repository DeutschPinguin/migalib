package go.sinzchr.migalib.persistent;

import go.sinzchr.migalib.point.Point;
import go.sinzchr.migalib.point.PointsContainer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


public class PointsPersistentState
        extends PersistentState
{
        
        protected final @NotNull Map<@NotNull Identifier, @NotNull PointsContainer> MAP = new HashMap<>();
        
        
        protected PointsPersistentState () {}
        
        
        public static @NotNull PointsPersistentState load (@NotNull ServerWorld world)
        {
                return world.getPersistentStateManager().getOrCreate(
                        PointsPersistentState::from,
                        PointsPersistentState::new,
                        "migalib.global_points"
                );
        }
        
        
        public static @NotNull PointsPersistentState from (@NotNull NbtCompound nbt)
        {
                var state = new PointsPersistentState();
                for (var k : nbt.getKeys())
                {
                        var container = PointsContainer.deserialize(nbt.getCompound(k));
                        state.MAP.put(container.id(), container);
                }
                return state;
        }
        
        
        @Override
        public NbtCompound writeNbt (NbtCompound nbt)
        {
                MAP.forEach((id, container) -> nbt.put(id.toString(), container.serialize()));
                return nbt;
        }
        
        
        public boolean has (@NotNull Identifier id)
        {
                return MAP.containsKey(id);
        }
        
        
        public @Nullable PointsContainer get (@NotNull Identifier id)
        {
                return MAP.get(id);
        }
        
        
        public void add (@NotNull PointsContainer container)
        {
                MAP.put(container.id(), container);
        }
        
        
        public void remove (@NotNull Identifier id)
        {
                MAP.remove(id);
        }
        
        
        public void remove (@NotNull Point point)
        {
                remove(point.id());
        }
        
        
        public @NotNull Map<@NotNull Identifier, @NotNull PointsContainer> containers ()
        {
                return MAP;
        }
        
}
