package com.ferralith.alkemia.client;

import com.ferralith.alkemia.entity.renderer.JarBlockEntityRenderer;
import com.ferralith.alkemia.item.renderer.JarItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class JarClientItemExtensions implements IClientItemExtensions {
    private final JarItemRenderer myBEWLR = new JarItemRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return myBEWLR;
    }
}
