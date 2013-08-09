import re
import argparse
from Registry import Registry

def hexdump(src, length=16):
    """
    no trailing newline
    """
    if len(src) == 0:
        return "0x%08X                                                 " % 0
    FILTER = ''.join([((len(repr(chr(x))) == 3) or chr(x) == "\\") and chr(x) or '.' for x in range(256)])
    lines = []
    for c in xrange(0, len(src), length):
        chars = src[c:c+length]
        hex = ' '.join(["%02X" % ord(x) for x in chars])
        printable = ''.join(["%s" % ((ord(x) <= 127 and FILTER[ord(x)]) or '.') for x in chars])
        lines.append("0x%08X %-*s%s\n" % (c, length*3, hex, printable))
    return ''.join(lines)[:-1]


def fix_type(s):
    map = {"RegSZ": "REG_SZ", "RegExpandSZ": "REG_EXPAND_SZ", "RegBin": "REG_BIN", "RegNone": "REG_NONE", 
           "RegDWord": "REG_DWORD", "RegQWord": "REG_QWORD", "RegBigEndian": "REG_BIG_ENDIAN", "RegMultiSZ": "REG_MULTI_SZ"}
    return map.get(s, s)

def printVKRecord(record, prefix):
    print prefix + "vkrecord has name: %s" % record.has_name()
    print prefix + "vkrecord has ascii name: %s" % record.has_ascii_name()
    print prefix + "vkrecord name: %s" % record.name()
    print prefix + "vkrecord value type: %s" % fix_type(record.data_type_str())
    print prefix + "vkrecord data length: %s" % record.data_length()
    if record.data_type() == Registry.RegSZ or record.data_type() == Registry.RegExpandSZ:
        print prefix + "vkrecord data: %s" % record.data().encode("utf-8")
    elif record.data_type() == Registry.RegBin or record.data_type() == Registry.RegNone:
        print prefix + "vkrecord data: "
        padding = prefix + (" " * len("vkrecord data: "))
        print padding + hexdump(record.data()).replace("\n", "\n" + padding)
    elif record.data_type() == Registry.RegDWord or record.data_type() == Registry.RegQWord or record.data_type == Registry.RegBigEndian:
        print prefix + "vkrecord data: " + hex(record.data())
    elif record.data_type() == Registry.RegMultiSZ:
        if len(record.data()) == 0:
            print prefix + "vkrecord data: "
        else:
            print prefix + "vkrecord data: " + record.data()[0]
            for s in record.data()[1:]:
                print prefix + (" " * len("vkrecord data: ")) + s
    else:
        print prefix + "vkrecord data: unsupported"


def printNKRecord(record, prefix):
    print prefix + "nkrecord has classname: %s" % (record.has_classname())
    print prefix + "nkrecord classname: %s" % (record.classname())
    print prefix + "nkrecord timestamp: %s" % (record.timestamp().isoformat("T") + "Z")
    print prefix + "nkrecord is root: %s" % (record.is_root())
    print prefix + "nkrecord name: %s" % (record.name())
    print prefix + "nkrecord has parent: %s" % (record.has_parent_key())
    print prefix + "nkrecord number of values: %d" % (record.values_number())
    print prefix + "nkrecord number of subkeys: %d" % (record.subkey_number())
    if record.values_number() > 0:
        for value in record.values_list().values():
            print prefix + "  value: " + value.name()
            printVKRecord(value, "    " + prefix)


def recurseNKRecord(record, prefix):
    printNKRecord(record, prefix)

    if record.subkey_number() == 0:
        return
    for r in record.subkey_list().keys():
        print prefix + "  key: " + r.name()
        recurseNKRecord(r, "    " + prefix)


def main():
    parser = argparse.ArgumentParser(
        description="Registry parsing library cases")
    parser.add_argument("registry_hive", type=str,
                        help="Path to the Windows Registry hive to process")
    args = parser.parse_args()

    reg = Registry.Registry(args.registry_hive)
    print "hive name: %s" % reg._regf.hive_name()
    print "major version: %d" % reg._regf.major_version()
    print "minor version: %d" % reg._regf.minor_version()
    print "number of hbins: %d" % len([c for c in reg._regf.hbins()])
    print "last hbin offset: %d" % reg._regf.last_hbin_offset()
    for i, hbin in enumerate(reg._regf.hbins()):
        ofs_first_hbin = hbin.unpack_dword(0x4)
        print "hbin %d, relative offset first hbin: %d" % (i, ofs_first_hbin)
        print "hbin %d, relative offset next hbin: %d" % \
            (i, hbin._reloffset_next_hbin)
        for j, cell in enumerate(hbin.records()):
            if cell.is_free():
                print "hbin %d, cell %d, is allocated: no" % (i, j)
            else:
                print "hbin %d, cell %d, is allocated: yes" % (i, j)
            print "hbin %d, cell %d, length: %s" % (i, j, cell.size())
    printNKRecord(reg._regf.first_key(), "root ")
    for record in reg._regf.first_key().subkey_list().keys():
        print "  " + record.name()
        printNKRecord(record, "    ")

    recurseNKRecord(reg._regf.first_key(), "")

if __name__ == "__main__":
    main()
