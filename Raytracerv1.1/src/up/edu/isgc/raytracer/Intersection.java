/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package up.edu.isgc.raytracer;

import up.edu.isgc.raytracer.objects.Object3D;

/**
 *
 * @author Jafet Rodríguez
 */
public class Intersection {

    private double distance;
    private Vector3D normal;
    private Vector3D position;
    private Object3D object;
    private float reflect;
    private float refract;

    public Intersection(Vector3D position, double distance, Vector3D normal, Object3D object, float reflect, float refract) {
        setPosition(position);
        setDistance(distance);
        setNormal(normal);
        setObject(object);
        setReflect(reflect);
        setRefract(refract);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Vector3D getNormal() {
        return normal;
    }

    public void setNormal(Vector3D normal) {
        this.normal = normal;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Object3D getObject() {
        return object;
    }

    public void setObject(Object3D object) {
        this.object = object;
    }

    public float getReflect() {return reflect; }

    public void setReflect(float reflect) {this.reflect = reflect;}

    public float getRefract() {return refract; }

    public void setRefract(float refract) {this.refract = refract;}
}
