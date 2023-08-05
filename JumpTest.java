import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/* Implements the game loop in the same fashion as Jump.java but for specifically for testing
complexity of jump.java. Start and end screens (lost() and start()) are omitted for simplicity
but do not affect functionality of jump().   */

public class JumpTest {

    private static final String AV = "Avenir"; // font used for text
    private static final String P = "Points: "; // text used repetitively

    private static String[] box = {
            "", "toothless.png",
            "eggie.png", "tiggy.png"
    }; // box skins
    private static String[] base = {
            "purple.png", "blue.png", "pink.png",
            "yellow.png", "green.png", "william.png"
    }; // base 1-point box skins

    // timer to keep track of how long SPACE bar is pressed for,
    // returns the double, time
    public static double timer() {
        double time = 0;

        /* @citation used Key Event to find key codes
            https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html
            Accessed 05/2/2022. */
        while (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
            int SCALE = 1000000; // scaling time to proper increment
            time += 1.0 / SCALE;
        }
        return time;
    }

    // select skin for box based on probability,
    // returns String skin type
    public static String skin() {
        double[] prob = { 0.7, 0.1, 0.1, 0.1 }; // set probabilities for boxes
        int index = StdRandom.discrete(prob); // random index based on probabilities

        if (index != 0) return box[index]; // if not a 1-point box, return which special box
        else return base[StdRandom.uniform(base.length)]; // equal likely chance of 1-point boxes

    }

    // the person jumps
    public static int jump() {
        StdDraw.clear();

        // animation characters
        String jess = "jess.png";
        String vickle = "vickle.png";


        ArrayList<Box> boxes = new ArrayList<Box>(); // keep track of boxes

        // first box initialized at position 0
        Box box0 = new Box(skin(), 0);
        boxes.add(box0);

        for (int i = 0; i < 10; i++) { // start with adding 10 boxes to list
            Box box1 = new Box(skin(),
                               boxes.get(boxes.size() - 1).position() +
                                       StdRandom.uniform(3.0, 10.0));
            boxes.add(box1);
        }


        int count = 0; // the amount of times SPACE is pressed
        int sum = 0; // keep track of points

        // draws initial positions of boxes (many off screen)
        for (int i = 0; i < boxes.size(); i++) {
            boxes.get(i).draw();
        }

        StdDraw.picture(0, 0, jess, 2, 3); // draw person

        Font font = new Font(AV, Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.text(0, 8, P + sum);

        double v = 10; // velocity
        double a = -9.8; // gravity acceleration

        boolean play = true;

        // main game loop
        while (play) {
            boolean noMove = true;

            double time = 0;

            // waits for space bar press
            while (noMove) {
                /* @citation for mouseX and mouseY locations in StdDraw API */
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                    count++;
                    noMove = false;
                    time = 1 / timer();

                    // boundary for time of SPACE pressed
                    if (time > 0.08) time = 0.08; // minimum time allowed for a jump (caps it)
                    if (time < 0.019) time = 0.019; // maximum time allowed for a jump

                }
            }

            double y; // position of person
            for (double t = 0; t < 2 * v; t += time) {

                StdDraw.enableDoubleBuffering();
                y = t * v + a * t * t / 2;

                StdDraw.clear();

                // increments each of the boxes by one time t
                for (int i = 0; i < boxes.size(); i++) {
                    boxes.get(i).move();
                }

                StdDraw.picture(0, y, vickle, 2, 3); // image of jumping person
                StdDraw.text(0, 8, P + sum);

                StdDraw.show();
                StdDraw.pause(8);

                int index = -1;
                // find the index of box that person lands on
                for (int i = 0; i < boxes.size(); i++) {
                    if (y < 0 && (boxes.get(i).position() < 1.3 &&
                            boxes.get(i).position() > -1.7)) {
                        index = i;
                    }
                }

                // person succeed in landing
                if (index != -1) {
                    sum = sum + boxes.get(index).points();

                    StdDraw.clear();

                    // redraw boxes & player once to display points correctly
                    for (int i = 0; i < boxes.size(); i++) {
                        boxes.get(i).draw();
                    }
                    StdDraw.picture(0, y, jess, 2, 3);
                    StdDraw.text(0, 8, P + sum);

                    StdDraw.show();
                    StdDraw.pause(8);

                    break;
                }

                // person fails to land
                if (y < -0.5) {

                    // displays the player falling off screen
                    for (double ti = t; ti < v / 3; ti += time) {

                        StdDraw.enableDoubleBuffering();
                        double ynow = ti * v + a * ti * ti / 2;

                        StdDraw.clear();


                        for (int i = 0; i < boxes.size(); i++) {
                            boxes.get(i).move();
                        }
                        StdDraw.picture(0, ynow, vickle, 2, 3);
                        StdDraw.text(0, 8, P + sum);

                        StdDraw.show();
                        StdDraw.pause(8);

                    }

                    // lost(sum);
                    play = false;
                    break;
                }
            }

            // add new box at random position
            double pos = StdRandom.uniform(4, 10);
            Box box2 = new Box(skin(),
                               boxes.get(boxes.size() - 1).position() + pos);
            boxes.add(box2);
            if (count > 10) boxes.remove(0); // remove past boxes

        }
        return count;


    }

    // tests Big-O complexity component of jump() by measuring elapsed time of each
    // loop call of jump()
    // call to lost() is removed from jump() in order to return a total count of boxes
    public static void testComp() {
        Stopwatch timer = new Stopwatch();

        int count = jump();
        double time = timer.elapsedTime();

        StdOut.println("Total count: " + count + "\tElapsed Time: " + time);
    }

    public static void main(String[] args) {

        // displays test cases given command line argument
        if (args[0].equals("--comp") || args[0].equals("-c")) {
            StdDraw.setXscale(-10, 10);
            StdDraw.setYscale(-10, 10);

            testComp();
        }

        // begins game given command line argument
        else if (args[0].equals("--game") || args[0].equals("-g")) {
            StdDraw.setXscale(-10, 10);
            StdDraw.setYscale(-10, 10);

            jump();
        }
    }
}
