/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package up.edu.isgc.raytracer.lights;

import up.edu.isgc.raytracer.Intersection;
import up.edu.isgc.raytracer.Vector3D;

import java.awt.*;

/**
 * @author Jafet Rodríguez
 */
public class PointLight extends Light {
    public PointLight(Vector3D position, Color color, double intensity) {
        super(position, color, intensity, 0, 0);
    }

    /**
     *
     * @param intersection
     * @return Dot product of Normal and the light
     */
    @Override
    public float getNDotL(Intersection intersection) {
        return (float) Math.max(Vector3D.dotProduct(intersection.getNormal(), Vector3D.normalize(Vector3D.substract(getPosition(), intersection.getPosition()))), 0.0);
    }

    /**
     *
     * @param intersection
     * @param halfVector
     * @return Dot product of the half vector and the intersection's normal
     */
    @Override
    public float getNDotH(Intersection intersection, Vector3D halfVector){
        return (float)Vector3D.dotProduct(intersection.getNormal(), halfVector);
    }
}
