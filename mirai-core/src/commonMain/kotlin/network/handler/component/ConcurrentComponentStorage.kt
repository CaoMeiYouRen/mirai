/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.internal.network.handler.component

import net.mamoe.mirai.utils.systemProp
import java.util.concurrent.ConcurrentHashMap

/**
 * A thread-safe implementation of [MutableComponentStorage]
 */
internal class ConcurrentComponentStorage(
    private val showAllComponents: Boolean = SHOW_ALL_COMPONENTS
) : ComponentStorage, MutableComponentStorage {
    private val map = ConcurrentHashMap<ComponentKey<*>, Any?>()

    override val size: Int get() = map.size

    override operator fun <T : Any> get(key: ComponentKey<T>): T {
        return getOrNull(key) ?: throw NoSuchComponentException(key, this)
    }

    override fun <T : Any> getOrNull(key: ComponentKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return map[key] as T?
    }

    override operator fun <T : Any> set(key: ComponentKey<T>, value: @UnsafeVariance T) {
        map[key] = value
    }

    override fun <T : Any> remove(key: ComponentKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return map.remove(key) as T?
    }

    override fun toString(): String {
        if (showAllComponents) {
            return buildString {
                append("ConcurrentComponentStorage(size=").append(map.size).append(") {").appendLine()
                for ((key, value) in map) {
                    append("  ").append(key.componentName(qualified = false)).append(": ").append(value).appendLine()
                }
                append('}')
            }
        }
        return "ConcurrentComponentStorage(size=${map.size})"
    }
}

private val SHOW_ALL_COMPONENTS = systemProp("mirai.debug.network.show.all.components", false)