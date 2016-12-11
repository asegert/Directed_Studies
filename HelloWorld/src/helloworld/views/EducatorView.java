package helloworld.views;

import java.io.File;

import helloworld.actions.SampleAction;
import helloworld.actions.varWriter;

import java.awt.Event;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.*;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.InputDialog;
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

public class EducatorView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "helloworld.views.GradingView";
	
	

	public int row=0;
	private TableViewer viewer;
	private Action build;
	private Action doubleClickAction;
	public int temp=0;
	public static double totalG=0;
	public static ArrayList evalType=new ArrayList();
	public static ArrayList corrMess=new ArrayList();
	public static ArrayList update=new ArrayList();
	public static Object[] viewColour;
	public static String[] evals;
	private boolean percent=false;
	public String unupdated;
	public String halfMarks="true";

	 
	
	class ViewContentProvider implements IStructuredContentProvider {
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return evalType.toArray();
		}
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
				return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}
	
	private void createColumns(final Composite parent, final TableViewer viewer) {
        String[] titles = { "Type to Set", "Updated" };
        int[] bounds = { 200, 100 };
        
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
        			Object[] g=update.toArray();
        			
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
                	Object[] Temp=update.toArray();
                	
                	String temp=(String) Temp[indX];
    				
                	if (temp.equals("Needs Updated")||temp.equals("false")) 
                	{
                		return Display.getDefault().getSystemColor(SWT.COLOR_RED);
                	}
                	else if (temp.equals("Updated")||temp.equals("true")) 
                	{
                		return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
                	}
                	else if (temp.equals("Updatable")) 
                	{
                		return  Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
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
	 * @throws IOException 
	 */
	public EducatorView() throws IOException {
		FileInputStream fis=null;
		try 
		{
			fis=new FileInputStream(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/EvalCat.txt");
			int c=fis.read();
			if(c != -1)
			{
				unupdated="Updatable";
			}
			else
			{
				unupdated="Needs Updated";
			}
			fis=new FileInputStream(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/Half.txt");
			int d=fis.read();
			if((""+(char)d).equalsIgnoreCase("f"))
			{
				halfMarks="false";
			}
		}
		catch(IOException y)
		{
			System.out.println(y);
		}
		finally 
		{
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String[] input=new String[1];
    	input[0]=halfMarks;
    	String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/Hints.txt";
      	varWriter b=new varWriter(input, workSpaceRootpath);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		evalType.add("Evaluation Catagories");
		evalType.add("Set Thresholds");
		evalType.add("Points/Catagory");
		evalType.add("");
		evalType.add("Use Half Marks");
		
		update.add(unupdated);
		update.add(unupdated);
		update.add(unupdated);
		update.add("");
		update.add(halfMarks);
		
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
			public void menuAboutToShow(IMenuManager manager) {
				EducatorView.this.fillContextMenu(manager);
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
		manager.add(build);
	}

	private void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {

	}

	private void makeActions() {
		build = new Action() {
			/*public void run()
			{
			  Manifest manifest = new Manifest();
			  manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
			  JarOutputStream target=null;
			try {
				target = new JarOutputStream(new FileOutputStream(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/META-INF/MANIFEST.MF"), manifest);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  try {
				add(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"src/build.xml"), target);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  try {
				target.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}

			private void add(File source, JarOutputStream target) throws IOException
			{
			  BufferedInputStream in = null;
			  try
			  {
			    if (source.isDirectory())
			    {
			      String name = source.getPath().replace("\\", "/");
			      if (!name.isEmpty())
			      {
			        if (!name.endsWith("/"))
			          name += "/";
			        JarEntry entry = new JarEntry(name);
			        entry.setTime(source.lastModified());
			        target.putNextEntry(entry);
			        target.closeEntry();
			      }
			      for (File nestedFile: source.listFiles())
			        add(nestedFile, target);
			      return;
			    }

			    JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
			    entry.setTime(source.lastModified());
			    target.putNextEntry(entry);
			    in = new BufferedInputStream(new FileInputStream(source));

			    byte[] buffer = new byte[1024];
			    while (true)
			    {
			      int count = in.read(buffer);
			      if (count == -1)
			        break;
			      target.write(buffer, 0, count);
			    }
			    target.closeEntry();
			  }
			  finally
			  {
			    if (in != null)
			      in.close();
			  }
			}*/
		};
		doubleClickAction = new Action() {
			public void run() {
				try {
					ex();
				} catch (ExecutionException | IOException e) {
					// TODO Auto-generated catch block
					
				}
				viewer.refresh();
			}
			public Object ex() throws ExecutionException, IOException{
    	        Table table =viewer.getTable();
    	        int row = table.getSelectionIndex();
    	        
    	        if(row==0)
    	        {
    	        	String[] input=new String[7];
    	        	
    	        	InputDialog dlg = new InputDialog(null, "Evaluation Catagories", "Evaluate Comment Quantity? (y/n)", "y/n", null);
  			      	if (dlg.open() == Window.OK) 
  			      	{
  			      		input[0] = dlg.getValue();
  			      		// TODO:do something with value
  			      	}
  			      	
  			        dlg = new InputDialog(null, "Evaluation Catagories", "Evaluate Constant Quantity? (y/n)", "y/n", null);
			      	if (dlg.open() == Window.OK) 
			      	{
			      		input[1] = dlg.getValue();
			      		// TODO:do something with value
			      	}
			      	dlg = new InputDialog(null, "Evaluation Catagories", "Evaluate Scan Quantity? (y/n)", "y/n", null);
			      	if (dlg.open() == Window.OK) 
			      	{
			      		input[2] = dlg.getValue();
			      		// TODO:do something with value
			      	}
			      	dlg = new InputDialog(null, "Evaluation Catagories", "Evaluate Data Validation? (y/n)", "y/n", null);
			      	if (dlg.open() == Window.OK) 
			      	{
			      		input[3] = dlg.getValue();
			      		// TODO:do something with value
			      	}
			      	dlg = new InputDialog(null, "Evaluation Catagories", "Evaluate Conditional? (y/n)", "y/n", null);
			      	if (dlg.open() == Window.OK) 
			      	{
			      		input[4] = dlg.getValue();
			      		// TODO:do something with value
			      	}
			      	dlg = new InputDialog(null, "Evaluation Catagories", "Evaluate Output Quantity? (y/n)", "y/n", null);
			      	if (dlg.open() == Window.OK) 
			      	{
			      		input[5] = dlg.getValue();
			      		// TODO:do something with value
			      	}
			      	dlg = new InputDialog(null, "Evaluation Catagories", "Evaluate Naming Conventions? (y/n)", "y/n", null);
			      	if (dlg.open() == Window.OK) 
			      	{
			      		input[6] = dlg.getValue();
			      		// TODO:do something with value
			      	}
			      	String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/EvalCat.txt";
			      	varWriter a=new varWriter(input, workSpaceRootpath);
			      	update.clear();
			      	update.add("Updated");
			      	update.add("Needs Updated");
			      	update.add("Needs Updated");
			      	update.add("");
			      	update.add(halfMarks);
			      	
			      	evals=input;
			      	
    	        }
    	        if(row==1)
    	        {
    	        	String[] input= new String[4];
    	        	String[] bool = new String[0];
    	        	String[] param = new String[0];
    	        	
    	        	if(evals[0].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Threshhold", "How many Comments are there?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[0] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
    	        	if(evals[1].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Threshhold", "How Many Constants are there?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[1] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
    	        	if(evals[2].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Threshhold", "How Many Scans are there?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[2] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
    	        	if(evals[4].equals("y"))
    	        	{
  			      			InputDialog ndlg = new InputDialog(null, "Booleans", "What is the next Boolean Comparison to be Checked?", "BooleanValue", null);
  			      				
  			      				if(ndlg.open() == Window.OK)
  			      				{
  			      					input[3]=(ndlg.getValue().replaceAll(" ", ""));
  			      				}
  			      			InputDialog dlg = new InputDialog(null, "Booleans", "How many Variables are in your comparison?", "BooleanValue", null);
			      			 int x=0;
			      				if(dlg.open() == Window.OK)
			      				{
			      					x=(Integer.parseInt(dlg.getValue())+1);
			      				}
  			      			
  			      			param=new String[x];
  			      			param[0]=""+(x-1);
  			      			for(int j=1; j<x; j++)
			      			{
			      				InputDialog nedlg = new InputDialog(null, "Booleans", "What are/if any of the variables used in the Boolean Comparison to be Checked?", "BooleanValue", null);
			      				
			      				if(nedlg.open() == Window.OK)
			      				{
			      					param[j]=nedlg.getValue();
			      				}
			      			}
  			      			
    	        	}

			      	String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/ThreshCat.txt";
			      	if(param.length>0)
			      	{
				      	String[] temp=new String[(input.length+param.length)];
				      	int i=0;
				      	for(i=0; i<input.length; i++)
				      	{
				      		temp[i]=input[i];
				      	}
				      	for(int j=0; j<param.length; j++)
				      	{
				      		temp[(i+j)]=param[j];
				      	}
				      	varWriter a=new varWriter(temp, workSpaceRootpath);
			      	}
			      	else
			      	{
			      		varWriter b=new varWriter(input, workSpaceRootpath);
			      	}
			      	String u1=(String) update.get(0);
			      	String u2=(String) update.get(2);
			      	update.clear();
			      	update.add(u1);
			      	update.add("Updated");
			      	update.add(u2);
			      	update.add("");
			      	update.add(halfMarks);
			      	
    	        }
    	        if(row==2)
    	        {
    	        	String[] input=new String[7];
    	        	
    	        	if(evals[0].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Points/Catagory", "Number of Points for Comments?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[0] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
    	        	if(evals[1].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Points/Catagory", "Number of Points for Constants?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[1] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
    	        	if(evals[2].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Points/Catagory", "Number of Points for Scans?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[2] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
    	        	if(evals[3].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Points/Catagory", "Number of Points for Data Validation?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[3] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
    	        	if(evals[4].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Points/Catagory", "Number of Points for Conditionals?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[4] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
    	        	if(evals[5].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Points/Catagory", "Number of Points for Output?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[5] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
    	        	if(evals[6].equals("y"))
    	        	{
    	        		InputDialog dlg = new InputDialog(null, "Points/Catagory", "Number of Points for Naming Conventions?", "0", null);
  			      		if (dlg.open() == Window.OK) 
  			      		{
  			      			input[6] = dlg.getValue();
  			      			// TODO:do something with value
  			      		}
    	        	}
			      	
			      	String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/PointCat.txt";
			      	varWriter b=new varWriter(input, workSpaceRootpath);
			      	String u1=(String) update.get(0);
			      	String u2=(String) update.get(1);
			      	update.clear();
			      	update.add(u1);
			      	update.add(u2);
			      	update.add("Updated");
			      	update.add("");
			      	update.add(halfMarks);
			      	
    	        }
    	        if(row==4)
    	        {
    	        	String u0=(String) update.get(0);
    	        	String u1=(String) update.get(1);
    	        	String u2=(String) update.get(2);
    	        	
    	        	if(halfMarks.equals("true"))
    	        		halfMarks="false";
    	        	else
    	        		halfMarks="true";
    	        	
    	        	String[] input=new String[1];
    	        	input[0]=halfMarks;
    	        	String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/Half.txt";
			      	varWriter b=new varWriter(input, workSpaceRootpath);
			      	
    	        	update.clear();
			      	update.add(u0);
			      	update.add(u1);
			      	update.add(u2);
			      	update.add("");
			      	update.add(halfMarks);
    	        }
			      return null; 
			 } 
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
		        		
		        doubleClickAction.run();
				//check which row has been clicked

					    }
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Educator View",
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

	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
}