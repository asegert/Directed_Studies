package helloworld.actions;


import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.dialogs.MessageDialog;
//tokenize by data type
//Store in Arraylist
//?value storage
/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	
	public static int numEvaluated=6;
	public static String correctEval="midterm2>midterm1";
	public static String[] params={"midterm1", "midterm2"};
	public static String half="true";
	
	//Use
	public boolean comment=true;
	public boolean constant=true;
	public boolean scanS=true;
	public boolean dataValidation=true;
	public boolean comparisons=true;
	public boolean printOuts=true;
	public boolean naming=true;
	
	public static variableList a;
	
	public int commentQuantity;
	public int constantCount;
	public int scanQuantity;
	public boolean printOut;
	//Threshold variables used for grading
	public int comments=5;
	public int constants=3;
	public int comparison=1;
	public int scans=5;
	
	//Grade (points) variables
	public int commentPoints=2;
	public int constantPoints=2;
	public int scansPoints=1;
	public int dataValidationPoints=2;
	public int comparisonPoints=1;
	public int printOutPoints=1;
	public int namingPoints=1;
	
	private IWorkbenchWindow window;
	public static double totalGrade=0;
	public static String finalGrade="";
	public int currFill=0;
	public String message="";
	
	public String scanName="";
	
    public String[] lineLabels;
	public static String[] evalType=new String[numEvaluated];
	public static String[] correctionMessage=new String[numEvaluated];
	public static String[] grade=new String[numEvaluated];
	
	String dt="";
	/**
	 * The constructor.
	 * @throws PartInitException 
	 */
	public SampleAction() throws PartInitException {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("helloworld.views.GradingView");
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	
	@Override
	public void run(IAction action) {
		init(this.window);
		try {
			setVariables();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		commentQuantity=0;
		constants=0;
		scanQuantity=0;
		printOut=false;
		totalGrade=0;
		finalGrade="";
		currFill=0;
        message="";
              
    	evalType=new String[numEvaluated];
    	correctionMessage=new String[numEvaluated];
    	grade=new String[numEvaluated];
		
        try {               
            IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

            if ( part instanceof ITextEditor ) {
                final ITextEditor editor = (ITextEditor)part;
                IDocumentProvider prov = editor.getDocumentProvider();
                IDocument doc = prov.getDocument( editor.getEditorInput() );
                ISelection sel = editor.getSelectionProvider().getSelection();
                
                
                if ( sel instanceof TextSelection ) {
                    final TextSelection textSel = (TextSelection)sel;
                    
                    String documentText=doc.get();
                    documentText=documentText.replaceAll("=", " = ");
                    documentText=commentCompile(documentText);
                    dt=documentText;
                    
                    a=new variableList(documentText);
                    
                    tokenize(documentText);
                    calculateGrade();
                    
                    MessageDialog.openInformation(
                            window.getShell(),
                            "TestPlugin",
                            "Your final grade is "+totalGrade);
                }
                finalGrade=totalGrade+"/"+(commentPoints+constantPoints+scansPoints+comparisonPoints+printOutPoints+namingPoints);
                helloworld.views.GradingView.refresh.run();
                
            }else{
                MessageDialog.openInformation(
                        window.getShell(),
                        "AnirudhPlugin",
                        "Not ITextEditor");
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

    }

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	@Override
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	//Initializes window
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	//Tokenizes window text or 'code'
	//run constants b4 variable list and return text with constants removed
	public void tokenize(String docText)
	{
		String[] result = docText.trim().split("\\s{1,}");
		lineLabels=new String[result.length];
		
	     for (int x=0; x<result.length; x++)
	     {
	    	 if(result[x].toLowerCase().equals("scanner"))
	    	 {
	    		 lineLabels[x]="scanner";
	    		 lineLabels[x+1]="scannername";
	    		 scanName=result[x+1];
	    		 x=x+1;
	    	 }
	    	 if(result[x].contains(scanName+".next"))
	    	 {
	    		 lineLabels[x]="readScannerInput";
	    	 }
	    	 if(result[x].contains("System.out.print(")|result[x].contains("System.out.println("))
	    	 {
	    		 printOut=true;
	    	 }
	     }
	}
	
	//Extracts comments from text and replaces all lines with '--comment--'
	//Can alter to store comments for further evaluation
	//m=multi-line comments m2=single line comments
	public String commentCompile(String docText)
	{
		String rep=docText;
		String s = docText;
		
		int multilines=0;
		int singlelines=0;
		
		Pattern p = Pattern.compile("(?s)/\\*.*?\\*/");
		Pattern p2= Pattern.compile("//.*");
		
		Matcher m = p.matcher(s);
		Matcher m2 = p2.matcher(s);
		
		while(m.find())
		{
		    //System.out.println(m.group());
		    rep=rep.replace( m.group(), "\n--comment--" );
		    multilines++;
		}
		while(m2.find())
		{
			//System.out.println(m2.group());
			rep=rep.replace( m2.group(), "\n--comment--" );
			singlelines++;
		}
		commentQuantity=commentQuantity+multilines+singlelines;
		return rep;
	}
	//Selects the string to be evaluated that can be sent as parameters to the mini-evaluator
	//Still needs to replace var names with values
	
	public void scanInputs()
	{
		for(int x=0;x<lineLabels.length;x++)
		{
			if(lineLabels[x]=="readScannerInput")
				scanQuantity++;
		}
	}
	
	public void calculateGrade() throws ScriptException
	{
		//Checks comment quantity
		if(comment)
		{
			if(commentQuantity>5)
			{
				totalGrade=totalGrade+commentPoints;
				evalType[currFill]="Comments";
				correctionMessage[currFill]="\nGood use of comments!";
				grade[currFill]=((double)commentPoints+"/"+commentPoints);
				currFill++;
			}
			else if(commentQuantity>=(((double)comments/2)))
			{
				totalGrade=totalGrade+(round((double)commentPoints/2));
				evalType[currFill]="Comments";
				correctionMessage[currFill]="\nPretty Good Commenting! Try adding a few more.";
				grade[currFill]=(round((double)commentPoints/2)+"/"+commentPoints);
				currFill++;
			}
			else
			{
				evalType[currFill]="Comments";
				correctionMessage[currFill]="\nPlease add more comments";
				grade[currFill]=("0/"+commentPoints);
				currFill++;
			}
		}
		//Checks scan (input) quantity
		if(scanS)
		{
			scanInputs();
			if(scanQuantity>=5)
			{
				totalGrade=totalGrade+scansPoints;
				evalType[currFill]="Scanner Input";
				correctionMessage[currFill]="\nAll input received.";
				grade[currFill]=(((double)scansPoints)+"/"+scansPoints);
				currFill++;
			}
			else if(scanQuantity>=((double)scans/2))
			{
				totalGrade=totalGrade+(round((double)scansPoints/2));
				evalType[currFill]="Scanner Input";
				correctionMessage[currFill]="\nSome scanner input used, but some is missing";
				grade[currFill]=(round((double)scansPoints/2)+"/"+scansPoints);
				currFill++;
			}
			else
			{
				evalType[currFill]="Scanner Input";
				correctionMessage[currFill]="\nPlease utilize your scanner.";
				grade[currFill]=("0/"+scansPoints);
				currFill++;
			}
		}
		//Checks that output exists
		if(printOuts)
		{
			if(printOut)
			{
				totalGrade=totalGrade+printOutPoints;
				evalType[currFill]="Output";
				correctionMessage[currFill]="\nGood Output!";
				grade[currFill]=((double)printOutPoints+"/"+printOutPoints);
				currFill++;
			}
			else
			{
				evalType[currFill]="Output";
				correctionMessage[currFill]="\nCheck your Output! It seems to be missing.";
				grade[currFill]=("0/"+printOutPoints);
				currFill++;
			}
		}
		//Checks Constants
		if(constant)
		{
			constants=a.getArrayListC().size();
			if(constants>=constantPoints)
			{
				totalGrade=totalGrade+constantPoints;
				evalType[currFill]="Use of Constants";
				correctionMessage[currFill]="\nGreat use of constants!";
				grade[currFill]=((double)constantPoints+"/"+constantPoints);
				currFill++;
			}
			else if(constants<(constantPoints))
			{
				evalType[currFill]="Use of Constants";
				correctionMessage[currFill]="\nPlease use constants!";
				grade[currFill]=("0/"+constantPoints);
				currFill++;
			}
			else
			{
				totalGrade=totalGrade+(round((double)constantPoints/2));
				evalType[currFill]="Use of Constants";
				correctionMessage[currFill]="\nGood use of constants! Try adding a few more.";
				grade[currFill]=(round((double)constantPoints/2)+"/"+constantPoints);
				currFill++;
			}
		}
		//Checks for accurate comparisons
		if(comparisons)
		{
			MiniEvaluator eval= new MiniEvaluator(dt, correctEval, params);
			if(eval.getExistance()==true)
			{
				if(eval.getEval()==true)
				{
					totalGrade=totalGrade+comparisonPoints;
        	
					evalType[currFill]="Comparisons";
					correctionMessage[currFill]="\nCorrect Evaluation";
					grade[currFill]=(comparisonPoints)+"/"+comparisonPoints;
					currFill++;
				}
				else
				{
					totalGrade=totalGrade+(round((double)comparisonPoints/2));
					
					evalType[currFill]="Comparisons";
					correctionMessage[currFill]="\nIncorrect Evaluation for \" "+eval.getCondition()+" \"";
					grade[currFill]=(round((double)comparisonPoints/2)+"/"+comparisonPoints);
					currFill++;
				}
			}
			else
			{
				evalType[currFill]="Comparisons";
				correctionMessage[currFill]="Conditional Statement is Missing";
				grade[currFill]=(0+"/"+comparisonPoints);
				currFill++;
			}
		}
		if(naming)
		{
			nameConventions evaluat=new nameConventions(a.getArrayListC(), a.getArrayListV());
			evaluat.conventionFollowed(0);
			evalType[currFill]="Naming Convention";
			correctionMessage[currFill]= helloworld.actions.nameConventions.message;
			grade[currFill]=(round(namingPoints*helloworld.actions.nameConventions.pntsprcnt))+"/"+namingPoints;
			totalGrade=totalGrade+(round(namingPoints*helloworld.actions.nameConventions.pntsprcnt));
		}
	}
	
	
	public static double round(double value) {

		if(half.equals("true"))
		{
		    BigDecimal bd = new BigDecimal(value);
		    bd = bd.setScale(3, RoundingMode.HALF_UP);
		    return bd.doubleValue();
		}
		else
		{
			BigDecimal bd = new BigDecimal(value);
		    bd = bd.setScale(0, RoundingMode.HALF_DOWN);
		    return bd.doubleValue();
		}
	}
	
	
	@SuppressWarnings("null")
	public void setVariables() throws IOException
	{
		//Category
		FileInputStream fis = null;
		try 
		{
			String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/EvalCat.txt";
	      	fis=new FileInputStream(workSpaceRootpath);
			
	      	int count=0;
			int content;
			String tem="";
            while ((content = fis.read()) != -1) {
                // convert to char and display it
            	boolean temp=false;
            	if((""+(char)content).matches("[a-zA-Z]+"))
            	{
            		if((char)content=="y".charAt(0))
            		{
            			count++;
            		}
            		else
            		{
            			if(count==0)
            				comment=false;
            			else if(count==1)
            				constant=false;
            			else if(count==2)
            				scanS=false;
            			else if(count==3)
            				dataValidation=false;
            			else if(count==4)
            				comparisons=false;
            			else if(count==5)
            				printOuts=false;
            			else if(count==6)
            				naming=false;
            		
            			count++;
            		}
            	}
            }
		}
		finally
		{
			fis.close();
		}
		
		//Points
				try 
				{
					String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/PointCat.txt";
			      	fis=new FileInputStream(workSpaceRootpath);
					
			      	int count=0;
					int content;
					String t="";
		            while ((content = fis.read()) != -1) {
		                // convert to char and display it
		            	boolean temp=false;
		            	t=t+(char)content;
		            	
		            	if(t.contains("\\s{1,}")||t.contains(System.getProperty("line.separator").substring(0)))
		            	{
		            		t="";
		            	}
		            	else if(t.contains("null"))
		            	{
		            		if(count==0)
		            			commentPoints=0;
		            		else if(count==1)
		            			constantPoints=0;
		            		else if(count==2)
		            			scansPoints=0;
		            		else if(count==3)
		            			dataValidationPoints=0;
		            		else if(count==4)
		            			comparisonPoints=0;
		            		else if(count==5)
		            			printOutPoints=0;
		            		else if(count==6)
		            			namingPoints=0;
		            		count++;
		            		t="";
		            	}
		            	else if(t.matches(".*\\d+.*"))
		            	{
		            		if(count==0)
		            			commentPoints=Integer.parseInt(t);
		            		else if(count==1)
		            			constantPoints=Integer.parseInt(t);
		            		else if(count==2)
		            			scansPoints=Integer.parseInt(t);
		            		else if(count==3)
		            			dataValidationPoints=Integer.parseInt(t);
		            		else if(count==4)
		            			comparisonPoints=Integer.parseInt(t);
		            		else if(count==5)
		            			printOutPoints=Integer.parseInt(t);
		            		else if(count==6)
		            			namingPoints=Integer.parseInt(t);
		            		t="";
		            		count++;
		            	}
		            	
		            }
				}
				finally
				{
					fis.close();
				}
				
				//Threshholds
				try 
				{
					String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/ThreshCat.txt";
			      	fis=new FileInputStream(workSpaceRootpath);
					
			      	int count=0;
					int content;
					String t="";
		            while ((content = fis.read()) != -1) {
		                // convert to char and display it
		            	boolean temp=false;
		            	t=t+(char)content;
		            	if(t.contains(" "))
		            	{
		            		t="";
		            	}
		            	else if(t.contains(System.getProperty("line.separator")))
		            	{
		            		t="";
		            		count++;
		            	}
		            	else if(t.contains("null"))
		            	{
		            		if(count==0)
		            			comments=0;
		            		else if(count==1)
		            			constants=0;
		            		else if(count==2)
		            			scans=0;
		            		else if(count==3)
		            			comparison=0;
		            		
		            		t="";
		            	}
		            	else if(t.matches(".*\\d+.*")&&(count<3||count==4))
		            	{
		            		if(count==0)
		            			comments=Integer.parseInt(t);
		            		else if(count==1)
		            			constants=Integer.parseInt(t);
		            		else if(count==2)
		            			scans=Integer.parseInt(t);
		            		else if(count==4)
		            			params=new String[Integer.parseInt(t)];
		            		t="";
		            	}
		            	else
		            	{
		            		String last = ""+t.charAt(t.length()-1);
		            		if(count==3 && (last.matches(".*\\d+.*")||last.matches("[a-zA-Z]+")))
		            		{
		            			correctEval=t;
		            		}
		            		else if(count>=5 && (last.matches(".*\\d+.*")||last.matches("[a-zA-Z]+")))
		            		{
		            			params[(count-5)]=t;
		            		}
		            		
		            	}
		            	
		            }
				}
				finally
				{
					fis.close();
				}
				
				//Half Marks
				try 
				{
					String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"/src/helloworld/actions/Half.txt";
			      	fis=new FileInputStream(workSpaceRootpath);
					
			      	int count=0;
					int content;
					String t="";
		            while ((content = fis.read()) != -1) {
		                // convert to char and display it
		            	String temp=""+(char)content;
		            	if(temp.matches("[a-zA-Z]+"))
		            	{
		            		t=t+temp;	
		            		half=t;
		            	}
		            }
				}
				finally
				{
					fis.close();
				}
	}
}