package ca.ucalgary.cpsc.ase.querymanager.views;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ca.ucalgary.cpsc.ase.common.ServiceProxy;
import ca.ucalgary.cpsc.ase.common.query.Query;
import ca.ucalgary.cpsc.ase.common.query.QueryInvocation;
import ca.ucalgary.cpsc.ase.common.query.QueryMethod;
import ca.ucalgary.cpsc.ase.common.heuristic.HeuristicManager;
import ca.ucalgary.cpsc.ase.common.heuristic.VotingResult;
import ca.ucalgary.cpsc.ase.common.query.QueryElement;

public class HeuristicsDialog extends Dialog {

	protected Query query;
	protected VotingResult result;
	protected HeuristicManager heuristicManager; 

	private static Logger logger = Logger.getLogger(HeuristicsDialog.class);	

	protected HeuristicsDialog(Shell parentShell, Query query, VotingResult result) throws NamingException {
		super(parentShell);
		this.query = query;
		this.result = result;
		heuristicManager = ServiceProxy.getHeuristicManager();
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout(2, true);
	    composite.setLayout(layout);		
		
		createLabels(composite);				
		
		createQueryTree(composite);

		try {
			createResultTree(composite);
		} catch (Exception e) {
			logger.error(e);
		}
		
		return composite;	
	}

	private void createLabels(Composite composite) {
		Label queryLabel = new Label(composite, SWT.NONE);
		queryLabel.setText("Query");
		queryLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));				

		Label resultLabel = new Label(composite, SWT.NONE);
		resultLabel.setText(result.getFqn() + " " + String.format("%.3f", result.getScore()));
		resultLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
	}

	private void createQueryTree(Composite composite) {
		Tree queryTree = new Tree(composite, SWT.BORDER);
		queryTree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		createTreeItem(queryTree, query.getTestClass());
		createTreeItem(queryTree, query.getTestMethod());
		createTreeItem(queryTree, query.getAssertions());
		createTreeItem(queryTree, query.getExceptions());
		createTreeItem(queryTree, query.getInvocations());
//		createTreeItem(queryTree, query.getParameters());
		createTreeItem(queryTree, query.getReferences());
		createDataFlowsTreeItem(queryTree, query.getDataFlows());
	}

	private void createDataFlowsTreeItem(Tree tree,
			Map<QueryMethod, Set<QueryInvocation>> dataFlows) {
		if (dataFlows == null || dataFlows.size() == 0)
			return;
		
		TreeItem item = new TreeItem(tree, 0);
		item.setText("Data Flows");
		
		for (QueryMethod method : dataFlows.keySet()) {
			TreeItem from = new TreeItem(item, 0);
			from.setText(method.toString());
			for (QueryInvocation invocation : dataFlows.get(method)) {
				if (!contains(from, invocation.toString())) {
					TreeItem to = new TreeItem(from, 0);
					to.setText(invocation.toString());				
				}
			}
		}
	}

	private void createResultTree(Composite composite) throws Exception {
		Tree resultTree = new Tree(composite, SWT.BORDER);
		resultTree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		for (String heuristic : result.getHeuristics()) {
			TreeItem heuristicItem = new TreeItem(resultTree, 0);
			heuristicItem.setText(heuristicManager.getFullName(heuristic) + " " + String.format("%.3f", result.getScore(heuristic)));
			if ("DF".equals(heuristic)) {
				Map<String, Set<String>> dataFlows = heuristicManager.getMatchingDataFlows(result.getId(), query);
				for (String method : dataFlows.keySet()) {
					TreeItem from = new TreeItem(heuristicItem, 0);
					from.setText(method);
					for (String invocation : dataFlows.get(method)) {
						if (!contains(from, invocation)) {
							TreeItem to = new TreeItem(from, 0);
							to.setText(invocation);				
						}
					}
				}
			}
			else {
				for (Object item : heuristicManager.getMatchingItems(result.getId(), query, heuristic)) {
					if (!contains(heuristicItem, item.toString())) {
						TreeItem matchedItem = new TreeItem(heuristicItem, 0);
						matchedItem.setText(item.toString());					
					}
				}				
			}
		}
	}
	
	private void createTreeItem(Tree tree, QueryElement element) {
		TreeItem item = new TreeItem(tree, 0);
		item.setText(element.getCaption());
		
		TreeItem subItem = new TreeItem(item, 0);
		subItem.setText(element.toString());
	}
	
	private void createTreeItem(Tree tree, List elements) {
		if (elements == null || elements.size() == 0)
			return;
		
		List<QueryElement> list = (List<QueryElement>)elements;

		TreeItem item = new TreeItem(tree, 0);
		item.setText(list.get(0).getCaption());
		
		for (QueryElement element : list) {
			if (!contains(item, element.toString())) {
				TreeItem subItem = new TreeItem(item, 0);
				subItem.setText(element.toString());
				if (!element.isResolved()) {
					subItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
				}				
			}
		}
	}
	
	private boolean contains(TreeItem item, String element) {
		for (int i = 0; i < item.getItemCount(); ++i) {
			if (item.getItem(i).getText().equals(element))
				return true;
		}
		return false;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	    newShell.setText("Heuristics");
	    newShell.setMinimumSize(650, 250);
	}
		
}

