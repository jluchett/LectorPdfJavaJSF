package com.jlsoft.jsfpdf.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.faces.bean.ApplicationScoped;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@ApplicationScoped
public class PdfService {

  public List<String> extractTextFromPdf(String base64Pdf) throws IOException {
        List<String> pagesText = new ArrayList<>();
        
        // Remover el prefijo si está presente
        String base64Data = base64Pdf.replaceFirst("^data:application/pdf;base64,", "");
        
        // Decodificar Base64
        byte[] pdfBytes = Base64.getDecoder().decode(base64Data);
        
        try (InputStream is = new ByteArrayInputStream(pdfBytes);
             PDDocument document = PDDocument.load(is)) {
            
            PDFTextStripper stripper = new PDFTextStripper();
            
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                stripper.setStartPage(page + 1);
                stripper.setEndPage(page + 1);
                String pageText = stripper.getText(document);
                pagesText.add(pageText);
            }
        }
        
        return pagesText;
    }

    public int getPageCount(String base64Pdf) throws IOException {
        String base64Data = base64Pdf.replaceFirst("^data:application/pdf;base64,", "");
        byte[] pdfBytes = Base64.getDecoder().decode(base64Data);
        
        try (InputStream is = new ByteArrayInputStream(pdfBytes);
             PDDocument document = PDDocument.load(is)) {
            
            return document.getNumberOfPages();
        }
    }

    public String extractFirstPageText(String base64Pdf) throws IOException {
        String base64Data = base64Pdf.replaceFirst("^data:application/pdf;base64,", "");
        byte[] pdfBytes = Base64.getDecoder().decode(base64Data);
        
        try (InputStream is = new ByteArrayInputStream(pdfBytes);
             PDDocument document = PDDocument.load(is)) {
            
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(1);
            
            return stripper.getText(document);
        }
    }
  
}
