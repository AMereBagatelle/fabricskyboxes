package io.github.amerebagatelle.fabricskyboxes.util.`object`

import org.apache.commons.lang3.builder.ToStringBuilder

data class MinMaxEntry(
    val min: Float,
    val max: Float
) {
    init {
        if(min > max) throw IllegalStateException("Maximum value is lower than minimum value:\n$this")
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}