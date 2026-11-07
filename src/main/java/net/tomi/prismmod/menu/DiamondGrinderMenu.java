package net.tomi.prismmod.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.tomi.prismmod.init.PrismBlocks;
import net.tomi.prismmod.init.PrismItems;
import net.tomi.prismmod.init.PrismMenus;

/**
 * The Diamond Grinder screen. Diamonds go in the left slot and the right slot always shows
 * the powder one diamond would make: 2 Diamond Powder. Taking it, by click or shift-click,
 * grinds exactly one diamond, the way taking a crafting result uses up one set of
 * ingredients.
 */
public class DiamondGrinderMenu extends AbstractContainerMenu {

    public static final int POWDER_PER_DIAMOND = 2;

    private static final int INPUT = 0;
    private static final int OUTPUT = 1;
    private static final int INVENTORY_START = 2;
    private static final int HOTBAR_START = INVENTORY_START + 27;
    private static final int END = HOTBAR_START + 9;

    private final ContainerLevelAccess access;
    private final Container input;
    private final Container result = new SimpleContainer(1);

    /** Client-side constructor; the position comes from the opening packet. */
    public DiamondGrinderMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, ContainerLevelAccess.create(inventory.player.level(), pos));
    }

    public DiamondGrinderMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(PrismMenus.DIAMOND_GRINDER, id);
        this.access = access;
        this.input = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                DiamondGrinderMenu.this.slotsChanged(this);
            }
        };

        addSlot(new Slot(input, 0, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.DIAMOND);
            }
        });
        addSlot(new Slot(result, 0, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            /** The preview is all-or-nothing: a right-click must not split the 2 powder. */
            @Override
            public ItemStack remove(int amount) {
                ItemStack stack = getItem();
                if (amount < stack.getCount() && !getCarried().isEmpty()) {
                    return ItemStack.EMPTY;
                }
                return result.removeItemNoUpdate(0);
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                grindOne();
            }
        });
        addStandardInventorySlots(inventory, 8, 84);
        refreshResult();
    }

    private void grindOne() {
        input.removeItem(0, 1);
        access.execute((level, pos) ->
                level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.6F, 1.0F));
        refreshResult();
    }

    private void refreshResult() {
        access.execute((level, pos) -> {
            if (level.isClientSide()) {
                return;
            }
            boolean hasDiamond = input.getItem(0).is(Items.DIAMOND);
            ItemStack shown = hasDiamond
                    ? new ItemStack(PrismItems.DIAMOND_POWDER, POWDER_PER_DIAMOND)
                    : ItemStack.EMPTY;
            if (!ItemStack.matches(result.getItem(0), shown)) {
                result.setItem(0, shown);
            }
            broadcastChanges();
        });
    }

    @Override
    public void slotsChanged(Container changed) {
        super.slotsChanged(changed);
        if (changed == input) {
            refreshResult();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            moved = stack.copy();
            if (index == OUTPUT) {
                if (!moveItemStackTo(stack, INVENTORY_START, END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == INPUT) {
                if (!moveItemStackTo(stack, INVENTORY_START, END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(Items.DIAMOND)) {
                if (!moveItemStackTo(stack, INPUT, INPUT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < HOTBAR_START) {
                if (!moveItemStackTo(stack, HOTBAR_START, END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, INVENTORY_START, HOTBAR_START, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == moved.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return moved;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, PrismBlocks.DIAMOND_GRINDER);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, pos) -> clearContainer(player, input));
    }
}
