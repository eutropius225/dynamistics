package eutros.dynamistics.jei.categories.ae2;

import eutros.dynamistics.helper.JeiHelper;
import eutros.dynamistics.helper.ModIds;
import eutros.dynamistics.helper.NBTHelper;
import eutros.dynamistics.jei.SingletonRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class PatternCategory implements IRecipeCategory<SingletonRecipe> {

    public static final String UID = "dynamistics:process_pattern_";
    private static final int HEIGHT = 126;
    private static final int WIDTH = 146;
    private final IDrawableStatic background;
    private final IDrawableStatic slot;
    private final ItemStack interfaceStack;
    private final IDrawableStatic craftingBackground;
    private final IDrawable icon;
    private final boolean crafting;
    private final boolean substitute;
    private final IDrawableStatic subs;
    private final IDrawableStatic arrow;
    private int guiStartX;
    private int guiStartY;

    public PatternCategory(IJeiHelpers helpers, boolean crafting, boolean substitute) {
        this.crafting = crafting;
        this.substitute = substitute;
        IGuiHelper guiHelper = helpers.getGuiHelper();

        slot = JeiHelper.getSlotDrawable();
        craftingBackground = guiHelper.createDrawable(
                new ResourceLocation(ModIds.AE2, "textures/guis/pattern" + (crafting ? "" : "2") + ".png"),
                9,
                85,
                126,
                68);
        background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        icon = guiHelper.createDrawableIngredient(new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.AE2, "encoded_pattern")))));
        subs = guiHelper.createDrawable(new ResourceLocation(ModIds.AE2, "textures/guis/states.png"),
                substitute ? 64 : 112,
                48,
                16,
                16);

        interfaceStack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.AE2, "interface"))));

        arrow = guiHelper.createDrawable(new ResourceLocation(ModIds.SELF, "textures/gui/arrows.png"),
                64,
                16,
                32,
                32);

        guiStartY = HEIGHT - craftingBackground.getHeight();
        guiStartX = (WIDTH - craftingBackground.getWidth()) / 2;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public String getUid() {
        return getUid(crafting, substitute);
    }

    public static String getUid(boolean crafting, boolean substitute) {
        return UID + getTypeId(crafting, substitute);
    }

    public static String getTypeId(boolean crafting, boolean substitute) {
        return (crafting ? "crafting_" + (substitute ? "substitute" : "nonsubstitute") : "processing");
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("dynamistics.category.title.ae2.pattern." + getTypeId(crafting, substitute));
    }

    @Nonnull
    @Override
    public String getModName() {
        return "Applied Energistics 2";
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, @Nonnull SingletonRecipe recipe, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        ItemStack patternStack = recipe.stack;

        if(patternStack == null) {
            patternStack = ingredients.getInputs(VanillaTypes.ITEM).get(0).get(0);
        }

        stacks.init(13, true, WIDTH / 2 - 8, 8);
        stacks.set(13, patternStack);
        stacks.setBackground(13, slot);
        stacks.init(14, true, WIDTH / 2 - 8, 29);
        stacks.set(14, interfaceStack);

        if(patternStack.hasTagCompound()) {
            NBTTagCompound tag = patternStack.getTagCompound();
            assert tag != null;

            NonNullList<ItemStack> inputs = NBTHelper.getItemStackList(tag, "in");
            NonNullList<ItemStack> outputs = NBTHelper.getItemStackList(tag, "out");

            int gridStartY = 7;
            int gridStartX = 8;
            int gridSize = 18;
            for(int i = 0; i < inputs.size(); i++) {
                stacks.init(i, true, guiStartX + gridStartX + (gridSize * (i % 3)), guiStartY + gridStartY + (gridSize * (i / 3)));
                stacks.set(i, inputs.get(i));
            }

            int outStartX = 100;
            if(crafting && !outputs.isEmpty()) {
                stacks.init(9, false, guiStartX + outStartX, guiStartY + gridStartY + gridSize);
                stacks.set(9, outputs.get(0));
            } else {
                for(int i = 0; i < outputs.size(); i++) {
                    stacks.init(9 + i, false, guiStartX + outStartX, guiStartY + gridStartY + gridSize * i);
                    stacks.set(9 + i, outputs.get(i));
                }
            }
        }
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        arrow.draw(minecraft,
                (WIDTH - arrow.getWidth()) / 2,
                27);
        craftingBackground.draw(minecraft, guiStartX, guiStartY);

        if(crafting) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiStartX, guiStartY, 0);
            double sf = 0.5;
            GlStateManager.scale(sf, sf, sf);
            subs.draw(minecraft);
            GlStateManager.popMatrix();
        }
    }

}
