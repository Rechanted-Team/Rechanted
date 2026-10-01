package net.tagtart.rechanted.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class EnchantingPowerConsumptionTest {
    @ParameterizedTest
    @CsvSource({"0.249, true", "0.25, false"})
    void normalShelvesKeepTheExistingRollCountAndChance(float roll, boolean shouldBreak) {
        Level level = mock(Level.class);
        Random random = randomWithRoll(roll);
        var sources = sources(1, 1, 1, 1, 1, 1);
        sources.consume(level, 4, 0.25, random);

        if (shouldBreak) {
            for (int i = 0; i < 4; ++i) {
                verify(level).destroyBlock(sources.providers().get(i).position(), false);
            }
        }
        verifyNoMoreInteractions(level);
        verify(random, times(4)).nextFloat();
        assertEquals(6, sources.providers().size());
        assertEquals(6.0, sources.totalPower());
    }

    @Test
    void fractionalSourcesCoverThePowerBudgetRatherThanABlockCount() {
        Level level = mock(Level.class);
        Random random = randomWithRoll(0.1f);
        var sources = sources(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
        sources.consume(level, 4, 0.25, random);

        for (int i = 0; i < 8; ++i) {
            verify(level).destroyBlock(sources.providers().get(i).position(), false);
        }
        verifyNoMoreInteractions(level);
        verify(random, times(8)).nextFloat();
    }

    @Test
    void failedRollsStillSpendTheSelectionBudget() {
        Level level = mock(Level.class);
        Random random = randomWithRoll(0.9f);
        sources(1, 1, 1, 1).consume(level, 2, 0.25, random);

        verify(random, times(2)).nextFloat();
        verifyNoInteractions(level);
    }

    @ParameterizedTest
    @CsvSource({"0.124, true", "0.125, false", "0.126, false"})
    void highPowerSourceGetsAProportionalBreakChance(float roll, boolean shouldBreak) {
        Level level = mock(Level.class);
        Random random = randomWithRoll(roll);
        var sources = sources(8);
        // Four of eight power is used: 25% base chance becomes 12.5%.
        sources.consume(level, 4, 0.25, random);

        if (shouldBreak) {
            verify(level).destroyBlock(sources.providers().getFirst().position(), false);
        } else {
            verifyNoInteractions(level);
        }
        verify(random).nextFloat();
    }

    @ParameterizedTest
    @CsvSource({"0.249, true", "0.25, false"})
    void mixedSourcesProrateOnlyTheFinalRoll(float finalRoll, boolean finalSourceBreaks) {
        Level level = mock(Level.class);
        Random random = randomWithRoll(0.1f);
        when(random.nextFloat()).thenReturn(0.1f, 0.1f, finalRoll);
        var sources = sources(1, 0.5f, 5, 1);
        // The first two sources use 1.5 power. The last contributes 2.5 of its 5,
        // reducing its 50% base chance to 25%; the surplus source is not rolled.
        sources.consume(level, 4, 0.5, random);

        verify(level).destroyBlock(sources.providers().get(0).position(), false);
        verify(level).destroyBlock(sources.providers().get(1).position(), false);
        if (finalSourceBreaks) {
            verify(level).destroyBlock(sources.providers().get(2).position(), false);
        }
        verifyNoMoreInteractions(level);
        verify(random, times(3)).nextFloat();
    }

    @Test
    void decimalRoundoffDoesNotAddAnExtraRoll() {
        Level level = mock(Level.class);
        Random random = randomWithRoll(0.0f);
        sources(3.8f, 0.2f, 1).consume(level, 4, 1.0, random);
        verify(random, times(2)).nextFloat();
    }

    @Test
    void zeroRequirementOrZeroChanceDoesNotConsumeSources() {
        Level level = mock(Level.class);
        Random random = mock(Random.class);
        var sources = sources(1, 1);
        sources.consume(level, 0, 0.5, random);
        sources.consume(level, 2, 0, random);
        verifyNoInteractions(level, random);
    }

    @Test
    void emptyScanHasNothingToConsume() {
        Level level = mock(Level.class);
        Random random = mock(Random.class);
        sources().consume(level, 4, 0.5, random);
        verifyNoInteractions(level, random);
    }

    private static EnchantingPowerSources sources(float... powers) {
        List<EnchantingPowerSources.Provider> providers = new ArrayList<>();
        double totalPower = 0;
        for (int i = 0; i < powers.length; ++i) {
            providers.add(new EnchantingPowerSources.Provider(new BlockPos(i + 1, 100, 0), powers[i]));
            totalPower += powers[i];
        }
        return new EnchantingPowerSources(totalPower, providers);
    }

    private static Random randomWithRoll(float roll) {
        Random random = mock(Random.class);
        when(random.nextFloat()).thenReturn(roll);
        // Make shuffle leave the fixture order intact so partial rolls are testable.
        when(random.nextInt(anyInt())).thenAnswer(call -> (int) call.getArgument(0) - 1);
        return random;
    }
}
