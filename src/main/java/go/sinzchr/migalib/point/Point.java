package go.sinzchr.migalib.point;

import go.sinzchr.migalib.MigaLibUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class Point
        implements Position
{
        
        protected final @NotNull Identifier ID;
        protected final int hash;
        
        protected final @NotNull Set<@NotNull Identifier> TAGS = new HashSet<>();
        protected final @NotNull Map<@NotNull Identifier, @NotNull NbtCompound> METADATA = new HashMap<>();
        
        protected @NotNull Identifier WORLD;
        protected double x, y, z;
        protected float pitch, yaw;
        
        
        public Point (
                @NotNull Identifier id,
                @NotNull Identifier world,
                double x, double y, double z,
                float pitch, float yaw
        )
        {
                ID = id;
                hash = ID.hashCode();
                WORLD = world;
                this.x = x;
                this.y = y;
                this.z = z;
                this.pitch = pitch;
                this.yaw = yaw;
        }
        
        
        public Point (@NotNull Identifier id, @NotNull Identifier world)
        {
                this(id, world, 0, 0, 0, 0, 0);
        }
        
        
        protected <T extends Point> @NotNull T to (@NotNull T point)
        {
                point.x = x;
                point.y = y;
                point.z = z;
                point.pitch = pitch;
                point.yaw = yaw;
                point.TAGS.addAll(TAGS);
                point.METADATA.putAll(METADATA);
                return point;
        }
        
        
        public @NotNull Point toPoint ()
        {
                return to(new Point(ID, WORLD));
        }
        
        
        public @NotNull MutPoint toMutablePoint ()
        {
                return to(new MutPoint(ID, WORLD));
        }
        
        
        public @NotNull NbtCompound serialize ()
        {
                var nbt = new NbtCompound();
                nbt.putString("id", ID.toString());
                nbt.putString("dimension", WORLD.toString());
                
                var tags = new NbtList();
                for (var id : TAGS) tags.add(NbtString.of(id.toString()));
                nbt.put("tags", tags);
                
                var metadata = new NbtCompound();
                METADATA.forEach((id, data) -> metadata.put(id.toString(), data));
                nbt.put("metadata", metadata);
                
                nbt.put("position", MigaLibUtils.serialize(x, y, z));
                nbt.put("rotation", MigaLibUtils.serialize(pitch, yaw));
                
                return nbt;
        }
        
        
        public static @NotNull Point deserialize (@NotNull NbtCompound nbt)
        {
                String id = nbt.getString("id"), world = nbt.getString("world");
                var point = new Point(
                        Objects.requireNonNull(Identifier.tryParse(id)),
                        Objects.requireNonNull(Identifier.tryParse(world))
                );
                
                var tagList = nbt.getList("tags", NbtElement.STRING_TYPE);
                tagList.forEach(element -> {
                        var tag = Identifier.tryParse(element.asString());
                        if (tag != null) point.TAGS.add(tag);
                });
                
                var metadataCompound = nbt.getCompound("metadata");
                for (var key : metadataCompound.getKeys())
                {
                        var modId = Identifier.tryParse(key);
                        if (modId == null) continue;
                        var compound = metadataCompound.getCompound(key);
                        point.METADATA.put(modId, compound);
                }
                
                var pos = MigaLibUtils.positionFrom(nbt.getList("position", NbtElement.DOUBLE_TYPE));
                point.x = pos[0];
                point.y = pos[1];
                point.z = pos[2];
                
                var rot = MigaLibUtils.rotationFrom(nbt.getList("rotation", NbtElement.FLOAT_TYPE));
                point.pitch = rot[0];
                point.yaw = rot[1];
                
                return point;
        }
        
        
        public boolean hasTag (@NotNull Identifier id)
        {
                return TAGS.contains(id);
        }
        
        
        public @NotNull Set<@NotNull Identifier> tags ()
        {
                return Collections.unmodifiableSet(TAGS);
        }
        
        
        public @NotNull Set<@NotNull Identifier> copyTags ()
        {
                return new HashSet<>(TAGS);
        }
        
        
        public boolean hasMetadata (@NotNull Identifier modId)
        {
                return METADATA.containsKey(modId);
        }
        
        
        public @Nullable NbtCompound metadata (@NotNull Identifier modId)
        {
                var compound = METADATA.get(modId);
                if (compound == null) return null;
                return compound.copy();
        }
        
        
        public @NotNull Map<@NotNull Identifier, @NotNull NbtCompound> allMetadata ()
        {
                return Collections.unmodifiableMap(METADATA);
        }
        
        
        public @NotNull Map<@NotNull Identifier, @NotNull NbtCompound> copyAllMetadata ()
        {
                return new HashMap<>(METADATA);
        }
        
        
        public @NotNull Identifier id ()
        {
                return ID;
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
                        || (obj instanceof Point other && other.hash == hash);
        }
        
        
        @Override
        public String toString ()
        {
                return "Point[" + ID + "]";
        }
        
        
        public @NotNull Identifier world ()
        {
                return WORLD;
        }
        
        
        public double squaredMagnitude ()
        {
                return (x * x) + (y * y) + (z * z);
        }
        
        
        public double magnitude ()
        {
                return Math.sqrt(squaredMagnitude());
        }
        
        
        public double squaredDistanceTo (double x, double y, double z)
        {
                double dx = (this.x - x), dy = (this.y - y), dz = (this.z - z);
                return (dx * dx) + (dy * dy) + (dz * dz);
        }
        
        
        public double distanceTo (double x, double y, double z)
        {
                return Math.sqrt(squaredDistanceTo(x, y, z));
        }
        
        
        public double squaredDistanceTo (@NotNull Position pos)
        {
                return squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ());
        }
        
        
        public double distanceTo (@NotNull Position pos)
        {
                return distanceTo(pos.getX(), pos.getY(), pos.getZ());
        }
        
        
        public double squaredDistanceTo (@NotNull Vec3i pos)
        {
                return squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ());
        }
        
        
        public double distanceTo (@NotNull Vec3i pos)
        {
                return distanceTo(pos.getX(), pos.getY(), pos.getZ());
        }
        
        
        public double[] position ()
        {
                return new double[]{x, y, z};
        }
        
        
        public double x ()
        {
                return x;
        }
        
        
        @Override
        public double getX ()
        {
                return x;
        }
        
        
        public double y ()
        {
                return y;
        }
        
        
        @Override
        public double getY ()
        {
                return y;
        }
        
        
        public double z ()
        {
                return z;
        }
        
        
        @Override
        public double getZ ()
        {
                return z;
        }
        
        
        public float[] rotation ()
        {
                return new float[]{pitch, yaw};
        }
        
        
        public float pitch ()
        {
                return pitch;
        }
        
        
        public float yaw ()
        {
                return yaw;
        }
        
        
        public @NotNull Vec3d toVec3d ()
        {
                return new Vec3d(x, y, z);
        }
        
        
        public @NotNull BlockPos toBlockPos ()
        {
                return BlockPos.ofFloored(this);
        }
        
}
