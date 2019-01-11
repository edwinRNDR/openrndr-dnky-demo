import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dnky.*
import org.openrndr.draw.*
import org.openrndr.extensions.Debug3D
import org.openrndr.extras.meshgenerators.*
import org.openrndr.math.Vector3
import org.openrndr.math.transforms.rotateY
import org.openrndr.math.transforms.transform

/*
DNKYDemo004
 */
fun main() = application {
    configure {
        width = 1920
        height = 900
    }
    program {
        extend(Debug3D())
        extend(DNKY()) {
            sceneRenderer = SceneRenderer()
            scene = scene {
                node {
                    hemisphereLight {
                        upColor = ColorRGBa.WHITE
                        downColor = ColorRGBa.WHITE
                        irradianceMap = Cubemap.fromUrl("file:data/evening_irr_hdr32.dds")
                    }
                    fog {
                        end = 100.0
                        color = ColorRGBa.PINK
                    }
                }
                node {
                    transform = transform {
                        translate(0.0, 8.0, 0.0)
                        rotate(Vector3.UNIT_X, 90.0)
                    }
                    update {
                        transform = transform {
                            translate(Math.cos(seconds) * 8.0, 18.0, Math.sin(seconds) * 20.0)
                            rotate(Vector3.UNIT_Z, Math.cos(seconds * 1.22) * 20.0)
                            rotate(Vector3.UNIT_Y, Math.cos(seconds * 1.32) * 20.0)
                            rotate(Vector3.UNIT_X, 90.0 + Math.cos(seconds) * 20.0)
                        }
                        transform = transform {
                            translate(28.0, 28.0, 0.0)
                            rotate(Vector3.UNIT_Z, -45.0)
                            rotate(Vector3.UNIT_X, 90.0)
                        }
                    }
                    mesh {
                        geometry = geometry(boxMesh(1.0, 1.0, 2.0))
                    }
                    spotLight {
                        direction = Vector3(0.0, 0.0, 1.0)
                        color = ColorRGBa.WHITE.shade(1.0)
                        innerAngle = 0.0
                        outerAngle = 50.0
                        shadows = true


                    }

                }
                node {
                    transform = transform {
                        translate(0.0, 2.0, 0.0)
                        rotateY(seconds * 10.0)
                    }

                    update {
                        transform = transform {
                            translate(0.0, 8.0, 0.0)
                            rotate(Vector3.UNIT_Y, seconds * 10.0)
                        }
                    }
                    mesh {
                        geometry = geometry(boxMesh(4.0, 16.0, 4.0))
                        basicMaterial {
                            metalness = 0.5
                            roughness = 0.1
                            environmentMap = true
                            texture {
                                target = TextureTarget.COLOR
                                source = Triplanar(texture = loadImage("data/ground.png").apply {
                                    wrapU = WrapMode.REPEAT
                                    wrapV = WrapMode.REPEAT
                                    filter(MinifyingFilter.LINEAR_MIPMAP_LINEAR, MagnifyingFilter.LINEAR)
                                }, sharpness = 5.0, scale = 0.06)
                            }
                            texture {
                                target = TextureTarget.NORMAL
                                source = Triplanar(texture = loadImage("data/ground_normal.png").apply {
                                    wrapU = WrapMode.REPEAT
                                    wrapV = WrapMode.REPEAT
                                    filter(MinifyingFilter.LINEAR_MIPMAP_LINEAR, MagnifyingFilter.LINEAR)
                                }, sharpness = 1.0, scale = 0.06)
                            }
                        }
                    }
                }
                node {
                    mesh {
                        geometry = geometry(groundPlaneMesh(500.0, 500.0, 20, 20))
                        basicMaterial {

                            color = ColorRGBa.WHITE


                            texture {
                                target = TextureTarget.COLOR
                                source = Triplanar(texture = loadImage("data/ground.png").apply {
                                    wrapU = WrapMode.REPEAT
                                    wrapV = WrapMode.REPEAT
                                    filter(MinifyingFilter.LINEAR_MIPMAP_LINEAR, MagnifyingFilter.LINEAR)
                                }, sharpness = 5.0, scale = 0.03)
                            }
                        }
                    }
                }
                // -- skybox
                node {
                    mesh {
                        geometry = geometry(boxMesh(500.0, 500.0, 500.0, invert = true))
                        basicMaterial {
                            color = ColorRGBa.BLACK
                            emission = ColorRGBa.PINK
                            metalness = 1.0
                            roughness = 1.0
                        }
                    }
                }
            }
        }
    }
}