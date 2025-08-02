//
//  DocumentPicker.swift
//  iosApp
//
//  Created by fredrick osuala on 2/8/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import UniformTypeIdentifiers
import PDFKit

struct DocumentPicker: UIViewControllerRepresentable {
   
    var onTextExtracted: (String) -> Void

    func makeUIViewController(context: Context) -> UIDocumentPickerViewController {
        
        let supportedTypes: [UTType] = [
            .plainText,
            .pdf
        ]
        let picker = UIDocumentPickerViewController(forOpeningContentTypes: supportedTypes, asCopy: true)
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIDocumentPickerViewController, context: Context) {
        // This view doesn't need to be updated
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    class Coordinator: NSObject, UIDocumentPickerDelegate {
        var parent: DocumentPicker

        init(parent: DocumentPicker) {
            self.parent = parent
        }

        func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
            guard let url = urls.first else { return }
            
            // Read the text from the selected file on a background thread
            DispatchQueue.global(qos: .userInitiated).async {
                var extractedText: String?
                
                if url.pathExtension.lowercased() == "pdf" {
                    if let pdfDocument = PDFDocument(url: url) {
                        extractedText = pdfDocument.string
                    }
                } else {
                    // Assume it's a text file
                    if let text = try? String(contentsOf: url) {
                        extractedText = text
                    }
                }
                
                // Switch back to the main thread to update the UI
                DispatchQueue.main.async {
                    if let text = extractedText {
                        self.parent.onTextExtracted(text)
                    }
                }
            }
        }
    }
}
