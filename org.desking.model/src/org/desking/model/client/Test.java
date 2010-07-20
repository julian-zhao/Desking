package org.desking.model.client;

import java.util.Iterator;
import java.util.List;

import org.desking.model.Filter;
import org.desking.model.IFilter;
import org.desking.model.IModel;
import org.desking.model.IProperty;
import org.desking.model.ModelException;
import org.desking.model.ModelService;

public class Test {

	private static ModelService service = ModelService.getInstance();
	
	public static void main(String[] args) {
		
		
		try {
			service.initialize("//localhost:1527/desking", "app", "test");
		} catch (ModelException e) {
			e.printStackTrace();
		}
		
		testUpdate();
		
		service.shutdown();
	}
	
	private static void testUpdate() {
		IModel<Order> model = null;
		
		try {
			model = service.getModel(Order.class);
			Order order = model.get("O0001");
			System.out.println(order);
			System.out.println("---------------------------------------------------------");
			
			Customer customer = order.getCustomer();
			System.out.println(customer);
			System.out.println("---------------------------------------------------------");
			
			String name = order.getName();
			order.setName(name + " * ");
			model.update(order);
			order.setId("A");
			//model.update(order);
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}
	
	private static void testOrder() {
		IModel<Order> model = null;
		
		try {
			model = service.getModel(Order.class);
			Order order = model.get("O0001");
			System.out.println(order);
			System.out.println("---------------------------------------------------------");
			
			Customer customer = order.getCustomer();
			System.out.println(customer);
			System.out.println("---------------------------------------------------------");
			
			List<Product> products = order.getProducts();
			Iterator<Product> it = products.iterator();
			while (it.hasNext()) {
				System.out.println(it.next());
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}

	private static void testProduct() {
		IModel<Product> model = null;
		try {
			model = service.getModel(Product.class);


			
			Product product = model.get("P0001", IProperty.LARGE_DATA | IProperty.ENTITY);
			System.out.println(product);
			System.out.println("---------------------------------------------------------");
			System.out.println("ID: " + product.getId());
			System.out.println("Name: " + product.getName());
			System.out.println("description: " + product.getDescription());

			System.out.println("description: " + product.getDescription());
			System.out.println("---------------------------------------------------------");
			ProductCategory category = product.getCategory();
			System.out.println(category);
			System.out.println(category);
			
			System.out.println("---------------------------------------------------------");
			
			
			int count = model.getCount(new Filter("NAME", IFilter.LIKE, "%2%"));
			System.out.println("Product Count: " + count);
			System.out.println("---------------------------------------------------------");
			
			List<Product> products =  model.getList(new Filter("NAME", IFilter.LIKE, "%2%"));//  category.getProducts();
			Iterator<Product> it = products.iterator();
			while (it.hasNext()) {
				System.out.println(it.next());
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void testFilter() {
		Filter root = new Filter(false);
		root.add("ID", IFilter.EQUALS, "P0001");
		Filter f1 = new Filter();
		root.add(f1);
		
		f1.add("NAME", IFilter.LIKE, "NAME");
		f1.add("DESC", IFilter.EQUALS, "LKJ");
		System.out.println(root);
		
		System.out.println("---------------------------------------------------------");
	}

}
