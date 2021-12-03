/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package up.edu.isgc.raytracer;

/**
 *
 * @author Jafet Rodríguez
 */
public class Vector3D {

    private static final Vector3D ZERO = new Vector3D(0.0, 0.0, 0.0);
    private double x, y, z;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector3D(double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }
    /**
     *
     * @param vectorA
     * @param vectorB
     * @return dot product of 2 vectors
     */
    public static double dotProduct(Vector3D vectorA, Vector3D vectorB){
        return (vectorA.getX() * vectorB.getX()) + (vectorA.getY() * vectorB.getY()) + (vectorA.getZ() * vectorB.getZ());
    }

    /**
     *
     * @param vectorA
     * @param vectorB
     * @return cross product of 2 vectors
     */
    public static Vector3D crossProduct(Vector3D vectorA, Vector3D vectorB){
        return new Vector3D((vectorA.getY() * vectorB.getZ()) - (vectorA.getZ() * vectorB.getY()),
                (vectorA.getZ() * vectorB.getX()) - (vectorA.getX() * vectorB.getZ()),
                (vectorA.getX() * vectorB.getY()) - (vectorA.getY() * vectorB.getX()));
    }

    /**
     *
     * @param vectorA
     * @return magnitude of a vector
     */
    public static double magnitude(Vector3D vectorA){
        return Math.sqrt(dotProduct(vectorA, vectorA));
    }

    /**
     *
     * @param vectorA
     * @param vectorB
     * @return sum of 2 vectors
     */
    public static Vector3D add(Vector3D vectorA, Vector3D vectorB){
        return new Vector3D(vectorA.getX() + vectorB.getX(), vectorA.getY() + vectorB.getY(), vectorA.getZ() + vectorB.getZ());
    }

    /**
     *
     * @param vectorA
     * @param vectorB
     * @return substraction of 2 vectors
     */
    public static Vector3D substract(Vector3D vectorA, Vector3D vectorB){
        return new Vector3D(vectorA.getX() - vectorB.getX(), vectorA.getY() - vectorB.getY(), vectorA.getZ() - vectorB.getZ());
    }

    /**
     *
     * @param vectorA
     * @return the normal of a vector
     */
    public static Vector3D normalize(Vector3D vectorA){
        double mag = Vector3D.magnitude(vectorA);
        return new Vector3D(vectorA.getX() / mag, vectorA.getY() / mag, vectorA.getZ() / mag);
    }

    /**
     *
     * @param vectorA
     * @param scalar
     * @return a vector's multiplication
     */
    public static Vector3D scalarMultiplication(Vector3D vectorA, double scalar){
        return new Vector3D(vectorA.getX() * scalar, vectorA.getY() * scalar, vectorA.getZ() * scalar);
    }

    /**
     *
     * @param vectorA
     * @param scalar
     * @return a vector's division
     */
    public static Vector3D scalarDivision(Vector3D vectorA, double scalar){
        return new Vector3D(vectorA.getX() / scalar, vectorA.getY() / scalar, vectorA.getZ() / scalar);
    }

    public static Vector3D negative(Vector3D vectorA){
        return new Vector3D(-vectorA.getX(), -vectorA.getY(), -vectorA.getZ());
    }

    public static Vector3D reflect(Vector3D inflectVector, Vector3D normalVector){
        double nDotI = Vector3D.dotProduct(normalVector, inflectVector);
        Vector3D reflectVector = substract(inflectVector,  scalarMultiplication(normalVector, 2*nDotI));
        return new Vector3D(reflectVector.getX(), reflectVector.getY(), reflectVector.getZ());
    }

    public static Vector3D refract(Vector3D inflectVector, Vector3D normalVector, double inTo, double outOf){
        double n = inTo/outOf;
        double nDotI = Vector3D.dotProduct(normalVector, inflectVector);
        double c2 = Math.sqrt(1-(Math.pow(n, 2))*(1-Math.pow(nDotI, 2)));
        Vector3D refractVector = add(scalarMultiplication(inflectVector, n), scalarMultiplication(normalVector, ((n*nDotI)-c2)));
        return new Vector3D(refractVector.getX(), refractVector.getY(), refractVector.getZ());
    }

    public static double getAngle(Vector3D vectorA, Vector3D vectorB){
        return Math.toDegrees(Math.acos(Vector3D.dotProduct(vectorA, vectorB))/(magnitude(vectorA) * magnitude(vectorB)));
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
    }

    public Vector3D clone(){
        return new Vector3D(getX(), getY(), getZ());
    }

    public static Vector3D ZERO(){
        return ZERO.clone();
    }
}
