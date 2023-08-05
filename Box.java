/* Class for Box objects, which are constructed with a skin String name, an initial x
position, and a point value. Boxes can return their point value and position as well as
decrement their position and output the drawing to StdDraw.
*/

public class Box {
    private double x; // location of object
    private String pic; // skin of box
    private int point; // point of each box

    // initialize the box
    public Box(String pic, double pos) {
        x = pos; // init to given random postion
        this.pic = pic;

        // gives points based on box skin
        if (pic.equals("purple.png")) point = 1;
        else if (pic.equals("blue.png")) point = 1;
        else if (pic.equals("pink.png")) point = 1;
        else if (pic.equals("green.png")) point = 1;
        else if (pic.equals("yellow.png")) point = 1;
        else if (pic.equals("william.png")) point = 1;
        else if (pic.equals("toothless.png")) point = 3;
        else if (pic.equals("eggie.png")) point = 4;
        else if (pic.equals("tiggy.png")) point = 5;

    }

    // return int of points
    public int points() {
        return point;
    }

    // return double of box position
    public double position() {
        return x;
    }

    // draw the box
    public void draw() {
        double size = 3;
        StdDraw.picture(x, -size / 1.5, pic, size, size);
    }

    // move the box
    public void move() {
        draw();
        x -= 0.1; // one time increment of box
    }

    public static void main(String[] args) {


    }
}

