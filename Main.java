
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    String worldAndRidesFileName = args[0];
    //modify the following to launch the allocation which would read the rides file and print 
    //the allocation to the standard output, e.g.
    World a = new World(worldAndRidesFileName);
    
  }
}