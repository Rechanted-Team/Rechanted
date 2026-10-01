package net.tagtart.rechanted.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Power and consumption candidates captured together during a single table scan. */
public record EnchantingPowerSources(double totalPower, List<Provider> providers) {
    public EnchantingPowerSources {
        providers = List.copyOf(providers);
    }

    public record Provider(BlockPos position, float power) {}

    public static boolean meetsRequirement(double suppliedPower, int requiredPower) {
        // The hook returns floats. Allow one float ULP at the requirement so decimal
        // contributions such as 3.8f + 0.2f do not incorrectly fail a requirement of 4.
        return suppliedPower >= requiredPower - (double) Math.ulp((float) requiredPower);
    }

    /** Rolls destruction against required power, not the number of provider blocks. */
    public void consume(Level level, int requiredPower, double breakChance, Random random) {
        if (requiredPower <= 0 || breakChance <= 0) {
            return;
        }

        List<Provider> candidates = new ArrayList<>(providers);
        Collections.shuffle(candidates, random);
        double remainingPower = requiredPower;
        for (Provider provider : candidates) {
            if (meetsRequirement(requiredPower - remainingPower, requiredPower)) {
                break;
            }

            // Select enough sources to cover the requirement, regardless of whether
            // earlier rolls broke blocks. Prorate the last roll if only part of that
            // source's power is needed. A normal bookshelf still gets one full roll.
            double powerInBudget = Math.min(provider.power(), remainingPower);
            double adjustedBreakChance = breakChance * (powerInBudget / provider.power());
            if (random.nextFloat() < adjustedBreakChance) {
                level.destroyBlock(provider.position(), false);
            }
            remainingPower -= powerInBudget;
        }
    }
}
