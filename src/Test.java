import com.williballenthin.rejistry.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.SimpleTimeZone;


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

    private static String getDatetimeString(Calendar c) {
        DateFormat isoformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
        isoformat.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        return isoformat.format(c.getTime());
    }

    private static void printNKRecord(NKRecord record, String prefix) throws IOException, RegistryParseException {
        System.out.println(prefix + "nkrecord has classname: " + getBooleanString(record.hasClassname()));
        System.out.println(prefix + "nkrecord classname: " + record.getClassname());
        System.out.println(prefix + "nkrecord timestamp: " + getDatetimeString(record.getTimestamp()));
        System.out.println(prefix + "nkrecord is root: " + getBooleanString(record.isRootKey()));
        System.out.println(prefix + "nkrecord name: " + record.getName());
        System.out.println(prefix + "nkrecord has parent: " + getBooleanString(record.hasParentRecord()));
        System.out.println(prefix + "nkrecord number of values: " + record.getNumberOfValues());
        System.out.println(prefix + "nkrecord number of subkeys: " + record.getSubkeyCount());
    }

    private static void recurseNKRecord(NKRecord record, String prefix) throws IOException, RegistryParseException {
        printNKRecord(record, prefix);

        Iterator<NKRecord> nkit = record.getSubkeyList().getSubkeys();
        while (nkit.hasNext()) {
            NKRecord r = nkit.next();
            System.out.println("  " + prefix + r.getName());
            recurseNKRecord(r, "    " + prefix);
        }
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
        printNKRecord(reg.getHeader().getRootNKRecord(), "root ");

        Iterator<NKRecord> nkit = reg.getHeader().getRootNKRecord().getSubkeyList().getSubkeys();
        while (nkit.hasNext()) {
            NKRecord record = nkit.next();
            System.out.println("  " + record.getName());
            printNKRecord(record, "    ");
        }

        recurseNKRecord(reg.getHeader().getRootNKRecord(), "");
    }
}
