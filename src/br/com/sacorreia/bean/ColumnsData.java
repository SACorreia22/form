package br.com.sacorreia.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ColumnsData {
	private String	name;
	private String	type;
	private int		size;
	private String	valueDefault;
	private boolean	required;
	private boolean	pk;
	private String	typePk;
	private Field	field;
	private Method	get;
	private Method	set;

	public ColumnsData() {
	}

	public ColumnsData(String name, String type, int size, String valueDefault, boolean required, boolean pk, String typePk) {
		this.name = name;
		this.type = type;
		this.size = size;
		this.valueDefault = valueDefault;
		this.required = required;
		this.pk = pk;
		this.typePk = typePk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getValueDefault() {
		return valueDefault;
	}

	public void setValueDefault(String valueDefault) {
		this.valueDefault = valueDefault;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isPk() {
		return pk;
	}

	public void setPk(boolean pk) {
		this.pk = pk;
	}

	public String getTypePk() {
		return typePk;
	}

	public void setTypePk(String typePk) {
		this.typePk = typePk;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Method getMethod() {
		return get;
	}

	public void setGetMethod(Method get) {
		this.get = get;
	}

	public Method setMethod() {
		return set;
	}

	public void setSetMethod(Method set) {
		this.set = set;
	}
}
