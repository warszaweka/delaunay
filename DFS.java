import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;
import parcs.*;

public class DFS implements AM {
    public void run(AMInfo info) {
        List<Point> points = (ArrayList)info.parent.readObject();
	List<Triangle> result;

	if (points.size() <= 1000) {
	    result = sequentialDelaunayTriangulation(points);
        } else {
            int mid = points.size() / 2;
            List<Point> leftPoints = new ArrayList<>(points.subList(0, mid));
            List<Point> rightPoints = new ArrayList<>(points.subList(mid, points.size()));

            point lp = info.createPoint();
            channel lc = lp.createChannel();
            lp.execute("DFS");
            lc.write((Serializable)leftPoints);

            point rp = info.createPoint();
            channel rc = rp.createChannel();
            rp.execute("DFS");
            rc.write((Serializable)rightPoints);

	    List<Triangle> leftResult = (ArrayList)lc.readObject();
	    List<Triangle> rightResult = (ArrayList)rc.readObject();
	    result = merge(leftResult, rightResult);
        }
        info.parent.write((Serializable)result);
    }

    private List<Triangle> sequentialDelaunayTriangulation(List<Point> points) {
        List<Triangle> triangles = new ArrayList<>();

        // Base case: Triangulate the points using the Delaunay triangulation algorithm
        if (points.size() == 3) {
            triangles.add(new Triangle(points.get(0), points.get(1), points.get(2)));
        } else if (points.size() > 3) {
            // Find the centroid of the points
            Point centroid = computeCentroid(points);

            // Sort the points counterclockwise around the centroid
            Collections.sort(points, (p1, p2) -> compareAngles(p1, p2, centroid));

            // Create the super triangle with the centroid and two extreme points
            Point extreme1 = points.get(0);
            Point extreme2 = points.get(points.size() - 1);
            Triangle superTriangle = new Triangle(centroid, extreme1, extreme2);
            triangles.add(superTriangle);

            // Add the remaining points one by one, updating the triangulation
            for (Point point : points) {
                List<Triangle> badTriangles = new ArrayList<>();
                for (Triangle triangle : triangles) {
                    if (isPointInsideCircumcircle(point, triangle)) {
                        badTriangles.add(triangle);
                    }
                }

                List<Edge> polygon = new ArrayList<>();
                for (Triangle badTriangle : badTriangles) {
                    for (Edge edge : badTriangle.edges()) {
                        if (!polygon.contains(edge) && isEdgeVisibleFromPoint(edge, point)) {
                            polygon.add(edge);
                        }
                    }
                }

                for (Triangle badTriangle : badTriangles) {
                    triangles.remove(badTriangle);
                }

                for (Edge edge : polygon) {
                    triangles.add(new Triangle(edge.p1, edge.p2, point));
                }
            }

            // Remove triangles that contain super triangle vertices
            triangles.removeIf(triangle -> isVertexFromSuperTriangle(triangle.p1, superTriangle) ||
                                           isVertexFromSuperTriangle(triangle.p2, superTriangle) ||
                                           isVertexFromSuperTriangle(triangle.p3, superTriangle));
        }

        return triangles;
    }

    private Point computeCentroid(List<Point> points) {
        double centroidX = 0;
        double centroidY = 0;
        int numPoints = points.size();

        for (Point point : points) {
            centroidX += point.x;
            centroidY += point.y;
        }

        centroidX /= numPoints;
        centroidY /= numPoints;

        return new Point(centroidX, centroidY);
    }

    private int compareAngles(Point p1, Point p2, Point center) {
        double angle1 = Math.atan2(p1.y - center.y, p1.x - center.x);
        double angle2 = Math.atan2(p2.y - center.y, p2.x - center.x);

        if (angle1 < angle2) {
            return -1;
        } else if (angle1 > angle2) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean isPointInsideCircumcircle(Point point, Triangle triangle) {
        double ax = triangle.p1.x - point.x;
        double ay = triangle.p1.y - point.y;
        double bx = triangle.p2.x - point.x;
        double by = triangle.p2.y - point.y;
        double cx = triangle.p3.x - point.x;
        double cy = triangle.p3.y - point.y;

        double det = ax * (by * cx - bx * cy) - ay * (bx * cx - by * cy) + (ax * by - ay * bx) * cx - (ax * by - ay * bx) * cy;

        return det > 0;
    }

    private boolean isEdgeVisibleFromPoint(Edge edge, Point point) {
        double ax = edge.p1.x - point.x;
        double ay = edge.p1.y - point.y;
        double bx = edge.p2.x - point.x;
        double by = edge.p2.y - point.y;

        double det = ax * by - ay * bx;

        return det <= 0;
    }

    private boolean isVertexFromSuperTriangle(Point vertex, Triangle superTriangle) {
        return vertex == superTriangle.p1 || vertex == superTriangle.p2 || vertex == superTriangle.p3;
    }

    private List<Triangle> merge(List<Triangle> left, List<Triangle> right) {
        List<Triangle> merged = new ArrayList<>();
        merged.addAll(left);
        merged.addAll(right);
        return merged;
    }

}
