``` java
package com.test.api.util;

import cn.hutool.core.util.ClassUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.eurksys.hbi.api.annotaion.CommonExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @Author: aGeng
 * @Description:
 * @Target:
 * @Date Created in 2022-09-23-15:15
 * @Modified By:
 */
@Slf4j
public class ExcelHandleFactory {
    /**
     * Create a sheet.
     *
     * @param name  SheetName
     * @param clazz This class requires the @ExcelProperty annotation.
     * @param <T>
     * @return
     */
    public static <T> WriteSheet createSheet(String name, Class<T> clazz) {
        WriteSheet sheet = EasyExcel.writerSheet(name)
                .registerWriteHandler(getStyleWrite())
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .head(getHeadTitle(clazz)).build();
        return sheet;
    }

    /**
     * Create a sheet.
     *
     * @param name SheetName
     * @param head Column Headers
     * @param <T>
     * @return WriteSheet
     */
    public static <T> WriteSheet createSheet(String name, List<List<String>> head) {
        WriteSheet sheet = EasyExcel.writerSheet(name)
                .registerWriteHandler(getStyleWrite())
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .head(head).build();
        return sheet;
    }

    /**
     * Create a basic style.
     *
     * @return
     */
    public static HorizontalCellStyleStrategy getStyleWrite() {
        //Header strategy.
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //Set the background to PALE_BLUE.
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 11);
        headWriteCellStyle.setWriteFont(headWriteFont);
        //Content strategy.
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        //Set the grid border style to fine lines.
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        //Set the grid background color to white.
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        //Font size.
        contentWriteFont.setFontHeightInPoints((short) 10);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    /**
     * Get the header.
     *
     * @param clazz This class requires the @ExcelProperty annotation.
     * @param <T>
     * @return
     */
    public static <T> List<List<String>> getHeadTitle(Class<T> clazz) {
        List<List<String>> list = new ArrayList<>();
        for (Field declaredField : ClassUtil.getDeclaredFields(clazz)) {
            CommonExcel annotation = declaredField.getAnnotation(CommonExcel.class);
            if (annotation != null) {
                List<String> item = new ArrayList<String>();
                item.add(annotation.name());
                list.add(item);
            }
        }
        return list;
    }

    /**
     * Set Excel HttpServletResponse.
     *
     * @param response
     * @param fileName
     */
    public static void setHeader(HttpServletResponse response, String fileName) {
        String toFileName = "new_excel_file";
        try {
            toFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            ExcelHandleFactory.log.error("response header fileName error");
        }
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=" + toFileName + ".xlsx");
    }

}

```