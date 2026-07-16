package io.github.xffc.packetcatcher

import net.minecraft.network.chat.Component

val String.translatable
    get() = Component.translatable(this)