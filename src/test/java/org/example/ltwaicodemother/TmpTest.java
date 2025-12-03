package org.example.ltwaicodemother;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


public class TmpTest {

    @Test
    public void test() throws IOException {
        File file =new File("C:\\Users\\Lenovo\\Desktop\\ltw-ai-code-mother\\tmp\\code_deploy\\index.html");
        System.out.println(file.isFile());
        System.out.println(file.isDirectory());
        System.out.println(file.canRead());
        if (file.createNewFile()) {
            System.out.println("文件创建成功");
        }
    }
}
