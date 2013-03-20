package ca.ucalgary.cpsc.ase.querymanager.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import ca.ucalgary.cpsc.ase.common.ServiceProxy;
import ca.ucalgary.cpsc.ase.common.query.Query;
import ca.ucalgary.cpsc.ase.common.heuristic.HeuristicManager;
import ca.ucalgary.cpsc.ase.common.heuristic.VotingResult;
import ca.ucalgary.cpsc.ase.factextractor.composer.QueryGenerator;
import ca.ucalgary.cpsc.ase.querymanager.views.ResultView;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SearchAction implements IWorkbenchWindowActionDelegate {
	
	private static Logger logger = Logger.getLogger(SearchAction.class);
	
	private static List<VotingResult> results;
	
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public SearchAction() {
	}
	
	public static List<VotingResult> getResults() {
		return results;
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		final IEditorPart activeEditor = window.getActivePage().getActiveEditor();
		if (activeEditor == null || !(activeEditor instanceof AbstractTextEditor)) {
			return;			
		}		
		ITextEditor editor = (ITextEditor)activeEditor;
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());		
		if (doc == null) {
			return;
		}

		QueryGenerator generator = new QueryGenerator();
		Query query = generator.generate(doc.get());
		
		System.out.println(query);
		
		try {
			HeuristicManager manager = ServiceProxy.getHeuristicManager();
			Map<Integer, VotingResult> resultsMap = manager.match(query);
			results = new ArrayList<VotingResult>();
			for (Integer id : resultsMap.keySet()) {
				VotingResult result = resultsMap.get(id);
				results.add(result);
				System.out.println("rank: " + result.getRank() + ", score: " + result.getScore() + ", fqn=" + result.getFqn());
			}
			ResultView resultView = (ResultView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ResultView.ID);
			resultView.setQuery(query);
		} catch (Exception e) {
			logger.error(e);
			MessageDialog.openError(
					window.getShell(),
					"TDR",
					"An error has happended when trying to retrieve results.");					
		}		
		
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}