package br.com.sacorreia.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {
	private static volatile String ROOT;

	public RootHandler(String diretorio) {
		ROOT = diretorio + "/web";
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		String path = t.getRequestURI().getPath();

		File file = new File(ROOT + path).getCanonicalFile();

		System.out.println("looking for: " + file);
		if (!file.isFile())
			if ("/".equals(path)) {
				path += "index.html";
				file = new File(ROOT + path).getCanonicalFile();
			}

		if (!file.isFile()) {
			// Object does not exist or is not a file: reject with 404 error.
			String response = "404 (Not Found)\n";
			t.sendResponseHeaders(404, response.length());

			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} else {
			// Object exists and is a file: accept with response code 200.
			String mime = "";

			if (path.substring(path.length() - 3).equals(".js"))
				mime = "application/javascript";
			else if (path.substring(path.length() - 3).equals("css"))
				mime = "text/css";
			else
				mime = "text/html";

			Headers h = t.getResponseHeaders();
			h.set("Content-Type", mime);
			t.sendResponseHeaders(200, 0);

			OutputStream os = t.getResponseBody();
			FileInputStream fs = new FileInputStream(file);
			final byte[] buffer = new byte[0x10000];
			int count = 0;
			while ((count = fs.read(buffer)) >= 0) {
				os.write(buffer, 0, count);
			}
			fs.close();
			os.close();
		}
	}
}