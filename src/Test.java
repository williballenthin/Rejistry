import com.williballenthin.rejistry.Cell;
import com.williballenthin.rejistry.HBIN;
import com.williballenthin.rejistry.RegistryHiveFile;
import com.williballenthin.rejistry.RegistryParseException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;


public class Test {

    /**
     * This is silly, but it formats the same way Python does by default.
     * @param b
     * @return
     */
    private static String getBooleanString(boolean b) {
        if (b) {
            return "True";
        } else {
            return "False";
        }
    }

    private static DateFormat isoformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    private static String getDatetimeString(GregorianCalendar c) {
        return isoformat.format(c.getTime());
    }

    /**
     * @param args
     * @throws IOException
     * @throws RegistryParseException
     */
    public static void main(String[] args) throws IOException, RegistryParseException {
        File f = new File(args[0]);
        RegistryHiveFile reg = new RegistryHiveFile(f);

        System.out.println("hive name: " + reg.getHeader().getHiveName());
        System.out.println("major version: " + reg.getHeader().getMajorVersion());
        System.out.println("minor version: " + reg.getHeader().getMinorVersion());
        int count = 0;
        for (Iterator<HBIN> it = reg.getHeader().getHBINs(); it.hasNext(); it.next()) {
            count++;
        }
        System.out.println("number of hbins: " + count);
        System.out.println("last hbin offset: " + reg.getHeader().getLastHbinOffset());

        int i = 0;
        Iterator<HBIN> it = reg.getHeader().getHBINs();
        while (it.hasNext()) {
            HBIN hbin = it.next();
            System.out.println("hbin " + i + ", relative offset first hbin: " + hbin.getRelativeOffsetFirstHBIN());
            System.out.println("hbin " + i + ", relative offset next hbin: " + hbin.getRelativeOffsetNextHBIN());

            int j = 0;
            Iterator<Cell> ic = hbin.getCells();
            while (ic.hasNext()) {
                Cell cell = ic.next();
                if (cell.isActive()) {
                    System.out.println("hbin " + i + ", cell " + j + ", is allocated: yes");
                } else {
                    System.out.println("hbin " + i + ", cell " + j + ", is allocated: no");
                }
                System.out.println("hbin " + i + ", cell " + j + ", length: " + cell.getLength());
                j++;
            }

            i++;

            //break; // TODO(wb): removeme
        }
        System.out.println("root nkrecord has classname: " + getBooleanString(reg.getHeader().getRootNKRecord().hasClassname()));
        System.out.println("root nkrecord classname: " + reg.getHeader().getRootNKRecord().getClassname());
        System.out.println("root nkrecord timestamp: " + getDatetimeString(reg.getHeader().getRootNKRecord().getTimestamp()));
        System.out.println("root nkrecord is root: " + getBooleanString(reg.getHeader().getRootNKRecord().isRootKey()));
    }
}
