import argparse
from Registry import Registry


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
    print "root nkrecord has classname: %s" % (reg._regf.first_key().has_classname())
    print "root nkrecord classname: %s" % (reg._regf.first_key().classname())
    print "root nkrecord timestamp: %s" % (reg._regf.first_key().timestamp().isoformat("T") + "Z")
    print "root nkrecord is root: %s" % (reg._regf.first_key().is_root())

if __name__ == "__main__":
    main()
