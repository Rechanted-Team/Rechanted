package net.tagtart.rechanted.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.tagtart.rechanted.component.ModDataComponents;
import net.tagtart.rechanted.config.RechantedCommonConfigs;
import net.tagtart.rechanted.enchantment.ModEnchantments;
import net.tagtart.rechanted.sound.ModSounds;
import net.tagtart.rechanted.util.BookRarityProperties;
import net.tagtart.rechanted.util.UtilFunctions;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RechantedBookItem extends Item {
    private static final String DEFAULT_BOOK_DESCRIPTION = "Mystical powers imbue this book granting its bearers power.";

    // Holds the item names for each icon on the tooltip
//    public static final String[] BASE_ICON_ITEMS = {
//            "minecraft:iron_helmet",
//            "minecraft:iron_chestplate",
//            "minecraft:iron_leggings",
//            "minecraft:iron_boots",
//            "minecraft:iron_pickaxe",
//            "minecraft:iron_axe",
//            "minecraft:iron_shovel",
//            "minecraft:iron_hoe",
//            "minecraft:iron_sword",
//            "minecraft:fishing_rod",
//            "minecraft:trident",
//            "minecraft:shield",
//            "minecraft:bow",
//            "minecraft:crossbow",
//            "minecraft:elytra",
//            "minecraft:iron_spear",
//            "minecraft:mace"
//    };

    private static HashMap<String, ItemStack> TOOLTIP_ICON_ITEMS;
    public static HashMap<String, ItemStack> getTooltipIconItems() {
        if (TOOLTIP_ICON_ITEMS != null) {
            return TOOLTIP_ICON_ITEMS;
        }

        TOOLTIP_ICON_ITEMS = new HashMap<>();

        List<? extends String> tooltipItemNames = RechantedCommonConfigs.COMPATIBLE_TOOLTIP_ITEMS_LIST.get();

        for (String itemName : tooltipItemNames) {

            ItemStack iconStack = UtilFunctions.getItemStackFromString(itemName);
            if (iconStack == ItemStack.EMPTY || iconStack.is(Items.AIR)) {
                continue;
            }

            // Extremely lazy way to do this; but the point is so that the custom tooltip component
            // that will parse this, see the "|-|" as a unique identifier, and will know this is an
            // icon stack we should render. It works though.
            TOOLTIP_ICON_ITEMS.put("|| "+itemName+" ||", iconStack);
        }

        return TOOLTIP_ICON_ITEMS;
    }

    public RechantedBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(ItemStack pStack) {

        ItemEnchantments enchants = pStack.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchants == null)
            return Component.literal("Empty book!");
        if (enchants.entrySet().isEmpty())
            return Component.literal("Invalid enchantment!");

        Holder<Enchantment> enchantmentHolder = enchants.entrySet().iterator().next().getKey();
        if (enchantmentHolder == null || enchants.entrySet().isEmpty())
            return Component.literal("Invalid enchantment!");

        Enchantment enchantment = enchantmentHolder.value();

        int enchantmentLevel = enchants.entrySet().iterator().next().getIntValue();
        String enchantmentRaw = enchantmentHolder.unwrapKey().orElseThrow().location().toString();

        String[] enchantmentInfo = enchantmentRaw.split(":");
        String enchantmentSource = enchantmentInfo[0];
        String enchantmentName = enchantmentInfo[1];
        BookRarityProperties enchantRarityInfo = UtilFunctions.getPropertiesFromEnchantment(enchantmentRaw);

        if (enchantRarityInfo == null)
            return Component.literal("Invalid enchantment!");

        String romanLevel = resolveEnchantmentLevelText(enchantment, enchantmentLevel);
        String enchantFormattedName = resolveEnchantmentDisplayName(enchantmentSource, enchantmentName);
        String rarityIcon = Component.translatable("enchantment.rarity." + enchantRarityInfo.key).getString();
        String fullName = romanLevel.isEmpty() ? enchantFormattedName : enchantFormattedName + " " + romanLevel;

        return Component.literal(rarityIcon + " ")
                .append(Component.literal(fullName)
                        .withStyle(Style.EMPTY.withColor(enchantRarityInfo.color)));

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int maxWidthTooltip = 165;
        int successRate = stack.getOrDefault(ModDataComponents.SUCCESS_RATE, 0);

        var storedEnchant = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (storedEnchant == null || storedEnchant.isEmpty()) {
            return;
        }
        Holder<Enchantment> enchantmentHolder = storedEnchant.entrySet().iterator().next().getKey();
        String enchantmentRaw = enchantmentHolder.unwrapKey().orElseThrow().location().toString();

        String[] enchantmentInfo = enchantmentRaw.split(":");

        tooltipComponents.add(Component.literal(" "));

        String descriptionKey = "enchantment." + enchantmentInfo[0] + "." + enchantmentInfo[1] + ".desc";
        String resolvedText = Component.translatable(descriptionKey).getString();
        if (resolvedText.equals(descriptionKey)) {
            resolvedText = DEFAULT_BOOK_DESCRIPTION;
        }
        List<String> splitText = UtilFunctions.wrapText(resolvedText, maxWidthTooltip);
        for (String line : splitText) {
            tooltipComponents.add(Component.literal(line.trim()));
        }

        tooltipComponents.add(Component.literal(" "));

        tooltipComponents.add(Component.literal("Success Rate: ")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN)) // Green for "Success Rate: "
                .append(Component.literal(successRate + "%")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))));
        tooltipComponents.add(Component.literal(" "));
        tooltipComponents.add(Component.literal("→ ᴅʀᴀɢ ɴ ᴅʀᴏᴘ ᴏɴᴛᴏ ʏᴏᴜʀ").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("ɪᴛᴇᴍ ᴛᴏ ᴀᴘᴘʟʏ ᴛʜɪꜱ ʙᴏᴏᴋ").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal(" "));

        tooltipComponents.add(getApplicableIcons(enchantmentHolder));


        List<Component> incompatibilityTooltipLines = UtilFunctions.getIncompatibilityTooltipLines(enchantmentHolder, context, tooltipFlag);
        if (!incompatibilityTooltipLines.isEmpty()) {
            tooltipComponents.add(Component.literal(" "));
            tooltipComponents.addAll(incompatibilityTooltipLines);
        }

    }



    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean canGrindstoneRepair(ItemStack stack) {
        return true;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        ItemStack itemToEnchantStack = slot.getItem();

        boolean shouldAttemptEnchant = action == ClickAction.PRIMARY
                && (itemToEnchantStack.isEnchanted() || itemToEnchantStack.isEnchantable());
        if (!shouldAttemptEnchant) {
            return false;
        }

        if (player.getAbilities().instabuild) {
            return applyCreative(stack, itemToEnchantStack, player);
        }

        return applyNormal(stack, itemToEnchantStack, player);
    }

    private boolean applyNormal(ItemStack stack, ItemStack itemToEnchantStack, Player player) {
        // Keep original non-creative behavior server-side only.
        if (player.level().isClientSide()) {
            return true;
        }

        return applyBook(stack, itemToEnchantStack, player, false);
    }

    private boolean applyCreative(ItemStack stack, ItemStack itemToEnchantStack, Player player) {
        return applyBook(stack, itemToEnchantStack, player, true);
    }

    private boolean applyBook(ItemStack stack, ItemStack itemToEnchantStack, Player player, boolean isCreative) {
        Level level = player.level();

        // Get the stored enchantment from the book using data components
        ItemEnchantments storedEnchants = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (storedEnchants == null || storedEnchants.isEmpty())
            return false;

        // Get the first (and should be only) enchantment from the book
        var bookEnchantEntry = storedEnchants.entrySet().iterator().next();
        Holder<Enchantment> enchantmentHolder = bookEnchantEntry.getKey();
        int enchantmentLevel = bookEnchantEntry.getIntValue();
        Enchantment enchantment = enchantmentHolder.value();
        boolean isRebirthBook = enchantmentHolder.unwrapKey().map(key -> key.equals(ModEnchantments.REBIRTH))
                .orElse(false);

        if (isRebirthBook && itemToEnchantStack.getOrDefault(ModDataComponents.REBORN, false)) {
            sendClientMessage(player,
                    Component.literal("This item has already been Reborn.").withStyle(ChatFormatting.RED));
            return true;
        }

        boolean canEnchantGeneral = itemToEnchantStack.supportsEnchantment(enchantmentHolder);

        // Get current enchantments on the target item
        ItemEnchantments itemEnchants = itemToEnchantStack.getOrDefault(DataComponents.ENCHANTMENTS,
                ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutableEnchants = new ItemEnchantments.Mutable(itemEnchants);

        if (canEnchantGeneral && !itemToEnchantStack.isEnchanted()) {
            // No enchantments on the other item so it can be applied
            mutableEnchants.set(enchantmentHolder, enchantmentLevel);
            applyEnchantsSafely(mutableEnchants, itemToEnchantStack, player, level, stack, isCreative);
        } else if (canEnchantGeneral) {
            // Check if item already has this enchantment
            if (itemEnchants.getLevel(enchantmentHolder) > 0) {
                int otherEnchantLevel = itemEnchants.getLevel(enchantmentHolder);
                if (otherEnchantLevel == enchantment.getMaxLevel()) {
                    sendClientMessage(player, Component.literal("This item already has this enchantment maxed!")
                            .withStyle(ChatFormatting.RED));

                } else if (enchantmentLevel < otherEnchantLevel) {
                    sendClientMessage(player,
                            Component.literal("This item already has this enchantment!").withStyle(ChatFormatting.RED));

                } else {
                    if (otherEnchantLevel == enchantmentLevel) {
                        mutableEnchants.set(enchantmentHolder, otherEnchantLevel + 1);
                    } else {
                        mutableEnchants.set(enchantmentHolder, enchantmentLevel);
                    }
                    applyEnchantsSafely(mutableEnchants, itemToEnchantStack, player, level, stack, isCreative);
                }
            } else {
                // Check compatibility with other enchantments
                boolean allCompatible = true;
                for (var otherEnchantEntry : itemEnchants.entrySet()) {
                    Holder<Enchantment> otherEnchantHolder = otherEnchantEntry.getKey();
                    Enchantment otherEnchantment = otherEnchantHolder.value();

                    boolean compatibleForward = Enchantment.areCompatible(enchantmentHolder, otherEnchantHolder);
                    boolean compatibleReverse = Enchantment.areCompatible(otherEnchantHolder, enchantmentHolder);
                    if (!compatibleForward || !compatibleReverse) {
                        sendClientMessage(player, Component.translatable(enchantment.description().getString())
                                .append(" is not compatible with ")
                                .append(Component.translatable(otherEnchantment.description().getString()))
                                .withStyle(ChatFormatting.RED));
                        allCompatible = false;
                        break;
                    }
                }

                if (allCompatible) {
                    // Enchant good to go, enchant that thing!
                    mutableEnchants.set(enchantmentHolder, enchantmentLevel);
                    applyEnchantsSafely(mutableEnchants, itemToEnchantStack, player, level, stack, isCreative);
                }
            }
        } else {
            sendClientMessage(player,
                    Component.literal("Enchantment cannot be applied to this item").withStyle(ChatFormatting.RED));
        }

        return true;
    }

    private void applyEnchantsSafely(ItemEnchantments.Mutable enchants, ItemStack item, Player player, Level level,
            ItemStack enchantedBook, boolean isCreative) {
        int successRate = enchantedBook.getOrDefault(ModDataComponents.SUCCESS_RATE, 0);

        if (isSuccessfulEnchant(successRate)) {
            // Apply the enchantments to the item using data components
            item.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());
            playEnchantSound(player, level, SoundEvents.PLAYER_LEVELUP, isCreative);
            sendClientMessage(player, Component.literal("Successfully enchanted.").withStyle(ChatFormatting.GREEN));
        } else {
            // Play bad sound
            playEnchantSound(player, level, ModSounds.ENCHANTED_BOOK_FAIL.get(), isCreative);
            sendClientMessage(player,
                    Component.literal("Enchantment failed to apply to item!").withStyle(ChatFormatting.RED));
        }
        // Break the book regardless of success or not
        enchantedBook.shrink(1);
    }

    private void playEnchantSound(Player player, Level level, SoundEvent soundEvent, boolean isCreative) {
        if (isCreative) {
            player.playSound(soundEvent, 1f, 1f);
            return;
        }

        level.playSound(null, player.getOnPos(), soundEvent, SoundSource.PLAYERS, 1f, 1f);
    }

    private void sendClientMessage(Player pPlayer, Component textComponent) {
        pPlayer.sendSystemMessage(textComponent);
    }

    private boolean isSuccessfulEnchant(int successRate) {
        Random random = new Random();
        return random.nextInt(100) < successRate;
    }

    public static ArrayList<ItemStack> getApplicableItemStacks(Holder<Enchantment> enchantment) {
        if (enchantment == null) {
            return new ArrayList<>();
        }


        ArrayList<ItemStack> enchantable_items = new ArrayList<>();
        List<? extends String> tooltipItemNames = RechantedCommonConfigs.COMPATIBLE_TOOLTIP_ITEMS_LIST.get();

        // Looping through config array so icon order is the same as defined in config.
        for (String itemName : tooltipItemNames) {
            String itemToKeyFormat = "|| "+itemName+" ||";
            if (!getTooltipIconItems().containsKey(itemToKeyFormat)) continue;

            ItemStack itemStack = getTooltipIconItems().get(itemToKeyFormat);
            if (itemStack.supportsEnchantment(enchantment))  {
                enchantable_items.add(itemStack);
            }

        }

        return enchantable_items;
    }

    public static Component getApplicableIcons(Holder<Enchantment> enchantment) {
        if (enchantment == null) {
            return Component.literal("");
        }

        MutableComponent text = Component.translatable("");
        List<? extends String> tooltipItemNames = RechantedCommonConfigs.COMPATIBLE_TOOLTIP_ITEMS_LIST.get();

        // Looping through config array so icon order is the same as defined in config.
        for (String itemName : tooltipItemNames) {
            String itemToKeyFormat = "|| "+itemName+" ||";
            if (!getTooltipIconItems().containsKey(itemToKeyFormat)) continue;

            if (getTooltipIconItems().get(itemToKeyFormat).supportsEnchantment(enchantment)) {
                text.append(Component.literal(itemToKeyFormat));
            }

        }
        return text;
    }

    private static String resolveEnchantmentDisplayName(String enchantmentSource, String enchantmentName) {
        String enchantmentTranslationKey = "enchantment." + enchantmentSource + "." + enchantmentName;
        String translatedName = Component.translatable(enchantmentTranslationKey).getString();
        if (!translatedName.equals(enchantmentTranslationKey)) {
            return translatedName;
        }

        return fallbackEnchantmentName(enchantmentName);
    }

    private static String resolveEnchantmentLevelText(Enchantment enchantment, int enchantmentLevel) {
        if (enchantment.getMaxLevel() == 1) {
            return "";
        }

        String levelTranslationKey = "enchantment.level." + enchantmentLevel;
        String translatedLevel = Component.translatable(levelTranslationKey).getString();
        if (!translatedLevel.equals(levelTranslationKey)) {
            return translatedLevel;
        }

        return UtilFunctions.intToRoman(enchantmentLevel);
    }

    private static String fallbackEnchantmentName(String enchantmentName) {
        String[] segments = enchantmentName.split("_");
        StringBuilder formattedName = new StringBuilder();
        for (String segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }

            if (!formattedName.isEmpty()) {
                formattedName.append(" ");
            }

            String lowercaseSegment = segment.toLowerCase(Locale.ROOT);
            formattedName.append(Character.toUpperCase(lowercaseSegment.charAt(0)));
            if (lowercaseSegment.length() > 1) {
                formattedName.append(lowercaseSegment.substring(1));
            }
        }

        return formattedName.toString();
    }
}
