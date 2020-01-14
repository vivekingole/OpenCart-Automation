package com.cg.demo.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Table.Cell;


public class ExcelReader 
{
	public  FileInputStream fis;
	public  Workbook wb;
	public  Sheet sheet;
	public  Row row;
	public  org.apache.poi.ss.usermodel.Cell cell;
	public  FileOutputStream fos;
	public  CellStyle style;
	public int rowcount;
	public int colcount;
	public String filename,sheetname;
	public int excludeCount;
	
	// Constructor
	public ExcelReader(String filename, String sheetname,int excludeCount) {
		initialize(filename, sheetname,excludeCount);
		refresh();
	}
	
	// To initialize Excel file properties
	public void initialize(String filename, String sheetname,int excludeCount) {
		try {
			fis = new FileInputStream(filename);
			wb = new XSSFWorkbook(fis);
			sheet = wb.getSheet(sheetname);
			rowcount =	sheet.getLastRowNum();			
			Row r = sheet.getRow(0);
			colcount = r.getLastCellNum();
			colcount-=excludeCount; // Because we want to exclude status column
			this.filename = filename;
			this.sheetname = sheetname;
			this.excludeCount = excludeCount;
			for(int i=1;i<=rowcount;i++)
				setCellData(i, colcount, "Hold");
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	// TO get row count
	public int getRowCount()
	{
		return rowcount;
	}

	// TO get col count
	public int getColCount()
	{
		return colcount;
	}
	
	// To get cell data from given row and column index
	public String getCellData(int Row, int Cell) throws IOException
	{
		String CellData="";
		try{
	    row = sheet.getRow(Row);
	    cell = row.getCell(Cell);
		CellData = cell.getStringCellValue();
		return CellData;
		}catch(NullPointerException e){
			return CellData;
		}
	}
	
	// To set cell data
	public void setCellData(int Row, int Cell, String Data) 
	{
		try {
			row = sheet.getRow(Row);
			cell = row.createCell(Cell);
			cell.setCellValue(Data);
			fos = new FileOutputStream(filename);
			wb.write(fos);
			//wb.close();
			//fis.close();
			//fos.close();
			//refresh(); // After performing writing operation it will update all ExcelDataHelper class fields.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refresh() {
		initialize(filename, sheetname,excludeCount);		
	}

	// TO fill given cell as given color
	public void fillColor(int Row, int Cell,short color)
	{
		try {
			row = sheet.getRow(Row);
			cell = row.getCell(Cell);
			style = wb.createCellStyle();
			style.setFillForegroundColor(color);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cell.setCellStyle(style);
			fos = new FileOutputStream(filename);
			wb.write(fos);
			wb.close();
			fis.close();
			fos.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// To get all data as Object Array
	public Object[][] getAllData(){
		Object data[][];
		try {				
		data = new Object[rowcount][colcount];
		for(int i=1; i <= rowcount ; i++){
			for(int j=0 ; j<colcount; j++){
					data[i-1][j] = getCellData(i, j);					
			}
		}
		return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String aep[]){
		String projectpath = System.getProperty("user.dir");
		String filepath = projectpath + "/testdata/";
		String filename = "TestData.xlsx";
		String sheetname = "Address";		
		ExcelReader reader = new ExcelReader(filepath+filename, sheetname,1);
		reader.getAllData();
	}
	
	
}
