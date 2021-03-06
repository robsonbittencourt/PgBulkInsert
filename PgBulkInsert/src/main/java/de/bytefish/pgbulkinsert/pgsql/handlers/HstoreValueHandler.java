// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.pgbulkinsert.pgsql.handlers;

import de.bytefish.pgbulkinsert.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Map;

public class HstoreValueHandler extends BaseValueHandler<Map<String, String>> {

    @Override
    protected void internalHandle(DataOutputStream buffer, final Map<String, String> value) throws Exception {

        // Write into a Temporary ByteArrayOutputStream:
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();

        // And wrap it in a DataOutputStream:
        DataOutputStream hstoreOutput = new DataOutputStream(byteArrayOutput);

        // First the Amount of Values to write:
        hstoreOutput.writeInt(value.size());

        // Now Iterate over the Array and write each value:
        for (Map.Entry<String, String> entry : value.entrySet()) {
            // Write the Key:
            writeKey(hstoreOutput, entry.getKey());
            // The Value can be null, use a different method:
            writeValue(hstoreOutput, entry.getValue());
        }

        // Now write the entire ByteArray to the COPY Buffer:
        buffer.writeInt(byteArrayOutput.size());
        buffer.write(byteArrayOutput.toByteArray());
    }

    private void writeKey(DataOutputStream buffer, String key) throws Exception {
        writeText(buffer, key);
    }

    private void writeValue(DataOutputStream buffer, String value) throws Exception {
        if(value == null) {
            buffer.writeInt(-1);
        } else {
            writeText(buffer, value);
        }
    }

    private void writeText(DataOutputStream buffer, String text) throws Exception {
        byte[] textBytes = StringUtils.getUtf8Bytes(text);

        buffer.writeInt(textBytes.length);
        buffer.write(textBytes);
    }
}
