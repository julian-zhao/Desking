package org.desking.model.internal.app.views;

import java.util.List;

import org.desking.model.IModel;
import org.desking.model.ModelException;
import org.desking.model.ModelService;
import org.desking.model.client.Product;
import org.desking.model.client.ProductCategory;
import org.desking.model.internal.app.editors.ProductEditorInput;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class ProductList extends ViewPart {

	private TableViewer tableViewer;
	private ISelectionListener categoryListener = new INullSelectionListener() {

		@Override
		public void selectionChanged(IWorkbenchPart part,
				ISelection selection) {
			doSelectionChanged(selection);
		}
		
	};
	
	public ProductList() {
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		getSite().getPage().addSelectionListener("org.desking.model.views.Navigator", categoryListener);
	}

	private void doSelectionChanged(ISelection selection) {
		if (selection == null)
			return;
		if (!(selection instanceof TreeSelection)) 
			return;
		
		Object s = ((TreeSelection) selection).getFirstElement();
		if (s == null)
			return;
		
		TreeNode node = (TreeNode) s;
		Object value = node.getValue();
		ProductCategory category = null;
		
		if (value != null)
			category = (ProductCategory) value;
		
		IModel<Product> model = null;
		
		try {
			model = ModelService.getInstance().getModel(Product.class);
			List<Product> list = null;
			
			if (category != null)
				list = category.getProducts();
			else
				list = model.getList();

			tableViewer.setInput(list);
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void createPartControl(Composite parent) {
		createViewer(parent);
		initialize();
	}
	
	private void createViewer(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		createColumns();
		tableViewer.setLabelProvider(new ProductLabelProvider());
		tableViewer.setContentProvider(new ProductContentProvider());
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection s = event.getSelection();
				editProduct(s);
			}
			
		});
		
		getSite().setSelectionProvider(tableViewer);
		
		
	}
	
	protected void editProduct(ISelection s) {
		if (s == null) 
			return;
		if (!(s instanceof IStructuredSelection))
			return;
		
		IStructuredSelection ss = (IStructuredSelection)s;
		Object o = ss.getFirstElement();
		if (o==null)
			return;
		Product p = (Product) o;
		try {
			getSite().getPage().openEditor(new ProductEditorInput(p), "org.desking.model.editors.ProductEditor");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private void createColumns() {

		String[] titles = { "ID", "Name", "Description"};
		int[] bounds = { 50, 100, 100 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	
	private void initialize() {
		IViewPart part = getSite().getPage().findView("org.desking.model.views.Navigator");
		if (part != null) {
			try {
				ISelection selection = part.getSite().getSelectionProvider().getSelection();
				if (selection != null)
					doSelectionChanged(selection);
			} catch(Exception e) {
				
			}
		}
	}



	@Override
	public void dispose() {
		getSite().getPage().removeSelectionListener("org.desking.model.views.Navigator", categoryListener);
		super.dispose();
		
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

}
