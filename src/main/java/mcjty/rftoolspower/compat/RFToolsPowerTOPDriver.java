package mcjty.rftoolspower.compat;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.varia.Tools;
import mcjty.rftoolspower.modules.blazing.BlazingSetup;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingGeneratorTileEntity;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellBlock;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import mcjty.rftoolspower.modules.endergenic.EndergenicSetup;
import mcjty.rftoolspower.modules.endergenic.blocks.EnderMonitorTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.modules.endergenic.data.EnderMonitorMode;
import mcjty.rftoolspower.modules.generator.CoalGeneratorSetup;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.modules.powercell.PowerCellConfig;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.data.SideType;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class RFToolsPowerTOPDriver implements TOPDriver {

    public static final RFToolsPowerTOPDriver DRIVER = new RFToolsPowerTOPDriver();

    private final Map<ResourceLocation, TOPDriver> drivers = new HashMap<>();

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        ResourceLocation id = blockState.getBlock().getRegistryName();
        if (!drivers.containsKey(id)) {
            if (blockState.getBlock() == CoalGeneratorSetup.COALGENERATOR.get()) {
                drivers.put(id, new CoalDriver());
            } else if (blockState.getBlock() instanceof PowerCellBlock) {
                drivers.put(id, new PowerCellDriver());
            } else if (blockState.getBlock() instanceof DimensionalCellBlock) {
                drivers.put(id, new DimensionalCellDriver());
            } else if (blockState.getBlock() == EndergenicSetup.ENDERGENIC.get()) {
                drivers.put(id, new EndergenicDriver());
            } else if (blockState.getBlock() == EndergenicSetup.ENDER_MONITOR.get()) {
                drivers.put(id, new EndermonitorDriver());
            } else if (blockState.getBlock() == BlazingSetup.BLAZING_GENERATOR.get()) {
                drivers.put(id, new BlazingGeneratorDriver());
            } else if (blockState.getBlock() == BlazingSetup.BLAZING_AGITATOR.get()) {
                drivers.put(id, new BlazingAgitatorDriver());
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
                    probeInfo.text(new StringTextComponent(TextFormatting.GREEN + "Producing " + te.getRfPerTick() + " RF/t")); // @todo 1.16
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
                    probeInfo.text(new StringTextComponent(TextFormatting.GREEN + "Input/Output: " + rfPerTick + " RF/t")); // @todo 1.16
                    SideType powermode = te.getMode(data.getSideHit());
                    if (powermode == SideType.INPUT) {
                        probeInfo.text(new StringTextComponent(TextFormatting.YELLOW + "Side: input")); // @todo 1.16
                    } else if (powermode == SideType.OUTPUT) {
                        probeInfo.text(new StringTextComponent(TextFormatting.YELLOW + "Side: output"));    // @todo 1.16
                    }
                } else {
                    probeInfo.text(new StringTextComponent(TextStyleClass.ERROR + "Too many blocks in network (max " + PowerCellConfig.NETWORK_MAX.get() + ")!"));  // @todo 1.16
                }

                int networkId = te.getNetwork().getNetworkId();
                if (mode == ProbeMode.DEBUG) {
                    probeInfo.text(new StringTextComponent(TextStyleClass.LABEL + "Network ID: " + TextStyleClass.INFO + networkId));   // @todo 1.16
                }
                if (mode == ProbeMode.EXTENDED) {
                    probeInfo.text(new StringTextComponent(TextStyleClass.LABEL + "Local Energy: " + TextStyleClass.INFO + te.getLocalEnergy()));   // @todo 1.16
                }
            }, "Bad tile entity!");
        }
    }

    private static class DimensionalCellDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (DimensionalCellTileEntity te) -> {
                int id = te.getNetworkId();
                if (mode == ProbeMode.EXTENDED) {
                    if (id != -1) {
                        probeInfo.text(new StringTextComponent(TextFormatting.GREEN + "ID: " + new DecimalFormat("#.##").format(id)));  // @todo 1.16
                    } else {
                        probeInfo.text(new StringTextComponent(TextFormatting.GREEN + "Local storage!"));   // @todo 1.16
                    }
                }

                float costFactor = te.getCostFactor();
                int rfPerTick = te.getRfPerTickPerSide();

                probeInfo.text(new StringTextComponent(TextFormatting.GREEN + "Input/Output: " + rfPerTick + " RF/t")); // @todo 1.16
                DimensionalCellTileEntity.Mode powermode = te.getMode(data.getSideHit());
                if (powermode == DimensionalCellTileEntity.Mode.MODE_INPUT) {
                    probeInfo.text(new StringTextComponent(TextFormatting.YELLOW + "Side: input")); // @todo 1.16
                } else if (powermode == DimensionalCellTileEntity.Mode.MODE_OUTPUT) {
                    int cost = (int) ((costFactor - 1.0f) * 1000.0f);
                    probeInfo.text(new StringTextComponent(TextFormatting.YELLOW + "Side: output (cost " + cost / 10 + "." + cost % 10 + "%)"));    // @todo 1.16
                }
                if (mode == ProbeMode.EXTENDED) {
                    int rfPerTickIn = te.getLastRfPerTickIn();
                    int rfPerTickOut = te.getLastRfPerTickOut();
                    probeInfo.text(new StringTextComponent(TextFormatting.GREEN + "In:  " + rfPerTickIn + "RF/t")); // @todo 1.16
                    probeInfo.text(new StringTextComponent(TextFormatting.GREEN + "Out: " + rfPerTickOut + "RF/t"));    // @todo 1.16
                }
            }, "Bad tile entity!");
        }
    }

    private static class EndergenicDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (EndergenicTileEntity te) -> {
                if (mode == ProbeMode.EXTENDED) {
                    IItemStyle style = probeInfo.defaultItemStyle().width(16).height(13);
                    ILayoutStyle layoutStyle = probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER);
                    probeInfo.text(new StringTextComponent(TextFormatting.BLUE + "Stats over the last 5 seconds:"));    // @todo 1.16
                    probeInfo.horizontal(layoutStyle)
                            .item(new ItemStack(Items.REDSTONE), style)
                            .text(new StringTextComponent("Charged " + te.getLastChargeCounter() + " time(s)"));    // @todo 1.16
                    probeInfo.horizontal(layoutStyle)
                            .item(new ItemStack(Items.ENDER_PEARL), style)
                            .text(new StringTextComponent("Fired " + te.getLastPearlsLaunched()))   // @todo 1.16
                            .text(new StringTextComponent(" / Lost " + te.getLastPearlsLost()));    // @todo 1.16
                    if (te.getLastPearlsLost() > 0) {
                        probeInfo.text(new StringTextComponent(TextFormatting.RED + te.getLastPearlsLostReason())); // @todo 1.16
                    }
                    if (te.getLastPearlArrivedAt() > -2) {
                        probeInfo.text(new StringTextComponent("Last pearl arrived at " + te.getLastPearlArrivedAt())); // @todo 1.16
                    }
                    probeInfo.horizontal()
                            .text(new StringTextComponent(TextFormatting.GREEN + "RF Gain " + te.getLastRfGained()))    // @todo 1.16
                            .text(new StringTextComponent(" / "))   // @todo 1.16
                            .text(new StringTextComponent(TextFormatting.RED + "Lost " + te.getLastRfLost()))   // @todo 1.16
                            .text(new StringTextComponent(" (RF/t " + te.getLastRfPerTick() + ")"));    // @todo 1.16
                } else {
                    probeInfo.text(new StringTextComponent("(sneak to get statistics)"));   // @todo 1.16
                }
            }, "Bad tile entity!");
        }
    }

    private static class EndermonitorDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (EnderMonitorTileEntity te) -> {
                EnderMonitorMode m = te.getMode();
                probeInfo.text(new StringTextComponent(TextFormatting.GREEN + "Mode: " + m.getDescription()));  // @todo 1.16
            }, "Bad tile entity!");
        }
    }

    private static class BlazingGeneratorDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (BlazingGeneratorTileEntity te) -> {
                // @todo
            }, "Bad tile entity!");
        }
    }

    private static class BlazingAgitatorDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getTileEntity(data.getPos()), (BlazingAgitatorTileEntity te) -> {
                // @todo
            }, "Bad tile entity!");
        }
    }

}
