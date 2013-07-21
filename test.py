import re
import argparse
from Registry import Registry


def printNKRecord(record, prefix):
    print prefix + "nkrecord has classname: %s" % (record.has_classname())
    print prefix + "nkrecord classname: %s" % (record.classname())
    print hex(record.unpack_word(0x4A))
    print prefix + "nkrecord timestamp: %s" % (record.timestamp().isoformat("T") + "Z")
    print prefix + "nkrecord is root: %s" % (record.is_root())
    print prefix + "nkrecord name: %s" % (record.name())
    print prefix + "nkrecord has parent: %s" % (record.has_parent_key())
    print prefix + "nkrecord number of values: %d" % (record.values_number())
    print prefix + "nkrecord number of subkeys: %d" % (record.subkey_number())


def recurseNKRecord(record, prefix):
    printNKRecord(record, prefix)

    if record.subkey_number() == 0:
        return
    for r in record.subkey_list().keys():
        print "  " + prefix + r.name()
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
        #break
    printNKRecord(reg._regf.first_key(), "root ")
    for record in reg._regf.first_key().subkey_list().keys():
        print "  " + record.name()
        printNKRecord(record, "    ")

    recurseNKRecord(reg._regf.first_key(), "")

if __name__ == "__main__":
    main()
