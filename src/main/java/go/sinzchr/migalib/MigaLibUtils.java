package go.sinzchr.migalib;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public final class MigaLibUtils
{
        
        private MigaLibUtils () {}
        
        
        public static @NotNull NbtList serialize (double... values)
        {
                NbtList list = new NbtList();
                for (double d : values) list.add(NbtDouble.of(d));
                return list;
        }
        
        
        public static double[] doubleArrayFrom (@NotNull NbtList list)
        {
                double[] arr = new double[list.size()];
                
                for (int i = 0; i < list.size(); i++)
                        arr[i] = ((NbtDouble) list.get(i)).doubleValue();
                
                return arr;
        }
        
        
        public static @NotNull NbtList serialize (float... values)
        {
                NbtList list = new NbtList();
                for (float f : values) list.add(NbtFloat.of(f));
                return list;
        }
        
        
        public static float[] floatArrayFrom (@NotNull NbtList list)
        {
                float[] arr = new float[list.size()];
                
                for (int i = 0; i < list.size(); i++)
                        arr[i] = ((NbtFloat) list.get(i)).floatValue();
                
                return arr;
        }
        
        
        public static @NotNull NbtList serialize (@NotNull Position pos)
        {
                double x = pos.getX(), y = pos.getY(), z = pos.getZ();
                var list = new NbtList();
                list.add(NbtDouble.of(x));
                list.add(NbtDouble.of(y));
                list.add(NbtDouble.of(z));
                return list;
        }
        
        
        public static double[] positionFrom (@NotNull NbtList list)
        {
                return new double[]{
                        list.getDouble(0),
                        list.getDouble(1),
                        list.getDouble(2)
                };
        }
        
        
        public static @NotNull Vec3d vecFrom (@NotNull NbtList list)
        {
                var position = positionFrom(list);
                return new Vec3d(position[0], position[1], position[2]);
        }
        
        
        public static @NotNull NbtList serialize (float pitch, float yaw)
        {
                var list = new NbtList();
                list.add(NbtFloat.of(pitch));
                list.add(NbtFloat.of(yaw));
                return list;
        }
        
        
        public static float[] rotationFrom (@NotNull NbtList list)
        {
                return new float[]{
                        list.getFloat(0),
                        list.getFloat(1)
                };
        }
        
        
        public static @NotNull RegistryEntry<DamageType> entryOf (
                @NotNull World world, @NotNull RegistryKey<DamageType> key
        )
        {
                return world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key);
        }
        
        
        static void init () {}
        
}
