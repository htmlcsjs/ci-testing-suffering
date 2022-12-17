package gregtech.api.capability.impl;

import gregtech.api.capability.IHeatingCoil;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.recipes.recipeproperties.TemperatureProperty;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

import static gregtech.api.recipes.logic.OverclockingLogic.heatingCoilOverclockingLogic;

/**
 * RecipeLogic for multiblocks that use temperature for raising speed and lowering energy usage
 * Used with RecipeMaps that run recipes using the {@link TemperatureProperty}
 */
public class HeatingCoilRecipeLogic extends MultiblockRecipeLogic {

    public HeatingCoilRecipeLogic(RecipeMapMultiblockController metaTileEntity) {
        super(metaTileEntity);
    }

    @Override
    protected int[] runOverclockingLogic(@Nonnull IRecipePropertyStorage propertyStorage, int recipeEUt, long maxVoltage, int duration, int amountOC) {
        // apply maintenance penalties
        Pair<Integer, Double> maintenanceValues = getMaintenanceValues();

        return heatingCoilOverclockingLogic(
                Math.abs(recipeEUt),
                maxVoltage,
                (int) Math.round(duration * maintenanceValues.getValue()),
                amountOC,
                ((IHeatingCoil) metaTileEntity).getCurrentTemperature(),
                propertyStorage.getRecipePropertyValue(TemperatureProperty.getInstance(), 0)
        );
    }


}
