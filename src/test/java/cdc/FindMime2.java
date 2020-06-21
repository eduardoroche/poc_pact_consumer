package cdc;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;

class FindMime2 {
    public static void main(String args[]) throws Exception {

        FileInputStream fis = new FileInputStream(new File("D:\\security\\poc\\Presentation1.ppt"));
        byte[] bytes = IOUtils.toByteArray(fis);

        fis = new FileInputStream(new File("D:\\security\\poc\\advinst.msi"));
        byte[] bytes2 = IOUtils.toByteArray(fis);

        fis = new FileInputStream(new File("D:\\security\\poc\\result.zip"));
        bytes = IOUtils.toByteArray(fis);

        if(true) {
            return;
        }

        int [] outmime = {0} ;     // simulated pointer to String

        if(args.length==0){
            System.out.println("Syntax:  jview FindMime <file name>") ;
            return ;
        }
        String url = args[0] ;

        int result = FindMimeFromData(null, url ,
                null, 1024, null, 0,
                outmime, 0) ;

        System.out.println("FindMimeFromData() returned:  0x" + Integer.toHexString(result)) ;
        if(result == 0x80004005)
            System.out.println("Failed to find MIME type") ;
        else {
            // System.out.println("Pointer to outMIME: " + outmime[0]) ;
            //String outstr = DllLib.ptrToStringUni(outmime[0]) ;
           // System.out.println("MIME Type: " + outstr) ;
        }
    }


    /** @dll.import("URLMON.DLL", unicode) */
    private static native int
    FindMimeFromData(String prt, String URL,
                     byte[] pbuff, int bufsize, String prop, int dmimeflag,
                     int[] pout, int res);
}
