package org.desking.model.internal.app.views;

import java.util.Iterator;
import java.util.List;

import org.desking.model.IModel;
import org.desking.model.ModelException;
import org.desking.model.ModelService;
import org.desking.model.client.ProductCategory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class Navigator extends ViewPart {

	private TreeViewer treeViewer;
	private TreeNode root;
	
	public Navigator() {
	}

	@Override
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.FULL_SELECTION);
		
		treeViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				TreeNode node = (TreeNode) element;
				if (node.getParent() == null)
					return "All Products";
				Object value = node.getValue();
				ProductCategory category = (ProductCategory) value;
				return category.getName();
			}
			
		});
		
		treeViewer.setContentProvider(new TreeNodeContentProvider());
		root = new TreeNode(null);
		loadProductCategories(root);
		treeViewer.setInput(new TreeNode[] { root });
		
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {

					showProductList(event.getSelection());

			}
		});
		
		getSite().setSelectionProvider(treeViewer);
	}

	protected void showProductList(ISelection selection) {
		try {
			getSite().getPage().showView("org.desking.model.views.ProductList");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private void loadProductCategories(TreeNode root) {
		IModel<ProductCategory> model = null;
		try {
			model = ModelService.getInstance().getModel(ProductCategory.class);
			List<ProductCategory> list = model.getList();
			Iterator<ProductCategory> it = list.iterator();
			TreeNode[] nodes = new TreeNode[list.size()];
			int idx = 0;
			while (it.hasNext()) {
				TreeNode node = new TreeNode(it.next());
				nodes[idx] = node;
				node.setParent(root);
				idx++;
			}
			root.setChildren(nodes);
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {

	}

}
