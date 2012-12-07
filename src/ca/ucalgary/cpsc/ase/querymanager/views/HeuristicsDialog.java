package ca.ucalgary.cpsc.ase.querymanager.views;

import java.util.List;

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

import ca.ucalgary.cpsc.ase.QueryManager.Query;
import ca.ucalgary.cpsc.ase.QueryManager.VotingResult;
import ca.ucalgary.cpsc.ase.QueryManager.Heuristic;
import ca.ucalgary.cpsc.ase.QueryManager.query.QueryElement;

public class HeuristicsDialog extends Dialog {

	protected Query query;
	protected VotingResult result;
	
	protected HeuristicsDialog(Shell parentShell, Query query, VotingResult result) {
		super(parentShell);
		this.query = query;
		this.result = result;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout(2, true);
	    composite.setLayout(layout);		
		
		Label queryLabel = new Label(composite, SWT.NONE);
		queryLabel.setText("Query");
		queryLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));				

		Label resultLabel = new Label(composite, SWT.NONE);
		resultLabel.setText(result.getFqn() + " " + result.getScore());
		resultLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));				
		
		Tree queryTree = new Tree(composite, SWT.BORDER);
		queryTree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		createTreeItem(queryTree, query.getTestClass());
		createTreeItem(queryTree, query.getTestMethod());
		createTreeItem(queryTree, query.getAssertions());
		createTreeItem(queryTree, query.getExceptions());
		createTreeItem(queryTree, query.getInvocations());
		createTreeItem(queryTree, query.getParameters());
		createTreeItem(queryTree, query.getReferences());

		Tree resultTree = new Tree(composite, SWT.BORDER);
		resultTree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		for (Heuristic heuristic : result.getHeuristics()) {
			TreeItem heuristicItem = new TreeItem(resultTree, 0);
			heuristicItem.setText(heuristic.getFullName() + " " + result.getScore(heuristic));
			for (Object item : heuristic.getMatchingItems(result.getId(), query)) {
				TreeItem matchedItem = new TreeItem(heuristicItem, 0);
				matchedItem.setText(item.toString());
			}				
		}
		
		return composite;	
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
			TreeItem subItem = new TreeItem(item, 0);
			subItem.setText(element.toString());
			if (!element.isResolved()) {
				subItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			}
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	    newShell.setText("Heuristics");
	    newShell.setMinimumSize(650, 250);
	}
		
}

