package com.williballenthin.rejistry.com.williballenthin.rejistry.valuetype;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Used for Registry value type REG_MULTI_SZ.
 */
public class MultiStringValueType implements ValueType {
    private static List<String> _l;

    public MultiStringValueType() {
        this._l = new LinkedList<String>();
    }

    public MultiStringValueType(List<String> l) {
        this._l = l;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = this._l.iterator();
        while (it.hasNext()) {
            String s = it.next();
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }
}
