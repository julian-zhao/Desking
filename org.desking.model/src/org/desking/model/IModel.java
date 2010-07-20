package org.desking.model;

import java.util.List;

public interface IModel<T> {

	public IProperty getProperty(String name);
	public IProperty[] getProperites();
	
	public T newInstance();
	public T newInstance(Class<?>[] argumentTypes, Object[] arguments);
	
	public T get(String id) throws ModelException;
	public T get(String id, int type) throws ModelException;
	
	public void insert(T e) throws ModelException;
	public void update(T e) throws ModelException;
	public void delete(T e) throws ModelException;
	public void delete(String id) throws ModelException;
	
	public int getCount() throws ModelException;
	public int getCount(IFilter filter) throws ModelException;
	
	public List<T> getList() throws ModelException;
	public List<T> getList(IFilter filter) throws ModelException;
}
