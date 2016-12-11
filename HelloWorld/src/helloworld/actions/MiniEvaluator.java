package helloworld.actions;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MiniEvaluator 
{
	ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");
    String conditional="";
    
    boolean evaluate;
    boolean exists=true;
	String[] params;
	public MiniEvaluator(String docText, String correctEvaluation, String[]parms) throws ScriptException
	{
	    params=parms;
		String testEval=evaluateGrab(docText);
		String correctEval=getVarVal(correctEvaluation);
		compare(testEval, correctEval);
	}
	
	public String evaluateGrab(String docText)
	{
		docText=docText.replaceAll("if ", "if");
		String[] tempDoc=docText.split("if");
		String eval="";

		if(tempDoc.length<=1)
			exists=false;
		for(int b=1; b<tempDoc.length; b++)
		{
			int start=tempDoc[b].indexOf("(");
			int end=tempDoc[b].indexOf(")");
			
			
			eval=tempDoc[b].substring(start+1, end);
			conditional="if("+eval+")";
			eval=getVarVal(eval);
		}
		return eval;
	}
	
	public String getVarVal(String varNam)
	{
		for(int t=0; t<params.length; t++)
		{
			Object num=t;
			
			if(varNam.contains(params[t]))
				varNam=varNam.replace(params[t], num.toString());
		}
		return varNam;
	}
	
	public void compare(String untested, String tested) throws ScriptException
	{
		if(exists)
		{
			untested=untested.replaceAll(" ", "");
			tested=tested.replaceAll(" ", "");
			boolean eval1=(boolean) engine.eval(untested);
			boolean eval2=(boolean) engine.eval(tested);
			
			if(eval1==eval2)
			{
			    evaluate=true;
			}
			else
			{
				evaluate=false;
			}
		}
	}
	
	public boolean getEval()
	{
		return evaluate;
		
	}
	
	public boolean getExistance()
	{
		return exists;
		
	}
	
	public String getCondition()
	{
		conditional=conditional.replaceAll("  ", "");
		return conditional;
	}
}
