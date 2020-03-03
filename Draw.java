
/**
 * Implementation of distributed drawing program.
 * Creates a canvas and transmits user input to another identical program
 * via a DatagramSocket to mirror the canvas's.
 * 
 * @author Ivar Lund
 * ivarnilslund@gmail.com
 * 
 */
import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * Main class of Draw program. Instantiates Paper class to create a drawing
 * canvas.
 * 
 * @author Ivar Lund
 *
 */
@SuppressWarnings("serial")
public class Draw extends JFrame {

	/**
	 * Main class constructor. Takes user input parameters to set DatagramSocket and
	 * DatagramPacket. Also sets up GUI.
	 * 
	 * @param port      the port for this units DatagramSocket
	 * @param host      the host that will be used for DatagramPackets InetAddress
	 * @param otherPort the port for DatagramPacket
	 */
	public Draw(int port, String host, int otherPort) {
		Paper p = new Paper(port, host, otherPort);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(p, BorderLayout.CENTER);
		setSize(640, 480);
		setVisible(true);
		setTitle("Draw");
		System.out.println("Transmitter port: " + port + "\n" + "Receiver host: " + host + "\n" + "Port " + otherPort);
	}

	/**
	 * Main method. Takes user input as parameters for connection setup. If user
	 * arguments are not provided default values will be used.
	 * 
	 * @param args array that holds user input
	 */
	public static void main(String[] args) {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 2000;
		String host = args.length > 1 ? args[1] : "localhost";
		int otherPort = args.length > 2 ? Integer.parseInt(args[2]) : 2001;

		new Draw(port, host, otherPort);
	}
}
