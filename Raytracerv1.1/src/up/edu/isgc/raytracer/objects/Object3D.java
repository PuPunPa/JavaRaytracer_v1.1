/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package up.edu.isgc.raytracer.objects;

import up.edu.isgc.raytracer.IIntersectable;
import up.edu.isgc.raytracer.Vector3D;

import java.awt.*;

/**
 *
 * @author Jafet Rodríguez
 */
public abstract class Object3D implements IIntersectable {

    private Vector3D position;
    private Color color;
    private float reflect;
    private float refract;

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getReflect() { return reflect; }

    public void setReflect(float reflect){this.reflect = reflect;}

    public float getRefract() { return refract; }

    public void setRefract(float refract){this.refract = refract;}

    public Object3D(Vector3D position, Color color, float reflect, float refract) {
        setPosition(position);
        setColor(color);
        setReflect(reflect);
        setRefract(refract);
    }

}
