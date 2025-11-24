package com.ferralith.alkemia.mixin;

import net.minecraft.world.entity.Interaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Interaction.class)
public interface InteractionAccessor {

    @Invoker("setWidth")
    void invokeSetWidth(float width);

    @Invoker("setHeight")
    void invokeSetHeight(float height);
}