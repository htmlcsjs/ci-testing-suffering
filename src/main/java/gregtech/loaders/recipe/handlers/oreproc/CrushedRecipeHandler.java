package gregtech.loaders.recipe.handlers.oreproc;

import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.OreProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;

import static gregtech.api.recipes.RecipeMaps.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.loaders.recipe.handlers.oreproc.OreRecipeHandler.processMetalSmelting;

public class CrushedRecipeHandler {

    public static void processCrushed(OrePrefix prefix, Material material, OreProperty property) {
        boolean chancePerTier = ConfigHolder.recipes.oreByproductChancePerTier;
        // Get the byproduct to use for this step
        Material byproduct = GTUtility.selectItemInList(1, material, property.getOreByProducts(), Material.class);
        OrePrefix byproductPrefix = byproduct.hasProperty(PropertyKey.GEM) ? gem : dust;
        int byproductMultiplier = 1;
        if (byproduct.hasProperty(PropertyKey.ORE))
            byproductMultiplier = byproduct.getProperty(PropertyKey.ORE).getOreMultiplier();

        // Forge Hammer recipe
        // Crushed Ore -> Impure Dust
        FORGE_HAMMER_RECIPES.recipeBuilder()
                .input(crushed, material)
                .output(dustImpure, material, property.getOreMultiplier())
                .duration(10).EUt(16).buildAndRegister();

        // Macerator recipe
        // Crushed Ore -> Impure Dust (with byproduct)
        MACERATOR_RECIPES.recipeBuilder()
                .input(crushed, material)
                .output(dustImpure, material, property.getOreMultiplier())
                .chancedOutput(byproductPrefix, byproduct, byproductMultiplier, 2000, chancePerTier ? 500 : 0)
                .output(dust, Stone)
                .duration(400).EUt(2).buildAndRegister();

        // Sluice recipe
        // Crushed Ore -> Purified Ore
        SLUICE_RECIPES.recipeBuilder()
                .input(crushed, material)
                .fluidInputs(Water.getFluid(1000))
                .output(crushedPurified, material)
                .chancedOutput(byproductPrefix, byproduct, byproductMultiplier, 1000, 0)
                .output(dust, Stone)
                .fluidOutputs(SluiceJuice.getFluid(1000))
                .duration(400).EUt(16).buildAndRegister();

        // Chemical Bath recipe
        // Crushed Ore -> Purified Ore + Purified Ore Byproduct
        // Only applies if a byproduct in this Material's byproduct
        // list contains either the WASHING_MERCURY or
        // WASHING_PERSULFATE flags
        // TODO yeet (moved to refined)
//        Material mercuryByproduct = null;
//        Material persulfateByproduct = null;
//        for (Material byproduct : property.getOreByProducts()) {
//            // find the last byproduct in the list with one of these flags (if any)
//            if (byproduct.hasFlag(WASHING_MERCURY)) mercuryByproduct = byproduct;
//            if (byproduct.hasFlag(WASHING_PERSULFATE)) persulfateByproduct = byproduct;
//        }
//
//        if (mercuryByproduct != null) {
//            CHEMICAL_BATH_RECIPES.recipeBuilder()
//                    .input(crushed, material)
//                    .fluidInputs(Mercury.getFluid(100))
//                    .output(crushedPurified, material)
//                    .output(crushedPurified, mercuryByproduct)
//                    .output(dust, SluiceSand)
//                    .output(dust, Stone)
//                    .duration(400).EUt(VA[LV]).buildAndRegister();
//        }
//
//        if (persulfateByproduct != null) {
//            CHEMICAL_BATH_RECIPES.recipeBuilder()
//                    .input(crushed, material)
//                    .fluidInputs(SodiumPersulfate.getFluid(100))
//                    .output(crushedPurified, material)
//                    .output(crushedPurified, persulfateByproduct)
//                    .output(dust, SluiceSand)
//                    .output(dust, Stone)
//                    .duration(400).EUt(VA[LV]).buildAndRegister();
//
//            CHEMICAL_BATH_RECIPES.recipeBuilder()
//                    .input(crushed, material)
//                    .fluidInputs(PotassiumPersulfate.getFluid(100))
//                    .output(crushedPurified, material)
//                    .output(crushedPurified, persulfateByproduct)
//                    .output(dust, SluiceSand)
//                    .output(dust, Stone)
//                    .duration(400).EUt(VA[LV]).buildAndRegister();
//        }

        // Hard Hammer crafting recipe
        // Crushed Ore -> Impure Dust
        ModHandler.addShapelessRecipe(String.format("crushed_ore_to_dust_%s", material),
                OreDictUnifier.get(dustImpure, material, property.getOreMultiplier()), 'h', new UnificationEntry(crushed, material));

        processMetalSmelting(prefix, material, property);
    }
}
