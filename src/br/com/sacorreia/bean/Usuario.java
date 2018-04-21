package br.com.sacorreia.bean;

import br.com.sacorreia.annotation.Column;
import br.com.sacorreia.annotation.Column.TYPE;
import br.com.sacorreia.annotation.Id;
import br.com.sacorreia.annotation.Id.ID;
import br.com.sacorreia.annotation.Table;

@Table("tb_ctr_usuario")
public class Usuario {
	@Id(ID.AUTO_INCREMENT)
	@Column(name = "id_usuario", type = TYPE.NUMBER, required = true)
	private int		idUsuario;

	@Column(name = "usuario", type = TYPE.VARCHAR, required = true, size = 128)
	private String	usuario;

	@Column(name = "senha", type = TYPE.VARCHAR, required = true, size = 128)
	private String	senha;

	@Column(name = "id_perfil", type = TYPE.NUMBER, required = true)
	private int		idPerfil;

	@Column(name = "ativo", type = TYPE.CHAR, required = true, size = 1, valueDefault = "'A'")
	private String	ativo = "A";

	public Usuario() {
	}

	public Usuario(String usuario, String senha, int idPerfil) {
		this.usuario = usuario;
		this.senha = senha;
		this.idPerfil = idPerfil;
	}

	public String getUsuario() {
		return usuario;
	}

	public Usuario setUsuario(String usuario) {
		this.usuario = usuario;
		
		return this;
	}

	public String getSenha() {
		return senha;
	}

	public Usuario setSenha(String senha) {
		this.senha = senha;

		return this;
	}

	public int getIdPerfil() {
		return idPerfil;
	}

	public Usuario setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
		
		return this;
	}

	public String getAtivo() {
		return ativo;
	}

	public Usuario setAtivo(String ativo) {
		this.ativo = ativo;
		
		return this;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public Usuario setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
		
		return this;
	}
}
