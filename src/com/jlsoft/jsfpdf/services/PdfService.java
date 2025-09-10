package com.jlsoft.jsfpdf.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfService {

    public List<String> extractTextFromPdf(String base64Pdf) throws IOException {
        List<String> pagesText = new ArrayList<>();
        
        String base64Data = base64Pdf.replaceFirst("^data:application/pdf;base64,", "");
        byte[] pdfBytes = Base64.getDecoder().decode(base64Data);
        
        // PDFBox 2.x: Usar load con byte[]
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            int totalPages = document.getNumberOfPages();
            
            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageText = stripper.getText(document);
                pagesText.add(pageText);
            }
        }
        
        return pagesText;
    }

    public int getPageCount(String base64Pdf) throws IOException {
        String base64Data = base64Pdf.replaceFirst("^data:application/pdf;base64,", "");
        byte[] pdfBytes = Base64.getDecoder().decode(base64Data);
        
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            return document.getNumberOfPages();
        }
    }

    public byte[] convertToPdfBytes(String base64Pdf) {
        String base64Data = base64Pdf.replaceFirst("^data:application/pdf;base64,", "");
        return Base64.getDecoder().decode(base64Data);
    }
  
}
