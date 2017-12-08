package org.codetome.zircon.api.beta.component

import org.codetome.zircon.api.Size
import org.codetome.zircon.api.TextCharacter
import org.codetome.zircon.api.graphics.TextImage

interface GameArea {

    /**
     * Returns the size of the 3D space this [GameArea] represents.
     */
    fun getSize(): Size3D

    /**
     * Returns the [TextCharacter]s at the given [Position3D].
     * Since there can be multiple layers on the same height a [List] is returned
     * instead of a single [TextCharacter].
     * Note that the returned [TextCharacter]s are ordered from bottom to top.
     */
    fun getCharactersAt(position: Position3D): List<TextCharacter>

    /**
     * Returns a 2D segment of 3D space at a given 3D position and of a given
     * 2D size.
     */
    fun getSegmentAt(offset: Position3D, size: Size): GameAreaSegment

    /**
     * Returns the indexes of all levels this [GameArea] has.
     */
    fun getLevelIndexes(): List<Int> = (0 until getSize().height).toList()
}