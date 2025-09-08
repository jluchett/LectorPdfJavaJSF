package com.jlsoft.jsfpdf.backing;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.Part;

import com.jlsoft.jsfpdf.services.PdfService;

@ManagedBean
@ViewScoped
public class PdfBacking implements Serializable{
    private Part uploadedFile;
    private String base64Pdf;
    private List<String> pagesText;
    private int currentPage = 1;
    private int totalPages = 0;
    private String errorMessage;
    private String fileName;

    private final PdfService pdfService = new PdfService();

    public void uploadPdf() {
        try {
            if (uploadedFile != null && uploadedFile.getSize() > 0) {
                // Convertir a Base64
                try (InputStream is = uploadedFile.getInputStream()) {
                    byte[] bytes = new byte[(int) uploadedFile.getSize()];
                    is.read(bytes);
                    base64Pdf = "data:application/pdf;base64," + 
                                Base64.getEncoder().encodeToString(bytes);
                    fileName = uploadedFile.getSubmittedFileName();
                    
                    // Extraer texto y contar páginas
                    totalPages = pdfService.getPageCount(base64Pdf);
                    pagesText = pdfService.extractTextFromPdf(base64Pdf);
                    currentPage = 1;
                    errorMessage = null;
                }
            } else {
                errorMessage = "Por favor, seleccione un archivo PDF válido.";
            }
        } catch (IOException e) {
            errorMessage = "Error al procesar el PDF: " + e.getMessage();
        }
    }

    public void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
        }
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
        }
    }

    public void goToPage(int page) {
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
        }
    }

    public String getCurrentPageText() {
        if (pagesText != null && !pagesText.isEmpty() && currentPage <= pagesText.size()) {
            return pagesText.get(currentPage - 1);
        }
        return "No hay contenido para mostrar.";
    }

    // Getters y Setters
    public Part getUploadedFile() { return uploadedFile; }
    public void setUploadedFile(Part uploadedFile) { this.uploadedFile = uploadedFile; }
    
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    
    public int getTotalPages() { return totalPages; }
    public String getErrorMessage() { return errorMessage; }
    public String getFileName() { return fileName; }
  
}
