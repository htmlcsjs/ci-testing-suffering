package gregtech.api.fluids;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.fluids.fluidType.FluidType;
import gregtech.api.fluids.fluidType.FluidTypes;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.info.MaterialIconSet;
import gregtech.api.unification.material.info.MaterialIconType;
import gregtech.api.unification.material.properties.FluidProperty;
import gregtech.api.unification.material.properties.PlasmaProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.util.FluidTooltipUtil;
import gregtech.api.util.GTUtility;
import gregtech.api.util.LocalizationUtils;
import gregtech.common.blocks.MetaBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetaFluids {

    private static final Set<ResourceLocation> fluidSprites = new ObjectOpenHashSet<>();
    private static final Map<String, Material> fluidToMaterialMappings = new Object2ObjectOpenHashMap<>();
    private static final Map<String, String> alternativeFluidNames = new Object2ObjectOpenHashMap<>();
    private static final Map<Material, Map<FluidType, ResourceLocation>> fluidTextureMap = new Object2ObjectOpenHashMap<>();

    public static final ResourceLocation AUTO_GENERATED_PLASMA_TEXTURE = new ResourceLocation(
            GTValues.MODID, "blocks/fluids/fluid.plasma.autogenerated");

    public static void registerSprites(TextureMap textureMap) {
        for (ResourceLocation spriteLocation : fluidSprites) {
            textureMap.registerSprite(spriteLocation);
        }
    }

    public static void init() {
        fluidSprites.add(AUTO_GENERATED_PLASMA_TEXTURE);
        registerIconFluidSprites();

        // handle vanilla fluids
        handleNonMaterialFluids(Materials.Water, FluidRegistry.WATER);
        handleNonMaterialFluids(Materials.Lava, FluidRegistry.LAVA);

        // alternative names for forestry fluids
        addAlternativeNames();

        // set custom textures for fluids
        setCustomTextures();

        // set custom additional tooltips for fluids
        setCustomTooltips();

        for (Material material : GregTechAPI.MATERIAL_REGISTRY) {
            FluidProperty fluidProperty = material.getProperty(PropertyKey.FLUID);

            if (fluidProperty != null && fluidProperty.getFluid() == null) {
                int temperature = Math.max(material.getBlastTemperature(), fluidProperty.getFluidTemperature());
                Fluid fluid = registerFluid(material, fluidProperty.getFluidType(), temperature, fluidProperty.hasBlock());
                fluidProperty.setFluid(fluid);
                fluidProperty.setFluidTemperature(fluid.getTemperature(), fluid.getTemperature() >= 0);
            }

            PlasmaProperty plasmaProperty = material.getProperty(PropertyKey.PLASMA);
            if (plasmaProperty != null && plasmaProperty.getPlasma() == null) {
                int temperature = (fluidProperty == null ? 0 : fluidProperty.getFluidTemperature()) + 10000;
                Fluid fluid = registerFluid(material, FluidTypes.PLASMA, temperature, false);
                plasmaProperty.setPlasma(fluid);
            }
        }
    }

    public static void handleNonMaterialFluids(@Nonnull Material material, @Nonnull Fluid fluid) {
        material.getProperty(PropertyKey.FLUID).setFluid(fluid);
        material.getProperty(PropertyKey.FLUID).setFluidTemperature(fluid.getTemperature());
        List<String> tooltip = new ArrayList<>();
        if (!material.getChemicalFormula().isEmpty()) {
            tooltip.add(TextFormatting.YELLOW + material.getChemicalFormula());
        }
        tooltip.add(LocalizationUtils.format("gregtech.fluid.temperature", material.getProperty(PropertyKey.FLUID).getFluidTemperature()));
        tooltip.add(LocalizationUtils.format(material.getProperty(PropertyKey.FLUID).getFluidType().getUnlocalizedTooltip()));
        tooltip.addAll(material.getProperty(PropertyKey.FLUID).getFluidType().getAdditionalTooltips());
        FluidTooltipUtil.registerTooltip(fluid, tooltip);
    }

    private static void addAlternativeNames() {
        setAlternativeFluidName(Materials.Ethanol, FluidTypes.LIQUID, "bio.ethanol");
        setAlternativeFluidName(Materials.SeedOil, FluidTypes.LIQUID, "seed.oil");
        setAlternativeFluidName(Materials.Ice, FluidTypes.LIQUID, "fluid.ice");
        setAlternativeFluidName(Materials.Diesel, FluidTypes.LIQUID, "fuel");
    }

    private static void setCustomTextures() {
        setMaterialFluidTexture(Materials.Air, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.Deuterium, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.Tritium, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.Helium, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.Helium, FluidTypes.PLASMA);
        setMaterialFluidTexture(Materials.Helium3, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.Fluorine, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.TitaniumTetrachloride, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Steam, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.OilHeavy, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.RawOil, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.OilLight, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.HydrogenSulfide, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.SulfuricGas, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.RefineryGas, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.SulfuricNaphtha, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.SulfuricLightFuel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.SulfuricHeavyFuel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Naphtha, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.LightFuel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.HeavyFuel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.LPG, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.LightlySteamCrackedLightFuel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.SeverelySteamCrackedLightFuel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.LightlySteamCrackedHeavyFuel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.SeverelySteamCrackedHeavyFuel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Chlorine, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.CetaneBoostedDiesel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.SodiumPersulfate, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.GlycerylTrinitrate, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Lubricant, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Creosote, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.SeedOil, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Oil, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Diesel, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Biomass, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Ethanol, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.SulfuricAcid, FluidTypes.ACID);
        setMaterialFluidTexture(Materials.Milk, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.McGuffium239, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Glue, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.HydrochloricAcid, FluidTypes.ACID);
        setMaterialFluidTexture(Materials.LeadZincSolution, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.NaturalGas, FluidTypes.GAS);
        setMaterialFluidTexture(Materials.Blaze, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.FluoroantimonicAcid, FluidTypes.ACID);
        setMaterialFluidTexture(Materials.Naquadah, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.NaquadahEnriched, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Naquadria, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.Ice, FluidTypes.LIQUID);
        setMaterialFluidTexture(Materials.UUMatter, FluidTypes.LIQUID);
    }

    private static void setCustomTooltips() {
        // TODO Vitriols
    }

    /**
     * Every {@link gregtech.api.unification.material.info.MaterialIconSet} that has fluids requires a registered sprite
     */
    private static void registerIconFluidSprites() {
        for (MaterialIconSet materialIconSet : MaterialIconSet.ICON_SETS.values()) {
            fluidSprites.add(new ResourceLocation(GTValues.MODID, "blocks/material_sets/" + materialIconSet.getName() + "/fluid"));
        }
    }

    /**
     * Changes the texture of specified material's fluid.
     * The material color is overlayed on top of the texture, set the materialRGB to 0xFFFFFF to remove the overlay.
     *
     * @param material  the material whose texture to change
     * @param fluidType the type of the fluid
     */
    public static void setMaterialFluidTexture(@Nonnull Material material, @Nonnull FluidType fluidType) {
        String path = "blocks/fluids/fluid." + material.toString();
        if (fluidType.equals(FluidTypes.PLASMA))
            path += ".plasma";
        ResourceLocation resourceLocation = new ResourceLocation(GTValues.MODID, path);
        setMaterialFluidTexture(material, fluidType, resourceLocation);
    }

    /**
     * Changes the texture of specified material's fluid.
     * The material color is overlayed on top of the texture, set the materialRGB to 0xFFFFFF to remove the overlay.
     *
     * @param material        the material whose texture to change
     * @param fluidType       the type of the fluid
     * @param textureLocation the location of the texture to use
     */
    public static void setMaterialFluidTexture(Material material, FluidType fluidType, ResourceLocation textureLocation) {
        fluidTextureMap.computeIfAbsent(material, key -> {
            Map<FluidType, ResourceLocation> map = new Object2ObjectOpenHashMap<>();
            map.put(fluidType, textureLocation);
            return map;
        }).computeIfAbsent(fluidType, key -> {
            Map<FluidType, ResourceLocation> map = fluidTextureMap.get(material);
            map.put(fluidType, textureLocation);
            return textureLocation;
        });
        fluidSprites.add(textureLocation);
    }

    public static void setAlternativeFluidName(Material material, @Nonnull FluidType fluidType, String alternativeName) {
        alternativeFluidNames.put(fluidType.getNameForMaterial(material), alternativeName);
    }

    @Nonnull
    public static Fluid registerFluid(@Nonnull Material material, @Nonnull FluidType fluidType, int temperature, boolean generateBlock) {
        String materialName = material.toString();
        String fluidName = fluidType.getNameForMaterial(material);
        Fluid fluid = FluidRegistry.getFluid(fluidName);

        // check if fluid is registered from elsewhere under an alternative name
        if (fluid == null && alternativeFluidNames.containsKey(fluidName)) {
            String altName = alternativeFluidNames.get(fluidName);
            fluid = FluidRegistry.getFluid(altName);
        }

        // if the material is still not registered by this point, register it
        if (fluid == null) {
            // determine texture for use
            ResourceLocation textureLocation = fluidTextureMap.computeIfAbsent(material, key -> {
                Map<FluidType, ResourceLocation> map = new Object2ObjectOpenHashMap<>();
                if (fluidType.equals(FluidTypes.PLASMA))
                    map.put(fluidType, AUTO_GENERATED_PLASMA_TEXTURE);
                else
                    map.put(fluidType, MaterialIconType.fluid.getBlockPath(material.getMaterialIconSet()));
                return map;
            }).computeIfAbsent(fluidType, key -> {
                Map<FluidType, ResourceLocation> map = fluidTextureMap.get(material);
                if (fluidType.equals(FluidTypes.PLASMA))
                    map.put(fluidType, AUTO_GENERATED_PLASMA_TEXTURE);
                else
                    map.put(fluidType, MaterialIconType.fluid.getBlockPath(material.getMaterialIconSet()));
                return map.get(fluidType);
            });

            // create the new fluid
            fluid = new MaterialFluid(fluidName, material, fluidType, textureLocation);
            fluid.setTemperature(temperature);
            fluid.setDensity((int) (material.getMass() * 100));
            if (material.hasFluidColor()) {
                fluid.setColor(GTUtility.convertRGBtoOpaqueRGBA_MC(material.getMaterialRGB()));
            } else {
                fluid.setColor(0xFFFFFFFF);
            }

            // set properties and register
            FluidType.setFluidProperties(fluidType, fluid);

            if (material.hasFlag(MaterialFlags.STICKY)) fluid.setViscosity(2000);

            ((MaterialFluid) fluid).registerFluidTooltip();
            FluidRegistry.registerFluid(fluid);
        }

        // add buckets for each fluid
        FluidRegistry.addBucketForFluid(fluid);

        // generate fluid blocks if the material has one, and the current state being handled is not plasma
        if (generateBlock && fluid.getBlock() == null && fluidType != FluidTypes.PLASMA) {
            GTFluidMaterial fluidMaterial = new GTFluidMaterial(GTUtility.getMapColor(material.getMaterialRGB()),
                    material.hasFlag(MaterialFlags.STICKY));

            BlockFluidBase fluidBlock = new MaterialFluidBlock(fluid, fluidMaterial, material);
            fluidBlock.setRegistryName("fluid." + materialName);
            MetaBlocks.FLUID_BLOCKS.add(fluidBlock);
        }

        fluidToMaterialMappings.put(fluid.getName(), material);
        return fluid;
    }

    @Nullable
    public static Material getMaterialFromFluid(@Nonnull Fluid fluid) {
        Material material = fluidToMaterialMappings.get(fluid.getName());
        if (material.hasFluid()) return material;
        return null;
    }
}
