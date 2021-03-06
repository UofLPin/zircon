package org.hexworks.zircon.api.data

import org.hexworks.zircon.api.behavior.TilesetHolder
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.modifier.Modifier
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.ImageDictionaryTilesetResources

/**
 * An [ImageTile] is a singular image that can be loaded using [ImageDictionaryTilesetResources].
 */
interface ImageTile : Tile, TilesetHolder {

    val name: String

    override val tileset: TilesetResource

    fun withName(name: String): ImageTile

    fun withTileset(tileset: TilesetResource): ImageTile

    override fun withForegroundColor(foregroundColor: TileColor): ImageTile

    override fun withBackgroundColor(backgroundColor: TileColor): ImageTile

    override fun withStyle(style: StyleSet): ImageTile

    override fun withModifiers(modifiers: Set<Modifier>): ImageTile

    override fun withModifiers(vararg modifiers: Modifier): ImageTile

    override fun withAddedModifiers(modifiers: Set<Modifier>): ImageTile

    override fun withAddedModifiers(vararg modifiers: Modifier): ImageTile

    override fun withRemovedModifiers(modifiers: Set<Modifier>): ImageTile

    override fun withRemovedModifiers(vararg modifiers: Modifier): ImageTile

    override fun withNoModifiers(): ImageTile

}
