package com.ds.bluetooth;

//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Environment;

public class Tools 
{
	//private final static String ALBUM_PATH  = Environment.getExternalStorageDirectory() + "/yueqiu/";
	
    public  int dip2px(Context context, float dpValue) 
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
   
    public  int px2dip(Context context, float pxValue) 
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    
    
    public ArrayList<Double> getintfrommap(HashMap<Double, Double> map)
    {
    	ArrayList<Double> dlk=new ArrayList<Double>();
    	int position=0;
    	@SuppressWarnings("rawtypes")
		Set set= map.entrySet();   
        @SuppressWarnings("rawtypes")
		Iterator iterator = set.iterator();
 
		 while(iterator.hasNext())
		{   
			@SuppressWarnings("rawtypes")
			Map.Entry mapentry  = (Map.Entry)iterator.next();   
			dlk.add((Double)mapentry.getKey());
		} 
		 for(int i=0;i<dlk.size();i++)
		 {
			 int j=i+1;  
	            position=i;  
	            Double temp=dlk.get(i);  
	            for(;j<dlk.size();j++)
	            {  
	            	if(dlk.get(j)<temp)
	            	{  
	            		temp=dlk.get(j);  
	            		position=j;  
	            	}  
	            }  
	            
	            dlk.set(position,dlk.get(i)); 
	            dlk.set(i,temp);  
		 }
		return dlk;
    	
    }

    /**
     * ����ͼƬ
     * @param bm
     * @param fileName
     * @throws IOException
     */

}
