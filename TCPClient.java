import java.io.*;
import java.net.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;

class Image_Attach extends javax.swing.JFrame 
{
   Image_Attach(JFrame f,URL pic)
   {
		//URL url=new URL("https://wallpaperaccess.com/full/3308617.png");  
		//f.setContentPane(new JLabel(new ImageIcon(url)));
		f.setContentPane(new JLabel(new ImageIcon(pic)));
		f.setLayout(null);
		f.setVisible(true);
   }
   Image_Attach(JFrame f,String pic)
   {
		f.setContentPane(new JLabel(new ImageIcon(pic)));
		f.setLayout(null);
		f.setVisible(true);
   }
}

class TCPClient extends JFrame implements ActionListener 
{
	JFrame Frame;
	JLabel title, Title, subT1,subT11, subT2, subT3, msg, error, servFiles;
	Font font,labelfont,labelfont2;
	JTextField txt1,txt2,txt3;
	JButton down;
	String name, file, path;
	int portNumber;

	TCPClient() 
	{
		Frame=new JFrame("Stage 1 ");
		Color c2 =new Color(102,254,102);  
		Frame.getContentPane().setBackground(Color.yellow);
		String pic=new String("w1.png");
		// This is the image, I stored in that directory in my machine
		new Image_Attach(Frame,pic);
		Frame.setVisible(true);
		Frame.setSize(2000,2000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		font = new Font("Algerian",Font.BOLD,47);
		Title=new JLabel("CLIENT SIDE Transfer of Files");
		Color c3 =new Color(255,153,0);  
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
		//down.setBackground(c2);
		labelfont2 = new Font("Calibri",Font.PLAIN,26);
		down.setFont(labelfont2);
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
					ip="localhost";		//default value being set to the IP number
				new TCPClient1(dir,ip,port); 
			}
	}

	public static void main(String args[]) 
	{
				new TCPClient();
	}
}


class TCPClient1 extends JFrame implements ActionListener, MouseListener {
	JFrame Frame2;
	JLabel title, subT, msg, error, servFiles;
	Font font,labelfont,tfont,bfont;
	JTextField txt;
	JButton up, down;
	String dirName;
	Socket clientSocket;
	InputStream inFromServer;
	OutputStream outToServer;
	BufferedInputStream bis;
	PrintWriter pw;
	String name, file, path;
	String hostAddr;
	int portNumber;
	int c;
	int size = 9022386;
	JList<String> filelist;
	String[] names = new String[10000];
	int len; 

	public TCPClient1(String dir, String host, int port) {
		Frame2=new JFrame("Stage 2 ");
		Frame2.getContentPane().setBackground(Color.RED);
		String pic=new String("w5.jpg");
		new Image_Attach(Frame2,pic);
		Frame2.setVisible(true);
		Frame2.setSize(2000,2000);
		dirName = dir;
		hostAddr = host;
		portNumber = port;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		font = new Font("Gill Sans", Font.BOLD, 50);
		title = new JLabel("Message File Transfer");
		Color c1 =new Color(102,0,153);  
		title.setForeground(c1);
		title.setFont(font);
		title.setBounds(700, 50, 700, 60);
		Frame2.add(title);

		labelfont = new Font("casteller", Font.PLAIN, 20);
		subT = new JLabel("File Name :");
		subT.setFont(labelfont);
		subT.setBounds(250, 450, 200, 50);
		Frame2.add(subT);

		txt = new JTextField();
		txt.setBounds(550, 450, 500, 50);
		Frame2.add(txt);
		tfont = new Font("Times New Roman", Font.PLAIN, 30);
		txt.setFont(tfont);

		bfont = new Font("Times New Roman", Font.BOLD,25);
		up = new JButton("Upload from Client");
		up.setBounds(550, 550, 300, 50);
		Frame2.add(up);
		up.setFont(bfont);

		down = new JButton("Download from Server");
		down.setBounds(950, 550, 300, 50);
		Frame2.add(down);
		down.setFont(bfont);

		error = new JLabel("");
		error.setFont(labelfont);
		error.setBounds(200, 650, 600, 50);
		Frame2.add(error);

		up.addActionListener(this);
		down.addActionListener(this);

		try {
			clientSocket = new Socket(hostAddr, portNumber);
			inFromServer = clientSocket.getInputStream();
			pw = new PrintWriter(clientSocket.getOutputStream(), true);
			outToServer = clientSocket.getOutputStream();
			ObjectInputStream oin = new ObjectInputStream(inFromServer);
			String s = (String) oin.readObject();
			System.out.println(s);

			len = Integer.parseInt((String) oin.readObject());
			System.out.println(len);

			String[] temp_names = new String[len];

			for(int i = 0; i < len; i++) {
				String filename = (String) oin.readObject();
				System.out.println(filename);
				names[i] = filename;
				temp_names[i] = filename;
			}
			Arrays.sort(temp_names);
			servFiles = new JLabel("Files in the Server Directory :");
			servFiles.setBounds(350, 125, 400, 50);
			Frame2.add(servFiles);
			filelist = new JList<>(temp_names);
			JScrollPane scroll = new JScrollPane(filelist);
			scroll.setBounds(300, 200, 400, 200);
			Frame2.add(scroll);
			filelist.addMouseListener(this);
		} 
		catch (Exception exc) {
			System.out.println("Exception: " + exc.getMessage());
			error.setText("Exception:" + exc.getMessage());
			error.setBounds(300,125,900,50);
			Frame2.revalidate();
		}
	}

    public void mouseClicked(MouseEvent click) {
        if (click.getClickCount() == 2) {
           String selectedItem = (String) filelist.getSelectedValue();
           txt.setText(selectedItem);
           Frame2.revalidate();
         }
    }

    public void mousePressed(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == up) {
			try {
				name = txt.getText();

				FileInputStream file = null;
				BufferedInputStream bis = null;

				boolean fileExists = true;
				path = dirName + name;

				try {
					file = new FileInputStream(path);
					bis = new BufferedInputStream(file);
				} catch (FileNotFoundException excep) {
					fileExists = false;
					System.out.println("FileNotFoundException:" + excep.getMessage());
					error.setText("FileNotFoundException:" + excep.getMessage());
					Frame2.revalidate();
				}

				if (fileExists) 
				{
					pw.println(name);
					System.out.println("Upload begins");
					error.setText("Upload begins");
					Frame2.revalidate();
					new Sender(dirName,name);
					path=dirName + name;
					file = new FileInputStream(path);
					bis = new BufferedInputStream(file);
					sendBytes(bis, outToServer);
					System.out.println("Completed");
					error.setText("Completed");
					Frame2.revalidate();
					boolean exists = false;
					for(int i = 0; i < len; i++)
					{
						if(names[i].equals(name)){
							exists = true;
							break;
						}
					}

					if(!exists)
					{
						names[len] = name;
						len++;
					}

					String[] temp_names = new String[len];
					for(int i = 0; i < len; i++){
						temp_names[i] = names[i];
					}
					Arrays.sort(temp_names);
					filelist.setListData(temp_names);
					bis.close();
					file.close();
					outToServer.close();
				}
			} 
			catch (Exception exc) {
				System.out.println("Exception: " + exc.getMessage());
				error.setText("Exception:" + exc.getMessage());
				Frame2.revalidate();
			}
		}
		else if (event.getSource() == down) {
			try {
				File directory = new File(dirName);

				if (!directory.exists()) {
					directory.mkdir();
				}
				boolean complete = true;
				byte[] data = new byte[size];
				name = txt.getText();
				file = new String("*" + name + "*");
				pw.println(file); //lets the server know which file is to be downloaded

				ObjectInputStream oin = new ObjectInputStream(inFromServer);
				String s = (String) oin.readObject();

				if(s.equals("Success")) {
					File f = new File(directory, name);
					FileOutputStream fileOut = new FileOutputStream(f);
					DataOutputStream dataOut = new DataOutputStream(fileOut);
					while (complete) {
						c = inFromServer.read(data, 0, data.length);
						if (c == -1) {
							complete = false;
							System.out.println("Completed");
							error.setText("Completed");
							Frame2.revalidate();

						} else {
							dataOut.write(data, 0, c);
							dataOut.flush();
						}
					}
					fileOut.close();
				}
				else {
					System.out.println("Requested file not found on the server.");
					error.setText("Requested file not found on the server.");
					Frame2.revalidate();
				}
			} 
			catch (Exception exc) {
				System.out.println("Exception: " + exc.getMessage());
				error.setText("Exception:" + exc.getMessage());
				Frame2.revalidate();
			}
		}
	}

	private static void sendBytes(BufferedInputStream in , OutputStream out) throws Exception {
		int size = 9022386;
		byte[] data = new byte[size];
		int c = in.read(data, 0, data.length);
		out.write(data, 0, c);
		out.flush();
	}
}
