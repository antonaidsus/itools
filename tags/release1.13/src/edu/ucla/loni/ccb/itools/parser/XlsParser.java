package edu.ucla.loni.ccb.itools.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.view.GuiUtil;


public class XlsParser {
	private static final Logger logger = Logger.getLogger(XlsParser.class);
	
	protected static Collator collator = Collator.getInstance();
	
   	static {
   		collator.setStrength(Collator.PRIMARY);
   	}
	public static String[] XPATH = {"Name","name"};
	String[] headNames;
	
	public static int getHeaderRowNum(Sheet sheet) {
		for (String v : XPATH) {
			for (int row = 0; row < 2; row++) {
				Cell[] row2 = sheet.getRow(row);
				for (int col = 0; col < row2.length; col++) {
					try {
						if (collator.equals(row2[col].getContents(), v)) {
							return row;
						}
					} catch(Exception e) {
						logger.error(e);
					}
				}
			}
		}
		
		return -1;
	}

   	public static Sheet[] getSheets(File excelFile) {
   		try {
   			return getSheets(new FileInputStream(excelFile));
   		} catch (Exception e) {
   			GuiUtil.showMsg(e.getMessage());
   			return new Sheet[0];
   		}
   	}
   	
   	public static Sheet[] getSheets(InputStream input) {
   		try {
   			Workbook workbook = Workbook.getWorkbook(input);
   			return workbook.getSheets();
   		} catch (Exception e) {
   			GuiUtil.showMsg(e.getMessage());
   			return new Sheet[0];
   		}
   	}
   	
   	public List<NcbcResource> parse(InputStream input) {
   		List<NcbcResource> rv = Collections.emptyList();
   		try {
			Workbook workbook = Workbook.getWorkbook(input);
			Sheet[] sheets = workbook.getSheets();
			Sheet sheet = sheets[0];
			String centerName = sheet.getName();
			int headRow = getHeaderRowNum(sheet);
			headNames = getHeadNames(sheet, headRow);
			int I = sheet.getRows();
			rv = new ArrayList<NcbcResource>(I);
			for ( int i = headRow +1; i < I; i++) {
				Cell[] cells = sheet.getRow(i);
				NcbcResource rsc = new NcbcResource();
				for (int j=0; j < headNames.length; j ++) {
					rsc.putProperty(headNames[j], cells[j].getContents().trim());
				}
				//standardize the rsc
				if (rsc.getCenter() == null) {
					rsc.setCenter(centerName);
				}
				rv.add(rsc);				
			}			
		} catch (Exception e) {
			logger.error("parser Excel file error.", e);
		}
   		return rv;
   	}

	private String[] getHeadNames(Sheet sheet, int headRow) {
		Cell[] heads = sheet.getRow(headRow);
		String[] rv = new String[heads.length];
		for (int i = 0; i < rv.length; i++) {
			rv[i] = heads[i].getContents().trim();
		}
		return rv;
	}
		
	public static void resource2Xls(List<NcbcResource> rscs, OutputStream output) {
		try {
			if (rscs.size() == 0) {
				return;
			} 
			WritableWorkbook w = Workbook.createWorkbook(output);
			WritableSheet sheet = w.createSheet(rscs.get(0).getCenter(), 0); 

			for (int i = 0; i < Descriptor.requiredNames.length; i++) {
				String head = Descriptor.requiredNames[i];
			    Label label = new Label(i, 0 , head);
			    sheet.addCell(label); 
			    int row = 0;
			    for (NcbcResource rsc : rscs) {
			    	row++;
			    	String str = rsc.getProperty(head);
			    	if (str != null) {
			            sheet.addCell(new Label(i, row, str));
			    	}
			    }
			}
						
			w.write();
			w.close();
		} catch (Exception e) {
			logger.warn("resource2Xls error.", e);
		}

	}
	public static void main(String[] args) throws FileNotFoundException {
		XlsParser xlsParser = new XlsParser();
		xlsParser.headNames= new String[]{"Head1", "Head2","Tail3" };
		XlsParser.resource2Xls(null, new FileOutputStream("c:/tmp/test.xls"));
	}

    public void row2Resource(NcbcResource rsc) {
		
	}

	public void setForSandBox(boolean b) {
		// TODO Auto-generated method stub
		
	}
}
