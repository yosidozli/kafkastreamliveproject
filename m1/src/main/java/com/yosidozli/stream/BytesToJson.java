package com.yosidozli.stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BytesToJson implements Function<ByteBuffer, List<Map<String, Object>>> {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<Map<String,Object>> TR =new TypeReference<Map<String, Object>>() {};
    @Override
    public List<Map<String, Object>> apply(ByteBuffer byteBuffer) {
        List<Map<String,Object>> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteBufferBackedInputStream(byteBuffer)));

        try {
            String line = reader.readLine();

            while (line != null) {
                Map<String,Object> map = mapper.readValue(line, TR);
                list.add(map);
                line = reader.readLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
