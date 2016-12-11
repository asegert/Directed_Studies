package helloworld.actions;

import java.util.ArrayList;

public class variableList
{
	String fullText="";
	String replaceText="";
	ArrayList vars=new ArrayList();
	ArrayList constantVars=new ArrayList();
	
	public variableList(String inputText)
	{
		fullText=inputText;
		fullText=fullText.replaceAll(";", " ; ");
		fullText=fullText.replaceAll("=", " = ");
		replaceText=fullText;
		
		tokenizeConstants();
		tokenizeString();
		tokenizeChar();
		tokenizeInt();
		tokenizeFloat();
		tokenizeByte();
		tokenizeDouble();
		tokenizeShort();
		tokenizeLong();
		tokenizeBoolean();
		tokenizeObject();
	}
	
	public void tokenizeConstants()
	{
		String text=fullText;
		String[]result=text.split("final ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int Start=(result[x].indexOf(" "));
				String temp=result[x].substring(Start+1);
				int End=(temp.indexOf(" ")+Start+1);
				
				int varEnd=result[x].indexOf(";");
				
				if(varEnd>result[x].indexOf("="))
					varEnd=result[x].indexOf("=");
				
				if(varEnd>-1 && (varEnd)<=(End+3))
				{
					constantReplace(result[x].substring(0, varEnd));
					constantVars.add("final "+result[x].substring(Start, (End+2)));
				}
			}
		}
		
		text=fullText;
		result=text.split("static ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int Start=(result[x].indexOf(" "));
				String temp=result[x].substring(Start+1);
				int End=(temp.indexOf(" ")+Start+1);
				
				int varEnd=result[x].indexOf(";");
				
				if(varEnd>result[x].indexOf("="))
					varEnd=result[x].indexOf("=");
				
				if(varEnd>-1 && (varEnd)<=(End+3))
				{
					constantReplace(result[x].substring(0, varEnd));
					constantVars.add("static "+result[x].substring(Start, (End+2)));
				}
			}
		}
		fullText=replaceText;
	}
	
	public void tokenizeString()
	{
		String text=fullText;
		String[] result = text.split("String ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
			
				if(End>-1)
				{
					vars.add("String "+result[x].substring(0, End));
				}
			}
		}
	}
	
	public void tokenizeChar()
	{
		String text=fullText;
		String[] result = text.split("char ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
			
				if(End>-1)
				{
					vars.add("char "+result[x].substring(0, End));
				}
			}
		}
	}
	
	public void tokenizeInt()
	{
		String text=fullText;
		String[] result = text.split("int ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
			
				if(End>-1)
				{
					vars.add("int "+result[x].substring(0, End));
				}
			}
		}
	}
	
	public void tokenizeFloat() 
	{
		String text=fullText;
		String[] result = text.split("float ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
			
				if(End>-1)
				{
					vars.add("float "+result[x].substring(0, End));
				}
			}
		}
	}
	
	public void tokenizeByte()
	{
		String text=fullText;
		String[] result = text.split("byte ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
			
				if(End>-1)
				{
					vars.add("byte "+result[x].substring(0, End));
				}
			}
		}
	}
	
	public void tokenizeDouble()
	{
		String text=fullText;
		String[] result = text.split("double ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
				
				if(End>-1)
				{
					vars.add("double "+result[x].substring(0, End));
				}
			}
		}
	}
	
	public void tokenizeShort()
	{
		String text=fullText;
		String[] result = text.split("short ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
			
				if(End>-1)
				{
					vars.add("short "+result[x].substring(0, End));
				}
			}
		}
	}
	
	public void tokenizeLong()
	{
		String text=fullText;
		String[] result = text.split("long ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
			
				if(End>-1)
				{
					vars.add("long "+result[x].substring(0, End));
				}
			}
		}
	}
	
	public void tokenizeBoolean()
	{
		String text=fullText;
		String[] result = text.split("boolean ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
			
				if(End>-1)
				{
					vars.add("boolean "+result[x].substring(0, End));
				}
			}
		}
	}

	public void tokenizeObject()
	{
		String text=fullText;
		String[] result = text.split("Object ");
		if(result.length>-1)
		{
			for(int x=1;x<result.length; x++)
			{
				int End=(result[x].indexOf(" "));
			
				if(End>-1)
				{
					vars.add("Object "+result[x].substring(0, End));
				}
			}
		}
	}
	
	public void constantReplace(String varLine)
	{
		replaceText=replaceText.replace(varLine, "constant");
	}
	
	public ArrayList getArrayListV()
	{
		return vars;
	}
	
	public ArrayList getArrayListC()
	{
		return constantVars;
	}
	
	public void setArrayList(String inputText)
	{
		fullText=inputText;
		fullText=fullText.replaceAll(";", " ; ");
		vars.clear();
		
		tokenizeString();
		tokenizeChar();
		tokenizeInt();
		tokenizeFloat();
		tokenizeByte();
		tokenizeDouble();
		tokenizeShort();
		tokenizeLong();
		tokenizeBoolean();
		tokenizeObject();
	}
}

