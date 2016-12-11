package helloworld.actions;

import java.io.FileOutputStream;
import java.io.IOException;

public class varWriter 
{
	public varWriter(String[] txt, String f) throws IOException
	{
		FileOutputStream fos=null;
		try 
		{
			fos=new FileOutputStream(f);
            
            int start=0;
            for(int i=0; i<txt.length; i++)
            {
            	String val=txt[i];
            	if(f.equalsIgnoreCase("evalcat.txt"))
            	{
            		if(val.equalsIgnoreCase("y"))
            		{
            			val="true";
            		}
            		else if(val.equalsIgnoreCase("n"))
            		{
            			val="false";
            		}
            		else
            		{
            			val="null";
            		}
            	}
            	
            	if(val==null)
            	{
            		val="null";
            	}
            	byte[] a=val.getBytes();
            	fos.write(a, start, (val.length()));
            	fos.write(System.getProperty("line.separator").getBytes());
            	fos.flush();
            }
			
		}
		catch(IOException y)
		{
			System.out.println(y);
		}
		finally 
		{
			if(fos!=null)
				fos.close();
		}
	}
}

