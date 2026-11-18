package com.terraformersmc.modmenu.api;

import net.minecraft.class_437;

@FunctionalInterface
public interface ConfigScreenFactory<S extends class_437> {
    S create(class_437 parent);
}
