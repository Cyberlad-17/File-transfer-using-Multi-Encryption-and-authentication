import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;

class TCPServer extends JFrame implements ActionListener 
{
	JFrame Frame;
	JLabel title, Title, subT1,subT11, subT2, subT3, msg, error, servFiles;
	Font font,labelfont;
	JTextField txt1,txt2,txt3;
	JButton down;
	String name, file, path;
	int portNumber;
	TCPServer() 
	{
		Frame=new JFrame("Stage 11 ");
		Frame.getContentPane().setBackground(Color.PINK);
		String pic=new String("w1.png");
		// This is the image, I stored in that directory in my machine
		new Image_Attach(Frame,pic);
		Frame.setVisible(true);
		Frame.setSize(2000,2000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		font = new Font("Algerian",Font.BOLD,47);
		Title=new JLabel("SERVER SIDE Transfer of Files");
		Title.setForeground(Color.RED);
		Title.setFont(font);
		Title.setBounds(350,60,1000,55);
		labelfont = new Font("Times New Roman",Font.PLAIN,30);
		subT11=new JLabel("Directory Name : ");
		subT11.setForeground(Color.white);
		subT11.setFont(labelfont);
		subT11.setBounds(440,255,300,40);
		txt1 = new JTextField();
		txt1.setBounds(950,255,300,40); 
		subT2 = new JLabel("Port Number :");
		subT2.setForeground(Color.white);
		subT2.setFont(labelfont);
		subT2.setBounds(440,415,300,40); 
		txt2 = new JTextField();
		txt2.setBounds(950,415,300,40); 
		subT3 = new JLabel("Host Address :");
		subT3.setForeground(Color.white);
		subT3.setFont(labelfont);
		subT3.setBounds(440,565,300,40); 
		txt3 = new JTextField();
		txt3.setBounds(950,565,300,40); 
		down = new JButton("Done");
		down.setForeground(Color.black);
		//down.setBackground(Color.yellow);
		down.setBounds(1000,760,150,45);
		error = new JLabel("");
		error.setFont(labelfont);
		error.setBounds(500, 630, 500, 50);
		Frame.add(Title);
		Frame.add(subT11);
		Frame.add(txt1);
		Frame.add(subT2); 
		Frame.add(txt2);
		Frame.add(subT3);
		Frame.add(txt3);
		Frame.add(down);
		Frame.add(error);
		down.addActionListener(this);
	}
	public void actionPerformed(ActionEvent event) 
	{
			String dir=txt1.getText();
			if(dir==null)
			{
				error.setText("Please enter a valid directory name ...");
			}
			else 
			{
				int port=Integer.parseInt(txt2.getText());
				if(port==0)
					port=3333;			//defult value being set to the port number
				String ip=txt3.getText();
				if(ip==null)
					ip="localhost";		//default value being set to the port number
				new TCPServer1(dir,ip,port); 
			}
		//}
	}

	public static void main(String args[]) 
	{
		new TCPServer();
	}
}

class TCPServer1 extends JFrame 
{
	TCPServer1(String dir,String ip,int port) 
	{
		try
		{
			int id=1;
			ServerSocket welcomeSocket;
			welcomeSocket = new ServerSocket(port);
			while (true) {
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("Client with ID " + id + " connected from " + connectionSocket.getInetAddress().getHostName() + "...");
				Thread server = new ThreadedServer(connectionSocket, id, dir);
				id++;
				server.start();
			}
		}
		catch(IOException exc)
		{
			System.out.println("Exception: " + exc.getMessage());
		}
	}
}

class ThreadedServer extends Thread {
	int n;
	int m;
	String name, f, ch, fileData;
	String filename;
	Socket connectionSocket;
	int counter;
	String dirName;

	public ThreadedServer(Socket s, int c, String dir) {
		connectionSocket = s;
		counter = c;
		dirName = dir;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			InputStream inFromClient = connectionSocket.getInputStream();
			// PrintWriter outPw = new PrintWriter(connectionSocket.getOutputStream());
			OutputStream output = connectionSocket.getOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(output);
			oout.writeObject("Server says Hi!");
			File ff = new File(dirName);
			ArrayList<String> names = new ArrayList<String>(Arrays.asList(ff.list()));
			int len = names.size();
			oout.writeObject(String.valueOf(names.size()));
			for(String name: names) 
			{
				oout.writeObject(name);
			}
			name = in.readLine();
			ch = name.substring(0, 1);
			if (ch.equals("*")) 
			{
				n = name.lastIndexOf("*");
				filename = name.substring(1, n);
				FileInputStream file = null;
				BufferedInputStream bis = null;
				boolean fileExists = true;
				System.out.println("Request to download file " + filename + " recieved from " + connectionSocket.getInetAddress().getHostName() + "...");
				String f2=filename;
				filename = dirName + filename;
				try 
				{
					file = new FileInputStream(filename);
					bis = new BufferedInputStream(file);
				} 
				catch (FileNotFoundException excep) 
				{
					fileExists = false;
					System.out.println("FileNotFoundException:" + excep.getMessage());
				}
				if (fileExists) {
					oout = new ObjectOutputStream(output);
					oout.writeObject("Success");
					System.out.println("Download begins");
					sendBytes(bis, output);
					new Receiver(dirName,f2);
					System.out.println("Completed");
					bis.close();
					file.close();
					oout.close();
					output.close();
				}
				else 
				{
					oout = new ObjectOutputStream(output);
					oout.writeObject("FileNotFound");
					bis.close();
					file.close();
					oout.close();
					output.close();
				}
			} 
			else{
				try {
					boolean complete = true;
					System.out.println("Request to upload file " + name + " recieved from " + connectionSocket.getInetAddress().getHostName() + "...");
					File directory = new File(dirName);
					if (!directory.exists()) {
						System.out.println("Dir made");
						directory.mkdir();
					}
					int size = 9022386;
					byte[] data = new byte[size];
					File fc = new File(directory, name);
					FileOutputStream fileOut = new FileOutputStream(fc);
					DataOutputStream dataOut = new DataOutputStream(fileOut);

					while (complete) {
						m = inFromClient.read(data, 0, data.length);
						if (m == -1) {
							complete = false;
							System.out.println("Completed");
						} else {
							dataOut.write(data, 0, m);
							dataOut.flush();
						}
					}
					fileOut.close();
				} catch (Exception exc) {
					System.out.println(exc.getMessage());
				}
			}
		} 
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	private static void sendBytes(BufferedInputStream in , OutputStream out) throws Exception 
	{
		int size = 9022386;
		byte[] data = new byte[size];
		int c = in .read(data, 0, data.length);
		out.write(data, 0, c);
		out.flush();
	}
}
