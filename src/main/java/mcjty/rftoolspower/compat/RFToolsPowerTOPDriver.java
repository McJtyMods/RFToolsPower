package mcjty.rftoolspower.compat;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.varia.Tools;
import mcjty.rftoolspower.modules.generator.CoalGeneratorSetup;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.data.SideType;
import mcjty.rftoolspower.modules.powercell.PowerCellConfig;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class RFToolsPowerTOPDriver implements TOPDriver {

    public static final RFToolsPowerTOPDriver DRIVER = new RFToolsPowerTOPDriver();

    private final Map<ResourceLocation, TOPDriver> drivers = new HashMap<>();

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        ResourceLocation id = blockState.getBlock().getRegistryName();
        if (!drivers.containsKey(id)) {
            if (blockState.getBlock() == CoalGeneratorSetup.COALGENERATOR) {
                drivers.put(id, new CoalDriver());
            } else if (blockState.getBlock() instanceof PowerCellBlock) {
                drivers.put(id, new PowerCellDriver());
            } else {
                drivers.put(id, new DefaultDriver());
            }
        }
        TOPDriver driver = drivers.get(id);
        if (driver != null) {
            driver.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    private static class DefaultDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    private static class CoalDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (CoalGeneratorTileEntity te) -> {
                Boolean working = te.isWorking();
                if (working) {
                    probeInfo.text(TextFormatting.GREEN + "Producing " + te.getRfPerTick() + " RF/t");
                }
            }, "Bad tile entity!");
        }
    }

    private static class PowerCellDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (PowerCellTileEntity te) -> {
                long rfPerTick = te.getRfPerTickReal();

                if (te.getNetwork().isValid()) {
                    probeInfo.text(TextFormatting.GREEN + "Input/Output: " + rfPerTick + " RF/t");
                    SideType powermode = te.getMode(data.getSideHit());
                    if (powermode == SideType.INPUT) {
                        probeInfo.text(TextFormatting.YELLOW + "Side: input");
                    } else if (powermode == SideType.OUTPUT) {
                        probeInfo.text(TextFormatting.YELLOW + "Side: output");
                    }
                } else {
                    probeInfo.text(TextStyleClass.ERROR + "Too many blocks in network (max " + PowerCellConfig.NETWORK_MAX.get() + ")!");
                }

                int networkId = te.getNetwork().getNetworkId();
                if (mode == ProbeMode.DEBUG) {
                    probeInfo.text(TextStyleClass.LABEL + "Network ID: " + TextStyleClass.INFO + networkId);
                }
                if (mode == ProbeMode.EXTENDED) {
                    probeInfo.text(TextStyleClass.LABEL + "Local Energy: " + TextStyleClass.INFO + te.getLocalEnergy());
                }
            }, "Bad tile entity!");
        }
    }

}
