package com.bartoszwesolowski.pickomino

import java.util.EnumSet

fun EnumSet<Side>.withUsed(side: Side): EnumSet<Side> {
    return EnumSet.copyOf(this).apply {
        add(side)
    }
}