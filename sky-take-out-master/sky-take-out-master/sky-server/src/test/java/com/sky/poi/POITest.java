package com.sky.poi;

import com.sky.SkyApplication;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @Description POITest
 * @Author songyu
 * @Date 2023-10-09
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//由于当前springboot环境里面有websocket必须有监听的端口处理请求，所以单元测试定义随机端口便于测试
public class POITest {


    //目标：创建内存的excel文件写出到磁盘d:/itcast.xlsx
    @Test
    public void testWrite() throws Exception {

        //1.创建工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();

        //2.创建工作表
        XSSFSheet sheet = workbook.createSheet("itcast");

        //3.创建行、单元格、写入数据
        //创建第一行，写入单元格下标1“姓名”，单元格下标2“地址”
        XSSFRow row = sheet.createRow(0);
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("地址");
        //创建第二行，写入单元格下标1“张三”，单元格下标2“广州”
        row = sheet.createRow(1);
        row.createCell(1).setCellValue("张三");
        row.createCell(2).setCellValue("广州");
        //创建第三行，写入单元格下标1“李四”，单元格下标2“深圳”
        row = sheet.createRow(2);
        row.createCell(1).setCellValue("李四");
        row.createCell(2).setCellValue("深圳");

        //4.创建磁盘输出流
        FileOutputStream outputStream = new FileOutputStream("d:/itcast.xlsx");

        //5.将内存工作薄写入磁盘
        workbook.write(outputStream);

        //6.释放资源
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    //目标：导入磁盘d:/itcast.xlsx文件数据，打印在控制台上
    @Test
    public void testRead() throws Exception {

        //1.获取磁盘文件d:/itcast.xlsx文件输入流
        FileInputStream in = new FileInputStream("d:/itcast.xlsx");

        //2.根据输入流创建工作薄
        XSSFWorkbook workbook = new XSSFWorkbook(in);

        //3.获取里面的工作表，名字“itcast”
        XSSFSheet sheet = workbook.getSheet("itcast");

        //4.获取有数据最大的行号
        int lastRowNum = sheet.getLastRowNum();

        //5.遍历所有行号，读取每一行数据进行打印
        for (int i = 0; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            //获取下标1和2的单元格值
            String name = row.getCell(1).getStringCellValue();
            String address = row.getCell(2).getStringCellValue();
            System.out.println("name="+name+",address="+address);
        }

        //6.释放资源
        in.close();
        workbook.close();

    }
}
