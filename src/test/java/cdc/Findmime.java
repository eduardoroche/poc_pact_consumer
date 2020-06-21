package cdc;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.IntByReference;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Findmime {
    public interface Kernel32 extends Library {
        // FREQUENCY is expressed in hertz and ranges from 37 to 32767
        // DURATION is expressed in milliseconds
        public boolean Beep(int FREQUENCY, int DURATION);

        public void Sleep(int DURATION);


        public Integer FindMimeFromData(Integer pBC, String pwzUrl, byte[] pBuffer, Integer cbSize, String pwzMimeProposed, Integer dwMimeFlags, int[] ppwzMimeOut, Integer dwReserverd);
    }

    public static void main(String[] args) throws IOException {
        Kernel32 lib = (Kernel32) Native.loadLibrary(
                "urlmon",
                Kernel32.class);
        Integer mime = 0;

        int [] outmime = {0} ;     // simulated pointer to String

//682453040
        //682224880
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(new File("D:\\JAVA_CODE\\pactconsumer\\result.zip")));
        System.out.println(lib.FindMimeFromData(0, null, bytes, 256, null, 0x20, outmime, 0));
        System.out.println(outmime[0]);
        IntByReference ibr = new IntByReference(outmime[0]);
        //StringArray dataBuffer = new StringArray();
        System.out.println(ibr.getPointer().getString(4, true));
        byte[] dataByteArray = new byte[ibr.getValue()];
       // System.arraycopy( dataBuffer, 0, dataByteArray, 0, ibr.getValue());
        String data = new String(dataByteArray, "UTF-8");
        //lib.Beep(698, 500);
        //lib.Sleep(500);
        //lib.Beep(698, 500);
    }

    private static native int FindMimeFromData(String prt, String URL,
                     byte[] pbuff, int bufsize, String prop, int dmimeflag,
                     int[] pout, int res);
}
