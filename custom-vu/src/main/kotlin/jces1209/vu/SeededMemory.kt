package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.memories.Memory
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class SeededMemory<T>(
    private val random: SeededRandom
) : Memory<T> {

    private val items = mutableSetOf<T>()

    override fun recall(): T? {
        return random.pick(items.toList())
    }

    fun recall(filter: (T) -> Boolean): T? {
        return random.pick(items.asSequence().filter(filter).toList())
    }

    override fun remember(memories: Collection<T>) {
        items.addAll(memories)
    }
}
