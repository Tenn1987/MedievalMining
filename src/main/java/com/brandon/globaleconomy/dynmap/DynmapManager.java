package com.brandon.globaleconomy.dynmap;

import com.brandon.globaleconomy.city.City;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import java.awt.geom.Point2D;
import java.util.*;

public class DynmapManager {
    private final Plugin plugin;
    private final DynmapAPI dynmapApi;
    private final MarkerSet cityMarkerSet;
    private final MarkerIcon markerIcon;

    public DynmapManager(Plugin plugin, DynmapAPI dynmapApi) {
        this.plugin = plugin;
        this.dynmapApi = dynmapApi;

        MarkerSet set = dynmapApi.getMarkerAPI().getMarkerSet("citymarkers");
        if (set == null) {
            set = dynmapApi.getMarkerAPI().createMarkerSet(
                    "citymarkers", "Cities", null, false
            );
        }
        this.cityMarkerSet = set;
        this.markerIcon = dynmapApi.getMarkerAPI().getMarkerIcon("default");
    }

    /**
     * Add or update a polygon for a city on Dynmap using claimed chunks.
     */
    public void addOrUpdateCityAreaPolygon(City city, Set<Chunk> claimedChunks) {
        if (claimedChunks == null || claimedChunks.isEmpty()) {
            return;
        }
        String markerId = "city-" + city.getName();
        AreaMarker marker = cityMarkerSet.findAreaMarker(markerId);

        List<Point2D> points = new ArrayList<>();
        for (Chunk chunk : claimedChunks) {
            int minX = chunk.getX() << 4;
            int minZ = chunk.getZ() << 4;
            points.add(new Point2D.Double(minX, minZ));
            points.add(new Point2D.Double(minX + 16, minZ));
            points.add(new Point2D.Double(minX + 16, minZ + 16));
            points.add(new Point2D.Double(minX, minZ + 16));
        }

        // Convex hull for prettier polygon
        List<Point2D> hull = computeConvexHull(points);

        double[] x = new double[hull.size()];
        double[] z = new double[hull.size()];
        for (int i = 0; i < hull.size(); i++) {
            x[i] = hull.get(i).getX();
            z[i] = hull.get(i).getY();
        }

        if (marker == null) {
            marker = cityMarkerSet.createAreaMarker(markerId, city.getName(), false,
                    city.getLocation().getWorld().getName(), x, z, false);
        } else {
            marker.setCornerLocations(x, z);
        }

        // Set color using city's color property (default fallback: magenta)
        String colorStr = city.getColor();
        int color = 0xFF00FF;
        try {
            if (colorStr != null && colorStr.length() == 7 && colorStr.startsWith("#")) {
                color = Integer.parseInt(colorStr.substring(1), 16);
            }
        } catch (Exception ignore) {}

        marker.setLineStyle(2, 1.0, color);
        marker.setFillStyle(0.45, color);
        marker.setDescription("City: " + city.getName() + "<br>Nation: " + city.getNation());
        System.out.println("[DynmapManager] City: " + city.getName());
        System.out.println("  World: " + city.getLocation().getWorld().getName());
        System.out.println("  Chunks: " + (claimedChunks == null ? "null" : claimedChunks.size()));
        System.out.println("  Color: " + city.getColor());
        System.out.println("  Polygon Points: " + points.size() + " hull: " + hull.size());

    }

    /**
     * Remove a city's polygon/area marker from Dynmap.
     */
    public void removeCityAreaPolygon(City city) {
        String markerId = "city-" + city.getName();
        AreaMarker marker = cityMarkerSet.findAreaMarker(markerId);
        if (marker != null) {
            marker.deleteMarker();
        }
    }

    /** Computes the convex hull of a list of 2D points. */
    public static List<Point2D> computeConvexHull(List<Point2D> points) {
        if (points == null || points.size() < 3) {
            return new ArrayList<>();
        }
        points = new ArrayList<>(points);
        points.sort(Comparator.comparing(Point2D::getX).thenComparing(Point2D::getY));
        List<Point2D> lower = new ArrayList<>();
        for (Point2D p : points) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0) {
                lower.remove(lower.size() - 1);
            }
            lower.add(p);
        }
        List<Point2D> upper = new ArrayList<>();
        for (int i = points.size() - 1; i >= 0; i--) {
            Point2D p = points.get(i);
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0) {
                upper.remove(upper.size() - 1);
            }
            upper.add(p);
        }
        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);
        lower.addAll(upper);
        return lower;
    }

    private static double cross(Point2D o, Point2D a, Point2D b) {
        return (a.getX() - o.getX()) * (b.getY() - o.getY()) -
                (a.getY() - o.getY()) * (b.getX() - o.getX());
    }
}
