import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;
import parcs.*;

public class Bluck {
    public static void main(String[] args) throws Exception {
	long startTime = System.currentTimeMillis();
        task curtask = new task();
        curtask.addJarFile("DFS.jar");
        List<Point> points = fromFile(curtask.findFile("input"));

        AMInfo info = new AMInfo(curtask, null);
        point p = info.createPoint();
        channel c = p.createChannel();
        p.execute("DFS");
        c.write((Serializable)points);


        System.out.println("Waiting for result...");
	List<Triangle> result = (ArrayList)c.readObject();
	long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time " + (endTime - startTime));
	//for (Triangle triangle : result) {
        //    System.out.println("Triangle: (" +
        //            triangle.p1.x + ", " + triangle.p1.y + "), " +
        //            "(" + triangle.p2.x + ", " + triangle.p2.y + "), " +
        //            "(" + triangle.p3.x + ", " + triangle.p3.y + ")");
        //}
        curtask.end();
    }

    public static List<Point> fromFile(String filename) throws Exception {
        Scanner sc = new Scanner(new File(filename));
        List<Point> points = new ArrayList<>();
	int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
	    double x = sc.nextDouble();
	    double y = sc.nextDouble();
            points.add(new Point(x, y));
        }
        return points;
    }
}
