package com.aomygod.library.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public final class GSONUtil {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new DefaultTypeAdapterFactory())
            .registerTypeAdapter(Short.class, new ShortSerializer())
            .registerTypeAdapter(Integer.class, new IntegerSerializer())
            .registerTypeAdapter(Long.class, new LongSerializer())
            .registerTypeAdapter(Float.class, new FloatSerializer())
            .registerTypeAdapter(Double.class, new DoubleSerializer())
            .registerTypeAdapter(Byte.class, new ByteSerializer())
            .registerTypeAdapter(Boolean.class, new BooleanSerializer())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();

    private static class ShortSerializer implements JsonSerializer<Short> {

        @Override
        public JsonElement serialize(Short src, Type type, JsonSerializationContext jsonSerializationContext) {
            if(src == src.intValue()) {
                return new JsonPrimitive(src.intValue());
            } else if(src == src.longValue()) {
                return new JsonPrimitive(src.longValue());
            } else if(src == src.shortValue()) {
                return new JsonPrimitive(src.shortValue());
            } else if(src == src.floatValue()) {
                return new JsonPrimitive(src.floatValue());
            } else if(src == src.doubleValue()) {
                return new JsonPrimitive(src.doubleValue());
            } else if(src == src.byteValue()) {
                return new JsonPrimitive(src.byteValue());
            }
            return new JsonPrimitive(src);
        }
    }

    private static class IntegerSerializer implements JsonSerializer<Integer> {

        @Override
        public JsonElement serialize(Integer src, Type type, JsonSerializationContext jsonSerializationContext) {
            if(src == src.intValue()) {
                return new JsonPrimitive(src.intValue());
            } else if(src == src.longValue()) {
                return new JsonPrimitive(src.longValue());
            } else if(src == src.shortValue()) {
                return new JsonPrimitive(src.shortValue());
            } else if(src == src.floatValue()) {
                return new JsonPrimitive(src.floatValue());
            } else if(src == src.doubleValue()) {
                return new JsonPrimitive(src.doubleValue());
            } else if(src == src.byteValue()) {
                return new JsonPrimitive(src.byteValue());
            }
            return new JsonPrimitive(src);
        }
    }

    private static class LongSerializer implements JsonSerializer<Long> {

        @Override
        public JsonElement serialize(Long src, Type type, JsonSerializationContext jsonSerializationContext) {
            if(src == src.intValue()) {
                return new JsonPrimitive(src.intValue());
            } else if(src == src.longValue()) {
                return new JsonPrimitive(src.longValue());
            } else if(src == src.shortValue()) {
                return new JsonPrimitive(src.shortValue());
            } else if(src == src.floatValue()) {
                return new JsonPrimitive(src.floatValue());
            } else if(src == src.doubleValue()) {
                return new JsonPrimitive(src.doubleValue());
            } else if(src == src.byteValue()) {
                return new JsonPrimitive(src.byteValue());
            }
            return new JsonPrimitive(src);
        }
    }

    private static class FloatSerializer implements JsonSerializer<Float> {

        @Override
        public JsonElement serialize(Float src, Type type, JsonSerializationContext jsonSerializationContext) {
            if(src == src.intValue()) {
                return new JsonPrimitive(src.intValue());
            } else if(src == src.longValue()) {
                return new JsonPrimitive(src.longValue());
            } else if(src == src.shortValue()) {
                return new JsonPrimitive(src.shortValue());
            } else if(src == src.floatValue()) {
                return new JsonPrimitive(src.floatValue());
            } else if(src == src.doubleValue()) {
                return new JsonPrimitive(src.doubleValue());
            } else if(src == src.byteValue()) {
                return new JsonPrimitive(src.byteValue());
            }
            return new JsonPrimitive(src);
        }
    }

    private static class DoubleSerializer implements JsonSerializer<Double> {

        @Override
        public JsonElement serialize(Double src, Type type, JsonSerializationContext jsonSerializationContext) {
            if(src == src.intValue()) {
                return new JsonPrimitive(src.intValue());
            } else if(src == src.longValue()) {
                return new JsonPrimitive(src.longValue());
            } else if(src == src.shortValue()) {
                return new JsonPrimitive(src.shortValue());
            } else if(src == src.floatValue()) {
                return new JsonPrimitive(src.floatValue());
            } else if(src == src.doubleValue()) {
                return new JsonPrimitive(src.doubleValue());
            } else if(src == src.byteValue()) {
                return new JsonPrimitive(src.byteValue());
            }
            return new JsonPrimitive(src);
        }
    }

    private static class ByteSerializer implements JsonSerializer<Byte> {

        @Override
        public JsonElement serialize(Byte src, Type type, JsonSerializationContext jsonSerializationContext) {
            if(src == src.intValue()) {
                return new JsonPrimitive(src.intValue());
            } else if(src == src.longValue()) {
                return new JsonPrimitive(src.longValue());
            } else if(src == src.shortValue()) {
                return new JsonPrimitive(src.shortValue());
            } else if(src == src.floatValue()) {
                return new JsonPrimitive(src.floatValue());
            } else if(src == src.doubleValue()) {
                return new JsonPrimitive(src.doubleValue());
            } else if(src == src.byteValue()) {
                return new JsonPrimitive(src.byteValue());
            }
            return new JsonPrimitive(src);
        }

    }

    private static class BooleanSerializer implements JsonSerializer<Boolean> {

        @Override
        public JsonElement serialize(Boolean src, Type type, JsonSerializationContext jsonSerializationContext) {
            if(src == src.booleanValue()) {
                return new JsonPrimitive(String.valueOf(src.booleanValue()));
            }
            return new JsonPrimitive(src);
        }
    }


    //=================================================================================
    private static class DefaultTypeAdapterFactory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> token) {
            Class<T> type = (Class<T>) token.getRawType();
            //Log.e("test","------------------->>"+type.name);
            if (type == String.class) {
                return (TypeAdapter<T>) new StringTypeAdapter();

            } else if (type == Boolean.class) {
                return (TypeAdapter<T>) new BooleanTypeAdapter();

            }else if(type == Double.class){
                return (TypeAdapter<T>) new DoubleTypeAdapter();

            }else if(type == Float.class){
                return (TypeAdapter<T>) new FloatTypeAdapter();

            }else if(type == Integer.class){
                return (TypeAdapter<T>) new IntegerTypeAdapter();

            }else if(type == Long.class){
                return (TypeAdapter<T>) new LongTypeAdapter();

            }
            //Log.e("test","+++++++++++++++++++++++"+type.name);
            return null;
        }
    }


    private static final class StringTypeAdapter extends TypeAdapter<String> {
        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
            }else{
                writer.value(value);
            }
        }

        @Override
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                return "";

            } else if (reader.peek() == JsonToken.BOOLEAN) {
                return String.valueOf(reader.nextBoolean());

            } else if (reader.peek() == JsonToken.NUMBER) {
                String str = null;
                try {
                    str = String.valueOf(reader.nextInt());
                } catch (Exception e) {}
                try {
                    str = String.valueOf(reader.nextLong());
                } catch (Exception e) {}
                try {
                    str = String.valueOf(reader.nextDouble());
                } catch (Exception e) {}
                if (str == null) {
                    return "0";
                }
                return str;
            }
            return reader.nextString();
        }
    }


    /**
     * 布尔型
     */
    private static final class BooleanTypeAdapter extends TypeAdapter<Boolean> {
        @Override
        public void write(JsonWriter writer, Boolean value) throws IOException {
            if (value == null) {
                writer.value(false);
            }else{
                writer.value(value);
            }
        }
        @Override
        public Boolean read(JsonReader reader) throws IOException {
            Boolean result = null;
            JsonToken token=reader.peek();
            if (token == JsonToken.STRING) {
                try{
                    result=Boolean.valueOf(reader.nextString());
                }catch (Exception e){
                    return false;
                }

            }else if(token==JsonToken.BOOLEAN){
                result=reader.nextBoolean();
            }
            return null==result?false:result;
        }
    }

    /**
     * 双精度浮点数
     */
    private static final class DoubleTypeAdapter extends TypeAdapter<Double> {
        @Override
        public void write(JsonWriter writer, Double value) throws IOException {
            //序列化bean to json
            if (value == null) {
                writer.value(false);
            }else{
                writer.value(value);
            }
        }

        @Override
        public Double read(JsonReader reader) throws IOException {
            //反序列化json to bean
            //不匹配的类型的才会在这里解析
            Double result = null;
            JsonToken token=reader.peek();
            if (token == JsonToken.STRING) {
                try{
                    result=Double.valueOf(reader.nextString());
                }catch (Exception e){
                    return 0d;
                }
            }else if(token == JsonToken.NUMBER){
                result=reader.nextDouble();
            }
            return null==result?0d:result;
        }
    }

    /**
     * 单精度浮点数
     */
    private static final class FloatTypeAdapter extends TypeAdapter<Float> {
        @Override
        public void write(JsonWriter writer, Float value) throws IOException {
            //序列化bean to json
            if (value == null) {
                writer.value(0f);
            }else{
                writer.value(value);
            }
        }

        @Override
        public Float read(JsonReader reader) throws IOException {
            //反序列化json to bean
            //不匹配的类型的才会在这里解析
            Float result = null;
            JsonToken token=reader.peek();
            if (token == JsonToken.STRING) {
                try{
                    result=Float.valueOf(reader.nextString());
                }catch (Exception e){
                    return 0f;
                }
            }else if(token == JsonToken.NUMBER){
                try{
                    result=Float.valueOf(String.valueOf(reader.nextDouble()));
                }catch (Exception e){
                    return 0f;
                }

            }
            return null==result?0f:result;
        }
    }


    /**
     * 整形
     */
    private static final class IntegerTypeAdapter extends TypeAdapter<Integer> {
        @Override
        public void write(JsonWriter writer, Integer value) throws IOException {
            //序列化bean to json
            if (value == null) {
                writer.value(0);
            }else{
                writer.value(value);
            }
        }

        @Override
        public Integer read(JsonReader reader) throws IOException {
            //反序列化json to bean
            //不匹配的类型的才会在这里解析
            Integer result = null;
            JsonToken token=reader.peek();
            if (token == JsonToken.STRING) {
                try{
                    result=Integer.valueOf(reader.nextString());
                }catch (Exception e){
                    result=0;
                }

            }else if(token == JsonToken.NUMBER){
                result=reader.nextInt();
            }
            return null==result?0:result;
        }
    }


    /**
     * 长整型
     */
    private static final class LongTypeAdapter extends TypeAdapter<Long> {
        @Override
        public void write(JsonWriter writer, Long value) throws IOException {
            //序列化bean to json
            if (value == null) {
                writer.value(0L);
            }else{
                writer.value(value);
            }
        }

        @Override
        public Long read(JsonReader reader) throws IOException {
            //反序列化json to bean
            //不匹配的类型的才会在这里解析
            Long result = null;
            JsonToken token=reader.peek();
            if (token == JsonToken.STRING) {
                try{
                    result=Long.valueOf(reader.nextString());
                }catch (Exception e){
                    return 0L;
                }

            }else if(token == JsonToken.NUMBER){
                result=reader.nextLong();
            }
            return null==result?0L:result;
        }
    }



}
