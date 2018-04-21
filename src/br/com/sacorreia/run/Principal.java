package br.com.sacorreia.run;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.ImageIcon;

import org.h2.tools.Server;
import org.h2.util.NetUtils;

import br.com.sacorreia.dao.connection.Engine;
import br.com.sacorreia.server.Servidor;

public class Principal {
	private static int		PORT;
	private static String	DIRETORIO_PRINCIPAL;

	private static void chamarNavegador() throws Exception {
		Server.openBrowser("http://" + NetUtils.getLocalAddress() + ":" + PORT);
	}

	//Obtain the image URL
	protected static Image createImage(String path, String description) {
		URL imageURL = Principal.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else
			return new ImageIcon(imageURL, description).getImage();
	}

	private static void iniciarSystemTray() throws Exception {
		//Check the SystemTray is supported
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}

		SystemTray tray = SystemTray.getSystemTray();

		int tamanhoIcone = (int) tray.getTrayIconSize().getHeight();
		if (tamanhoIcone != 16 && tamanhoIcone != 32)
			if (tamanhoIcone < 16)
				tamanhoIcone = 16;
			else if (tamanhoIcone > 32)
				tamanhoIcone = 32;
			else
				tamanhoIcone = 16;

		PopupMenu popup = new PopupMenu();
		TrayIcon trayIcon = new TrayIcon(createImage("images/icon" + tamanhoIcone + ".jpg", "tray icon"));

		// Create a pop-up menu components
		MenuItem menuAbrir = new MenuItem("Abrir");
		MenuItem menuSair = new MenuItem("Sair");

		ActionListener abrirUrl = e -> {
			try {
				chamarNavegador();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		};

		//Add components to pop-up menu
		popup.add(menuAbrir).addActionListener(abrirUrl);
		popup.addSeparator();
		popup.add(menuSair).addActionListener(e -> System.exit(1));

		trayIcon.setPopupMenu(popup);
		trayIcon.addActionListener(abrirUrl);
		trayIcon.setToolTip("S.A.Correia Server");

		tray.add(trayIcon);
	}

	public static void main(String[] args) {
		try {
			descompactarJar();

			iniciarServidor();

			iniciarSystemTray();

			Engine.init(DIRETORIO_PRINCIPAL);

			chamarNavegador();
		} catch (Exception e) {
			e.printStackTrace();

			System.exit(1);
		}
	}

	private static void iniciarServidor() {
		Servidor servidor = new Servidor();

		while (true)
			try {
				PORT = 12345;

				servidor.start(DIRETORIO_PRINCIPAL, PORT);

				break;
			} catch (IOException e) {
				PORT++;
			}
	}

	private static String getJarFile() throws FileNotFoundException {
		String path = Principal.class.getResource(Principal.class.getSimpleName() + ".class").getFile();

		if (path.startsWith("/")) {
			throw new FileNotFoundException("This is not a jar file: \n" + path);
		}

		path = ClassLoader.getSystemClassLoader().getResource(path).getFile();

		return path.substring(6, path.lastIndexOf('!'));
	}

	private static void descompactarJar() throws Exception {
		String caminho;
		try {
			caminho = getJarFile();
		} catch (Exception e) {
			caminho = "./src.";
		}

		DIRETORIO_PRINCIPAL = caminho.substring(0, caminho.lastIndexOf("."));

		System.out.println(DIRETORIO_PRINCIPAL);

		if ("./src.".equals(caminho))
			return;

		new File(DIRETORIO_PRINCIPAL).mkdirs();

		JarFile jar = new JarFile(caminho);
		Enumeration<JarEntry> enumEntries = jar.entries();

		while (enumEntries.hasMoreElements()) {
			JarEntry file = enumEntries.nextElement();

			System.out.print(file.getName());

			if (!file.getName().startsWith("web/")) {
				System.out.println();
				continue;
			}
			System.out.println(" -> OK");

			File f = new File(DIRETORIO_PRINCIPAL + File.separator + file.getName());
			if (file.isDirectory()) {
				// if its a directory, create it
				f.mkdirs();
				continue;
			}

			f.getParentFile().mkdirs();

			InputStream is = jar.getInputStream(file);
			FileOutputStream fos = new FileOutputStream(f);

			int qtd;
			while ((qtd = is.available()) > 0) {
				byte[] bytes = new byte[qtd];
				is.read(bytes);

				fos.write(bytes);
			}

			fos.close();
			is.close();
		}

		jar.close();

		return;
	}
}
