package br.com.sacorreia.bean;

import br.com.sacorreia.annotation.Column;
import br.com.sacorreia.annotation.Column.TYPE;
import br.com.sacorreia.annotation.Id;
import br.com.sacorreia.annotation.Table;
import br.com.sacorreia.annotation.Id.ID;

@Table("tb_ctr_perfil")
public class Perfil {
	@Id(ID.AUTO_INCREMENT)
	@Column(name = "id_perfil", type = TYPE.NUMBER, required = true)
	private int		idPerfil;

	@Column(name = "nivel", type = TYPE.NUMBER, required = true)
	private int		nivel;

	@Column(name = "nome", type = TYPE.VARCHAR, required = true, size = 128)
	private String	nome;

	
	public Perfil() {
	}

	public Perfil(int nivel, String nome) {
		this.nivel = nivel;
		this.nome = nome;
	}

	public int getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
