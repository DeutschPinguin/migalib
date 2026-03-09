package go.sinzchr.migalib.point;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class PointsContainer
{
        
        protected final @NotNull Identifier ID;
        protected final int hash;
        
        protected final @NotNull Map<@NotNull Identifier, @NotNull Point> MAP = new HashMap<>();
        
        
        public PointsContainer (@NotNull Identifier id)
        {
                ID = id;
                hash = ID.hashCode();
        }
        
        
        public @NotNull PointsContainer copy ()
        {
                var container = new PointsContainer(ID);
                for (var point : MAP.values())
                {
                        var c = point.toPoint();
                        container.MAP.put(c.id(), c);
                }
                return container;
        }
        
        
        public @NotNull NbtCompound serialize ()
        {
                NbtCompound nbt = new NbtCompound(), points = new NbtCompound();
                
                nbt.putString("id", ID.toString());
                
                for (var point : MAP.values()) points.put(point.id().toString(), point.serialize());
                nbt.put("points", points);

                return nbt;
        }
        
        
        public static @NotNull PointsContainer deserialize (@NotNull NbtCompound nbt)
        {
                var container = new PointsContainer(
                        Objects.requireNonNull(Identifier.tryParse(nbt.getString("id")))
                );
                
                var points = nbt.getCompound("points");
                for (var k : points.getKeys())
                {
                        var point = Point.deserialize(points.getCompound(k));
                        container.MAP.put(point.id(), point);
                }
                
                return container;
        }
        
        
        public boolean has (@NotNull Identifier id)
        {
                return MAP.containsKey(id);
        }
        
        
        public boolean has (@NotNull Point point)
        {
                return has(point.id());
        }
        
        
        public @Nullable Point get (@NotNull Identifier id)
        {
                return MAP.get(id);
        }
        
        
        public void set (@NotNull Point point)
        {
                MAP.put(point.id(), point);
        }
        
        
        public void add (@NotNull Point point)
        {
                MAP.putIfAbsent(point.id(), point);
        }
        
        
        public void remove (@NotNull Point point)
        {
                MAP.remove(point.id());
        }
        
        
        public void set (@NotNull Point... points)
        {
                for (var point : points) set(point);
        }
        
        
        public void add (@NotNull Point... points)
        {
                for (var point : points) add(point);
        }
        
        
        public void remove (@NotNull Point... points)
        {
                for (var point : points) remove(point);
        }
        
        
        public void set (@NotNull Collection<@NotNull Point> points)
        {
                for (var point : points) set(point);
        }
        
        
        public void add (@NotNull Collection<@NotNull Point> points)
        {
                for (var point : points) add(point);
        }
        
        
        public void remove (@NotNull Collection<@NotNull Point> points)
        {
                for (var point : points) remove(point);
        }
        
        
        public @NotNull Identifier id ()
        {
                return ID;
        }
        
        
        public @NotNull Map<@NotNull Identifier, @NotNull Point> points ()
        {
                return MAP;
        }
        
        
        public @NotNull Map<@NotNull Identifier, @NotNull Point> copyPoints ()
        {
                return new HashMap<>(MAP);
        }
        
        
        @Override
        public int hashCode ()
        {
                return hash;
        }
        
        
        @Override
        public boolean equals (Object obj)
        {
                return obj == this
                        || (obj instanceof PointsContainer other && other.hash == hash);
        }
        
        
        @Override
        public String toString ()
        {
                return "PointsContainer[" + ID + "]";
        }
        
}
