package efruchter.glutilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.util.vector.Vector3f;

public class ObjLoader {
	public static List<Vector3f> importObj(final File objFile) throws FileNotFoundException {
		Scanner objScanner = new Scanner(objFile);
		List<Vector3f> vertices = new LinkedList<Vector3f>();
		while(objScanner.hasNextLine()) {
			String line = objScanner.nextLine().trim();
			if (line.startsWith("v ")) {
				String vS[] = line.substring(2).trim().split(" ");
				vertices.add(new Vector3f(Float.parseFloat(vS[0]), Float.parseFloat(vS[1]), Float.parseFloat(vS[2])));
			}
		}
		return vertices;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		for (Vector3f v :ObjLoader.importObj(new File("shape.obj"))) {
			System.out.println(v);
		}
	}
}
