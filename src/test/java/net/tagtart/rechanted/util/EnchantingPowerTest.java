package net.tagtart.rechanted.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnchantingPowerTest {
    @Test
    @ExtendWith(EphemeralTestServerProvider.class)
    void vanillaAndDatapackProvidersUseNeoForgePower(MinecraftServer server) {
        // The ephemeral server loads real datapack tags but does not create a world.
        assertNotNull(server.registryAccess());
        Level level = mock(Level.class);
        BlockPos table = new BlockPos(10, 100, 10);
        BlockPos shelf = table.offset(-3, 0, -3);
        BlockPos chiseled = table.offset(3, 2, 3);
        when(level.getBlockState(any(BlockPos.class))).thenReturn(Blocks.AIR.defaultBlockState());
        when(level.getBlockState(shelf)).thenReturn(Blocks.BOOKSHELF.defaultBlockState());
        when(level.getBlockState(chiseled)).thenReturn(Blocks.CHISELED_BOOKSHELF.defaultBlockState());
        when(level.getBlockState(table.above())).thenReturn(Blocks.STONE.defaultBlockState());

        var sources = UtilFunctions.scanEnchantingPowerSources(level, table);
        assertEquals(2.0, sources.totalPower());
        assertEquals(List.of(
                new EnchantingPowerSources.Provider(shelf, 1.0f),
                new EnchantingPowerSources.Provider(chiseled, 1.0f)), sources.providers());
    }

    @Test
    void scanUsesWorldPositionAndPreservesExistingBounds() {
        Fixture fixture = new Fixture();
        BlockPos table = new BlockPos(40, 100, -20);
        BlockPos lowerCorner = table.offset(-3, 0, -3);
        BlockPos upperCorner = table.offset(3, 2, 3);
        BlockPos adjacent = table.offset(1, 0, 0);
        BlockState lowerSource = fixture.addProvider(lowerCorner, 0.5f);
        BlockState upperSource = fixture.addProvider(upperCorner, 2.5f);
        BlockState adjacentSource = fixture.addProvider(adjacent, 1.0f);
        fixture.addProvider(table.offset(4, 0, 0), 1.0f);
        fixture.addProvider(table.below(), 1.0f);
        fixture.addProvider(table.above(3), 1.0f);

        var sources = UtilFunctions.scanEnchantingPowerSources(fixture.level, table);
        assertEquals(List.of(
                new EnchantingPowerSources.Provider(lowerCorner, 0.5f),
                new EnchantingPowerSources.Provider(adjacent, 1.0f),
                new EnchantingPowerSources.Provider(upperCorner, 2.5f)), sources.providers());
        assertEquals(4.0, sources.totalPower());
        verify(lowerSource).getEnchantPowerBonus(fixture.level, lowerCorner);
        verify(upperSource).getEnchantPowerBonus(fixture.level, upperCorner);
        verify(adjacentSource).getEnchantPowerBonus(fixture.level, adjacent);
        verify(fixture.level, never()).getBlockState(table.offset(4, 0, 0));
        verify(fixture.level, never()).getBlockState(table.below());
        verify(fixture.level, never()).getBlockState(table.above(3));
    }

    @ParameterizedTest
    @CsvSource({"0.5, 2.5, 3", "3.8, 0.2, 4", "2.8, 0.2, 3", "0.9, 0.1, 1"})
    void fractionalPowerIsSummedBeforeCheckingRequirement(float first, float second, int requirement) {
        Fixture fixture = new Fixture();
        fixture.addProvider(new BlockPos(1, 0, 0), first);
        fixture.addProvider(new BlockPos(2, 0, 0), second);
        BookRarityProperties properties = mock(BookRarityProperties.class);
        properties.requiredBookShelves = requirement;

        var sources = UtilFunctions.scanEnchantingPowerSources(fixture.level, BlockPos.ZERO);
        assertTrue(UtilFunctions.playerMeetsEnchantingPowerRequirement(properties, sources.totalPower()));
        assertFalse(UtilFunctions.playerMeetsEnchantingPowerRequirement(properties, requirement - 0.01));
        assertFalse(UtilFunctions.playerMeetsEnchantingPowerRequirement(properties, 0));
    }

    @Test
    void toleranceDoesNotHideARealPowerShortfall() {
        assertTrue(EnchantingPowerSources.meetsRequirement(4.0, 4));
        assertFalse(EnchantingPowerSources.meetsRequirement(4.0 - 2 * Math.ulp(4.0f), 4));
        assertFalse(EnchantingPowerSources.meetsRequirement(3.99, 4));
        assertTrue(EnchantingPowerSources.meetsRequirement(0, 0));
    }

    @ParameterizedTest
    @ValueSource(floats = {0, -1, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY})
    void invalidPowerIsNotCountedOrConsumed(float power) {
        Fixture fixture = new Fixture();
        fixture.addProvider(new BlockPos(1, 0, 0), power);
        var sources = UtilFunctions.scanEnchantingPowerSources(fixture.level, BlockPos.ZERO);
        assertTrue(sources.providers().isEmpty());
        assertEquals(0.0, sources.totalPower());
    }

    @Test
    void eachScanReevaluatesProviderContents() {
        Fixture fixture = new Fixture();
        BlockPos pos = new BlockPos(1, 0, 0);
        BlockState source = fixture.addProvider(pos, 1.0f);
        when(source.getEnchantPowerBonus(fixture.level, pos)).thenReturn(1.0f, 0.0f);

        var before = UtilFunctions.scanEnchantingPowerSources(fixture.level, BlockPos.ZERO);
        var after = UtilFunctions.scanEnchantingPowerSources(fixture.level, BlockPos.ZERO);
        assertEquals(1.0, before.totalPower());
        assertEquals(0.0, after.totalPower());
        assertTrue(after.providers().isEmpty());
        verify(source, times(2)).getEnchantPowerBonus(fixture.level, pos);
    }

    @Test
    void destroyedSourcesDisappearFromTheNextScan() {
        Fixture fixture = new Fixture();
        fixture.addProvider(new BlockPos(1, 0, 0), 1.0f);
        fixture.addProvider(new BlockPos(2, 0, 0), 1.0f);
        fixture.addProvider(new BlockPos(3, 0, 0), 1.0f);
        when(fixture.level.destroyBlock(any(BlockPos.class), eq(false))).thenAnswer(call ->
                fixture.blocks.remove(call.getArgument(0)) != null);

        var before = UtilFunctions.scanEnchantingPowerSources(fixture.level, BlockPos.ZERO);
        before.consume(fixture.level, 2, 1.0, new Random(7));
        var after = UtilFunctions.scanEnchantingPowerSources(fixture.level, BlockPos.ZERO);
        assertEquals(3.0, before.totalPower());
        assertEquals(1.0, after.totalPower());
        assertEquals(1, after.providers().size());
        verify(fixture.level, times(2)).destroyBlock(any(BlockPos.class), eq(false));
    }

    private static class Fixture {
        final Level level = mock(Level.class);
        final Map<BlockPos, BlockState> blocks = new HashMap<>();

        Fixture() {
            BlockState empty = mock(BlockState.class);
            when(level.getBlockState(any(BlockPos.class))).thenAnswer(call ->
                    blocks.getOrDefault(call.getArgument(0), empty));
        }

        BlockState addProvider(BlockPos pos, float power) {
            BlockState state = mock(BlockState.class);
            when(state.getEnchantPowerBonus(level, pos)).thenReturn(power);
            blocks.put(pos, state);
            return state;
        }
    }
}
