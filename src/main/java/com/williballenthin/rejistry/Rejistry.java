package com.williballenthin.rejistry;

import com.williballenthin.rejistry.record.NKRecord;
import com.williballenthin.rejistry.record.VKRecord;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.SimpleTimeZone;


public class Rejistry {

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

    private static void printVKRecord(VKRecord record, String prefix) throws RegistryParseException, UnsupportedEncodingException {
        System.out.println(prefix + "vkrecord has name: " + getBooleanString(record.hasName()));
        System.out.println(prefix + "vkrecord has ascii name: " + getBooleanString(record.hasAsciiName()));
        System.out.println(prefix + "vkrecord name: " + record.getName());
        System.out.println(prefix + "vkrecord value type: " + record.getValueType().toString());
        System.out.println(prefix + "vkrecord data length: " + record.getDataLength());

        ValueData data = record.getValue();
        char[] padding = new char["vkrecord data: ".length()];
        Arrays.fill(padding, ' ');
        switch(data.getValueType()) {
            case REG_SZ:
            case REG_EXPAND_SZ:
                System.out.println(prefix + "vkrecord data: " + data.getAsString());
                break;

            case REG_MULTI_SZ: {
                StringBuilder sb = new StringBuilder();
                Iterator<String> it = data.getAsStringList().iterator();
                if (it.hasNext()) {
                    String s = it.next();
                    sb.append(s);
                }
                while (it.hasNext()) {
                    sb.append("\n" + prefix + new String(padding));
                    String s = it.next();
                    sb.append(s);
                }
                System.out.println(prefix + "vkrecord data: " + sb.toString());
                break;
            }

            case REG_DWORD:
            case REG_QWORD:
            case REG_BIG_ENDIAN:
                System.out.println(prefix + "vkrecord data: " + String.format("0x%x", data.getAsNumber()));
                break;

            default: {
                String s = HexDump.dumpHexString(data.getAsRawData());
                s = s.replace("\n", "\n" + prefix + new String(padding));
                System.out.println(prefix + "vkrecord data: " + s);
            }
        }
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
        Iterator<VKRecord> vkit = record.getValueList().getValues();
        while (vkit.hasNext()) {
            VKRecord r = vkit.next();
            System.out.println(prefix + "  value: " + r.getName());
            printVKRecord(r, "    " + prefix);
        }
    }

    private static void recurseNKRecord(NKRecord record, String prefix) throws IOException, RegistryParseException {
        printNKRecord(record, prefix);

        Iterator<NKRecord> nkit = record.getSubkeyList().getSubkeys();
        while (nkit.hasNext()) {
            NKRecord r = nkit.next();
            System.out.println(prefix + "  key: " + r.getName());
            recurseNKRecord(r, "    " + prefix);
        }
    }

    private static void printUsage() {
        System.out.println("usage: java -cp Rejistry[...].jar Rejistry <hive file path>");
    }

    /**
     * @param args
     * @throws IOException
     * @throws RegistryParseException
     */
    public static void main(String[] args) throws IOException, RegistryParseException {
        if (args.length != 1) {
            printUsage();
            System.exit(1);
        }
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
