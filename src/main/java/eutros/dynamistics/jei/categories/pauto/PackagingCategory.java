package eutros.dynamistics.jei.categories.pauto;

import mezz.jei.api.IJeiHelpers;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;

public class PackagingCategory extends UnpackagingCategory {

    public static String UID = "dynamistics:packaging";

    public PackagingCategory(IJeiHelpers helpers) {
        super(helpers);
        gridStartY = GRID_SIZE / 2;
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Override
    protected boolean isUnpackaging() {
        return !super.isUnpackaging();
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("dynamistics.category.title.pauto.package");
    }

}
