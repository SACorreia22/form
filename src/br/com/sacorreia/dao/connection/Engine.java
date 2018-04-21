package br.com.sacorreia.dao.connection;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import br.com.sacorreia.annotation.Column;
import br.com.sacorreia.annotation.Id;
import br.com.sacorreia.annotation.Table;
import br.com.sacorreia.bean.ColumnsData;
import br.com.sacorreia.bean.Coluna;
import br.com.sacorreia.bean.Perfil;
import br.com.sacorreia.bean.Tabela;
import br.com.sacorreia.bean.Usuario;
import br.com.sacorreia.util.StringUtil;

public class Engine extends Conexao {
	private static volatile ConcurrentHashMap<Class<?>, TreeMap<Integer, ?>>	CACHE_CLASS		= new ConcurrentHashMap<>();
	private static volatile ConcurrentHashMap<Class<?>, List<ColumnsData>>		CACHE_FIELDS	= new ConcurrentHashMap<>();

	private static volatile ConcurrentHashMap<Integer, Tabela>					TABELA			= new ConcurrentHashMap<>();

	private static volatile ConcurrentHashMap<Integer, String>					DELETE			= new ConcurrentHashMap<>();
	private static volatile ConcurrentHashMap<Integer, String>					INSERT			= new ConcurrentHashMap<>();
	private static volatile ConcurrentHashMap<Integer, String>					SELECT			= new ConcurrentHashMap<>();
	private static volatile ConcurrentHashMap<Integer, String>					UPDATE			= new ConcurrentHashMap<>();

	protected static void createSystem(Class<?> classe) throws Exception {
		List<String> colunasSql = new ArrayList<>();
		List<String> idSql = new ArrayList<>();

		for (ColumnsData coluna : getColumnsData(classe)) {
			String col = coluna.getName() +
					" " +
					coluna.getType() +
					(coluna.getSize() > 0 ? "(" + coluna.getSize() + ")" : "") +
					(coluna.getValueDefault().length() > 0 ? " DEFAULT " + coluna.getValueDefault() : "") +
					(coluna.isRequired() ? " NOT NULL" : " NULL");

			if (coluna.isPk()) {
				col += " " + coluna.getTypePk();

				idSql.add(coluna.getName());
			}

			colunasSql.add(col);
		}

		try {
			execute("CREATE TABLE IF NOT EXISTS " + nameTable(classe) + " (" + String.join(",", colunasSql) + ", PRIMARY KEY (" + String.join(",", idSql) + "))");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<ColumnsData> getColumnsData(Class<?> classe) throws Exception {
		List<ColumnsData> cache;
		if ((cache = CACHE_FIELDS.get(classe)) != null)
			return cache;

		List<ColumnsData> retorno = new ArrayList<>();

		for (Field field : getAllFields(classe)) {
			Column coluna = field.getAnnotation(Column.class);

			ColumnsData cd = new ColumnsData();

			cd.setField(field);

			String methodName = StringUtil.capitalize(field.getName());
			String prefixMethod = (field.getType().equals(boolean.class) ? "is" : "get");

			cd.setGetMethod(classe.getDeclaredMethod(prefixMethod + methodName));
			cd.setSetMethod(classe.getDeclaredMethod("set" + methodName, field.getType()));

			cd.setName(coluna.name());
			cd.setType(coluna.type().toString());
			cd.setSize(coluna.size());
			cd.setValueDefault(coluna.valueDefault());
			cd.setRequired(coluna.required());

			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				cd.setPk(true);
				cd.setTypePk(id.value().toString());
			} else {
				cd.setPk(false);
			}

			retorno.add(cd);
		}

		CACHE_FIELDS.put(classe, retorno);

		return retorno;
	}

	private static String nameTable(Class<?> classe) {
		return SCHEMA_BASE + "." + classe.getAnnotation(Table.class).value();
	}

	private static List<String> getAllColumns(Class<?> classe) throws Exception {
		List<String> colunas = new ArrayList<>();

		getColumnsData(classe).forEach(cd -> colunas.add(cd.getName()));

		return colunas;
	}

	private static List<String> getEditableColumns(Class<?> classe) throws Exception {
		List<String> colunas = new ArrayList<>();

		getColumnsData(classe).forEach(cd -> {
			if (!cd.isPk())
				colunas.add(cd.getName());
		});

		return colunas;
	}

	private static <T> List<Field> getAllFields(Class<T> classe) {
		List<Field> colunas = new ArrayList<>();

		for (Field field : classe.getDeclaredFields()) {
			Column coluna = field.getAnnotation(Column.class);

			if (coluna == null)
				continue;

			colunas.add(field);
		}

		return colunas;
	}

	@SuppressWarnings("unchecked")
	protected static <T> TreeMap<Integer, T> getResult(Class<T> classe) throws Exception {
		TreeMap<Integer, ?> cache = CACHE_CLASS.get(classe);
		if (cache != null)
			return (TreeMap<Integer, T>) cache;

		TreeMap<Integer, T> retorno = new TreeMap<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String query = "SELECT " + String.join(", ", getAllColumns(classe)) + " FROM " + nameTable(classe);
			System.out.println(query);

			con = getConnection();
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();

			List<ColumnsData> listaCD = getColumnsData(classe);
			while (rs.next()) {
				T obj = classe.newInstance();

				int id = 0;
				String valores = "\t" + classe.getSimpleName() + " [";
				for (ColumnsData cd : listaCD) {
					Object valor;

					if (cd.getField().getType().equals(int.class)) {
						valor = rs.getInt(cd.getName());
						if (cd.isPk())
							id = (int) valor;
					} else
						valor = rs.getObject(cd.getName());

					cd.setMethod().invoke(obj, valor);

					valores += cd.getName() + ": " + valor + ", ";
				}

				System.out.println(valores.substring(0, valores.length() - 2) + "]");

				retorno.put(id, obj);
			}

			CACHE_CLASS.put(classe, retorno);
		} finally {
			closeAll(rs, ps, con);
		}

		return retorno;
	}

	protected static <T> int insert(Class<T> classe, T objeto) throws Exception {
		List<String> colunas = getEditableColumns(classe);

		List<String> param = new ArrayList<>();
		List<Object> paramValues = new ArrayList<>();

		for (ColumnsData campo : getColumnsData(classe))
			if (campo.isPk() && campo.getTypePk().equals(Id.ID.AUTO_INCREMENT.toString()))
				continue;
			else {
				paramValues.add(campo.getMethod().invoke(objeto));

				param.add("?");
			}

		int qtd = execute("INSERT INTO " + nameTable(classe) + " (" + String.join(", ", colunas) + ") VALUES (" + String.join(", ", param) + ")", paramValues.toArray());

		if (qtd > 0) {
			CACHE_CLASS.remove(classe);

			if (classe.equals(Coluna.class))
				gerarDDL(classe, objeto);
		}

		return qtd;
	}

	protected static <T> int update(Class<T> classe, T objeto) throws Exception {
		List<String> id = new ArrayList<>();
		List<Object> idValues = new ArrayList<>();

		List<String> param = new ArrayList<>();
		List<Object> paramValues = new ArrayList<>();

		for (ColumnsData campo : getColumnsData(classe)) {
			if (campo.isPk()) {
				id.add(campo.getName() + " = ?");
				idValues.add(campo.getMethod().invoke(objeto));
			} else {
				param.add(campo.getName() + " = ?");
				paramValues.add(campo.getMethod().invoke(objeto));
			}
		}
		paramValues.addAll(idValues);

		int qtd = execute("UPDATE " + nameTable(classe) + " SET " + String.join(", ", param) + " WHERE " + String.join(", ", id), paramValues.toArray());

		if (qtd > 0) {
			CACHE_CLASS.remove(classe);

			if (classe.equals(Coluna.class))
				gerarDDL(classe, objeto);
		}

		return qtd;
	}

	private static <T> void gerarDDL(Class<T> classe, T objeto) throws Exception {

	}

	public static void init(String diretorio) throws Exception {
		Conexao.setDiretorio(diretorio + "/");
		
		List<Class<?>> classes = getClassesWithTable();

		for (Class<?> c : classes) {
			createSystem(c);
		}
		for (Class<?> c : classes) {
			getResult(c);
		}

		if (getResult(Perfil.class).isEmpty())
			insert(Perfil.class, new Perfil(0, "Administrador"));

		if (getResult(Usuario.class).isEmpty())
			insert(Usuario.class, new Usuario("admin", "admin", 1));
	}

	private static List<Class<?>> getClassesWithTable() throws Exception {
		List<Class<?>> classes = new ArrayList<>();

		try {
			Set<JavaFileObject.Kind> kinds = new HashSet<JavaFileObject.Kind>();
			kinds.add(JavaFileObject.Kind.CLASS);

			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
			for (JavaFileObject javaFileObject : standardFileManager.list(StandardLocation.CLASS_PATH, "br.com.sacorreia.bean", kinds, false)) {
				String arq = javaFileObject.getName();

				arq = arq.substring(arq.lastIndexOf(File.separator) + 1);

				try {
					Class<?> classe = Class.forName("br.com.sacorreia.bean." + arq.substring(0, arq.indexOf(".")));
					if (classe.getAnnotation(Table.class) != null)
						classes.add(classe);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			classes.add(Tabela.class);
			classes.add(Coluna.class);
			classes.add(Usuario.class);
			classes.add(Perfil.class);
		}
		return classes;
	}
}
