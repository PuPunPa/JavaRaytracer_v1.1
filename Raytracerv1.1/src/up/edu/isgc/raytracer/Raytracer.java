/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package up.edu.isgc.raytracer;

import up.edu.isgc.raytracer.lights.DirectionalLight;
import up.edu.isgc.raytracer.lights.Light;
import up.edu.isgc.raytracer.lights.PointLight;
import up.edu.isgc.raytracer.objects.*;
import up.edu.isgc.raytracer.tools.OBJReader;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Jafet Rodríguez
 * @author Jose Pablo Lopez Portillo Perez Lete
 */
public class Raytracer {

    public static BufferedImage image;
    public static float ambient = 100;
    public static float shiny = 100;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        System.out.println(new Date());
        Scene scene01 = new Scene();
        scene01.setCamera(new Camera(new Vector3D(0, 0, -8), 160, 160, 2000, 2000, 8.2f, 50f));
        image = new BufferedImage(scene01.getCamera().getResolutionWidth(), scene01.getCamera().getResolutionHeight(), BufferedImage.TYPE_INT_RGB);
        scene01.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(0f, -1f, 1f), Color.WHITE, 15));
        scene01.addLight(new PointLight(new Vector3D(0, 5, 5), Color.white, 10));
        scene01.addLight(new PointLight(new Vector3D(2, 5, 10), Color.blue, 10));//scene01.addLight(new PointLight(new Vector3D(-2f, 0f, 20f), Color.RED, 10));
        scene01.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(-1.0, -1, 1.0), Color.WHITE, 15));
        scene01.addLight(new PointLight(new Vector3D(2f, 2, 20f), Color.white, 10));
        scene01.addLight(new PointLight(new Vector3D(2f, 0, 20f), Color.BLUE, 10));
        scene01.addLight(new PointLight(new Vector3D(0f, 2, -2f), Color.WHITE, 10));
        scene01.addObject(new Sphere(new Vector3D(0f, -2f, 2f), 0.5f, Color.yellow, 0f, 0.75f));
        scene01.addObject(new Sphere(new Vector3D(0f, -1f, 6f), 0.5f, Color.red, 0.5f, 0f));
        scene01.addObject(new Sphere(new Vector3D(0f, 0f, 6f), 0.5f, Color.blue, 0f, 0f));
        scene01.addObject(new Sphere(new Vector3D(1f, -1f, 6f), 0.5f, Color.green, 1f, 0f));
        scene01.addObject(new Sphere(new Vector3D(1f, 0f, 6f), 0.5f, Color.red, 0.5f, 0f));
        scene01.addObject(new Sphere(new Vector3D(0f, -2f, 6f), 0.5f, Color.blue, 0f, 0f));
        scene01.addObject(new Sphere(new Vector3D(-1f, -1f, 6f), 0.5f, Color.green, 1f, 0f));
        scene01.addObject(new Sphere(new Vector3D(-1f, -2f, 6f), 0.5f, Color.red, 0.5f, 0f));
        scene01.addObject(new Sphere(new Vector3D(1f, -2f, 6f), 0.5f, Color.blue, 0f, 0f));
        scene01.addObject(new Sphere(new Vector3D(-1f, 0f, 6f), 0.5f, Color.green, 1f, 0f));
        scene01.addObject(OBJReader.GetPolygon("Cube.obj", new Vector3D(3f, 0f, 10f), Color.red, 0,45f,0, 1f, 0f));
        scene01.addObject(OBJReader.GetPolygon("Floor.obj", new Vector3D(0f, -3.1f, 0f), Color.WHITE, 0,0,0, 0.5f, 0f));

        Runnable runnable = raytrace(scene01, 1e-5);
        executorService.execute(runnable);

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.MINUTES)){
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(!executorService.isTerminated()){
                System.err.println("Cancel non-finifhed");
            }
        }
        executorService.shutdownNow();

        File outputImage = new File("image.png");
        try {
            ImageIO.write(image, "png", outputImage);
        } catch (IOException ioe) {
            System.out.println("Something failed");
        }
        System.out.println(new Date());
    }

    /**
     *
     * @param scene
     * @param shadowBias
     * @return rendered scene
     */
    public static Runnable raytrace(Scene scene, double shadowBias) {
        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                Camera mainCamera = scene.getCamera();
                ArrayList<Light> lights = scene.getLights();
                float[] nearFarPlanes = mainCamera.getNearFarPlanes();
                ArrayList<Object3D> objects = scene.getObjects();

                Vector3D[][] positionsToRaytrace = mainCamera.calculatePositionsToRay();
                for (int i = 0; i < positionsToRaytrace.length; i++) {
                    for (int j = 0; j < positionsToRaytrace[i].length; j++) {
                        double x = positionsToRaytrace[i][j].getX() + mainCamera.getPosition().getX();
                        double y = positionsToRaytrace[i][j].getY() + mainCamera.getPosition().getY();
                        double z = positionsToRaytrace[i][j].getZ() + mainCamera.getPosition().getZ();

                        Ray ray = new Ray(mainCamera.getPosition(), new Vector3D(x, y, z));
                        float cameraZ = (float) mainCamera.getPosition().getZ();
                        Intersection closestIntersection = raycast(ray, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

                        //Background color
                        Color pixelColor = Color.BLACK;
                        if (closestIntersection != null) {
                            pixelColor = Color.BLACK;
                            for (Light light : lights) {
                                Vector3D lightPos = new Vector3D(light.getPosition().getX(), light.getPosition().getY(), light.getPosition().getZ());
                                Vector3D rayOffPos = Vector3D.add(closestIntersection.getPosition(), Vector3D.scalarMultiplication(closestIntersection.getNormal(), shadowBias));
                                Vector3D lightPosGlob = Vector3D.substract(lightPos, rayOffPos);
                                double lightDistance = Math.pow(Vector3D.magnitude(lightPosGlob), 2);
                                Ray rayShadow = new Ray(rayOffPos, lightPosGlob);

                                Color lightColor = light.getColor();
                                Color objColor = closestIntersection.getObject().getColor();
                                float[] lightColors = new float[]{lightColor.getRed() / 255.0f, lightColor.getGreen() / 255.0f, lightColor.getBlue() / 255.0f};
                                Color ambientColor = new Color(clamp(lightColors[0] / ambient, 0, 1), clamp(lightColors[1] / ambient, 0, 1), clamp(lightColors[2] / ambient, 0, 1));
                                pixelColor = addColor(pixelColor, ambientColor);
                                Intersection shadowIntersection = raycast(rayShadow, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

                                if (shadowIntersection == null) {
                                    //specular
                                    Vector3D halfVectorSum = Vector3D.add(mainCamera.getPosition(), light.getPosition());
                                    Vector3D halfVector = Vector3D.scalarDivision(halfVectorSum, Vector3D.magnitude(halfVectorSum));
                                    float nDotH = light.getNDotH(closestIntersection, halfVector);
                                    float reflection = (float) Math.pow(nDotH, shiny);

                                    //diffuse
                                    float nDotL = light.getNDotL(closestIntersection);
                                    float intensity = (float) (light.getIntensity() / lightDistance) * nDotL;
                                    float[] objColors = new float[]{objColor.getRed() / 255.0f, objColor.getGreen() / 255.0f, objColor.getBlue() / 255.0f};
                                    for (int colorIndex = 0; colorIndex < objColors.length; colorIndex++) {
                                        objColors[colorIndex] *= intensity * lightColors[colorIndex];
                                    }
                                    Color diffuse = new Color(clamp(objColors[0], 0, 1), clamp(objColors[1], 0, 1), clamp(objColors[2], 0, 1));
                                    Color specular = new Color(clamp(reflection, 0, 1), clamp(reflection, 0, 1), clamp(reflection, 0, 1));
                                    Color blinnPhong = addColor(diffuse, specular);
                                    pixelColor = addColor(pixelColor, blinnPhong);

                                    float reflIndex = closestIntersection.getReflect();
                                    float refrIndex = closestIntersection.getRefract();

                                    if (reflIndex > 0) {
                                        pixelColor = Reflection(ray, scene, light, pixelColor, shadowBias, reflIndex, false);
                                    }
                                    if (refrIndex > 0) {
                                        pixelColor = Refraction(ray, 1, 1.5, scene, light, pixelColor, shadowBias, refrIndex, false);
                                    }
                                }
                            }
                        }
                        setRGB(i, j, pixelColor.getRGB());
                    }
                }
            }
        };
        return aRunnable;
    }

    public static synchronized void setRGB(int x, int y, int pixelColor){ image.setRGB(x, y, pixelColor); }

    /**
     *
     * @param ray
     * @param scene
     * @param pixelColor
     * @param shadowBias
     * @return reflection of depth 2
     * @see <a href="https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/reflection-refraction-fresnel">Scratchpixel 2.0</a>
     */
    public static Color Reflection (Ray ray, Scene scene, Light light, Color pixelColor, double shadowBias, float reflIndex, boolean recursion){
        reflIndex = clamp(reflIndex, 0, 1);
        float[] pixelColors = new float[]{pixelColor.getRed() / 255.0f, pixelColor.getGreen() / 255.0f, pixelColor.getBlue() / 255.0f};
        for (int colorIndex = 0; colorIndex < pixelColors.length; colorIndex++) {
            pixelColors[colorIndex] *= (1-reflIndex);
        }
        pixelColor = new Color(clamp(pixelColors[0], 0, 1), clamp(pixelColors[1], 0, 1), clamp(pixelColors[2], 0, 1));

        Camera mainCamera = scene.getCamera();
        float [] nearFarPlanes = mainCamera.getNearFarPlanes();
        ArrayList<Object3D> objects = scene.getObjects();
        Vector3D inflectVector = ray.getDirection();
        float cameraZ = (float) mainCamera.getPosition().getZ();

        Intersection closestIntersection = raycast(ray, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});
        Vector3D rayOffPos = Vector3D.add(closestIntersection.getPosition(), Vector3D.scalarMultiplication(closestIntersection.getNormal(), shadowBias));
        Vector3D normalVector = closestIntersection.getNormal();
        Vector3D reflectVector = Vector3D.reflect(inflectVector, normalVector);
        Ray rayReflect = new Ray(rayOffPos, reflectVector);

        Intersection reflIntersect = raycast(rayReflect, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});
        if (reflIntersect != null){
            Vector3D lightPos = new Vector3D(light.getPosition().getX(), light.getPosition().getY(), light.getPosition().getZ());
            rayOffPos = Vector3D.add(reflIntersect.getPosition(), Vector3D.scalarMultiplication(reflIntersect.getNormal(), shadowBias));
            Vector3D lightPosGlob = Vector3D.substract(lightPos, rayOffPos);
            double lightDistance = Math.pow(Vector3D.magnitude(lightPosGlob), 2);
            Ray rayShadow = new Ray(rayOffPos, lightPosGlob);

            Color lightColor = light.getColor();
            float[] lightColors = new float[]{lightColor.getRed() / 255.0f, lightColor.getGreen() / 255.0f, lightColor.getBlue() / 255.0f};
            Color ambientColor = new Color(clamp(lightColors[0] / ambient * reflIndex, 0, 1), clamp(lightColors[1] / ambient * reflIndex, 0, 1), clamp(lightColors[2] / ambient * reflIndex, 0, 1));
            pixelColor = addColor(pixelColor, ambientColor);
            Intersection shadowIntersection = raycast(rayShadow, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

            if (shadowIntersection == null) {
                //specular
                Vector3D halfVectorSum = Vector3D.add(mainCamera.getPosition(), light.getPosition());
                Vector3D halfVector = Vector3D.scalarDivision(halfVectorSum, Vector3D.magnitude(halfVectorSum));
                float nDotH = light.getNDotH(reflIntersect, halfVector);
                float reflection = (float) Math.pow(nDotH, shiny);

                //diffuse
                float nDotL = light.getNDotL(reflIntersect);
                float intensity = (float) (light.getIntensity() / lightDistance) * nDotL;
                Color objColor = reflIntersect.getObject().getColor();
                float[] objColors = new float[]{objColor.getRed() / 255.0f, objColor.getGreen() / 255.0f, objColor.getBlue() / 255.0f};
                for (int colorIndex = 0; colorIndex < objColors.length; colorIndex++) {
                    objColors[colorIndex] *= intensity * lightColors[colorIndex] * reflIndex;
                }
                Color diffuse = new Color(clamp(objColors[0], 0, 1), clamp(objColors[1], 0, 1), clamp(objColors[2], 0, 1));
                Color specular = new Color(clamp(reflection * reflIndex, 0, 1), clamp(reflection * reflIndex, 0, 1), clamp(reflection * reflIndex, 0, 1));
                Color blinnPhong = addColor(diffuse, specular);
                pixelColor = addColor(pixelColor, blinnPhong);
                Color reflColor = reflIntersect.getObject().getColor();
                float[] reflColors = new float[]{reflColor.getRed() / 255.0f, reflColor.getGreen() / 255.0f, reflColor.getBlue() / 255.0f};
                pixelColors = new float[]{pixelColor.getRed() / 255.0f, pixelColor.getGreen() / 255.0f, pixelColor.getBlue() / 255.0f};
                for (int colorIndex = 0; colorIndex < reflColors.length; colorIndex++) {
                    reflColors[colorIndex] *= pixelColors[colorIndex] * objColors[colorIndex] * reflIndex;
                }
                reflColor = new Color(clamp(reflColors[0], 0, 1),clamp(reflColors[1], 0, 1),clamp(reflColors[2], 0, 1));

                pixelColor = addColor(pixelColor, reflColor);
                if (!recursion) {
                    reflIndex *= reflIntersect.getReflect();
                    if (reflIndex > 0) {
                        pixelColor = Reflection(rayReflect, scene, light, pixelColor, shadowBias, reflIndex, true);

                    }
                }
            }
        }
        return pixelColor;
    }

    /**
     *
     * @param ray
     * @param indexInTo
     * @param indexOutOf
     * @param scene
     * @param light
     * @param shadowBias
     * @param pixelColor
     * @return refraction
     */
    public static Color Refraction (Ray ray, double indexInTo, double indexOutOf, Scene scene, Light light, Color pixelColor, double shadowBias, float refrIndex, boolean recursion) {
        refrIndex = clamp(refrIndex, 0, 1);
        float[] pixelColors = new float[]{pixelColor.getRed() / 255.0f, pixelColor.getGreen() / 255.0f, pixelColor.getBlue() / 255.0f};
        for (int colorIndex = 0; colorIndex < pixelColors.length; colorIndex++) {
            pixelColors[colorIndex] *= (1 - refrIndex);
        }
        pixelColor = new Color(clamp(pixelColors[0], 0, 1), clamp(pixelColors[1], 0, 1), clamp(pixelColors[2], 0, 1));
        Camera mainCamera = scene.getCamera();
        float[] nearFarPlanes = mainCamera.getNearFarPlanes();
        ArrayList<Object3D> objects = scene.getObjects();
        Vector3D inflectVector = ray.getDirection();
        float cameraZ = (float) mainCamera.getPosition().getZ();

        Intersection closestIntersection = raycast(ray, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});
        Vector3D normalVector = closestIntersection.getNormal();
        if (recursion){
            normalVector = Vector3D.negative(normalVector);
        }
        Vector3D rayOffPos = Vector3D.add(closestIntersection.getPosition(), Vector3D.scalarMultiplication(normalVector, shadowBias));
        Vector3D refractVector = Vector3D.refract(inflectVector, normalVector, indexInTo, indexOutOf);
        Ray rayRefract = new Ray(rayOffPos, refractVector);

        Intersection refrIntersect = raycast(rayRefract, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

        if (refrIntersect != null){
            /*Color color = refrIntersect.getObject().getColor();
            pixelColor = addColor(pixelColor, color);*/

            //rewrite this (again) ((again))
            Vector3D lightPos = new Vector3D(light.getPosition().getX(), light.getPosition().getY(), light.getPosition().getZ());
            rayOffPos = Vector3D.add(refrIntersect.getPosition(), Vector3D.scalarMultiplication(refrIntersect.getNormal(), shadowBias));
            Vector3D lightPosGlob = Vector3D.substract(lightPos, rayOffPos);
            double lightDistance = Math.pow(Vector3D.magnitude(lightPosGlob), 2);
            Ray rayShadow = new Ray(rayOffPos, lightPosGlob);

            Color lightColor = light.getColor();
            float[] lightColors = new float[]{lightColor.getRed() / 255.0f, lightColor.getGreen() / 255.0f, lightColor.getBlue() / 255.0f};
            Color ambientColor = new Color(clamp(lightColors[0] / ambient * refrIndex, 0, 1), clamp(lightColors[1] / ambient * refrIndex, 0, 1), clamp(lightColors[2] / ambient * refrIndex, 0, 1));
            pixelColor = addColor(pixelColor, ambientColor);
            Intersection shadowIntersection = raycast(rayShadow, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

            //Eh sure
            if (shadowIntersection == null || shadowIntersection.getObject().getRefract() != 0) {
                //specular
                Vector3D halfVectorSum = Vector3D.add(mainCamera.getPosition(), light.getPosition());
                Vector3D halfVector = Vector3D.scalarDivision(halfVectorSum, Vector3D.magnitude(halfVectorSum));
                float nDotH = light.getNDotH(refrIntersect, halfVector);
                float reflection = (float) Math.pow(nDotH, shiny);

                //diffuse
                float nDotL = light.getNDotL(refrIntersect);
                float intensity = (float) (light.getIntensity() / lightDistance) * nDotL;
                Color objColor = refrIntersect.getObject().getColor();
                float[] objColors = new float[]{objColor.getRed() / 255.0f, objColor.getGreen() / 255.0f, objColor.getBlue() / 255.0f};
                for (int colorIndex = 0; colorIndex < objColors.length; colorIndex++) {
                    objColors[colorIndex] *= intensity * lightColors[colorIndex] * refrIndex;
                }
                Color diffuse = new Color(clamp(objColors[0], 0, 1), clamp(objColors[1], 0, 1), clamp(objColors[2], 0, 1));
                Color specular = new Color(clamp(reflection * refrIndex, 0, 1), clamp(reflection * refrIndex, 0, 1), clamp(reflection * refrIndex, 0, 1));
                Color blinnPhong = addColor(diffuse, specular);
                pixelColor = addColor(pixelColor, blinnPhong);
                Color refrColor = refrIntersect.getObject().getColor();
                float[] refrColors = new float[]{refrColor.getRed() / 255.0f, refrColor.getGreen() / 255.0f, refrColor.getBlue() / 255.0f};
                pixelColors = new float[]{pixelColor.getRed() / 255.0f, pixelColor.getGreen() / 255.0f, pixelColor.getBlue() / 255.0f};
                for (int colorIndex = 0; colorIndex < refrColors.length; colorIndex++) {
                    refrColors[colorIndex] *= pixelColors[colorIndex] * objColors[colorIndex] * refrIndex;
                }
                refrColor = new Color(clamp(refrColors[0], 0, 1), clamp(refrColors[1], 0, 1), clamp(refrColors[2], 0, 1));
                if (refrIntersect.getObject().getReflect() != 0){
                    refrColor = Reflection(rayRefract, scene, light, refrColor, shadowBias, refrIntersect.getObject().getReflect(), false);
                }
                pixelColor = addColor(pixelColor, refrColor);
            }
            if (!recursion) {
                refrIndex = refrIntersect.getRefract();
                if (refrIndex > 0) {
                    pixelColor = Refraction(rayRefract, indexOutOf, indexInTo, scene, light, pixelColor, shadowBias, refrIndex, true);
                }
            }
        }
        return pixelColor;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     *
     * @param original
     * @param otherColor
     * @return the sum of 2 colors
     */
    public static Color addColor(Color original, Color otherColor){
        float red = clamp((original.getRed() / 255.0f) + (otherColor.getRed() / 255.0f), 0, 1);
        float green = clamp((original.getGreen() / 255.0f) + (otherColor.getGreen() / 255.0f), 0, 1);
        float blue = clamp((original.getBlue() / 255.0f) + (otherColor.getBlue() / 255.0f), 0, 1);
        return new Color(red, green, blue);
    }

    /**
     *
     * @param ray
     * @param objects
     * @param caster
     * @param clippingPlanes
     * @return the object closest to a ray
     */
    public static Intersection raycast(Ray ray, ArrayList<Object3D> objects, Object3D caster, float[] clippingPlanes) {
        Intersection closestIntersection = null;

        for (int k = 0; k < objects.size(); k++) {
            Object3D currentObj = objects.get(k);
            if (caster == null || !currentObj.equals(caster)) {
                Intersection intersection = currentObj.getIntersection(ray);
                if (intersection != null) {
                    double distance = intersection.getDistance();
                    if (distance >= 0 &&
                            (closestIntersection == null || distance < closestIntersection.getDistance()) &&
                            (clippingPlanes == null || (intersection.getPosition().getZ() >= clippingPlanes[0] &&
                                    intersection.getPosition().getZ() <= clippingPlanes[1]))) {
                        closestIntersection = intersection;
                    }
                }
            }
        }

        return closestIntersection;
    }
}
