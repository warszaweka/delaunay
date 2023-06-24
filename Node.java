import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

class Point implements Serializable {
    public double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Edge implements Serializable {
    public Point p1, p2;

    public Edge(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Edge other = (Edge) obj;
        return (p1.equals(other.p1) && p2.equals(other.p2)) ||
               (p1.equals(other.p2) && p2.equals(other.p1));
    }

    @Override
    public int hashCode() {
        return p1.hashCode() + p2.hashCode();
    }
}

class Triangle implements Serializable {
    public Point p1, p2, p3;

    public Triangle(Point p1, Point p2, Point p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public List<Edge> edges() {
        List<Edge> triangleEdges = new ArrayList<>();
        triangleEdges.add(new Edge(p1, p2));
        triangleEdges.add(new Edge(p2, p3));
        triangleEdges.add(new Edge(p3, p1));
        return triangleEdges;
    }
}

