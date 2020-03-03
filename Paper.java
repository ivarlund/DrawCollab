import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.JPanel;

/**
 * Worker class of Draw program. Creates a canvas that allows for user input via
 * the mouse to paint on the canvas. Sets up a connection with a DatagramSocket
 * and transceives user input with DatagramPackets. Runs a separate Runnable
 * Thread to listen for incoming data from a foreign host.
 *
 * @author Ivar Lund
 * ivarnilslund@gmail.com
 */
@SuppressWarnings("serial")
public class Paper extends JPanel implements Runnable {

    private Set<Point> hs = new ConcurrentHashMap<>().newKeySet();
    private int otherPort;

    private DatagramSocket socket;
    InetAddress address;
    Thread t = new Thread(this);

    /**
     * Class constructor. Sets up the DatagramSocket and InetAdress and port number
     * for DatagramPackets and starts the listener thread. Also sets up the GUI
     * canvas.
     *
     * @param port      the port for this units DatagramSocket
     * @param host      the host that will be used for DatagramPackets InetAddress
     * @param otherPort the port for DatagramPacket
     */
    public Paper(int port, String host, int otherPort) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Could not connect socket to port: " + port + ".\nTerminating");
            System.exit(1);
            e.printStackTrace();
        }
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.out.println("Unknown foreign host: " + host + "\nTerminating");
            System.exit(1);
            e.printStackTrace();
        }
        this.otherPort = otherPort;
        t.start();
        setBackground(Color.white);
        addMouseListener(new L1());
        addMouseMotionListener(new L2());
    }

    /**
     * Thread that listens for incoming data.
     */
    @Override
    public void run() {
        while (true) {
            receiveMsg();
        }
    }

    /**
     * Paint component for GUI canvas.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        Iterator<Point> i = hs.iterator();
        while (i.hasNext()) {
            Point p = i.next();
            g.fillOval(p.x, p.y, 3, 3);
        }
    }

    /**
     * Method for receiving data from foreign host. Constructs a local
     * DatagramPacket for receival of data and processes the data.
     */
    private void receiveMsg() {
        byte[] b = new byte[1024];
        DatagramPacket packet = new DatagramPacket(b, b.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            System.out.println("Error: Could not properly receive package.");
            e.printStackTrace();
        }
        String msg = new String(packet.getData(), 0, packet.getLength());
        String[] xy = msg.split(" ");
        Point p = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
        System.out.println(msg + " recieved");
        hs.add(p);
        repaint();
    }

    /**
     * Method for sending user input data to foreign host. Constructs a local
     * DatagramPacket for transmitting data. Uses InetAddress and port number
     * provided from class constructor.
     *
     * @param p         Point object to send
     * @param otherPort Port number to send to
     */
    private void sendMsg(Point p, int otherPort) {
        byte[] b;
        String msg = Integer.toString(p.x) + " " + Integer.toString(p.y);
        b = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(b, b.length, address, otherPort);

        try {
            socket.send(packet);
        } catch (IOException e) {
            System.out.println("Error: could not properly send package.");
            e.printStackTrace();
        }

        System.out.println(msg + " sent");
    }

    /**
     * Method for adding and updating GUI canvas with new Points. Also transmits
     * Points.
     *
     * @param p Point to operate.
     */
    private void addPoint(Point p) {
        hs.add(p);
        repaint();
        sendMsg(p, otherPort);
    }

    /**
     * Class for mouse user input.
     */
    private class L1 extends MouseAdapter {
        /**
         * Provides Points from mouse peripherals.
         *
         * @param me Provides Point data
         */
        public void mousePressed(MouseEvent me) {
            addPoint(me.getPoint());
        }
    }

    /**
     * Class for mouse user input.
     */
    private class L2 extends MouseMotionAdapter {
        /**
         * Provides Points from mouse peripherals.
         *
         * @param me Provides Point data
         */
        public void mouseDragged(MouseEvent me) {
            addPoint(me.getPoint());
        }
    }

}
