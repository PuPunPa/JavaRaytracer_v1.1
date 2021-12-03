/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package up.edu.isgc.raytracer.objects;

import up.edu.isgc.raytracer.Intersection;
import up.edu.isgc.raytracer.Ray;
import up.edu.isgc.raytracer.Vector3D;

import java.awt.*;

/**
 *
 * @author Jafet Rodríguez
 */
public class Sphere extends Object3D {

    private float radius;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     *
     * @param position
     * @param radius
     * @param color
     */
    public Sphere(Vector3D position, float radius, Color color, float reflect, float refract) {
        super(position, color, reflect, refract);
        setRadius(radius);
    }

    /**
     *
     * @param ray
     * @return the intersection between a ray and a sphere
     */
    @Override
    public Intersection getIntersection(Ray ray) {

        float reflect = getReflect();
        float refract = getRefract();
        double distance = -1;
        Vector3D normal = Vector3D.ZERO();
        Vector3D position = Vector3D.ZERO();

        Vector3D directionSphereRay = Vector3D.substract(ray.getOrigin(), getPosition());
        double firstP = Vector3D.dotProduct(ray.getDirection(), directionSphereRay);
        double secondP = Math.pow(Vector3D.magnitude(directionSphereRay), 2);
        double intersection = Math.pow(firstP, 2) - secondP + Math.pow(getRadius(), 2);

        if(intersection >= 0){
            double sqrtIntersection = Math.sqrt(intersection);
            double part1 = -firstP + sqrtIntersection;
            double part2 = -firstP - sqrtIntersection;

            distance = Math.min(part1, part2);
            position = Vector3D.add(ray.getOrigin(), Vector3D.scalarMultiplication(ray.getDirection(), distance));
            normal = Vector3D.normalize(Vector3D.substract(position, getPosition()));
        } else {
            return null;
        }

        return new Intersection(position, distance, normal, this, reflect, refract);
    }
}
