package helloworld.views;



import java.net.URL;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.osgi.framework.Bundle;


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

public class GradingView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "helloworld.views.GradingView";

	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action percentage;
	private Action gradePoints;
	public static Action refresh;
	private Action doubleClickAction;
	public int temp=0;
	public static double totalG=0;
	public static ArrayList evalType=new ArrayList();
	public static ArrayList corrMess=new ArrayList();
	public static ArrayList grades=new ArrayList();
	public static Object[] viewColour;
	private boolean percent=false;
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
		
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		@Override
		public void dispose() {
		}
		@Override
		public Object[] getElements(Object parent) {
			return evalType.toArray();
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
				return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}
	
	private void createColumns(final Composite parent, final TableViewer viewer) {
        String[] titles = { "Catagory", "Grades" };
        int[] bounds = { 200, 60 };
        
        TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
            	return (String)element;
            }
            
    });

        // second column is for the last name
        col = createTableViewerColumn(titles[1], bounds[1], 1);
        col.setLabelProvider(new ColumnLabelProvider() {
        		
        		int indX;
        		@Override
            	public String getText(Object element) {
        			indX=evalType.indexOf(element);
        			Object[] g=grades.toArray();
        			
        			this.getForeground(element);
        			
        			if(percent)
        			{
        				String temp=(String) g[indX];
        				int a=((temp).indexOf("/"));
        				
        				if(a==-1)
        					return g[indX].toString();
        				
        				temp=(temp).substring(0, a);
        				double p1=Double.parseDouble(temp);
        				
        				temp=(String) g[indX];
        				temp=(temp).substring((a+1));
        				double p2=Double.parseDouble(temp);
        				
        				Object prcnt=((p1*100)/p2)+"%";
        				return (String) prcnt;
        			}
        				
        			return g[indX].toString();
        			}
        		
                @Override
                public Color getForeground(final Object element) {
                	Object[] Temp=grades.toArray();
                	
                	if(indX==(Temp.length-1))
                		return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
             
    				String temp=(String) Temp[indX];
    				int a=((temp).indexOf("/"));
    				if(a<0)
    					return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
    				
    				temp=(temp).substring(0, a);
    				double p1=Double.parseDouble(temp);
    				
    				temp=(String) Temp[indX];
    				temp=(temp).substring((a+1));
    				double p2=Double.parseDouble(temp);
                	
                	
                	if (p1<p2 && p1>0) 
                	{
                		return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
                	}
                	else if (p1<p2 && p1<=0) 
                	{
                		return Display.getDefault().getSystemColor(SWT.COLOR_RED);
                	}
                	else if (p1==p2) 
                	{
                		return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
                	}
                	else
                	{
                		return  Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
                	}
                }
        		
    });
        col.getColumn().setAlignment(SWT.RIGHT);
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, int colNumber) {
		 final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
                 SWT.NONE);
		 final org.eclipse.swt.widgets.TableColumn column = viewerColumn.getColumn();
		 column.setText(title);
		 column.setWidth(bound);
		 column.setResizable(true);
		 column.setMoveable(true);
		 return viewerColumn;
	}

	/**
	 * The constructor.
	 */
	public GradingView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		evalType.add("eval1");
		evalType.add("eval2");
		evalType.add("eval3");
		
		grades.add("100.0/100.0");
		grades.add("50.0/100.0");
		grades.add("0.0/100.0");
		
		corrMess.add("Mess1");
		corrMess.add("Mess2");
		corrMess.add("Mess3");
		
	
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(evalType);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "HelloWorld.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				GradingView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		/*manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
		manager.add(new Separator());*/
		manager.add(refresh);
		manager.add(gradePoints);
		manager.add(percentage);
	}

	private void fillContextMenu(IMenuManager manager) {
		/*manager.add(action1);
		manager.add(action2);*/
		manager.add(refresh);
		manager.add(gradePoints);
		manager.add(percentage);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		/*manager.add(action1);
		manager.add(action2);*/
		manager.add(refresh);
	}

	private void makeActions() {
		refresh = new Action() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				Object[] evalT=helloworld.actions.SampleAction.evalType; 
				Object[] grad=helloworld.actions.SampleAction.grade;
				Object[] mess=helloworld.actions.SampleAction.correctionMessage;
				
				evalType.clear();
				grades.clear();
				corrMess.clear();
				
				for(int pos=0; pos<evalT.length; pos++)
				{
					if(evalT[pos]!=null)
						evalType.add(evalT[pos]);
				}
				evalType.add(" ");
				evalType.add("Total Grade");
				for(int pos=0; pos<grad.length; pos++)
				{
					if(grad[pos]!=null)
						grades.add(grad[pos]);
				}
				grades.add(" ");
				grades.add(helloworld.actions.SampleAction.finalGrade);
				for(int pos=0; pos<mess.length; pos++)
				{
					if(mess[pos]!=null)
						corrMess.add(mess[pos]);
				}
				corrMess.add("");
				corrMess.add("Total Grade: "+helloworld.actions.SampleAction.finalGrade);
				viewer.refresh();
			}
		};
		refresh.setText("Refresh");
		refresh.setToolTipText("Refresh tooltip");
		Bundle bundle = Platform.getBundle("HelloWorld");
		final URL fullPathString = FileLocator.find(bundle, new Path("icons.refresh.gif"), null);
		refresh.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		percentage = new Action() {
			@Override
			public void run() 
			{
				percent=true;
				viewer.refresh();
			}
		};
		percentage.setText("Change Grades to Percentages");
		percentage.setToolTipText("Percentage tooltip");
		percentage.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		gradePoints = new Action() {
			@Override
			public void run() 
			{
				percent=false;
				viewer.refresh();
			}
		};
		gradePoints.setText("Change Grades to Points");
		gradePoints.setToolTipText("Points tooltip");
		gradePoints.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		doubleClickAction = new Action() {
			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				
				int q=0;
				for(q=0; q<evalType.size(); q++)
				{
					Object[] eT=evalType.toArray();
					Object temp=eT[q];
					if(temp.equals(obj))
					{
						break;
					}
				}
				Object[] cM=corrMess.toArray();
				Object[] g=grades.toArray();
				showMessage(cM[q]+"\n"+(g[q]));
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Grading View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	/*public void setFocus() {
		viewer.getControl().setFocus();
	}
}*/

	/*@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
}

