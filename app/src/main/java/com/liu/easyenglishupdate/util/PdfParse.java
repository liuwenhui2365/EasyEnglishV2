package com.liu.easyenglishupdate.util;


import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.util.List;

/**
 * pdf文档解析工具
 */
public class PdfParse {
    public static String readFdf(InputStream file) {
        String docText = "";
        PDDocument doc;
        doc = null;
        try {
//            FileInputStream fis = new FileInputStream(file);
            COSDocument cosDoc = null;
            PDFParser parser = new PDFParser(file);
            parser.parse();
            cosDoc = parser.getDocument();
            doc = new PDDocument(cosDoc);
//            PDDocument pdoc = PDDocument.load(fis);
//            PDDocumentInformation pInformation =  pdoc.getDocumentInformation();
//            String title = pInformation.getTitle();
//            System.out.println("标题是"+title);
            int pages = doc.getNumberOfPages();
            System.out.println("一共有"+pages+"页");
            PDFTextStripper stripper = new PDFTextStripper();
            
            stripper.setStartPage(1);
            stripper.setEndPage(1);
//            stripper.setEndPage(pages);
            stripper.setArticleStart("开始");
            stripper.setArticleEnd("结束");
            stripper.setLineSeparator("");
            stripper.setPageEnd("");
            stripper.setParagraphStart("    ");
            stripper.setParagraphEnd("\n\n");

            float index = stripper.getIndentThreshold();
            docText = stripper.getText(doc);
//            System.out.println("pdf "+docText);
            parser.close();
//            PDDocumentCatalog cata = doc.getDocumentCatalog();
//            List allPages = (List) cata.getPages();
//            int count = 1;
//            for( int i = 0; i < allPages.size(); i++ )
//            {
//                PDPage pdPage = ( PDPage ) allPages.get( i );
//                if( null != pdPage )
//                {
//                    PDResources res = pdPage.getResources();
//                    PDImageXObject pdImageXObject = new PDImageXObject();
//                    if( null != imgs )
//                    {
//                        Set keySet = imgs.keySet();
//                        Iterator it = keySet.iterator();
//                        while( it.hasNext() )
//                        {
//                            Object obj =  it.next();
//                            PDXObjectImage img = ( PDXObjectImage ) imgs.get( obj );
//                            PDImageXObject img = (PDImageXObject) imgs.get( obj );
//                            PDStream pdStream = img.getPDStream();0
//                            InputStream imgInput = pdStream.createInputStream();
//
//                            count++;
//                        }
//                    }
//                }
//            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        }
        return docText;
    }
}
