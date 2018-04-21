package br.com.sacorreia.dao.connection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Conexao {
	private static volatile String diretorio;

	private static volatile ConcurrentHashMap<Integer, Connection>	POOL_USING	= new ConcurrentHashMap<Integer, Connection>();
	private static volatile ConcurrentHashMap<Integer, Connection>	POOL_IDLE	= new ConcurrentHashMap<Integer, Connection>();

	public static final String										SCHEMA_BASE	= "BASE";
	public static final String										SCHEMA_DATA	= "DADOS";

	private static Connection openNew() throws Exception {
		Class.forName("org.h2.Driver");

		Connection con = DriverManager.getConnection("jdbc:h2:" + diretorio + "db/database;AUTO_SERVER=TRUE;INIT=create schema if not exists " + SCHEMA_BASE + "\\;create schema if not exists " + SCHEMA_DATA, "Saulo",
				"Saulo");
		
		return con;
	}

	protected static Connection getConnection() throws Exception {
		Connection con = null;

		if (POOL_IDLE.isEmpty()) {
			con = openNew();
		} else {
			con = POOL_IDLE.remove(POOL_IDLE.keys().nextElement());
		}

		int hashCode = con.hashCode();
		if (POOL_USING.get(hashCode) == null)
			POOL_USING.put(hashCode, con);
		else {
			close(con);

			throw new Exception("Conexão com mesmo hash " + hashCode);
		}

		return con;
	}

	protected static void closeAll(Object... arg) throws Exception {
		Class<?>[] classes = new Class<?>[] { ResultSet.class, PreparedStatement.class, CallableStatement.class, Connection.class };

		int fechados = 0;
		for (Class<?> classe : classes) {
			for (Object o : arg) {
				if (o == null) {
					fechados++;
					continue;
				}

				if (Arrays.asList(o.getClass().getGenericInterfaces()).contains(classe)) {
					Conexao.class.getDeclaredMethod("close", classe).invoke(null, classe.cast(o));
					fechados++;
				}
			}
		}

		if (fechados < arg.length)
			throw new Exception("Não foi possivel finalizar todos os objetos passados");
	}

	protected static void close(Connection con) {
		int hashCode = con.hashCode();

		POOL_USING.remove(hashCode);
		POOL_IDLE.put(hashCode, con);
	}

	protected static void close(PreparedStatement ps) throws Exception {
		if (ps != null)
			ps.close();
	}

	protected static void close(CallableStatement cs) throws Exception {
		if (cs != null)
			cs.close();
	}

	protected static void close(ResultSet rs) throws Exception {
		if (rs != null)
			rs.close();
	}

	protected static int execute(String query) throws Exception {
		return execute(query, new Object[] {});
	}

	protected static int execute(String query, Object... parametros) throws Exception {
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(query);

		System.out.println(query);
		if (parametros.length > 0) {
			List<String> dados = new ArrayList<>();

			for (Object o : parametros)
				if (o == null)
					dados.add("null");
				else
					dados.add(o.toString());

			System.out.println("\t[" + String.join(", ", dados) + "]");
		}

		int retorno;
		try {
			for (int i = 0; i < parametros.length; i++)
				ps.setObject(i + 1, parametros[i]);

			retorno = ps.executeUpdate();
		} finally {
			closeAll(ps, con);
		}
		return retorno;
	}

	public static void setDiretorio(String diretorio) {
		Conexao.diretorio = diretorio;
	}
}
