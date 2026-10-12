package com.kerbalcraft.event;

import com.kerbalcraft.KerbalCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Deterministic bowl-shaped craters carved into the Moon's flat prototype surface. */
@Mod.EventBusSubscriber(modid = KerbalCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MoonCraterEvents {
    private static final ResourceLocation MOON = new ResourceLocation(KerbalCraft.MOD_ID, "moon");
    private static final int SURFACE_Y = 6;
    private static final int GRID = 48;

    private MoonCraterEvents() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide
                || !level.dimension().location().equals(MOON)
                || !(event.getChunk() instanceof LevelChunk chunk)) return;

        int minX = chunk.getPos().x << 4;
        int minZ = chunk.getPos().z << 4;
        for (int x = minX; x < minX + 16; x++) {
            for (int z = minZ; z < minZ + 16; z++) {
                int cellX = Math.floorDiv(x, GRID);
                int cellZ = Math.floorDiv(z, GRID);
                double nearestDistance = Double.MAX_VALUE;
                int radius = 0;
                int depth = 0;
                for (int cx = cellX - 1; cx <= cellX + 1; cx++) {
                    for (int cz = cellZ - 1; cz <= cellZ + 1; cz++) {
                        long seed = mix(((long) cx * 341873128712L) ^ ((long) cz * 132897987541L));
                        int centerX = cx * GRID + 12 + (int) Math.floorMod(seed, 24);
                        int centerZ = cz * GRID + 12 + (int) Math.floorMod(seed >>> 8, 24);
                        int candidateRadius = 5 + (int) Math.floorMod(seed >>> 16, 8);
                        double distance = Math.sqrt((x - centerX) * (double) (x - centerX)
                                + (z - centerZ) * (double) (z - centerZ));
                        if (distance < candidateRadius && distance < nearestDistance) {
                            nearestDistance = distance;
                            radius = candidateRadius;
                            depth = 2 + (int) Math.floorMod(seed >>> 24, 3);
                        }
                    }
                }
                if (radius == 0) continue;

                double bowl = 1.0D - nearestDistance / radius;
                int floorY = SURFACE_Y - Math.max(1, (int) Math.round(bowl * depth));
                for (int y = floorY + 1; y <= SURFACE_Y; y++) {
                    chunk.setBlockState(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), false);
                }
                chunk.setBlockState(new BlockPos(x, floorY, z), Blocks.GRAVEL.defaultBlockState(), false);
            }
        }
    }

    private static long mix(long value) {
        value = (value ^ (value >>> 30)) * 0xbf58476d1ce4e5b9L;
        value = (value ^ (value >>> 27)) * 0x94d049bb133111ebL;
        return value ^ (value >>> 31);
    }
}
