package org.desking.model.client;

import java.lang.reflect.Field;

public class TestReflect {


	public static void main(String[] args) {
		ProductProxy proxy = new ProductProxy();
		proxy.setId("AAAAAA");
		proxy.setName("BBBBBBB");
		proxy.setCategory(new ProductCategory());
		
		Class c = proxy.getClass().getSuperclass();
		
		Field[] fields = c.getDeclaredFields();
		for (Field field: fields) {
			field.setAccessible(true);
			Object value = null;
			try {
				value = field.get(proxy);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			System.out.println(field.getName() + " = " + value);
		}
	}

}
