package org.desking.model.internal.devices;

public class Column implements IColumn {
	private Table table;
	private int index;
	private String name;
	private String label;
	private String className;
	private int type;
	private String typeName;
	private int length;
	private int precision;
	private int displaySize;
	private int nullable;
	private boolean autoIncrement;
	private boolean caseSensitive;
	private boolean searchable;
	private boolean currency;
	private boolean signed;
	private boolean readOnly;
	private boolean writable;
	private boolean primaryKey;
	
	public Column(String name, String label, String className, int index,
			int type, String typeName, int length, int precision,
			int displaySize, int nullable, boolean autoIncrement,
			boolean caseSensitive, boolean searchable, boolean currency,
			boolean signed, boolean readOnly, boolean writable) {
		this.name = name;
		this.label = label;
		this.className = className;
		this.index = index;
		this.type = type;
		this.typeName = typeName;
		this.length = length;
		this.precision = precision;
		this.displaySize = displaySize;
		this.nullable = nullable;
		this.autoIncrement = autoIncrement;
		this.caseSensitive = caseSensitive;
		this.searchable = searchable;
		this.currency = currency;
		this.signed = signed;
		this.readOnly = readOnly;
		this.writable = writable;
	}
	
	public void setTable(Table table) {
		this.table = table;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public ITable getTable() {
		return table;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public int getPrecision() {
		return precision;
	}

	@Override
	public int getDisplaySize() {
		return displaySize;
	}

	@Override
	public int isNullable() {
		return nullable;
	}

	@Override
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	@Override
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	@Override
	public boolean isSearchable() {
		return searchable;
	}

	@Override
	public boolean isCurrency() {
		return currency;
	}

	@Override
	public boolean isSigned() {
		return signed;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public String toString() {
		return (primaryKey?"*":"") + "Column [index=" + index + ", name=" + name + ", label=" + label
				+ ", className=" + className + ", type=" + type + ", typeName="
				+ typeName + ", length=" + length + ", precision=" + precision
				+ ", displaySize=" + displaySize + ", nullable=" + nullable
				+ ", autoIncrement=" + autoIncrement + ", caseSensitive="
				+ caseSensitive + ", searchable=" + searchable + ", currency="
				+ currency + ", signed=" + signed + ", readOnly=" + readOnly
				+ ", writable=" + writable + "]";
	}

	@Override
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean value) {
		this.primaryKey = value;
	}
}
