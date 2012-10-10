package ca.ucalgary.cpsc.ase.querymanager.views;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import ca.ucalgary.cpsc.ase.QueryManager.Heuristic;
import ca.ucalgary.cpsc.ase.QueryManager.VotingResult;
import ca.ucalgary.cpsc.ase.querymanager.actions.SearchAction;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ResultView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ca.ucalgary.cpsc.ase.querymanager.views.ResultView";

	private TableViewer viewer;
//	private Action action1;
//	private Action action2;
	private Action doubleClickAction;
	
	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object inputElement) {
			return ((List) inputElement).toArray();
		}
	}
	//	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
	//		public String getColumnText(Object obj, int index) {
	//			return getText(obj);
	//		}
	//		public Image getColumnImage(Object obj, int index) {
	//			return getImage(obj);
	//		}
	//		public Image getImage(Object obj) {
	//			return PlatformUI.getWorkbench().
	//					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	//		}
	//	}
//	class NameSorter extends ViewerSorter {
//	}

	/**
	 * The constructor.
	 */
	public ResultView() {
//		VotingResult r1 = new VotingResult(1, "abc.test.xyz");
//		r1.setRank(1);
//		r1.add(new ReferenceHeuristic(), 0.8);
//		r1.add(new InvocationHeuristic(), 0.75);
//		r1.add(new AssertionHeuristic(), 0.68);
//		results = new ArrayList<VotingResult>();
//		results.add(r1);
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.setContentProvider(new ViewContentProvider());
		//viewer.setLabelProvider(new ViewLabelProvider());
//		viewer.setSorter(new NameSorter());
		viewer.setInput(SearchAction.getResults());

		// Create the help context id for the viewer's control
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "QueryManager.viewer");
		makeActions();
//		hookContextMenu();
		hookDoubleClickAction();
//		contributeToActionBars();
	}

	// This will create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = {"Rank", "Score", "FQN", "Heuristics"};
		int[] bounds = {50, 75, 300, 100};

		// First column is for the rank
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				VotingResult result = (VotingResult) element;
				return result.getRank().toString();
			}
		});

		// Second column is for the score
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				VotingResult result = (VotingResult) element;
				return result.getScore().toString();
			}
		});

		// Now the name
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				VotingResult result = (VotingResult) element;
				return result.getFqn();
			}
		});

		// Now the heuristics
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				VotingResult result = (VotingResult) element;
				StringBuilder label = new StringBuilder();
				for (Heuristic heuristic : result.getHeuristics()) {
					label.append(heuristic.getName());
					label.append(" ");			    	
				}
				return label.toString().trim();
			}
		});

	}	

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu");
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				ResultView.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(viewer.getControl());
//		viewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuMgr, viewer);
//	}

//	private void contributeToActionBars() {
//		IActionBars bars = getViewSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
//		fillLocalToolBar(bars.getToolBarManager());
//	}
//
//	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(new Separator());
//		manager.add(action2);
//	}
//
//	private void fillContextMenu(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//		// Other plug-ins can contribute there actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}
//
//	private void fillLocalToolBar(IToolBarManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//	}
//
	private void makeActions() {
//		action1 = new Action() {
//			public void run() {
//				showMessage("Action 1 executed");
//			}
//		};
//		action1.setText("Action 1");
//		action1.setToolTipText("Action 1 tooltip");
//		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
//
//		action2 = new Action() {
//			public void run() {
//				showMessage("Action 2 executed");
//			}
//		};
//		action2.setText("Action 2");
//		action2.setToolTipText("Action 2 tooltip");
//		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
//				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
//	private void showMessage(String message) {
//		MessageDialog.openInformation(
//				viewer.getControl().getShell(),
//				"Search Results",
//				message);
//	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}