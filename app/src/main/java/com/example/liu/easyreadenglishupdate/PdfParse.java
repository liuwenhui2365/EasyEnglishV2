package com.example.liu.easyreadenglishupdate;


import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PdfParse {
    public String readFdf(InputStream file) {
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
            stripper.setEndPage(2);
//            stripper.setEndPage(pages);
           
//          ֵ
            float index = stripper.getIndentThreshold();
            docText = stripper.getText(doc);
//            System.out.println("pdf "+docText);
//            PDGraphicsState pState = stripper.getGraphicsState();
//
//            //System.out.println("begin page:", stripper.getStartPage());
//            //System.out.println("end page:", stripper.getEndPage());
//
//            PDDocumentCatalog cata = doc.getDocumentCatalog();
//            List allPages = (List) cata.getPages();
//            int count = 1;
//            for( int i = 0; i < allPages.size(); i++ )
//            {
//                PDPage pdPage = ( PDPage ) allPages.get( i );
//                if( null != pdPage )
//                {
//                    PDResources res = pdPage.getResources();
////                    Map imgs = res.get();
////                    if( null != imgs )
////                    {
////                        Set keySet = imgs.keySet();
////                        Iterator it = keySet.iterator();
////                        while( it.hasNext() )
////                        {
////                            Object obj =  it.next();
//////                            PDXObjectImage img = ( PDXObjectImage ) imgs.get( obj );
//////                            PDStream pdStream = img.getPDStream();
//////                            InputStream imgInput = pdStream.createInputStream();
////
////                            count++;
////                        }
////                    }
//                }
//            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        }
        return docText;
    }
}
