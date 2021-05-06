package ink.ptms.adyeshach.extension.apps

import ink.ptms.adyeshach.api.event.AdyeshachEntityTickEvent
import ink.ptms.adyeshach.api.event.AdyeshachNaturalMetaGenerateEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.type.AdyExperienceOrb
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.extension.AdyExtension
import ink.ptms.adyeshach.internal.trait.KnownTraits
import ink.ptms.adyeshach.internal.trait.Trait
import io.izzel.taboolib.kotlin.sendLocale
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * AdyTags
 * ink.ptms.adyeshach.extension.apps.AdyPlaceholder
 *
 * @author sky
 * @since 2021/5/3 5:53 下午
 */
@TListener
class AdyPlaceholder : Trait(), Listener {

    init {
        KnownTraits.traits.add(this)
    }

    override fun getName(): String {
        return "papi-name"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        if (entityInstance.isPapi()) {
            data.set(entityInstance.uniqueId, null)
            player.sendLocale("trait-papi-off")
        } else {
            data.set("${entityInstance.uniqueId}.state", true)
            player.sendLocale("trait-papi-on")
        }
    }

    @EventHandler
    fun e(e: AdyeshachNaturalMetaGenerateEvent) {
        if (e.meta.key == "customName" && e.value is String && e.entity.isPapi()) {
            e.value = TLocale.Translate.setPlaceholders(e.player, e.value.toString())
        }
    }

    @EventHandler
    fun e(e: AdyeshachEntityTickEvent) {
        val entity = e.entity
        if (entity.isPapi() && entity.isPapiNext()) {
            if (entity is AdyHuman) {
                entity.setName(entity.getName())
            } else {
                entity.setCustomName(entity.getCustomName())
            }
        }
    }

    fun EntityInstance.isPapi() = data.getBoolean("${uniqueId}.state")

    fun EntityInstance.isPapiNext() = if (data.getLong("${uniqueId}.update") < System.currentTimeMillis()) {
        data.set("${uniqueId}.update", System.currentTimeMillis() + AdyExtension.conf.getInt("papi-name-update-pariod"))
        true
    } else false
}