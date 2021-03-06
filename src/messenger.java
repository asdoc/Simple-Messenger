import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class messenger extends Thread
{
	static String ip=null;
	static int port=5000;
	static String globalmsg;
	static myui ui=new myui();

	private ServerSocket serverSocket;

	public static class myui extends JFrame
	{

		final JLabel message=new JLabel("Please enter ip to connect");
		final JTextField ipui=new JTextField("127.0.0.1");
		final JButton okbtn=new JButton("Connect");
		final JTextField sendmsg=new JTextField();
		final JButton toSend=new JButton("send");

		public void initialise()
		{
			final JPanel panel = new JPanel();
			setContentPane(panel);
			panel.setLayout(null);
			ipui.setBounds(20,20,200,40);
			panel.add(ipui);
			okbtn.setBounds(20,70,200,40);
			panel.add(okbtn);
			Action okButtonAction = new AbstractAction(){

				@Override
				public void actionPerformed(ActionEvent event)
				{
					ip=ipui.getText();
					message.setText("Connecting to: "+ip);
					panel.setVisible(false);
					repaint();
					validate();
					afterinitialise();
				}
			};
			okbtn.addActionListener(okButtonAction);
			ipui.addActionListener(okButtonAction);
			setTitle("Initialise");
			setSize(250, 170);
			setResizable(false);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}

		public void afterinitialise()
		{
			JPanel newpanel = new JPanel();
			setContentPane(newpanel);
			newpanel.setLayout(null);
			message.setBounds(10,10,390,80);
			newpanel.add(message);
			sendmsg.setBounds(10,100,300,30);
			newpanel.add(sendmsg);
			toSend.setBounds(310,100,80,30);
			newpanel.add(toSend);

			Action toSendAction = new AbstractAction(){

				@Override
				public void actionPerformed(ActionEvent e) {
					globalmsg=sendmsg.getText();
					Thread p=new send(port);
					p.start();
					sendmsg.setText("");
				}

			};
			toSend.addActionListener(toSendAction);
			sendmsg.addActionListener(toSendAction);
			setTitle("Simple Messenger");
			setResizable(true);
			setSize(400, 200);
			setResizable(true);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}

		public myui()
		{
			initialise();
		}

		public void onrecieved(String msgrecieved)
		{
			message.setText(msgrecieved);
		}
	}

	public static class send extends Thread
	{
		int port;
		public send(int a)
		{
			port=a;
		}

		public void run()
		{
			try {
				Socket socket=new Socket(ip,port);
				DataOutputStream out=new DataOutputStream(socket.getOutputStream());
				String msg;
				out.writeUTF(globalmsg);
				out.flush();
				socket.close();
			}catch(IOException e){}
		}
	}
	public messenger(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
	}

	public void run()
	{
		while(true)
		{
			try
			{
				Socket server = serverSocket.accept();
				DataInputStream in =
						new DataInputStream(server.getInputStream());
				ui.onrecieved(in.readUTF());
				server.close();
			}catch(SocketTimeoutException s)
			{
				System.out.println("Socket timed out!");
				break;
			}catch(IOException e)
			{
				e.printStackTrace();
				break;
			}
		}
	}
	public static void main(String [] args)
	{

		ui.setVisible(true);
		try
		{
			Thread t = new messenger(port);
			t.start();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
