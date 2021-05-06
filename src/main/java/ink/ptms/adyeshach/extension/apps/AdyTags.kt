package ink.ptms.adyeshach.extension.apps

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.AdyeshachEntitySpawnEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityTeleportEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.util.Inputs
import ink.ptms.adyeshach.internal.trait.KnownTraits
import ink.ptms.adyeshach.internal.trait.Trait
import io.izzel.taboolib.kotlin.asList
import io.izzel.taboolib.kotlin.colored
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.hologram.Hologram
import io.izzel.taboolib.module.hologram.THologram
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*
import kotlin.collections.HashMap

/**
 * @author sky
 * @since 2021/4/19 1:06 上午
 */
@TListener
class AdyTags : Trait(), Listener {

    val map = HashMap<String, ArrayList<Hologram>>()

    init {
        KnownTraits.traits.add(this)
    }

    override fun getName(): String {
        return "tags"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        player.sendLocale("trait-tags")
        Inputs.bookIn(player) {
            if (it.all { line -> line.isBlank() }) {
                data.set(entityInstance.uniqueId, null)
            } else {
                data.set(entityInstance.uniqueId, it)
                refresh(entityInstance)
            }
            player.sendLocale("trait-tags-finish")
        }
    }

    fun refresh(entity: EntityInstance) {
        if (data.contains(entity.uniqueId)) {
            val holograms = ArrayList<Hologram>()
            val loc = entity.getLocation().add(0.0, entity.entityType.entitySize.height + 0.25, 0.0)
            val message = data.get(entity.uniqueId)!!.asList().colored()
            message.forEachIndexed { index, content ->
                holograms.add(THologram.create(loc.clone().add(0.0, (((message.size - 1) - index) * 0.3), 0.0), content).also {
                    if (content.isNotEmpty()) {
                        it.toAll()
                    }
                })
            }
            map.put(entity.uniqueId, holograms)?.forEach {
                it.delete()
            }
        }
    }

    @TSchedule
    fun reload() {
        AdyeshachAPI.getEntityManagerPublic().getEntities().forEach {
            refresh(it)
        }
        AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().forEach {
            refresh(it)
        }
    }

    @TFunction.Cancel
    fun cancel() {
        map.forEach {
            it.value.forEach { npc ->
                npc.delete()
            }
        }
    }

    @EventHandler
    fun e(e: AdyeshachEntitySpawnEvent) {
        if (e.entity.manager?.isPublic() == true) {
            refresh(e.entity)
        }
    }

    @EventHandler
    fun e(e: AdyeshachEntityTeleportEvent) {
        if (map.containsKey(e.entity.uniqueId)) {
            val holograms = map[e.entity.uniqueId]!!
            val loc = e.entity.getLocation().add(0.0, e.entity.entityType.entitySize.height + 0.25, 0.0)
            holograms.forEachIndexed { index, hologram ->
                hologram.flash(loc.clone().add(0.0, (((holograms.size - 1) - index) * 0.3), 0.0))
            }
        }
    }
}