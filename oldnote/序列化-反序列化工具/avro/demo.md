* 为什么要选avro
  * 1.flume支持avro的sink,source
  * 2.kafka支持avro的序列化方式
  * 3.spark支持avro的序列化方式
  * avro有支持多种语言的：c, c++, python, java



因为数据主要是发送打kafka的，而发送的数据的方式有：Python，或者是java，所以要有下面的方式的支持

todo list

- [x] java版的demo
- [x] Python版的demo
- [x] kafka的消费者demo
- [x] kafka的生产者demo
- [ ] spark的消费demo
- [ ] 测试代码（说明序列化之后的性能和存储空间比较）
- [ ] 将netflow的数据，作为发送的数据格式进行测试



[TOC]



### 1.java版的demo 



#### 1.1.Generic的方式

```java
package cn.avro.test;

import cn.domain.avro.User;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by landun on 2018/7/10.
 */
public class AvroGenericDemo {


    public static void main(String[] args) {

        // 1.首先通过user.avsc生成一个User 类（这一步通过maven可以自动生成）
        Schema schema = null;
        try {
            schema = new Schema.Parser().parse(new File("E:\\test-workspace\\avroseriable\\src\\main\\java\\avro\\user.avsc"));
            GenericRecord user1 = new GenericData.Record(schema);
            user1.put("name", "test");
            user1.put("favorite_number", 256);

            // 2.序列化
            byte[] byteObj = seriableObject(schema, user1);
            // 反序列化
            GenericRecord record = deseriableObject(schema, byteObj);
            System.out.println(record.get("name") + ":" + record.get("favorite_number"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 反序列化( byte -> obj )
    public static GenericRecord deseriableObject(Schema schema, byte[] byteObj){
        try {
            DatumReader<GenericRecord> reader=new GenericDatumReader<GenericRecord>(schema);
            Decoder decoder=DecoderFactory.get().binaryDecoder(byteObj,null);
            GenericRecord result=reader.read(null,decoder);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 序列化( obj -> byte )
    public static byte[] seriableObject(Schema schema, GenericRecord obj){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            //DatumWriter可以将GenericRecord变成edncoder可以理解的类型
            DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
            //encoder可以将数据写入流中，binaryEncoder第二个参数是重用的encoder，这里不重用，所用传空
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            writer.write(obj,encoder);
            encoder.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("serializeUser==" + outputStream.toByteArray().length);

        return outputStream.toByteArray();
    }
}

```



#### 1.2.Specific的方式

```java
package cn.avro.test;

import cn.domain.avro.User;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by landun on 2018/7/10.
 */
public class AvroSpecificDemo {

    public static void main(String[] args) {

        // 1.首先通过user.avsc生成一个User 类（这一步通过maven可以自动生成）
        User user2 = new User();
        user2.setName("test");
        user2.setFavoriteNumber(256);

        // 序列化和反序列化
        byte[] byteObj = seriableObject(user2);
        User newUser = deseriableObject(byteObj, user2.getClass());
        System.out.println(newUser.getName() + ":" + newUser.getFavoriteNumber());

    }

    // 反序列化( byte -> obj )
    public static <T> T deseriableObject(byte[] byteObj, Class cls){
        T result= null;
        try {
            DatumReader<T> reader=new SpecificDatumReader(cls);
            Decoder decoder= DecoderFactory.get().binaryDecoder(byteObj,null);
            result = reader.read(null,decoder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    // 序列化( obj -> byte )
    public static <T> byte[] seriableObject(T obj){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            DatumWriter<T> userDatumWriter = new SpecificDatumWriter(obj.getClass());
            // 这里并不是按照官网的demo来的，官网的demo序列化之后很大（官网是通过DataFileReader 写入文件的）
            BinaryEncoder binaryEncoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
            userDatumWriter.write(obj, binaryEncoder);

            binaryEncoder.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("serializeUser==" + outputStream.toByteArray().length);

        return outputStream.toByteArray();
    }

}

```







### 2.Python版的demo



Python里用avro序列化比较自由，可以直接序列化JSON字符串。例如：

```
def test_serialize(schema_file, json_string):
    '''read json string, return one serialized avro record'''
    schema = avro.schema.parse(open(schema_file).read())
    writer = StringIO()
    encoder = avro.io.BinaryEncoder(writer)
    datum_writer = avro.io.DatumWriter(schema)
    datum = json.loads(json_string)
    datum_writer.write(datum, encoder)
    return writer.getvalue()
```

直接使用 `avro.io.DatumReader` 和 `avro.io.DatumWriter` 还是得小心，因为这种做法分离了序列化的record和schema。更常用的方法还是文件级的 `avro.datafile.DataFileReader` 和 `avro.datafile.DataFileWriter`，这样可以保证schema和序列化结果保存在一起。





### 3.kafka的生产者demo

```

```



### 4.kafka的消费者demo

### 5.spark的消费demo

### 6.测试代码（说明序列化之后的性能和存储空间比较）

### 7.将netflow的数据，作为发送的数据格式进行测试





这里有两种方式：

1. 直接生成类，然后通过类序列化（SpecificDatumWriter）
2. 动态的生成类，然后序列化（generic 方式）



官网提供的demo是序列化之后，并没有多少减少，官网写的是：









参考：

https://blog.csdn.net/lastsweetop/article/details/9773233   （有压缩的效果）

https://blog.csdn.net/qq_26182553/article/details/75048394 （和官网类似，但是没有压缩的效果）

http://avro.apache.org/docs/1.8.2/gettingstartedjava.html  （官网）

https://www.iteblog.com/archives/1008.html  (maven设置可以参考)

https://yanbin.blog/apache-avro-serializing-deserializing/#more-7488

https://yanbin.blog/kafka-produce-consume-avro-data/ 	(kafka的生产者，消费者)

