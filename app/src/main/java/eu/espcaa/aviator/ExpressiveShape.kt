package eu.espcaa.aviator

import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.toPath

class MaterialExpressiveShape(
    private val polygon: androidx.graphics.shapes.RoundedPolygon
) : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val androidPath = polygon.toPath()
        val composePath = androidPath.asComposePath()
        return Outline.Generic(composePath)
    }
}
