package br.com.sacorreia.bean;

import br.com.sacorreia.annotation.Column;
import br.com.sacorreia.annotation.Column.TYPE;
import br.com.sacorreia.annotation.Id;
import br.com.sacorreia.annotation.Id.ID;
import br.com.sacorreia.annotation.Table;

@Table("tb_ctr_coluna")
public class Coluna implements Comparable<Coluna> {
	@Id(ID.AUTO_INCREMENT)
	@Column(name = "id_coluna", type = TYPE.NUMBER)
	private int		idColuna;

	@Column(name = "id_tabela", type = TYPE.NUMBER, required = true)
	private int		idTabela;

	@Column(name = "tipo", type = TYPE.VARCHAR, size = 20, required = true)
	private String	tipo;

	@Column(name = "tamanho", type = TYPE.NUMBER, required = true)
	private int		tamanho;

	@Column(name = "obrigatorio", type = TYPE.BIT, required = true, valueDefault = "0")
	private boolean	obrigatorio;

	@Column(name = "nome", type = TYPE.VARCHAR, size = 50, required = true)
	private String	nome;

	@Column(name = "posicao", type = TYPE.NUMBER, required = true)
	private int		posicao;

	@Column(name = "input", type = TYPE.VARCHAR, size = 20, required = true)
	private String	input;

	@Column(name = "id_tabela_relacao", type = TYPE.NUMBER)
	private int		idTabelaRelacao;

	@Column(name = "style", type = TYPE.VARCHAR, size = 150)
	private String	style;

	@Column(name = "css", type = TYPE.VARCHAR, size = 150)
	private String	css;

	@Column(name = "ativo", type = TYPE.CHAR, size = 1, required = true, valueDefault = "'A'")
	private String	ativo	= "A";

	public Coluna() {
	}

	public Coluna(String tipo, int tamanho) {
		this.tipo = tipo;
		this.tamanho = tamanho;
	}

	public int getIdColuna() {
		return idColuna;
	}

	public void setIdColuna(int idColuna) {
		this.idColuna = idColuna;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getTamanho() {
		return tamanho;
	}

	public void setTamanho(int tamanho) {
		this.tamanho = tamanho;
	}

	public boolean isObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public String getAtivo() {
		return ativo;
	}

	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ativo == null) ? 0 : ativo.hashCode());
		result = prime * result + ((css == null) ? 0 : css.hashCode());
		result = prime * result + idColuna;
		result = prime * result + idTabela;
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + (obrigatorio ? 1231 : 1237);
		result = prime * result + posicao;
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		result = prime * result + tamanho;
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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

		Coluna other = (Coluna) obj;
		if (ativo == null && other.ativo != null)
			return false;
		else if (!ativo.equals(other.ativo))
			return false;

		if (css == null && other.css != null)
			return false;
		else if (!css.equals(other.css))
			return false;

		if (idColuna != other.idColuna)
			return false;
		if (idTabela != other.idTabela)
			return false;
		if (input == null && other.input != null)
			return false;
		else if (!input.equals(other.input))
			return false;

		if (nome == null && other.nome != null)
			return false;
		else if (!nome.equals(other.nome))
			return false;

		if (obrigatorio != other.obrigatorio)
			return false;

		if (posicao != other.posicao)
			return false;

		if (style == null && other.style != null)
			return false;
		else if (!style.equals(other.style))
			return false;

		if (tamanho != other.tamanho)
			return false;

		if (tipo == null && other.tipo != null)
			return false;
		else if (!tipo.equals(other.tipo))
			return false;

		return true;
	}

	@Override
	public int compareTo(Coluna o) {
		if (this.hashCode() < o.hashCode())
			return -1;
		else if (this.hashCode() > o.hashCode())
			return 1;

		return 0;
	}

	public int getIdTabelaRelacao() {
		return idTabelaRelacao;
	}

	public void setIdTabelaRelacao(int idTabelaRelacao) {
		this.idTabelaRelacao = idTabelaRelacao;
	}
}
