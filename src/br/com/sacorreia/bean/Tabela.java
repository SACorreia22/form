package br.com.sacorreia.bean;

import java.util.Set;

import br.com.sacorreia.annotation.Column;
import br.com.sacorreia.annotation.Column.TYPE;
import br.com.sacorreia.annotation.Id;
import br.com.sacorreia.annotation.Id.ID;
import br.com.sacorreia.annotation.Table;

@Table("tb_ctr_tabela")
public class Tabela implements Comparable<Tabela> {
	@Id(ID.AUTO_INCREMENT)
	@Column(name = "id_tabela", type = TYPE.NUMBER)
	private int			idTabela;

	@Column(name = "nome", type = TYPE.VARCHAR, size = 128, required = true)
	private String		nome;

	@Column(name = "label", type = TYPE.VARCHAR, size = 50, required = true)
	private String		label;

	@Column(name = "ativo", type = TYPE.CHAR, size = 1, required = true, valueDefault = "'A'")
	private String		ativo	= "A";

	private Set<Coluna>	colunas;

	public Tabela() {
	}

	public Tabela(int idTabela, String nome, String label) {
		this.idTabela = idTabela;
		this.nome = nome;
		this.label = label;
	}

	public int getIdTabela() {
		return idTabela;
	}

	public void setIdTabela(int idTabela) {
		this.idTabela = idTabela;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Set<Coluna> getColunas() {
		return colunas;
	}

	public void setColunas(Set<Coluna> colunas) {
		this.colunas = colunas;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAtivo() {
		return ativo;
	}

	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ativo == null) ? 0 : ativo.hashCode());
		result = prime * result + ((colunas == null) ? 0 : colunas.hashCode());
		result = prime * result + idTabela;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tabela other = (Tabela) obj;
		if (ativo == null) {
			if (other.ativo != null)
				return false;
		} else if (!ativo.equals(other.ativo))
			return false;
		if (colunas == null) {
			if (other.colunas != null)
				return false;
		} else if (!colunas.equals(other.colunas))
			return false;
		if (idTabela != other.idTabela)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public int compareTo(Tabela o) {
		if (this.hashCode() < o.hashCode())
			return -1;
		else if (this.hashCode() > o.hashCode())
			return 1;

		return 0;
	}

}
