package com.syric.aetheric_tetranomicon.effects;

import com.aetherteam.aether.item.AetherItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.zepalesque.redux.client.audio.ReduxSoundEvents;
import net.zepalesque.redux.item.ReduxItems;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.event.ModularItemDamageEvent;
import se.mickelus.tetra.items.modular.ModularItem;

import java.util.ArrayList;
import java.util.List;

public class VeridiumInfusionEffect {
    public static final ItemEffect veridium_infusable = ItemEffect.get("aetheric_tetranomicon:veridium_infusable");

    //Handle mining speed
    /**
     * @param event Infused veridium tools have increased mining speed.
     */
    @SubscribeEvent
    public void mineEvent(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.getItem() instanceof ModularItem item) {
            int level = item.getEffectLevel(heldStack, veridium_infusable);

            boolean veridium = level > 0;
            boolean infused = getInfusionLevel(heldStack) > 0;
            boolean notCanceled = !event.isCanceled();

            if (veridium && infused && notCanceled) {
                float original_speed = event.getOriginalSpeed();
                float new_speed = original_speed + 4.75F;
                event.setNewSpeed(new_speed);
            }
        }
    }

    //Handle durability
    /**
     * @param event Infused veridium tools use 4x as much durability.
     */
    @SubscribeEvent
    public void durabilityEvent(ModularItemDamageEvent event) {
        ItemStack stack = event.getItemStack();
        ModularItem modularItem = (ModularItem) stack.getItem();
        int level = modularItem.getEffectLevel(stack, veridium_infusable);

        boolean veridium = level > 0;
        boolean infused = getInfusionLevel(stack) > 0;
        boolean notCanceled = !event.isCanceled();
        boolean takingDamage = event.getAmount() > 0;

        if (veridium && infused && notCanceled && takingDamage) {
            event.setAmount(event.getAmount() * 4);
        }
    }

    //Handle tooltip
    public static List<Component> addTooltip(ItemStack stack) {
        List<Component> tooltipComponents = new ArrayList<>();
        if (stack.getItem() instanceof ModularItem item) {
            int level = item.getEffectLevel(stack, veridium_infusable);
            if (level > 0) {
                MutableComponent component = Component.translatable("tooltip.aether_redux.ambrosium_charge", getInfusionLevel(stack)).withStyle(ChatFormatting.GRAY);
                tooltipComponents.add(component);
                Component c = ReduxItems.TooltipUtils.SHIFT_OR_DEFAULT.apply(Component.translatable("gui.aether_redux.infusion_tooltip"));
                tooltipComponents.add(c);
            }
        }
        return tooltipComponents;
    }

    //Handle adding ambrosium
    /**
     * @param event Veridium tools can be infused by adding ambrosium shards.
     */
    @SubscribeEvent
    public void infuseEvent(ItemStackedOnOtherEvent event) {
//        AethericTetranomicon.LOGGER.info("ItemStackedOnOtherEvent detected");
        ItemStack carriedItemStack = event.getStackedOnItem();
        ItemStack stackedOnItemStack = event.getCarriedItem();
        Slot slot = event.getSlot();
        Player player = event.getPlayer();
        ClickAction click = event.getClickAction();
//        AethericTetranomicon.LOGGER.info(String.format("Stacking a %s on a %s", carriedItemStack.getItem(), stackedOnItemStack.getItem()));

        if (stackedOnItemStack.getItem() instanceof ModularItem item) {
//            AethericTetranomicon.LOGGER.info("stacking on modular item");
            int level = item.getEffectLevel(stackedOnItemStack, veridium_infusable);

            boolean veridium = level > 0;
            boolean allowed_slot = slot.allowModification(player);
            boolean carried_is_ambrosium = carriedItemStack.getItem() == AetherItems.AMBROSIUM_SHARD.get();
            boolean right_click = click == ClickAction.SECONDARY;
            boolean not_full_charge = getInfusionLevel(stackedOnItemStack) < 64;

//            AethericTetranomicon.LOGGER.info(String.format("Veridium: %s, allowed slot: %s, ambrosium: %s, right click: %s, not full charge: %s", veridium, allowed_slot, carried_is_ambrosium, right_click, not_full_charge));
//            AethericTetranomicon.LOGGER.info(String.format("Click action: %s", click.toString()));

            if (veridium && allowed_slot && carried_is_ambrosium && right_click && not_full_charge) {
//                AethericTetranomicon.LOGGER.info("Attempting to increase infusion level");
                increaseInfusionLevel(stackedOnItemStack);
                player.playSound(ReduxSoundEvents.INFUSE_ITEM.get(), 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                carriedItemStack.shrink(1);
                event.setCanceled(true);
            }
        }
    }

    //Handle using up charge
    /**
     * @param event Infused veridium tools reduce their charge when they break a block.
     */
    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.getItem() instanceof ModularItem item) {
            int level = item.getEffectLevel(heldStack, veridium_infusable);

            boolean veridium = level > 0;
            boolean infused = getInfusionLevel(heldStack) > 0;
            boolean notCanceled = !event.isCanceled();
            boolean notCreative = !player.isCreative();

            if (veridium && infused && notCanceled && notCreative) {
//                AethericTetranomicon.LOGGER.info("Attempting to decrease infusion level due to broken block");
                decreaseInfusionLevel(heldStack);
            }
        }
    }
    /**
     * @param event Infused veridium tools reduce their charge when they damage an enemy.
     */
    @SubscribeEvent
    public void damageEvent(LivingHurtEvent event) {
        Entity entity = event.getSource().getEntity();
        if (entity instanceof Player player) {
            ItemStack heldStack = player.getMainHandItem();
            if (heldStack.getItem() instanceof ModularItem item) {
                int level = item.getEffectLevel(heldStack, veridium_infusable);

                boolean veridium = level > 0;
                boolean infused = getInfusionLevel(heldStack) > 0;
                boolean notCanceled = !event.isCanceled();
                boolean notCreative = !player.isCreative();

                if (veridium && infused && notCanceled && notCreative) {
//                    AethericTetranomicon.LOGGER.info("Attempting to decrease infusion level due to damaged enemy");
                    decreaseInfusionLevel(heldStack);
                }
            }
        }
    }


    //Handle checking charge
    private static int getInfusionLevel(ItemStack itemStack) {
        CompoundTag compound = itemStack.hasTag() ? itemStack.getOrCreateTag().copy() : new CompoundTag();
        return compound.getInt("veridium_infusion_charge");
    }

    //Handle changing charge (increase by 8 to max of 64, decrease by 1 to min of 0)
    private void increaseInfusionLevel(ItemStack itemStack) {
        int previousLevel = getInfusionLevel(itemStack);
        int newLevel = Math.min(previousLevel + 8, 64);
        setInfusionLevel(itemStack, newLevel);
    }
    private void decreaseInfusionLevel(ItemStack itemStack) {
        int previousLevel = getInfusionLevel(itemStack);
        int newLevel = Math.max(previousLevel - 1, 0);
        setInfusionLevel(itemStack, newLevel);
    }
    private void setInfusionLevel(ItemStack itemStack, int level) {
        level = Math.max(level, 0);
        level = Math.min(level, 64);
        CompoundTag compoundTag = itemStack.hasTag() ? itemStack.getOrCreateTag().copy() : new CompoundTag();
        compoundTag.putInt("veridium_infusion_charge", level);
        itemStack.setTag(compoundTag);
//        AethericTetranomicon.LOGGER.info(String.format("setInfusionLevel attempted to set an item's charge to %s", level));
    }


}
