package io.github.opencubicchunks.dumpids;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

import static java.util.Comparator.comparingInt;

@Mod(modid = DumpIDs.MODID, name = DumpIDs.NAME, version = DumpIDs.VERSION)
public class DumpIDs {
    public static final String MODID = "dumpids";
    public static final String NAME = "Dump Block IDs";
    public static final String VERSION = "1.0";

    @SuppressWarnings("unchecked")
    @EventHandler
    public void init(FMLPostInitializationEvent event) {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Minecraft.getMinecraft().gameDir.toPath().resolve("blockstates.csv"), StandardCharsets.UTF_8))) {
            pw.println("ID,registry name,meta,isPrimaryForMeta,properties...");
            Map.Entry<ResourceLocation, Block>[] entries = ForgeRegistries.BLOCKS.getEntries().toArray(new Map.Entry[0]);
            Arrays.sort(entries, comparingInt(e -> Block.REGISTRY.getIDForObject(e.getValue())));
            for (Map.Entry<ResourceLocation, Block> e : entries) {
                Block block = e.getValue();
                String name = e.getKey().toString();
                int id = Block.REGISTRY.getIDForObject(block);
                for (IBlockState state : block.getBlockState().getValidStates()) {
                    int meta = block.getMetaFromState(state);
                    boolean isPrimaryForMeta = state == block.getStateFromMeta(meta);
                    StringBuilder sb = new StringBuilder(1000);
                    sb.append(id).append(",")
                            .append(name).append(",")
                            .append(meta).append(",")
                            .append(isPrimaryForMeta);

                    for (Map.Entry<IProperty<?>, Comparable<?>> prop : state.getProperties().entrySet()) {
                        sb.append(",").append(prop.getKey().getName()).append("=").append(prop.getValue().toString());
                    }
                    pw.println(sb.toString());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
