package helloworld.actions;
import java.util.ArrayList;

public class nameConventions
{
	static Object[] constant;
	static Object[] var;
	static boolean cons;
	static boolean vrs;
	static String message="";
	static double pntsprcnt=-1;
	static String badConstant="";
	static String badVars="";
	
	public nameConventions(ArrayList c, ArrayList v)
	{
		constant=c.toArray();
		var=v.toArray();
		
		stripConstants();
		stripVars();
		
		cons=checkConstants();
		vrs=checkVars();
		
	}
	
	public void stripConstants()
	{
		for(int u=0; u<constant.length; u++)
		{
			String temp=constant[u].toString();
			temp=temp.replace("final", "");
			temp=temp.replace("static", "");
			temp=temp.replace(" ", "");
			message=message+temp+"\n";
			constant[u]=temp;
		}
	}
	
	public void stripVars()
	{
		for(int u=0; u<var.length; u++)
		{
			String temp=var[u].toString();
			temp=temp.replace("String", "");
			temp=temp.replace("char", "");
			temp=temp.replace("int", "");
			temp=temp.replace("float", "");
			temp=temp.replace("byte", "");
			temp=temp.replace("double", "");
			temp=temp.replace("short", "");
			temp=temp.replace("long", "");
			temp=temp.replace("boolean", "");
			temp=temp.replace("Object", "");
			temp=temp.replace(" ", "");
			message=message+temp+"\n";
			var[u]=temp;
		}
	}
	
	public boolean checkConstants()
	{
		for(int u=0; u<constant.length; u++)
		{
			String temp=(String) constant[u];
			for(int c=0; c<temp.length(); c++)
			{
				if(Character.isAlphabetic(temp.charAt(c)) && !Character.isUpperCase(temp.charAt(c)))
				{
					badConstant=temp;
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean checkVars()
	{
		boolean New=false;
		
		for(int u=0; u<var.length; u++)
		{
			String temp=(String) var[u];
			
			for(int c=0; c<temp.length(); c++)
			{
				if(Character.isAlphabetic((temp.charAt(c))))
				{
					if(New==true && !Character.isLowerCase(temp.charAt(c)))
					{
						badVars=temp;
						return false;
					}
					else if(New==false && !Character.isLowerCase(temp.charAt(c)))
					{
						New=true;
					}
					else if(New==true && Character.isLowerCase(temp.charAt(c)))
					{
						New=false;
					}
				}
			}
		}
		return true;
	}
	
	//elim 0=check constants and vars 1=check constants 2=check vars
	public boolean conventionFollowed(int elim)
	{
		if(elim==0)
		{
			if(cons && vrs)
			{
				message="All Naming Conventions Satisfied!";
				pntsprcnt=1;
				return true;
			}
			else if(cons)
			{
				message="Some Naming Conventions Satisfied! Check your Variable: "+badVars+".";
				pntsprcnt=0.5;
				return true;
			}
			else if(vrs)
			{
				message="Some Naming Conventions Satisfied! Check your Constant: "+badConstant+".";
				pntsprcnt=0.5;
				return true;
			}
		}
		else if(elim==1)
		{
			if(cons)
			{
				message="Naming Conventions Satisfied!";
				pntsprcnt=1;
				return true;
			}
			else
			{
				message="Naming Conventions NOT Satisfied!\nPlease check your Constant: "+badConstant+".";
				pntsprcnt=0;
				return false;
			}
		}
		else if(elim==2)
		{
			if(vrs)
			{
				message="Naming Conventions Satisfied!";
				pntsprcnt=1;
				return true;
			}
			else
			{
				message="Naming Conventions NOT Satisfied!\nPlease check your Constant: "+badVars+".";
				pntsprcnt=0;
				return false;
			}
		}
		message="Naming Conventions NOT Satisfied!\n";
		pntsprcnt=0;
		return false;
	}
}
