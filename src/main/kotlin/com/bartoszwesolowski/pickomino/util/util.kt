package com.bartoszwesolowski.pickomino.util

import com.bartoszwesolowski.pickomino.model.Side
import java.util.EnumSet

fun EnumSet<Side>.withUsed(side: Side): EnumSet<Side> {
    return EnumSet.copyOf(this).apply {
        add(side)
    }
}