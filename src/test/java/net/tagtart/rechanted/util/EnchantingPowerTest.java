package net.tagtart.rechanted.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;

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
        BlockPos pos = new BlockPos(0, 100, 0);
        assertEquals(1.0f, Blocks.BOOKSHELF.defaultBlockState().getEnchantPowerBonus(level, pos));
        assertEquals(1.0f, Blocks.CHISELED_BOOKSHELF.defaultBlockState().getEnchantPowerBonus(level, pos));
        assertEquals(0.0f, Blocks.STONE.defaultBlockState().getEnchantPowerBonus(level, pos));

        var sources = new Pair<>(new BlockState[]{Blocks.BOOKSHELF.defaultBlockState(),
                Blocks.CHISELED_BOOKSHELF.defaultBlockState()}, new BlockPos[]{pos, pos.above()});
        assertEquals(2.0, UtilFunctions.getEnchantingPower(level, sources));

        // Scan real block states and loaded tags through a controlled level fixture.
        BlockPos table = new BlockPos(10, 100, 10);
        BlockPos shelf = table.offset(-3, 0, -3);
        BlockPos chiseled = table.offset(3, 2, 3);
        when(level.getBlockState(any(BlockPos.class))).thenReturn(Blocks.AIR.defaultBlockState());
        when(level.getBlockState(shelf)).thenReturn(Blocks.BOOKSHELF.defaultBlockState());
        when(level.getBlockState(chiseled)).thenReturn(Blocks.CHISELED_BOOKSHELF.defaultBlockState());
        when(level.getBlockState(table.above())).thenReturn(Blocks.STONE.defaultBlockState());
        var scanned = UtilFunctions.scanAroundBlockForBookshelves(level, table);
        assertArrayEquals(new BlockPos[]{shelf, chiseled}, scanned.getB());
        assertEquals(2.0, UtilFunctions.getEnchantingPower(level, scanned));
    }

    @Test
    void scanUsesWorldPositionAndPreservesExistingBounds() {
        Level level = mock(Level.class);
        BlockState empty = mock(BlockState.class);
        Map<BlockPos, BlockState> blocks = new HashMap<>();
        when(level.getBlockState(any(BlockPos.class))).thenAnswer(call ->
                blocks.getOrDefault(call.getArgument(0), empty));

        BlockPos table = new BlockPos(40, 100, -20);
        BlockPos lowerCorner = table.offset(-3, 0, -3);
        BlockPos upperCorner = table.offset(3, 2, 3);
        BlockPos adjacent = table.offset(1, 0, 0);
        BlockState lowerSource = mock(BlockState.class);
        BlockState upperSource = mock(BlockState.class);
        BlockState adjacentSource = mock(BlockState.class);
        when(lowerSource.getEnchantPowerBonus(level, lowerCorner)).thenReturn(0.5f);
        when(upperSource.getEnchantPowerBonus(level, upperCorner)).thenReturn(2.5f);
        when(adjacentSource.getEnchantPowerBonus(level, adjacent)).thenReturn(1.0f);
        blocks.put(lowerCorner, lowerSource);
        blocks.put(upperCorner, upperSource);
        blocks.put(adjacent, adjacentSource);
        blocks.put(table.offset(4, 0, 0), upperSource);
        blocks.put(table.below(), upperSource);
        blocks.put(table.above(3), upperSource);

        var sources = UtilFunctions.scanAroundBlockForBookshelves(level, table);
        assertArrayEquals(new BlockPos[]{lowerCorner, adjacent, upperCorner}, sources.getB());
        assertArrayEquals(new BlockState[]{lowerSource, adjacentSource, upperSource}, sources.getA());
        assertEquals(4.0, UtilFunctions.getEnchantingPower(level, sources));
        verify(level, never()).getBlockState(table.offset(4, 0, 0));
        verify(level, never()).getBlockState(table.below());
        verify(level, never()).getBlockState(table.above(3));
    }

    @Test
    void fractionalPowerIsSummedBeforeCheckingRequirement() {
        Level level = mock(Level.class);
        BlockState source = mock(BlockState.class);
        BlockPos first = new BlockPos(1, 0, 0);
        BlockPos second = new BlockPos(2, 0, 0);
        when(source.getEnchantPowerBonus(level, first)).thenReturn(0.5f);
        when(source.getEnchantPowerBonus(level, second)).thenReturn(2.5f);
        var sources = new Pair<>(new BlockState[]{source, source}, new BlockPos[]{first, second});
        BookRarityProperties properties = mock(BookRarityProperties.class);
        properties.requiredBookShelves = 3;

        double power = UtilFunctions.getEnchantingPower(level, sources);
        assertTrue(UtilFunctions.playerMeetsBookshelfRequirement(properties, power));
        assertFalse(UtilFunctions.playerMeetsBookshelfRequirement(properties, 2.99));
        assertFalse(UtilFunctions.playerMeetsBookshelfRequirement(properties, 0));
    }

    @Test
    void nonPositiveBlocksAreNotConsumptionCandidates() {
        Level level = mock(Level.class);
        BlockState negative = mock(BlockState.class);
        when(level.getBlockState(any(BlockPos.class))).thenReturn(negative);
        when(negative.getEnchantPowerBonus(eq(level), any(BlockPos.class))).thenReturn(-1.0f);
        var sources = UtilFunctions.scanAroundBlockForBookshelves(level, BlockPos.ZERO);
        assertEquals(0, sources.getA().length);
        assertEquals(0, sources.getB().length);
        assertEquals(0.0, UtilFunctions.getEnchantingPower(level, sources));
    }

    @Test
    void powerIsReevaluatedWhenProviderContentsChange() {
        Level level = mock(Level.class);
        BlockState source = mock(BlockState.class);
        BlockPos pos = new BlockPos(1, 0, 0);
        when(source.getEnchantPowerBonus(level, pos)).thenReturn(1.0f, 0.0f);
        var sources = new Pair<>(new BlockState[]{source}, new BlockPos[]{pos});
        assertEquals(1.0, UtilFunctions.getEnchantingPower(level, sources));
        assertEquals(0.0, UtilFunctions.getEnchantingPower(level, sources));
    }
}
