package mcjty.rftoolspower.compat;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.varia.Tools;
import mcjty.rftoolspower.modules.blazing.BlazingModule;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingAgitatorTileEntity;
import mcjty.rftoolspower.modules.blazing.blocks.BlazingGeneratorTileEntity;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellBlock;
import mcjty.rftoolspower.modules.dimensionalcell.blocks.DimensionalCellTileEntity;
import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import mcjty.rftoolspower.modules.endergenic.blocks.EnderMonitorTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.modules.endergenic.data.EnderMonitorMode;
import mcjty.rftoolspower.modules.generator.CoalGeneratorModule;
import mcjty.rftoolspower.modules.generator.blocks.CoalGeneratorTileEntity;
import mcjty.rftoolspower.modules.powercell.PowerCellConfig;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock;
import mcjty.rftoolspower.modules.powercell.blocks.PowerCellTileEntity;
import mcjty.rftoolspower.modules.powercell.data.SideType;
import mcjty.theoneprobe.api.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static mcjty.theoneprobe.api.TextStyleClass.*;

public class RFToolsPowerTOPDriver implements TOPDriver {

    public static final RFToolsPowerTOPDriver DRIVER = new RFToolsPowerTOPDriver();

    private final Map<ResourceLocation, TOPDriver> drivers = new HashMap<>();

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
        ResourceLocation id = blockState.getBlock().getRegistryName();
        if (!drivers.containsKey(id)) {
            if (blockState.getBlock() == CoalGeneratorModule.COALGENERATOR.get()) {
                drivers.put(id, new CoalDriver());
            } else if (blockState.getBlock() instanceof PowerCellBlock) {
                drivers.put(id, new PowerCellDriver());
            } else if (blockState.getBlock() instanceof DimensionalCellBlock) {
                drivers.put(id, new DimensionalCellDriver());
            } else if (blockState.getBlock() == EndergenicModule.ENDERGENIC.get()) {
                drivers.put(id, new EndergenicDriver());
            } else if (blockState.getBlock() == EndergenicModule.ENDER_MONITOR.get()) {
                drivers.put(id, new EndermonitorDriver());
            } else if (blockState.getBlock() == BlazingModule.BLAZING_GENERATOR.get()) {
                drivers.put(id, new BlazingGeneratorDriver());
            } else if (blockState.getBlock() == BlazingModule.BLAZING_AGITATOR.get()) {
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
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
        }
    }

    private static class CoalDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (CoalGeneratorTileEntity te) -> {
                Boolean working = te.isWorking();
                if (working) {
                    probeInfo.text(CompoundText.createLabelInfo("Producing ", te.getRfPerTick() + " RF/t"));
                }
            }, "Bad tile entity!");
        }
    }

    private static class PowerCellDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (PowerCellTileEntity te) -> {
                long rfPerTick = te.getRfPerTickReal();

                if (te.getNetwork().isValid()) {
                    probeInfo.text(CompoundText.createLabelInfo("Input/Output: ", rfPerTick + " RF/t"));
                    SideType powermode = te.getMode(data.getSideHit());
                    if (powermode == SideType.INPUT) {
                        probeInfo.text(CompoundText.create().style(HIGHLIGHTED).text("Side: input"));
                    } else if (powermode == SideType.OUTPUT) {
                        probeInfo.text(CompoundText.create().style(HIGHLIGHTED).text("Side: output"));
                    }
                } else {
                    probeInfo.text(CompoundText.create().style(ERROR).text("Too many blocks in network (max " + PowerCellConfig.NETWORK_MAX.get() + ")!"));
                }

                int networkId = te.getNetwork().getNetworkId();
                if (mode == ProbeMode.DEBUG) {
                    probeInfo.text(CompoundText.createLabelInfo("Network ID: ", networkId));
                }
                if (mode == ProbeMode.EXTENDED) {
                    probeInfo.text(CompoundText.createLabelInfo("Local Energy: ", te.getLocalEnergy()));
                }
            }, "Bad tile entity!");
        }
    }

    private static class DimensionalCellDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (DimensionalCellTileEntity te) -> {
                int id = te.getNetworkId();
                if (mode == ProbeMode.EXTENDED) {
                    if (id != -1) {
                        probeInfo.text(CompoundText.createLabelInfo("ID: ", new DecimalFormat("#.##").format(id)));
                    } else {
                        probeInfo.text(CompoundText.create().style(TextStyleClass.INFO).text("Local storage!"));
                    }
                }

                float costFactor = te.getCostFactor();
                int rfPerTick = te.getRfPerTickPerSide();

                probeInfo.text(CompoundText.createLabelInfo( "Input/Output: ",rfPerTick + " RF/t"));
                DimensionalCellTileEntity.Mode powermode = te.getMode(data.getSideHit());
                if (powermode == DimensionalCellTileEntity.Mode.MODE_INPUT) {
                    probeInfo.text(CompoundText.create().style(HIGHLIGHTED).text("Side: input"));
                } else if (powermode == DimensionalCellTileEntity.Mode.MODE_OUTPUT) {
                    int cost = (int) ((costFactor - 1.0f) * 1000.0f);
                    probeInfo.text(CompoundText.create().style(HIGHLIGHTED).text("Side: output (cost " + cost / 10 + "." + cost % 10 + "%)"));
                }
                if (mode == ProbeMode.EXTENDED) {
                    int rfPerTickIn = te.getLastRfPerTickIn();
                    int rfPerTickOut = te.getLastRfPerTickOut();
                    probeInfo.text(CompoundText.createLabelInfo("In:  ",rfPerTickIn + "RF/t"));
                    probeInfo.text(CompoundText.createLabelInfo("Out: ",rfPerTickOut + "RF/t"));
                }
            }, "Bad tile entity!");
        }
    }

    private static class EndergenicDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (EndergenicTileEntity te) -> {
                if (mode == ProbeMode.EXTENDED) {
                    IItemStyle style = probeInfo.defaultItemStyle().width(16).height(13);
                    ILayoutStyle layoutStyle = probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER);
                    probeInfo.text(CompoundText.create().style(INFO).text("Stats over the last 5 seconds:"));
                    probeInfo.horizontal(layoutStyle)
                            .item(new ItemStack(Items.REDSTONE), style)
                            .text(CompoundText.createLabelInfo("Charged ", te.getLastChargeCounter() + " time(s)"));
                    probeInfo.horizontal(layoutStyle)
                            .item(new ItemStack(Items.ENDER_PEARL), style)
                            .text(CompoundText.createLabelInfo("Fired ", te.getLastPearlsLaunched()))
                            .text(CompoundText.createLabelInfo(" / Lost ", te.getLastPearlsLost()));
                    if (te.getLastPearlsLost() > 0) {
                        probeInfo.text(CompoundText.create().style(ERROR).text(te.getLastPearlsLostReason()));
                    }
                    if (te.getLastPearlArrivedAt() > -2) {
                        probeInfo.text(CompoundText.createLabelInfo("Last pearl arrived at ", te.getLastPearlArrivedAt()));
                    }
                    probeInfo.horizontal()
                            .text(CompoundText.create().style(OK).text("RF Gain " + te.getLastRfGained()))
                            .text(CompoundText.create().text(" / "))
                            .text(CompoundText.create().style(ERROR).text("Lost " + te.getLastRfLost()))
                            .text(CompoundText.create().text(" (RF/t " + te.getLastRfPerTick() + ")"));
                } else {
                    probeInfo.text(CompoundText.create().text("(sneak to get statistics)"));
                }
            }, "Bad tile entity!");
        }
    }

    private static class EndermonitorDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (EnderMonitorTileEntity te) -> {
                EnderMonitorMode m = te.getMode();
                probeInfo.text(CompoundText.createLabelInfo("Mode: ", m.getName()));
            }, "Bad tile entity!");
        }
    }

    private static class BlazingGeneratorDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (BlazingGeneratorTileEntity te) -> {
                // @todo
            }, "Bad tile entity!");
        }
    }

    private static class BlazingAgitatorDriver implements TOPDriver {
        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
            McJtyLibTOPDriver.DRIVER.addStandardProbeInfo(mode, probeInfo, player, world, blockState, data);
            Tools.safeConsume(world.getBlockEntity(data.getPos()), (BlazingAgitatorTileEntity te) -> {
                // @todo
            }, "Bad tile entity!");
        }
    }

}
