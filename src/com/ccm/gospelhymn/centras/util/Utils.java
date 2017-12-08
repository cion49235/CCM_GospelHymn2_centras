package com.ccm.gospelhymn.centras.util;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.view.WindowManager;
 
public class Utils {
	public static String data = "doslvhxps^^;";
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        @SuppressWarnings("deprecation")
        int displayHeight = wm.getDefaultDisplay().getHeight();
        return displayHeight;
    }
}