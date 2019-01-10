import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.dnky.*
import org.openrndr.draw.*
import org.openrndr.extensions.Debug3D
import org.openrndr.extras.meshgenerators.*
import org.openrndr.math.Vector3
import org.openrndr.math.transforms.transform

/*
Same as DNKYDemo001 but using the photographic renderer preset
 */
fun main() = application {
    configure {
        width = 1920
        height = 900
    }
    program {
        extend(Debug3D())
        extend(DNKY()) {
            val pr = photographicRenderer()
            pr.aperture = 2.0
            pr.focalPlane = 4.0
            pr.exposure = 0.5
            sceneRenderer = pr.renderer
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
                        translate(0.0, 16.0, 0.0)
                        rotate(Vector3.UNIT_X, 90.0)
                    }
                    update {
                        transform = transform {
                            translate(Math.cos(seconds) * 20.0, 20.0, Math.sin(seconds) * 20.0)
                            rotate(Vector3.UNIT_Z, Math.cos(seconds * 1.22) * 20.0)
                            rotate(Vector3.UNIT_Y, Math.cos(seconds * 1.32) * 20.0)
                            rotate(Vector3.UNIT_X, 90.0 + Math.cos(seconds) * 20.0)
                        }
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
                        translate(0.0, 7.0, 0.0)
                    }
                    mesh {
                        geometry = geometry(sphereMesh(32, 32, 4.0))
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
                node {
                    instancedMesh {
                        geometry = geometry(sphereMesh(16, 16, 1.0))
                        instances = 500

                        basicMaterial {
                            texture {
                                target = TextureTarget.NORMAL
                                source = Triplanar(texture = loadImage("data/ground_normal.png").apply {
                                    wrapU = WrapMode.REPEAT
                                    wrapV = WrapMode.REPEAT
                                    filter(MinifyingFilter.LINEAR_MIPMAP_LINEAR, MagnifyingFilter.LINEAR)
                                }, sharpness = 5.0, scale = 0.03)
                            }
                            metalness = 0.0
                            roughness = 0.8
                            vertexTransform = """
                            float inst = int(instance);// + (instance%10)/20.0;
                            float p = 3.1415/8.0;
                            mat4 mat = mat4(1.0);
                            float pp = (p * inst + p_time)*-1.0 + 3.1415;
                            mat4 rmat = mat4(1.0);
                            rmat[0][0] = cos(pp);
                            rmat[0][2] = -sin(pp);
                            rmat[2][0] = sin(pp);
                            rmat[2][2] = cos(pp);
                            mat[3][0] = cos(inst*p*1.232 + p_time) * 20.0;
                            mat[3][1] = 2.0 + cos(inst + p_time);
                            mat[3][2] = sin(inst*p*2.0 + p_time) * 20.0;
                            mat4 smat = mat4(1.0) * (cos(instance)*0.5+0.7);
                            //x_modelNormalMatrix *= rmat;
                            x_modelMatrix *= (mat*smat);
                        """.trimIndent()
                            update {
                                parameters["time"] = seconds * 0.1
                            }
                        }
                    }
                }
            }
        }
    }
}