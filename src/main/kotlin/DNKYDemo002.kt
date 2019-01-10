import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dnky.*
import org.openrndr.draw.*
import org.openrndr.extensions.Debug3D
import org.openrndr.extra.jumpfill.JumpFlooder
import org.openrndr.extras.meshgenerators.*
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector3
import org.openrndr.math.transforms.transform
import org.openrndr.numate.inOutExpo
import org.openrndr.numate.outExpo
import org.openrndr.numate.storyboard

fun main() = application {
    configure {
        width = 1920
        height = 900
    }
    program {
        extend(Debug3D())
        val jumpFlooder = JumpFlooder(512, 512)

        val rt = renderTarget(512, 512) {
            colorBuffer()
            depthBuffer()
        }

        var flooded = jumpFlooder.result
        extend {
            // -- draw a pattern that will be used by the area lights later
            drawer.isolatedWithTarget(rt) {
                ortho(rt)
                drawer.view = Matrix44.IDENTITY
                drawer.model = Matrix44.IDENTITY
                drawer.background(ColorRGBa.BLACK)
                drawer.fill = ColorRGBa.WHITE

                val steps = (Math.cos(seconds)*12.0 + 20.0).toInt()
                for (i in 0 until steps) {
                    val x = Math.cos(i.toDouble()/steps * Math.PI*2.0) * 200.0 + 256.0
                    val y = Math.sin(i.toDouble()/steps * Math.PI*2.0) *200.0 + 256.0
                    drawer.circle(x, y, 16.0)
                }
            }
            rt.colorBuffer(0).generateMipmaps()

            // -- convert to distance maps by finding distance to contour using orx-jumpflood
            flooded = jumpFlooder.distanceToContour(drawer, rt.colorBuffer(0), 0.5)
            flooded.filter(MinifyingFilter.NEAREST, MagnifyingFilter.NEAREST)
            flooded.wrapU = WrapMode.CLAMP_TO_EDGE
            flooded.wrapV = WrapMode.CLAMP_TO_EDGE
        }

        extend(DNKY()) {
            scene = scene {
                node {
                    fog {
                        end = 100.0
                        color = ColorRGBa.BLACK
                    }
                }
                node {
                    update {
                        transform = transform {
                            translate(0.0, 25.0, 0.0)
                            rotate(Vector3.UNIT_X, 90.0)
                        }
                    }
                    areaLight {
                        distanceField = flooded
                        color = ColorRGBa.WHITE.shade(0.250)
                        width = 50.0
                        height = 50.0
                    }
                }

                node {
                    update {
                        transform = transform {
                            translate(0.0, 12.5, -12.5)
                        }
                    }
                    areaLight {
                        distanceField = flooded
                        color = ColorRGBa.RED.shade(1.125)
                        width = 25.0
                        height = 25.0
                    }
                }

                node {
                    update {
                        transform = transform {
                            translate(0.0, 12.5, 12.5)
                            rotate(Vector3.UNIT_Y, 180.0)
                        }
                    }
                    areaLight {
                        distanceField = flooded
                        color = ColorRGBa.BLUE.shade(1.125)
                        width = 25.0
                        height = 25.0
                    }
                }

                node {
                    update {
                        transform = transform {
                            translate(-12.5, 12.5, 0.0)
                            rotate(Vector3.UNIT_Y, 90.0)
                        }
                    }
                    areaLight {
                        distanceField = flooded
                        color = ColorRGBa.WHITE.shade(0.125)
                        width = 25.0
                        height = 25.0
                    }
                }

                node {
                    update {
                        transform = transform {
                            translate(12.5, 12.5, 0.0)
                            rotate(Vector3.UNIT_Y, -90.0)
                        }
                    }
                    areaLight {
                        distanceField = flooded
                        color = ColorRGBa.WHITE.shade(0.125)
                        width = 25.0
                        height = 25.0
                    }
                }

                node {
                    val anim = object {
                        var position = Vector3(4.0, 7.0, 0.0)
                    }

                    storyboard(true) {
                        anim::position to Vector3((Math.random()-0.5) * 24.0,  4.0 + Math.random()*4.0, (Math.random()-0.5) * 24.0) during 0.5 eased inOutExpo
                    }

                    update {
                        transform = transform { translate(anim.position) }
                    }

                    mesh {
                        geometry = geometry(sphereMesh(32, 32, 4.0))
                        basicMaterial {

                        }
                    }
                }

                node {
                    val anim = object {
                        var position = Vector3(4.0, 7.0, 0.0)
                    }
                    storyboard(true) {
                        anim::position to Vector3((Math.random()-0.5) * 24.0,  7.0, (Math.random()-0.5) * 24.0) during 1.5 eased inOutExpo
                    }

                    update {
                        transform = transform { translate(anim.position) }
                    }
                }

                node {
                    mesh {
                        geometry = geometry(groundPlaneMesh(500.0, 500.0, 20, 20))
                        basicMaterial {
                            color = ColorRGBa.WHITE
                        }
                    }
                }
                // -- skybox
                node {
                    mesh {
                        geometry = geometry(boxMesh(500.0, 500.0, 500.0, invert = true))
                        basicMaterial {
                            color = ColorRGBa.PINK
                        }
                    }
                }
            }
        }
    }
}