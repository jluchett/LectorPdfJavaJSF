function printContent() {
    var content = document.querySelector('.text-container').innerHTML;
    var printWindow = window.open('', '_blank');
    printWindow.document.write('<html><head><title>Imprimir PDF</title>');
    printWindow.document.write('<style>body { font-family: Arial, sans-serif; padding: 20px; }</style>');
    printWindow.document.write('</head><body>');
    printWindow.document.write('<pre style="white-space: pre-wrap;">' + content + '</pre>');
    printWindow.document.write('</body></html>');
    printWindow.document.close();
    printWindow.focus();
    printWindow.print();
    printWindow.close();
}

// Permitir selección de texto
document.addEventListener('DOMContentLoaded', function() {
    var textContainer = document.querySelector('.text-container');
    if (textContainer) {
        textContainer.addEventListener('mouseup', function() {
            var selectedText = window.getSelection().toString();
            if (selectedText.length > 0) {
                console.log('Texto seleccionado:', selectedText);
            }
        });
    }
});