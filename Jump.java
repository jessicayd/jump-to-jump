import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;

/* Implements the game loop. Tracks the time of a space bar press and converts this into
distance of the jump. Player attempts to jump on blocks until a jump is unsuccessful.
A start and end (lost) screen is supported, where the start screen displays the rules
and the end screen displays the leaderboard and supports mouse clicks. */

public class Jump {
    private static ArrayList<Integer> lboard =
            new ArrayList<Integer>(); // leaderboard to keep track of all points
        /* @citation used ArrayList API
            https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
            Accessed 05/2/2022. */
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

    // screen when the player has lost the game
    public static void lost(int points) {
        StdDraw.disableDoubleBuffering();
        StdDraw.clear();

        Font font = new Font(AV, Font.BOLD, 40);
        StdDraw.setFont(font);

        String bg = "sheer.png"; // background color

        StdDraw.picture(0, 0, "bg2.png", 20, 20); // background picture
        StdDraw.picture(0, 7.5, bg, 15, 4);
        StdDraw.text(0, 8, "FINAL SCORE: " + points); // display final score

        Font font2 = new Font(AV, Font.PLAIN, 20);
        StdDraw.setFont(font2);

        StdDraw.text(0, 6.5, "Click screen to replay!");

        // adding value into leaderboard, remove if below top 5
        lboard.add(points);
        lboard.sort(Comparator.reverseOrder());
            /* @citation referenced online resource to reverse sort Arraylist
            https://www.java67.com/2017/07/how-to-sort-arraylist-of-objects-using.html
            Accessed 05/2/2022. */
        if (lboard.size() == 6) lboard.remove(5);

        Font font3 = new Font(AV, Font.BOLD, 30);
        StdDraw.setFont(font3);

        StdDraw.picture(0, -1, bg, 9, 11.5);
        StdDraw.text(0, 3.5, "Leaderboard");

        StdDraw.setFont(font2);
        StdDraw.text(-2, 2, "Rank");
        StdDraw.text(2, 2, "Points");

        Font font4 = new Font(AV, Font.BOLD, 22);
        StdDraw.setFont(font4);

        int count = 0; // amount of scores kept

        // display leaderboard, top 5 ranks
        for (int score : lboard) {
            StdDraw.text(-2, 0.5 - 1.45 * count, "★ " + (count + 1));
            StdDraw.text(2, 0.5 - 1.45 * count, String.valueOf(score));
            count++;
        }

        StdDraw.setFont(font2);

        StdDraw.picture(5, -8.5, bg, 8, 1.5);
        StdDraw.text(5, -8.5, "Clear Leaderboard");

        StdDraw.picture(-5, -8.5, bg, 5, 1.5);
        StdDraw.text(-5, -8.5, "See Rules");

        while (true) {
            /* @citation for mouseX and mouseY locations in StdDraw API */
            // when mouse hovers over clear leaderboard (white button, dimensions below)
            boolean clear = StdDraw.mouseY() > -9.25 && StdDraw.mouseY() < -7.75
                    && StdDraw.mouseX() > 1 && StdDraw.mouseX() < 9;
            // when mouse hovers over rules (white button, dimensions below)
            boolean rules = StdDraw.mouseY() > -9.25 && StdDraw.mouseY() < -7.75
                    && StdDraw.mouseX() > -7.5 && StdDraw.mouseX() < -2.5;

            // if Clear Leaderboard is pressed
            if (clear && StdDraw.isMousePressed()) {
                lboard.clear();
                StdDraw.clear();
                StdDraw.pause(100);
                lost(points);
            }
            // if See Rules is pressed
            if (rules && StdDraw.isMousePressed()) {
                StdDraw.clear();
                StdDraw.pause(100);
                start();
            }

            // if the rest of the screen is pressed
            else if (!clear && !rules && StdDraw.isMousePressed()) jump();
        }
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
    public static void jump() {
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

                    lost(sum);
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


    }

    // start up screen
    public static void start() {
        StdDraw.picture(0.1, 0.5, "rules.png", 19.5, 17.5);

        // begins game is mouse is pressed
        while (true) {
            if (StdDraw.isMousePressed()) {
                jump();
            }

        }
    }

    // tests probability of skin of box & lost screen (method in Jump)
    public static void test() {
        StdOut.println("Tests for skin() method");

        int calls = 100000; // number of calls to method
        double[] freq
                = new double[10]; // base, purple, blue, pink, yellow, green,
        // will, tooth, eggie, tiggy

        // increments frequency array for each skin given 100,000 calls
        for (int i = 0; i < calls; i++) {
            String text = skin();
            if (text.equals(base[0]) || text.equals(base[1])
                    || text.equals(base[2])
                    || text.equals(base[3]) || text.equals(base[4])
                    || text.equals(base[5])) freq[0]++;

            if (text.equals(base[0])) freq[1]++;
            else if (text.equals(base[1])) freq[2]++;
            else if (text.equals(base[2])) freq[3]++;
            else if (text.equals(base[3])) freq[4]++;
            else if (text.equals(base[4])) freq[5]++;
            else if (text.equals(base[5])) freq[6]++;
            else if (text.equals(box[1])) freq[7]++;
            else if (text.equals(box[2])) freq[8]++;
            else if (text.equals(box[3])) freq[9]++;
        }

        double[] expect = { 0.7, 0.1, 0.1, 0.1 }; // expected prob of skins

        String str = "\t\tActual Probability of ";
        String s = "\t\t\t\t\t\t";

        StdOut.println("   Test 1: " + calls +
                               " calls probabilities of special skins");
        StdOut.printf("\tExpected Probability of Base Skin: %.3f " + str
                              + "Base Skin: %.3f\n",
                      expect[0], (freq[0] / calls));
        StdOut.printf("\tExpected Probability of toothless.png: %.3f " + str
                              + "toothless.png: %.3f\n",
                      expect[1], (freq[7] / calls));
        StdOut.printf("\tExpected Probability of eggie.png: %.3f " + str
                              + "eggie.png: %.3f\n",
                      expect[2], (freq[8] / calls));
        StdOut.printf("\tExpected Probability of tiggy.png: %.3f " + str
                              + "tiggy.png: %.3f\n",
                      expect[3], (freq[9] / calls));

        StdOut.println("   Test 2: " + calls +
                               " calls probabilities of base skins");
        StdOut.print("\tExpected Probability of Each Base Skin: 0.167");
        StdOut.printf(str + "purple.png: %.3f\n", (freq[1] / freq[0]));
        StdOut.printf(s + str + "blue.png: %.3f\n", (freq[2] / freq[0]));
        StdOut.printf(s + str + "pink.png: %.3f\n", (freq[3] / freq[0]));
        StdOut.printf(s + str + "yellow.png: %.3f\n", (freq[4] / freq[0]));
        StdOut.printf(s + str + "green.png: %.3f\n", (freq[5] / freq[0]));
        StdOut.printf(s + str + "william.png: %.3f\n", (freq[6] / freq[0]));


        // tests mouseClick for the lost screen
        // when clicking on the bottom left box,
        // screen should go to the rules(start) page
        // when clicking on the bottom right box,
        // screen should clear all the past records
        // of points except for the newest one inputted
        // (which is 30 in this case)
        // when clicked anywhere else on the screen,
        // the screen should go to the game playing mode
        StdOut.println("\nTests for lost() method: see screen");

        int call2 = 4;
        int point = 30;
        StdDraw.setXscale(-10, 10);
        StdDraw.setYscale(-10, 10);
        for (int i = 0; i < call2; i++) {
            lboard.add(StdRandom.uniform(100));
        }
        lost(point);


    }


    public static void main(String[] args) {

        // displays test cases given command line argument
        if (args[0].equals("--test") || args[0].equals("-t")) {
            test();
        }

        // begins game given command line argument
        else if (args[0].equals("--game") || args[0].equals("-g")) {
            StdDraw.setXscale(-10, 10);
            StdDraw.setYscale(-10, 10);

            start();
        }
    }
}
