package com.jlsoft.jsfpdf.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.jlsoft.jsfpdf.services.PdfService;

@SuppressWarnings("deprecation")
@ManagedBean(name = "pdfbacking")
@ViewScoped
public class PdfBacking implements Serializable{
    
    private String base64Pdf;
    private List<String> pagesText;
    private int currentPage = 1;
    private int totalPages = 0;
    private String errorMessage;
    private String searchText;
    private int currentZoom = 100;
    private boolean textFound = false;
    private int foundPage = -1;
    private int foundIndex = -1;

    private final PdfService pdfService = new PdfService();

    public void loadPdf() {
        try {
            errorMessage = null;
            if (base64Pdf != null && !base64Pdf.trim().isEmpty()) {
                if (!base64Pdf.startsWith("data:application/pdf;base64,")) {
                    errorMessage = "El formato Base64 debe comenzar con 'data:application/pdf;base64,'";
                    return;
                }
                
                totalPages = pdfService.getPageCount(base64Pdf);
                pagesText = pdfService.extractTextFromPdf(base64Pdf);
                currentPage = 1;
                currentZoom = 100;
                
                if (totalPages == 0) {
                    errorMessage = "No se pudieron extraer páginas del PDF";
                }
            } else {
                errorMessage = "Por favor, ingrese un PDF en formato Base64";
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

    public void searchText() {
        textFound = false;
        foundPage = -1;
        foundIndex = -1;
        
        if (searchText != null && !searchText.trim().isEmpty() && pagesText != null) {
            String searchTerm = searchText.toLowerCase();
            
            for (int i = 0; i < pagesText.size(); i++) {
                String pageText = pagesText.get(i).toLowerCase();
                int index = pageText.indexOf(searchTerm);
                
                if (index != -1) {
                    textFound = true;
                    foundPage = i + 1;
                    foundIndex = index;
                    currentPage = foundPage;
                    break;
                }
            }
            
            if (!textFound) {
                errorMessage = "Texto no encontrado en el documento";
            }
        }
    }

    public void zoomIn() {
        if (currentZoom < 200) {
            currentZoom += 10;
        }
    }

    public void zoomOut() {
        if (currentZoom > 50) {
            currentZoom -= 10;
        }
    }

    public void resetZoom() {
        currentZoom = 100;
    }

    public void downloadPdf() {
        try {
            byte[] pdfBytes = pdfService.convertToPdfBytes(base64Pdf);
            
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            
            response.reset();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=document.pdf");
            response.setContentLength(pdfBytes.length);
            
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(pdfBytes);
            outputStream.flush();
            outputStream.close();
            
            facesContext.responseComplete();
        } catch (IOException e) {
            errorMessage = "Error al descargar el PDF: " + e.getMessage();
        }
    }

    public void printPdf() {
        FacesContext.getCurrentInstance().getPartialViewContext().getEvalScripts()
            .add("window.open('about:blank').document.write("
                + "'<html><head><title>Imprimir PDF</title></head><body>"
                + "<pre style=\"white-space: pre-wrap; font-family: Arial, sans-serif;\">'"
                + " + encodeURIComponent('" + getCurrentPageText().replace("'", "\\'") + "')"
                + " + '</pre></body></html>'); window.print();");
    }

    public String getCurrentPageText() {
        if (pagesText != null && !pagesText.isEmpty() && currentPage <= pagesText.size()) {
            String text = pagesText.get(currentPage - 1);
            
            // Resaltar texto buscado si está en esta página
            if (textFound && foundPage == currentPage && searchText != null) {
                String highlighted = text.replaceAll("(?i)(" + searchText + ")", 
                    "<mark style='background-color: yellow;'>$1</mark>");
                return highlighted;
            }
            
            return text;
        }
        return "No hay contenido para mostrar.";
    }

    // Getters y Setters
    public String getBase64Pdf() { return base64Pdf; }
    public void setBase64Pdf(String base64Pdf) { this.base64Pdf = base64Pdf; }
    
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    
    public int getTotalPages() { return totalPages; }
    public String getErrorMessage() { return errorMessage; }
    
    public String getSearchText() { return searchText; }
    public void setSearchText(String searchText) { this.searchText = searchText; }
    
    public int getCurrentZoom() { return currentZoom; }
    public boolean isTextFound() { return textFound; }
    public int getFoundPage() { return foundPage; }
    
    public boolean getHasPdf() {
        return pagesText != null && !pagesText.isEmpty();
    }

    public int getFoundIndex() { return foundIndex; }
  
}
